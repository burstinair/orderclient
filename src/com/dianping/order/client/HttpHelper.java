package com.dianping.order.client;

import android.net.http.AndroidHttpClient;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
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
public final class HttpHelper {

    public interface HttpCallback {
        void handle(JSONTokener result, int requestCode);
    }

    private static final String USER_AGENT = "OrderApi 1.0 (com.dianping.order 1.0)";
    private static final String HOST_NAME = "burstpc.tk";
    private static final int HOST_PORT = 9090;

    private static final HttpClient HTTP_CLIENT = AndroidHttpClient.newInstance(USER_AGENT);
    private static final HttpHost HTTP_HOST = new HttpHost(HOST_NAME, HOST_PORT);

    private static String readFully(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        return new String(byteArrayOutputStream.toByteArray(), "UTF-8");
    }

    public static void execute(final HttpParams params, final HttpCallback callback, final int requestCode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpRequest httpRequest = new HttpPost();
                    httpRequest.setParams(params);
                    HttpResponse response = HTTP_CLIENT.execute(HTTP_HOST, httpRequest);
                    callback.handle(new JSONTokener(readFully(response.getEntity().getContent())), requestCode);
                } catch (IOException e) {
                    callback.handle(null, requestCode);
                }
            }
        });
    }

    public static void rec() {
        HttpParams httpParams = new BasicHttpParams();
        httpParams.setIntParameter("a", 1);
        httpParams.setIntParameter("b", 1);
    }

    public static List<DishMenu> resolve(byte[] photo) {
        return null;
    }

    public static void submit(Map<String, Integer> selectResult) {

    }
}
