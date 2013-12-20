package com.dianping.order.client;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author zhongkai.zhao
 *         13-12-21 上午12:32
 */
public class HttpHelper extends AsyncTask<String, Void, HttpResponse> {

    @Override
    protected HttpResponse doInBackground(String... strings) {
        HttpResponse response = null;
        JSONTokener result = null;
        try {
            return HTTP_CLIENT.execute(HTTP_HOST, request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(HttpResponse response) {
        try {
            callback.handle(new JSONTokener(readFully(response.getEntity().getContent())), requestCode);
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        callback.handle(null, requestCode);
    }

    public interface HttpCallback {
        void handle(JSONTokener result, int requestCode);
    }

    private static final String USER_AGENT = "OrderApi 1.0 (com.dianping.order 1.0)";
    private static final String HOST_NAME = "192.168.56.1";
    private static final int HOST_PORT = 8321;

    private static final HttpClient HTTP_CLIENT = AndroidHttpClient.newInstance(USER_AGENT);
    private static final HttpHost HTTP_HOST = new HttpHost(HOST_NAME, HOST_PORT);

    private static String readFully(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        try {
            while ((length = inputStream.read(buffer)) == 1024) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
        } catch (Throwable ex) { }
        if(length > 0) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        return new String(byteArrayOutputStream.toByteArray(), "UTF-8");
    }

    public static String buildUri(String path) {
        return "http://" + HOST_NAME + ":" + HOST_PORT + path;
    }

    private HttpUriRequest request;
    private HttpCallback callback;
    private int requestCode;

    public HttpHelper(HttpUriRequest request, HttpCallback callback, int requestCode) {
        this.request = request;
        this.callback = callback;
        this.requestCode = requestCode;
    }

    public static void rec(HttpCallback callback, int requestCode) {
        HttpUriRequest request = new HttpGet(buildUri("/api/rec?a=34&b=56"));
        new HttpHelper(request, callback, requestCode).execute();
    }

    public static List<DishMenu> resolve(byte[] photo) {
        return null;
    }

    public static void submit(Map<String, Integer> selectResult) {

    }
}
