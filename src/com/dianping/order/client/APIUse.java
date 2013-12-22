package com.dianping.order.client;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.dianping.order.client.framework.*;
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

    public static class ResolveResult implements Parcelable {
        private List<DishMenu> dishMenuList;
        private Bitmap filteredBitmap;

        public List<DishMenu> getDishMenuList() {
            return dishMenuList;
        }

        public void setDishMenuList(List<DishMenu> dishMenuList) {
            this.dishMenuList = dishMenuList;
        }

        public Bitmap getFilteredBitmap() {
            return filteredBitmap;
        }

        public void setFilteredBitmap(Bitmap filteredBitmap) {
            this.filteredBitmap = filteredBitmap;
        }

        public ResolveResult() { }

        public ResolveResult(Parcel parcel) {
            this.dishMenuList = new ArrayList<DishMenu>();
            parcel.readTypedList(this.dishMenuList, DishMenu.CREATOR);
            parcel.readParcelable(Drawable.class.getClassLoader());
        }

        public static final Creator<ResolveResult> CREATOR = new Creator<ResolveResult>() {
            @Override
            public ResolveResult createFromParcel(Parcel source) {
                return new ResolveResult(source);
            }

            @Override
            public ResolveResult[] newArray(int size) {
                return new ResolveResult[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedList(dishMenuList);
            dest.writeParcelable(filteredBitmap, flags);
        }
    }

    public static Cancelable resolve(final Callback<ResolveResult> callback, byte[] photo) {
        HttpPost request = new HttpPost(HttpHelper.buildUri("/api/resolve"));
        request.setEntity(new ByteArrayEntity(photo));
        List<BaseTask> tasks = new ArrayList<BaseTask>();
        tasks.add(new HttpHelper<List<DishMenu>>(request, new PostDealer<byte[], List<DishMenu>>() {
            @Override
            public List<DishMenu> postDeal(byte[] raw) {
                List<DishMenu> result = new ArrayList<DishMenu>();
                try {
                    JSONObject root = new JSONObject(new String(raw, "UTF-8"));
                    if ("!success".equals(root.getString("status"))) {
                        return null;
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
                        return result;
                    }
                } catch (Throwable ex) {
                    Log.w("APIUse.resolve", ex);
                    return null;
                }
            }
        }));
        tasks.add(new Blur(photo));
        return BaseTask.execute(new Callback<List<Object>>() {
            @Override
            public void handle(List<Object> result, ResultStatus resultStatus) {
                if(resultStatus == ResultStatus.SUCCESS) {
                    ResolveResult resolveResult = new ResolveResult();
                    resolveResult.setDishMenuList((List<DishMenu>) result.get(0));
                    resolveResult.setFilteredBitmap((Bitmap) result.get(1));
                    callback.handle(resolveResult, resultStatus);
                } else {
                    callback.handle(null, resultStatus);
                }
            }
        }, tasks);
    }

    public static Cancelable submit(final Callback<String> callback, List<DishMenu> selectResult) {
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
            Log.w("APIUse.submit", ex);
        }
        return new HttpHelper<String>(request, new PostDealer<byte[], String>() {
            @Override
            public String postDeal(byte[] raw) {
                try {
                    JSONObject root = new JSONObject(new String(raw, "UTF-8"));
                    return root.getString("status");
                } catch (Throwable ex) {
                    Log.w("APIUse.submit", ex);
                    return null;
                }
            }
        }).execute(callback);
    }
}
