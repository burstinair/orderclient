package com.dianping.order.client;

import android.net.http.AndroidHttpClient;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public static String buildUri(String path) {
        return "http://" + HOST_NAME + ":" + HOST_PORT + path;
    }

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

    private HttpUriRequest request;

    public HttpHelper(HttpUriRequest request) {
        this.request = request;
    }
}
