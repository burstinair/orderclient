package com.dianping.order.client;

import android.app.Application;
import android.content.Context;

/**
 * @author Burst
 *         13-12-22 下午6:08
 */
public class OrderClientApplication extends Application {

    private static Context CONTEXT;

    public void onCreate(){
        super.onCreate();
        OrderClientApplication.CONTEXT = getApplicationContext();
    }

    public static Context getAppContext() {
        return OrderClientApplication.CONTEXT;
    }
}
