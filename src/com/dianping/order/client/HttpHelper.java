package com.dianping.order.client;

import android.net.http.AndroidHttpClient;
import android.util.JsonReader;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author zhongkai.zhao
 *         13-12-21 上午12:32
 */
public final class HttpHelper {

    private static final String USER_AGENT = "OrderApi 1.0 (com.dianping.order 1.0)";
    private static final String HOST_NAME = "burstpc.tk";
    private static final int HOST_PORT = 9090;

    private static void execute() {
        HttpClient httpClient = AndroidHttpClient.newInstance(USER_AGENT);
        HttpHost httpHost = new HttpHost(HOST_NAME, HOST_PORT);
        HttpRequest httpRequest = new HttpPost();
        HttpParams httpParams = new BasicHttpParams();
        httpParams.setIntParameter("a", 1);
        httpParams.setIntParameter("b", 1);
        httpRequest.setParams(httpParams);
        try {
            HttpResponse response = httpClient.execute(httpHost, httpRequest);
            JsonReader jsonReader = new JsonReader(new InputStreamReader(response.getEntity().getContent()));
            jsonReader.beginObject();
            //Toast.makeText(getBaseContext(), jsonReader.nextInt(), Toast.LENGTH_LONG);
        } catch (IOException e) {
            //Toast.makeText(getBaseContext(), "exception " + Arrays.toString(e.getStackTrace()), Toast.LENGTH_LONG);
        }
    }

    public static List<DishMenu> resolve(byte[] photo) {
        return null;
    }

    public static void submit(Map<String, Integer> selectResult) {

    }
}
