package com.example.administrator.a1511ashopping.model.bean;

/**
 * Created by Administrator on 2018/1/15.
 */

public class CountPriceBean {

    private int count;
    private String priceString;

    public CountPriceBean(String priceString, int count) {
        this.priceString = priceString;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPriceString() {
        return priceString;
    }

    public void setPriceString(String priceString) {
        this.priceString = priceString;
    }
}