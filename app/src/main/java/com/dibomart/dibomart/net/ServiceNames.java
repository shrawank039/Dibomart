package com.dibomart.dibomart.net;


import com.dibomart.dibomart.LoginActivity;
import com.dibomart.dibomart.PrefManager;

public class ServiceNames {
    // https://kolkata.dibomart.in/api/rest/banners
    /*Set BASE URL here*/

    public static String PRODUCTION_API = "https://dibomart.in/";

    /* Set API VERSION here*/
    public static final String API_VERSION = "api/rest/";


    /*Set API URL here*/
    private static final String API = PRODUCTION_API + API_VERSION;

    /*END POINTS*/

    public static final String PINCODE =  "https://dibomart.in/pin_api.php?pincode=";

    public static final String CHANGE_PASS = API + "update_password";
    public static final String USER_REGISTRATION = API + "register";
    public static final String USER_LOGIN = API + "login";
    public static final String EDIT_PROFILE = API + "edit_user";
    public static final String BANNER_IMG = API + "banners";
    public static final String AD_BANNER_IMG = API + "banners/7";
    public static final String MANUFACTURERS = API + "manufacturers";
    public static final String CATEGORY = API + "categories";
    public static final String ALL_CATEGORIES = API + "categories/level/2";
    public static final String SUB_CATEGORY = API + "categories/parent/";
    public static final String PRODUCT_LIST = API + "products/category/";
    public static final String SEARCH_PRODUCT_LIST = API + "products/search/";
    public static final String SESSION = API + "session";
    public static final String CART = API + "cart";
    public static final String ADDRESS = API + "shippingaddress";
    public static final String DELETE_ADDRESS = API + "account/address/";
    public static final String EXISTING_ADDRESS = API + "shippingaddress/existing";
    public static final String PAY_ADDRESS = API + "paymentaddress";
    public static final String EXISTING_PAY_ADDRESS = API + "paymentaddress/existing";
    public static final String SHIPPING_METHOD = API + "shippingmethods";
    public static final String PAYMENT_METHOD = API + "paymentmethods";
    public static final String COUPON = API + "coupon";
    public static final String CONFIRM = API + "confirm";
    public static final String PAY = API + "pay";
    public static final String GET_MAIL = "https://dibomart.in/get_email.php?telephone=8100101025";
    public static final String FORGOT_PASS = API + "forgotten";

}