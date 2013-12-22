package com.dianping.order.client;

import android.net.http.AndroidHttpClient;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author zhongkai.zhao
 *         13-12-21 上午12:32
 */
public class HttpHelper extends Task<byte[]> {

    @Override
    protected byte[] doInBackground() {
        try {
            HttpClient httpClient = AndroidHttpClient.newInstance(USER_AGENT);
            HttpHost httpHost = new HttpHost(HOST_NAME, HOST_PORT);
            return readFully(httpClient.execute(httpHost, request).getEntity().getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    public HttpHelper(HttpUriRequest request) {
        this.request = request;
    }

    public static void resolve(final Callback<List<DishMenu>> callback, byte[] photo) {
        HttpPost request = new HttpPost(buildUri("/api/resolve"));
        request.setEntity(new ByteArrayEntity(photo));
        new HttpHelper(request).execute(new Callback<byte[]>() {
            @Override
            public void handle(byte[] raw) {
                List<DishMenu> result = new ArrayList<DishMenu>();
                try {
                    JSONObject root = new JSONObject(new String(raw, "UTF-8"));
                    if ("!success".equals(root.getString("status"))) {
                        result = null;
                    } else {
                        JSONArray resultArray = root.getJSONArray("result");
                        for (int i = 0, l = resultArray.length(); i < l; ++i) {
                            JSONObject dishMenuRaw = resultArray.getJSONObject(i);
                            DishMenu dishMenu = new DishMenu();
                            dishMenu.setId(dishMenuRaw.getInt("id"));
                            dishMenu.setName(dishMenuRaw.getString("name"));
                            dishMenu.setPrice(dishMenuRaw.getDouble("price"));
                            result.add(dishMenu);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    result = null;
                }
                callback.handle(result);
            }
        });
    }

    public static void submit(final Callback<String> callback, List<DishMenu> selectResult) {
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
        new HttpHelper(request).execute(new Callback<byte[]>() {
            @Override
            public void handle(byte[] raw) {
                try {
                    JSONObject root = new JSONObject(new String(raw, "UTF-8"));
                    callback.handle(root.getString("status"));
                } catch (Throwable e) {
                    e.printStackTrace();
                    callback.handle(null);
                }
            }
        });
    }
}
