package com.dianping.order.client.framework;

/**
 * @author Burst
 *         13-12-22 下午3:49
 */
public interface PostDealer<RawType, ResultType> {
    ResultType postDeal(RawType raw);
}
