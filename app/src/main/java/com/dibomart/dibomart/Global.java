package com.dibomart.dibomart;

import com.dibomart.dibomart.model.Category;
import com.dibomart.dibomart.model.SubCategory;

import java.util.ArrayList;
import java.util.List;

public class Global {

    public static List<Category> ongoingList;
    public static List<SubCategory> subCatList;
    public static int cartTotalPrice =0;
    public static int cartTotalItem =0;
    public static String defaultPayAddress = "";
    public static int shippingCharges = 0;
    public static int ongoingPrice = 0;
    public static int ongoingSpecialPrice = 0;
    public static ArrayList<String> banner = new ArrayList<String>();
    public static ArrayList<String> promoBanner = new ArrayList<String>();

}
