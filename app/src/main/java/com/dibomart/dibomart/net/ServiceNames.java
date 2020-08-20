package com.dibomart.dibomart.net;


import com.dibomart.dibomart.LoginActivity;
import com.dibomart.dibomart.PrefManager;

public class ServiceNames {

    // http://134.209.108.210/adminarea/index.php/Webservices/version_info?
    /*Set BASE URL here*/

    public static String PRODUCTION_API = "https://kolkata.dibomart.in/";

    /* Set API VERSION here*/
    public static final String API_VERSION = "api/rest/";


    /*Set API URL here*/
    private static final String API = PRODUCTION_API + API_VERSION;

    /*END POINTS*/

    public static final String PINCODE =  "https://dibomart.in/pin_api.php?pincode=";

    public static final String CHANGE_PASS = API + "update_password";
    public static final String USER_REGISTRATION = API + "signup";
    public static final String USER_LOGIN = API + "do_login";
    public static final String EDIT_PROFILE = API + "edit_user";
    public static final String BANNER_IMG = API + "banners";
    public static final String AD_BANNER_IMG = API + "banners/7";
    public static final String MANUFACTURERS = API + "manufacturers";
    public static final String CATEGORY = API + "categories";
    public static final String SUB_CATEGORY = API + "categories/parent/";
    public static final String PRODUCT_LIST = API + "products/category/";

}
