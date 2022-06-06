package com.devpos.hotelapp.models;

public class PayModel {
    private String typePay;
    private int pricePay;

    public PayModel() {

    }

    public String getTypePay() {
        return typePay;
    }

    public void setTypePay(String typePay) {
        this.typePay = typePay;
    }

    public int getPricePay() {
        return pricePay;
    }

    public void setPricePay(int pricePay) {
        this.pricePay = pricePay;
    }
}
