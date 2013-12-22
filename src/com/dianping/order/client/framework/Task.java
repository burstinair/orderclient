package com.dianping.order.client.framework;

/**
 * @author Burst
 *         13-12-22 下午4:00
 */
public abstract class Task<T> extends BaseTask<T, T> {

    public static class DoNothingPostDealer implements com.dianping.order.client.framework.PostDealer {

        public static final DoNothingPostDealer INSTANCE = new DoNothingPostDealer();

        @Override
        public Object postDeal(Object raw) {
            return raw;
        }
    }

    public Task() {
        super(DoNothingPostDealer.INSTANCE);
    }

    public Cancelable execute(Callback<T> callback) {
        return super.execute(callback);
    }
}
