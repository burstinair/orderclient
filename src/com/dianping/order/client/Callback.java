package com.dianping.order.client;

/**
 * @author Burst
 *         13-12-22 下午2:40
 */
public interface Callback<T> {
    void handle(T result);
}
