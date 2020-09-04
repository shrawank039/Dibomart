package com.dibomart.dibomart.model;

import java.io.Serializable;

public class ShippingMethodList implements Serializable {

    private String title;
    private String code;
    private int charges;

    public ShippingMethodList() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCharges() {
        return charges;
    }

    public void setCharges(int charges) {
        this.charges = charges;
    }
}
