package com.nepxion.eventbus.thread.constant;

public interface ThreadConstant {

    int CPUS = Math.max(2, Runtime.getRuntime().availableProcessors());

    /**
     * 多个线程池是否进行线程隔离。如果是，那么每个不同identifier的事件都会占用一个单独的线程池，否则共享一个线程池
     */
    String THREAD_POOL_MULTI_MODE = "threadpool.multi.mode";

    /**
     * 共享线程池的名称
     */
    String THREAD_POOL_SHARED_NAME = "threadpool.shared.name";

    /**
     * 是否显示自定义的线程池名
     */
    String THREAD_POOL_NAME_CUSTOMIZED = "threadpool.name.customized";

    /**
     * 线程池名称中是否显示ip
     */
    String THREAD_POOL_NAME_IP_SHOWN = "threadpool.name.ipshown";
    String THREAD_POOL_CORE_POOL_SIZE = "threadpool.core.pool.size";
    String THREAD_POOL_MAXIMUM_POOL_SIZE = "threadpool.maximum.pool.size";
    String THREAD_POOL_KEEP_ALIVE_TIME = "threadpool.keep.alive.time";
    String THREAD_POOL_ALLOW_CORE_THREAD_TIMEOUT = "threadpool.allow.core.thread.timeout";
    String THREAD_POOL_QUEUE = "threadpool.queue";
    String THREAD_POOL_QUEUE_CAPACITY = "threadpool.queue.capacity";
    String THREAD_POOL_REJECTED_POLICY = "threadpool.rejected.policy";

    /**
     * 共享线程池的默认名称
     */
    String DEFAULT_THREADPOOL_SHARED_NAME = "SharedThreadPool";
}