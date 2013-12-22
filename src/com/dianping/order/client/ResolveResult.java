package com.dianping.order.client;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Burst
 *         13-12-22 下午9:09
 */
public class ResolveResult implements Parcelable {
    private List<DishMenu> dishMenuList;
    private byte[] filteredImage;

    public List<DishMenu> getDishMenuList() {
        return dishMenuList;
    }

    public void setDishMenuList(List<DishMenu> dishMenuList) {
        this.dishMenuList = dishMenuList;
    }

    public byte[] getFilteredImage() {
        return filteredImage;
    }

    public void setFilteredImage(byte[] filteredImage) {
        this.filteredImage = filteredImage;
    }

    public ResolveResult() { }

    public ResolveResult(Parcel parcel) {
        this.dishMenuList = new ArrayList<DishMenu>();
        parcel.readTypedList(this.dishMenuList, DishMenu.CREATOR);
        this.filteredImage = new byte[parcel.readInt()];
        parcel.readByteArray(this.filteredImage);
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
        dest.writeInt(filteredImage.length);
        dest.writeByteArray(filteredImage);
    }
}