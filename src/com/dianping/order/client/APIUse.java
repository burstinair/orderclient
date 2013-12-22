package com.dianping.order.client;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Burst
 *         13-12-22 下午3:22
 */
public final class APIUse {

    private APIUse() { }

    public static void resolve(final Callback<List<DishMenu>> callback, byte[] photo) {
        HttpPost request = new HttpPost(HttpHelper.buildUri("/api/resolve"));
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
        HttpPost request = new HttpPost(HttpHelper.buildUri("/api/submit"));
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
