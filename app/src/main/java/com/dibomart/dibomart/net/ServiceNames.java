package com.dibomart.dibomart.net;

import com.dibomart.dibomart.Global;

public class ServiceNames {
    // https://kolkata.dibomart.in/api/rest/banners
    /*Set BASE URL here 712249*/

    public static String PRODUCTION_API = Global.base_url;

    /* Set API VERSION here*/
    public static String API_VERSION = "api/rest/";


    /*Set API URL here*/
    public static String API = API_VERSION;

    /*END POINTS*/

    public static String PINCODE =  "https://dibomart.in/pin_api.php?pincode=";

    public static String CHANGE_PASS = API + "password";
    public static String USER_REGISTRATION = API + "register";
    public static String USER_LOGIN = API + "login";
    public static String LOGOUT = API + "logout";
    public static String EDIT_PROFILE = API + "account";
    public static String BANNER_IMG = API + "banners";
    public static String AD_BANNER_IMG = API + "banners/7";
    public static String MANUFACTURERS = API + "manufacturers";
    public static String CATEGORY = API + "categories";
    public static String ALL_CATEGORIES = API + "categories/level/2";
    public static String SUB_CATEGORY = API + "categories/parent/";
    public static String PRODUCT_LIST = API + "products/category/";
    public static String SEARCH_PRODUCT_LIST = API + "products/search/";
    public static String SESSION = API + "session";
    public static String CART = API + "cart";
    public static String ADDRESS = API + "shippingaddress";
    public static String DELETE_ADDRESS = API + "account/address/";
    public static String EXISTING_ADDRESS = API + "shippingaddress/existing";
    public static String PAY_ADDRESS = API + "paymentaddress";
    public static String UPDATE_ADDRESS = API + "account/address/";
    public static String EXISTING_PAY_ADDRESS = API + "paymentaddress/existing";
    public static String SHIPPING_METHOD = API + "shippingmethods";
    public static String PAYMENT_METHOD = API + "paymentmethods";
    public static String COUPON = API + "coupon";
    public static String CONFIRM = API + "confirm";
    public static String PAY = API + "pay";
    public static String GET_MAIL = "https://dibomart.in/get_email.php?telephone=8100101025";
    public static String ORDER_HISTORY = API + "customerorders/";
    public static String CANCEL_ORDER = API + "orderhistory/";
    public static String RETURN = API + "returns";
    public static String CONTACT_US = API + "contact";
    public static String TERMS_CONDITIONS = "https://www.dibomart.in/terms.html";
    public static String PRIVACY_POLICY = "https://www.dibomart.in/privacy.html";
    public static String RETURN_POLICY = "https://www.dibomart.in/return_policy.html";
    public static String SMS_API ="https://www.smsalert.co.in/api/push.json?apikey=5ff012a30ecb3&sender=TLCHRG&mobileno=";

    //forgot pass
    public static String FORGOT_GET  = "https://dibomart.in/get_email.php?telephone=";
    public static String FORGOT_POST = API + "forgotten";


}