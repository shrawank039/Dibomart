package com.dibomart.dibomart.net;


public class ServiceNames {

    // http://134.209.108.210/adminarea/index.php/Webservices/version_info?
    /*Set BASE URL here*/

    private static final String PRODUCTION_API = "https://matrixdeveloper.com";

    /* Set API VERSION here*/
    public static final String API_VERSION = "/sattamaking";


    /*Set API URL here*/
    private static final String API = PRODUCTION_API + API_VERSION;

    /*END POINTS*/

    public static final String CHANGE_PASS = API + "/update_password";
    public static final String USER_REGISTRATION = API + "/signup";
    public static final String USER_LOGIN = API + "/do_login";
    public static final String EDIT_PROFILE = API + "/edit_user";

}
