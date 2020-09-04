package com.dibomart.dibomart.model;

import java.io.Serializable;

public class PaymentMethodList implements Serializable {

    private String code;
    private String title;
    private String terms;
    private String sort_order;

    public PaymentMethodList() {
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

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public String getSort_order() {
        return sort_order;
    }

    public void setSort_order(String sort_order) {
        this.sort_order = sort_order;
    }
}
