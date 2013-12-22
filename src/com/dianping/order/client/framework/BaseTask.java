package com.dianping.order.client.framework;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Burst
 *         13-12-22 下午2:41
 */
public abstract class BaseTask<RawType, ResultType> extends AsyncTask<Void, Void, RawType> implements Cancelable {

    private static final Executor EXECUTOR = Executors.newCachedThreadPool();

    private Callback<ResultType> callback;
    private PostDealer<RawType, ResultType> postDealer;

    public BaseTask(PostDealer<RawType, ResultType> postDealer) {
        this.postDealer = postDealer;
    }

    public Cancelable execute(Callback<ResultType> callback) {
        this.callback = callback;
        executeOnExecutor(EXECUTOR);
        return this;
    }

    @Override
    protected void onPostExecute(RawType result) {
        try {
            callback.handle(postDealer.postDeal(result), getResultStatus());
        } catch (Throwable ex) {
            Log.w("BaseTask.onPostExecute", ex);
            setResultStatus(ResultStatus.EXCEPTION_IN_POST_DEAL);
            callback.handle(null, ResultStatus.EXCEPTION_IN_POST_DEAL);
        }
        callback = null;
        super.onPostExecute(result);
        cancel(true);
    }

    protected abstract RawType doInBackground();

    @Override
    protected RawType doInBackground(Void... voids) {
        return doInBackground();
    }

    private ResultStatus resultStatus = ResultStatus.SUCCESS;

    public ResultStatus getResultStatus() {
        return resultStatus;
    }

    protected void setResultStatus(ResultStatus resultStatus) {
        this.resultStatus = resultStatus;
    }

    @Override
    public void cancel() {
        setResultStatus(ResultStatus.CANCELED);
        this.cancel(true);
        callback.handle(null, ResultStatus.CANCELED);
    }

    public static Cancelable execute(final Callback<List<Object>> callback, final List<BaseTask> tasks) {

        final int count = tasks.size();

        final AtomicInteger counter = new AtomicInteger(0);
        final Map<Integer, Object> resultMap = new HashMap<Integer, Object>();
        final List<ResultStatus> statusList = new ArrayList<ResultStatus>();

        for(final BaseTask task : tasks) {
            task.execute(new Callback() {
                @Override
                public void handle(Object result, ResultStatus resultStatus) {
                    synchronized (resultMap) {
                        resultMap.put(tasks.indexOf(task), result);
                        statusList.add(resultStatus);
                    }
                    if(counter.incrementAndGet() == count) {
                        List<Object> resultList = new ArrayList<Object>();
                        for(int i = 0; i < count; ++i) {
                            resultList.add(resultMap.get(i));
                        }
                        ResultStatus finalResultStatus = ResultStatus.SUCCESS;
                        for(ResultStatus status : statusList) {
                            if(status.getValue() > finalResultStatus.getValue()) {
                                finalResultStatus = status;
                            }
                        }
                        callback.handle(resultList, finalResultStatus);
                    }
                }
            });
        }

        return new Cancelable() {
            @Override
            public void cancel() {
                for(final BaseTask task : tasks) {
                    task.cancel();
                }
            }
        };
    }
}
