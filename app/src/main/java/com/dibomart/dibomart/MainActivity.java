package com.dibomart.dibomart;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dibomart.dibomart.adapter.ExpandableListAdapter;
import com.dibomart.dibomart.fragment.HomeFragment;
import com.dibomart.dibomart.model.MenuModel;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager;
    NavigationView navigationView;
    Toolbar toolbar;
    private static PrefManager prf;
    boolean doubleBackToExitPressedOnce = false;
    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    List<MenuModel> headerList = new ArrayList<>();
    HashMap<MenuModel, List<MenuModel>> childList = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PincodeActivity.class));
            }
        });
        fragmentManager = getSupportFragmentManager();
        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        prf = new PrefManager(MainActivity.this);

        expandableListView = findViewById(R.id.expandableListView);
        prepareMenuData();
        populateExpandableList();

        HomeFragment homeFragment = new HomeFragment();
        loadFrag(homeFragment, prf.getString("pincode"), fragmentManager);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {

                    case R.id.menu_go_category:
                              return true;
                    case R.id.menu_go_profile:
                       return true;
                    case R.id.menu_go_logout:
                        return true;
                }
                return false;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        toolbar.setNavigationIcon(R.drawable.ic_side_nav);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float density  = getResources().getDisplayMetrics().density;
      //  float height =  displayMetrics.heightPixels/density;
        float width = displayMetrics.widthPixels/density;
        int a = (int) (width*density/3.189);  // 1920*602
        int b = (int) (width*density/1.66); // 800*480

        prf.setInt("banner_height",a+20);
        prf.setInt("pbanner_height",b+20);

    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm) {
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.Container, f1, name);
        ft.commit();
        setToolbarTitle(name);
    }

    public void setToolbarTitle(String Title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(Title);
            toolbar.setLogo(R.drawable.ic_baseline_location_on_24);
        }
    }

    private void prepareMenuData() {

        MenuModel menuModel = new MenuModel("Home", true, false, null); //Menu of Android Tutorial. No sub menus
        headerList.add(menuModel);

        if (!menuModel.hasChildren) {
            childList.put(menuModel, null);
        }

        menuModel = new MenuModel("Category", true, true, ""); //Menu of Java Tutorials
        headerList.add(menuModel);
        List<MenuModel> childModelsList = new ArrayList<>();
        MenuModel childModel = new MenuModel("Vegetable", false, false, null);
        childModelsList.add(childModel);

        childModel = new MenuModel("Fruits", false, false, null);
        childModelsList.add(childModel);

        childModel = new MenuModel("Oils", false, false, null);
        childModelsList.add(childModel);


        if (menuModel.hasChildren) {
            Log.d("API123","here");
            childList.put(menuModel, childModelsList);
        }

        childModelsList = new ArrayList<>();
        menuModel = new MenuModel("Setting", true, true, ""); //Menu of Python Tutorials
        headerList.add(menuModel);
        childModel = new MenuModel("Account", false, false, null);
        childModelsList.add(childModel);

        childModel = new MenuModel("Address", false, false, null);
        childModelsList.add(childModel);

        if (menuModel.hasChildren) {
            childList.put(menuModel, childModelsList);
        }
    }

    private void populateExpandableList() {

        expandableListAdapter = new ExpandableListAdapter(this, headerList, childList);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (headerList.get(groupPosition).isGroup) {
                    if (!headerList.get(groupPosition).hasChildren) {

                        onBackPressed();
                    }
                }

                return false;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if (childList.get(headerList.get(groupPosition)) != null) {
                    MenuModel model = childList.get(headerList.get(groupPosition)).get(childPosition);
                    if (model.url.length() > 0) {
                        onBackPressed();
                    }
                }

                return false;
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (fragmentManager.getBackStackEntryCount() != 0) {
            String tag = fragmentManager.getFragments().get(fragmentManager.getBackStackEntryCount() - 1).getTag();
            setToolbarTitle(tag);
            super.onBackPressed();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.back_key), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }
    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        super.onStop();
    }

}
