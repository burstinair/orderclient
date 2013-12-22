package com.dianping.order.client.framework;

/**
 * @author Burst
 *         13-12-22 下午6:59
 */
public enum ResultStatus {
    SUCCESS(0),
    EXCEPTION_IN_POST_DEAL(1),
    EXCEPTION_IN_RUNNING(2),
    CANCELED(3);

    private int value;

    public int getValue() {
        return value;
    }

    ResultStatus(int value) {
        this.value = value;
    }
}
