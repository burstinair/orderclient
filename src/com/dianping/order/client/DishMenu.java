package com.dianping.order.client;

/**
 * @author zhongkai.zhao
 *         13-12-21 上午12:32
 */
public class DishMenu {

    private String title;
    private double price;
    private int count = 0;

    public void addCount() {
        this.count++;
    }

    public void delCount() {
        if (this.count > 0) {
            this.count--;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}
