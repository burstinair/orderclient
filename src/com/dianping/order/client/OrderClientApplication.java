package com.dianping.order.client;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * @author Burst
 *         13-12-22 下午6:08
 */
public class OrderClientApplication extends Application {

    private static Context context;

    private static String deviceId;

    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = tm.getDeviceId();
    }

    public static Context getAppContext() {
        return context;
    }

    public static String getDeviceId() {
        return deviceId;
    }
}
