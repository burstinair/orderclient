package com.dianping.order.client;

/**
 * @author zhongkai.zhao
 *         13-12-21 上午12:32
 */
public class DishMenu {

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
}
