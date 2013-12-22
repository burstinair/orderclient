package com.dianping.order.client.framework;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.util.Log;
import com.dianping.order.client.BuildConfig;
import com.dianping.order.client.OrderClientApplication;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author zhongkai.zhao
 *         13-12-21 上午12:32
 */
public class HttpHelper<T> extends BaseTask<byte[], T> {

    private boolean isNetworkAvailable() {
        ConnectivityManager cManager = (ConnectivityManager) OrderClientApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cManager.getActiveNetworkInfo();
        return info != null && info.isAvailable();
    }

    @Override
    public Cancelable execute(Callback<T> callback) {
        if(isNetworkAvailable()) {
            return super.execute(callback);
        } else {
            setResultStatus(ResultStatus.EXCEPTION_IN_RUNNING);
            callback.handle(null, ResultStatus.EXCEPTION_IN_RUNNING);
            return this;
        }
    }

    @Override
    protected byte[] doInBackground() {
        try {
            HttpClient httpClient = AndroidHttpClient.newInstance(USER_AGENT);
            HttpHost httpHost = new HttpHost(HOST_NAME, HOST_PORT);
            return readFully(httpClient.execute(httpHost, request).getEntity().getContent());
        } catch (IOException ex) {
            Log.w("HttpHelper.doInBackground", ex);
            setResultStatus(ResultStatus.EXCEPTION_IN_RUNNING);
        }
        return null;
    }

    private static final String USER_AGENT;
    private static final String HOST_NAME;
    private static final int HOST_PORT;
    private static final int TIMEOUT;

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
        } catch (IOException ex) {
            Log.w("HttpHelper.static", ex);
        }
        USER_AGENT = properties.getProperty("userAgent");
        HOST_NAME = properties.getProperty("hostName");
        HOST_PORT = Integer.parseInt(properties.getProperty("hostPort"));
        TIMEOUT = Integer.parseInt(properties.getProperty("timeout"));
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
            Log.w("HttpHelper.readFully", ex);
        }
        if(length > 0) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private HttpUriRequest request;

    public HttpHelper(HttpUriRequest request, PostDealer<byte[], T> postDealer) {
        super(postDealer);
        HttpParams httpParams = request.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT);
        request.setParams(httpParams);
        this.request = request;
    }
}
