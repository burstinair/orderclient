package com.dianping.order.client;

import android.os.AsyncTask;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Burst
 *         13-12-22 下午2:41
 */
public abstract class Task<T> extends AsyncTask<Void, Void, T> {

    private static final Executor EXECUTOR = Executors.newCachedThreadPool();

    private Callback<T> callback;

    public void execute(Callback<T> callback) {
        this.callback = callback;
        executeOnExecutor(EXECUTOR);
    }

    @Override
    protected void onPostExecute(T result) {
        try {
            callback.handle(result);
        } catch (Throwable e) {
            e.printStackTrace();
            callback.handle(null);
        }
        callback = null;
        super.onPostExecute(result);
        cancel(true);
    }

    protected abstract T doInBackground();

    @Override
    protected T doInBackground(Void... voids) {
        return doInBackground();
    }
}
