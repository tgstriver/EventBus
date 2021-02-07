package com.nepxion.eventbus.thread;

import com.nepxion.eventbus.thread.constant.ThreadConstant;
import com.nepxion.eventbus.thread.entity.ThreadCustomization;
import com.nepxion.eventbus.thread.entity.ThreadParameter;
import com.nepxion.eventbus.thread.entity.ThreadQueueType;
import com.nepxion.eventbus.thread.entity.ThreadRejectedPolicyType;
import com.nepxion.eventbus.thread.policy.AbortPolicyWithReport;
import com.nepxion.eventbus.thread.policy.BlockingPolicyWithReport;
import com.nepxion.eventbus.thread.policy.CallerRunsPolicyWithReport;
import com.nepxion.eventbus.thread.policy.DiscardedPolicyWithReport;
import com.nepxion.eventbus.thread.policy.RejectedPolicyWithReport;
import com.nepxion.eventbus.thread.util.NetUtil;
import com.nepxion.eventbus.thread.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ThreadPoolFactory.class);

    private volatile Map<String, ThreadPoolExecutor> threadPoolExecutorMap = new ConcurrentHashMap<>();
    private volatile ThreadPoolExecutor threadPoolExecutor;

    private ThreadCustomization threadCustomization;
    private ThreadParameter threadParameter;

    public ThreadPoolFactory(ThreadCustomization threadCustomization, ThreadParameter threadParameter) {
        this.threadCustomization = threadCustomization;
        this.threadParameter = threadParameter;
    }

    /**
     * 根据线程池名称获取线程池执行器
     *
     * @param threadPoolName 线程池名称
     * @return
     */
    public ThreadPoolExecutor getThreadPoolExecutor(String threadPoolName) {
        boolean threadPoolMultiMode = threadCustomization.isThreadPoolMultiMode(); // 多个线程池是否进行线程隔离
        String poolName = this.createThreadPoolName(threadPoolName);

        if (threadPoolMultiMode) {
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(poolName);
            if (threadPoolExecutor == null) {
                ThreadPoolExecutor newThreadPoolExecutor = this.createThreadPoolExecutor(poolName);
                threadPoolExecutor = threadPoolExecutorMap.putIfAbsent(poolName, newThreadPoolExecutor);
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = newThreadPoolExecutor;
                }
            }

            return threadPoolExecutor;
        } else {
            return this.createSharedThreadPoolExecutor();
        }
    }

    private ThreadPoolExecutor createSharedThreadPoolExecutor() {
        String threadPoolSharedName = threadCustomization.getThreadPoolSharedName();
        if (StringUtils.isEmpty(threadPoolSharedName)) {
            threadPoolSharedName = ThreadConstant.DEFAULT_THREADPOOL_SHARED_NAME;
        }

        String poolName = this.createThreadPoolName(threadPoolSharedName);
        if (threadPoolExecutor == null) {
            synchronized (this) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = this.createThreadPoolExecutor(poolName);
                }
            }
        }

        return threadPoolExecutor;
    }

    /**
     * 创建线程池名称
     *
     * @param threadPoolName
     * @return
     */
    private String createThreadPoolName(String threadPoolName) {
        boolean threadPoolNameIPShown = threadCustomization.isThreadPoolNameIPShown(); // 线程池名称中是否显示ip
        return threadPoolNameIPShown ? StringUtil.firstLetterToUpper(threadPoolName) + "-" + NetUtil.getLocalHost() + "-thread" : StringUtil.firstLetterToUpper(threadPoolName) + "-thread";
    }

    private ThreadPoolExecutor createThreadPoolExecutor(String threadPoolName) {
        boolean threadPoolNameCustomized = threadCustomization.isThreadPoolNameCustomized();
        return threadPoolNameCustomized ? createThreadPoolExecutor(threadPoolName, threadParameter) : createThreadPoolExecutor(threadParameter);
    }

    public static ThreadPoolExecutor createThreadPoolExecutor(String threadPoolName, ThreadParameter threadParameter) {
        int corePoolSize = threadParameter.getThreadPoolCorePoolSize();
        int maximumPoolSize = threadParameter.getThreadPoolMaximumPoolSize();
        long keepAliveTime = threadParameter.getThreadPoolKeepAliveTime();
        boolean allowCoreThreadTimeout = threadParameter.isThreadPoolAllowCoreThreadTimeout();
        String queue = threadParameter.getThreadPoolQueue();
        int queueCapacity = threadParameter.getThreadPoolQueueCapacity();
        String rejectedPolicy = threadParameter.getThreadPoolRejectedPolicy();

        LOG.info("Thread pool executor is created, threadPoolName={}, threadParameter={}", threadPoolName, threadParameter);

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.MILLISECONDS,
                createBlockingQueue(queue, queueCapacity),
                new ThreadFactory() {
                    private final AtomicInteger number = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable runnable) {
                        return new Thread(runnable, threadPoolName + "-" + number.getAndIncrement());
                    }
                },
                createRejectedPolicy(rejectedPolicy));
        threadPoolExecutor.allowCoreThreadTimeOut(allowCoreThreadTimeout);

        return threadPoolExecutor;
    }

    public static ThreadPoolExecutor createThreadPoolExecutor(ThreadParameter threadParameter) {
        int corePoolSize = threadParameter.getThreadPoolCorePoolSize();
        int maximumPoolSize = threadParameter.getThreadPoolMaximumPoolSize();
        long keepAliveTime = threadParameter.getThreadPoolKeepAliveTime();
        boolean allowCoreThreadTimeout = threadParameter.isThreadPoolAllowCoreThreadTimeout();
        String queue = threadParameter.getThreadPoolQueue();
        int queueCapacity = threadParameter.getThreadPoolQueueCapacity();
        String rejectedPolicy = threadParameter.getThreadPoolRejectedPolicy();

        LOG.info("Thread pool executor is created, threadParameter={}", threadParameter);

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.MILLISECONDS,
                createBlockingQueue(queue, queueCapacity),
                createRejectedPolicy(rejectedPolicy));
        threadPoolExecutor.allowCoreThreadTimeOut(allowCoreThreadTimeout);

        return threadPoolExecutor;
    }

    private static BlockingQueue<Runnable> createBlockingQueue(String queue, int queueCapacity) {
        ThreadQueueType queueType = ThreadQueueType.fromString(queue);

        switch (queueType) {
            case LINKED_BLOCKING_QUEUE:
                return new LinkedBlockingQueue<>(queueCapacity);
            case ARRAY_BLOCKING_QUEUE:
                return new ArrayBlockingQueue<>(queueCapacity);
            case SYNCHRONOUS_QUEUE:
                return new SynchronousQueue<>();
            default:
                break;
        }

        return null;
    }

    private static RejectedExecutionHandler createRejectedPolicy(String rejectedPolicy) {
        ThreadRejectedPolicyType rejectedPolicyType = ThreadRejectedPolicyType.fromString(rejectedPolicy);

        switch (rejectedPolicyType) {
            case BLOCKING_POLICY_WITH_REPORT:
                return new BlockingPolicyWithReport();
            case CALLER_RUNS_POLICY_WITH_REPORT:
                return new CallerRunsPolicyWithReport();
            case ABORT_POLICY_WITH_REPORT:
                return new AbortPolicyWithReport();
            case REJECTED_POLICY_WITH_REPORT:
                return new RejectedPolicyWithReport();
            case DISCARDED_POLICY_WITH_REPORT:
                return new DiscardedPolicyWithReport();
            default:
                break;
        }

        return null;
    }

    public void shutdown() {
        if (threadPoolExecutor != null && !threadPoolExecutor.isShutdown()) {
            LOG.info("Shutting down thread pool executor [{}]...", threadPoolExecutor);
            threadPoolExecutor.shutdown();
        }

        for (Map.Entry<String, ThreadPoolExecutor> entry : threadPoolExecutorMap.entrySet()) {
            ThreadPoolExecutor executor = entry.getValue();

            if (executor != null && !executor.isShutdown()) {
                LOG.info("Shutting down thread pool executor [{}] ...", threadPoolExecutor);
                executor.shutdown();
            }
        }
    }
}