package com.dianping.order.client;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author zhongkai.zhao
 *         13-12-21 上午12:32
 */
public class HttpHelper extends AsyncTask<Void, Void, byte[]> {

    private static final Executor EXECUTOR = Executors.newCachedThreadPool();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected byte[] doInBackground(Void... voids) {
        try {
            HttpClient httpClient = AndroidHttpClient.newInstance(USER_AGENT);
            HttpHost httpHost = new HttpHost(HOST_NAME, HOST_PORT);
            return readFully(httpClient.execute(httpHost, request).getEntity().getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(byte[] response) {
        try {
            String resultString = new String(response, "UTF-8");
            callback.handle(new JSONTokener(resultString));
        } catch (Throwable e) {
            e.printStackTrace();
            callback.handle(null);
        }
        request = null;
        callback = null;
        super.onPostExecute(response);
        cancel(true);
    }

    public interface HttpCallback<T> {
        void handle(T result);
    }

    private static final String USER_AGENT;
    private static final String HOST_NAME;
    private static final int HOST_PORT;

    static {
        Properties properties = new Properties();
        InputStream inputStream;
        if(BuildConfig.DEBUG) {
            inputStream = HttpHelper.class.getResourceAsStream("/assets/settings-debug.properties");
        } else {
            inputStream = HttpHelper.class.getResourceAsStream("/assets/settings.properties");
        }
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        USER_AGENT = properties.getProperty("userAgent");
        HOST_NAME = properties.getProperty("hostName");
        HOST_PORT = Integer.parseInt(properties.getProperty("hostPort"));
    }

    private static byte[] readFully(InputStream inputStream) throws IOException {
        inputStream = new BufferedInputStream(inputStream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        try {
            while ((length = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        if(length > 0) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static String buildUri(String path) {
        return "http://" + HOST_NAME + ":" + HOST_PORT + path;
    }

    private HttpUriRequest request;
    private HttpCallback<JSONTokener> callback;

    public HttpHelper(HttpUriRequest request, HttpCallback<JSONTokener> callback) {
        this.request = request;
        this.callback = callback;
    }

    public static void rec(HttpCallback<JSONTokener> callback) {
        HttpUriRequest request = new HttpGet(buildUri("/api/rec?a=34&b=56"));
        new HttpHelper(request, callback).executeOnExecutor(EXECUTOR);
    }

    public static void resolve(final HttpCallback<List<DishMenu>> callback, byte[] photo) {
        HttpPost request = new HttpPost(buildUri("/api/resolve"));
        request.setEntity(new ByteArrayEntity(photo));
        new HttpHelper(request, new HttpCallback<JSONTokener>() {
            @Override
            public void handle(JSONTokener result) {
                callback.handle(/* TODO */null);
            }
        }).executeOnExecutor(EXECUTOR);
    }

    public static void submit(final HttpCallback<JSONTokener> callback, int requestCode, Map<String, Integer> selectResult) {
        HttpUriRequest request = new HttpPost();
        //TODO
        new HttpHelper(request, callback).executeOnExecutor(EXECUTOR);
    }
}
