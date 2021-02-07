package com.nepxion.eventbus.thread.entity;

/**
 * 线程池拒绝策略
 */
public enum ThreadRejectedPolicyType {

    BLOCKING_POLICY_WITH_REPORT("BlockingPolicyWithReport"),
    CALLER_RUNS_POLICY_WITH_REPORT("CallerRunsPolicyWithReport"),
    ABORT_POLICY_WITH_REPORT("AbortPolicyWithReport"),
    REJECTED_POLICY_WITH_REPORT("RejectedPolicyWithReport"),
    DISCARDED_POLICY_WITH_REPORT("DiscardedPolicyWithReport");

    private final String value;

    ThreadRejectedPolicyType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ThreadRejectedPolicyType fromString(String value) {
        for (ThreadRejectedPolicyType type : ThreadRejectedPolicyType.values()) {
            if (type.getValue().equalsIgnoreCase(value.trim())) {
                return type;
            }
        }

        throw new IllegalArgumentException("Mismatched type with value=" + value);
    }

    @Override
    public String toString() {
        return value;
    }
}