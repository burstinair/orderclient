package com.dianping.order.client;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

    public static void resolve(final HttpCallback<List<DishMenu>> callback, byte[] photo) {
        HttpPost request = new HttpPost(buildUri("/api/resolve"));
        request.setEntity(new ByteArrayEntity(photo));
        new HttpHelper(request, new HttpCallback<JSONTokener>() {
            @Override
            public void handle(JSONTokener raw) {
                List<DishMenu> result = new ArrayList<DishMenu>();
                try {
                    JSONObject root = (JSONObject) raw.nextValue();
                    if("!success".equals(root.getString("status"))) {
                        result = null;
                    } else {
                        JSONArray resultArray = root.getJSONArray("result");
                        for(int i = 0, l = resultArray.length(); i < l; ++i) {
                            JSONObject dishMenuRaw = resultArray.getJSONObject(i);
                            DishMenu dishMenu = new DishMenu();
                            dishMenu.setName(dishMenuRaw.getString("name"));
                            dishMenu.setPrice(dishMenuRaw.getDouble("price"));
                            result.add(dishMenu);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    result = null;
                }
                callback.handle(result);
            }
        }).executeOnExecutor(EXECUTOR);
    }

    public static void submit(final HttpCallback<String> callback, List<DishMenu> selectResult) {
        HttpPost request = new HttpPost(buildUri("/api/submit"));
        try {
            JSONArray parameter = new JSONArray();
            for(DishMenu dishMenu : selectResult) {
                JSONObject dishMenuParam = new JSONObject();
                dishMenuParam.put("id", dishMenu.getId());
                dishMenuParam.put("name", dishMenu.getName());
                dishMenuParam.put("count", dishMenu.getCount());
                parameter.put(dishMenuParam);
            }
            request.setEntity(new StringEntity(parameter.toString(), "UTF-8"));
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        new HttpHelper(request, new HttpCallback<JSONTokener>() {
            @Override
            public void handle(JSONTokener raw) {
                try {
                    JSONObject root = (JSONObject) raw.nextValue();
                    callback.handle(root.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.handle(null);
                }
            }
        }).executeOnExecutor(EXECUTOR);
    }
}
