package com.dibomart.dibomart.model;

import java.io.Serializable;
import java.util.List;

public class ProductList implements Serializable {
    private String name;
    private String image_url;
    private String id;
    private String weight_class;
    private String weight;
    private int item_count;
    private String product_id;
    private int price;
    private int special_price;
    private String description;
    private String product_option_id;
    private List<ProductOption> productOptions;

    public ProductList() {
    }

    public String getWeight_class() {
        return weight_class;
    }

    public void setWeight_class(String weight_class) {
        this.weight_class = weight_class;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSpecial_price() {
        return special_price;
    }

    public void setSpecial_price(int special_price) {
        this.special_price = special_price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getItem_count() {
        return item_count;
    }

    public void setItem_count(int item_count) {
        this.item_count = item_count;
    }

    public List<ProductOption> getProductOptions() {
        return productOptions;
    }

    public void setProductOptions(List<ProductOption> productOptions) {
        this.productOptions = productOptions;
    }

    public String getProduct_option_id() {
        return product_option_id;
    }

    public void setProduct_option_id(String product_option_id) {
        this.product_option_id = product_option_id;
    }
}
