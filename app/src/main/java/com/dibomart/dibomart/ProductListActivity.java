package com.dibomart.dibomart;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dibomart.dibomart.adapter.ProductListAdapter;
import com.dibomart.dibomart.model.Category;
import com.dibomart.dibomart.model.SubCategory;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.dibomart.dibomart.ui.SectionsPagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductListActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener{

    private SectionsPagerAdapter sectionsPagerAdapter;
    private List<SubCategory> subCategoryList = new ArrayList<>();
    private static PrefManager prf;
    ViewPager viewPager;
    TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        prf = new PrefManager(this);
        subCategoryList = new ArrayList<>();
        viewPager = findViewById(R.id.view_pager);
        tabs = findViewById(R.id.tabs);

        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        List<String> spinnerArray =  new ArrayList<String>();

        spinnerArray.clear();
        for (int i = 0; i < Global.ongoingList.size(); i++) {
            Category category = Global.ongoingList.get(i);
            spinnerArray.add(category.getName());
        }

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,R.layout.spinner_item,spinnerArray);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

    }
    public List<SubCategory> sendData() {
        return subCategoryList;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
       // Toast.makeText(getApplicationContext(), Global.ongoingList.get(position).getName() , Toast.LENGTH_LONG).show();
        Category category = Global.ongoingList.get(position);
        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(),category.getSubCategoryList());
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setupWithViewPager(viewPager);
        if (category.getSubCategoryList().size()>0) {
            SubCategory subCategory = category.getSubCategoryList().get(0);
            subCategoryList = category.getSubCategoryList();
            Global.subCatList = subCategoryList;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void backPress(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void searchClick(View view) {
        startActivity(new Intent(getApplicationContext(),SearchActivity.class));
    }
    public void cartClick(View view) {
        startActivity(new Intent(getApplicationContext(),CartActivity.class));
    }
}