package com.dianping.order.client;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

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

    public static void execute(final Callback<List<Object>> callback, final List<Task> tasks) {

        final int count = tasks.size();

        final AtomicInteger counter = new AtomicInteger(0);
        final Map<Integer, Object> resultMap = new ConcurrentHashMap<Integer, Object>();

        for(final Task task : tasks) {
            task.execute(new Callback() {
                @Override
                public void handle(Object result) {
                    resultMap.put(tasks.indexOf(task), result);
                    if(counter.incrementAndGet() == count) {
                        List<Object> resultList = new ArrayList<Object>();
                        for(int i = 0; i < count; ++i) {
                            resultList.add(resultMap.get(i));
                        }
                        callback.handle(resultList);
                    }
                }
            });
        }
    }
}
