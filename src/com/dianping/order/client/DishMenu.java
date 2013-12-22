package com.dianping.order.client;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author zhongkai.zhao
 *         13-12-21 上午12:32
 */
public class DishMenu implements Parcelable {

    private int id;
    private String name;
    private double price;
    private int count = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addCount() {
        this.count++;
    }

    public void delCount() {
        if (this.count > 0) {
            this.count--;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public DishMenu() { }

    public DishMenu(Parcel parcel) {
        this.id = parcel.readInt();
        this.name = parcel.readString();
        this.price = parcel.readDouble();
        this.count = parcel.readInt();
    }

    public static final Creator<DishMenu> CREATOR = new Creator<DishMenu>() {
        @Override
        public DishMenu createFromParcel(Parcel source) {
            return new DishMenu(source);
        }

        @Override
        public DishMenu[] newArray(int size) {
            return new DishMenu[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeInt(count);
    }
}
