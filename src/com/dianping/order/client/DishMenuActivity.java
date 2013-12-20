package com.dianping.order.client;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author zhongkai.zhao
 *         13-12-20 下午11:33
 */
public class DishMenuActivity extends Activity {

    private ArrayList<Integer> dishNameList = new ArrayList<Integer>();
    private HashMap<String, Integer> dish2BuyCount = new HashMap<String, Integer>();
    private HashMap<String, Double> dish2Price = new HashMap<String, Double>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dishmenu);
    }
}
