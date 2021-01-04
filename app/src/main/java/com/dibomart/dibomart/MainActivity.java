package com.dibomart.dibomart;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.request.RequestOptions;
import com.dibomart.dibomart.adapter.ExpandableListAdapter;
import com.dibomart.dibomart.fragment.HomeFragment;
import com.dibomart.dibomart.model.Category;
import com.dibomart.dibomart.model.MenuModel;
import com.dibomart.dibomart.model.SubCategory;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;
import com.dibomart.dibomart.ui.PageViewModel;
import com.glide.slider.library.slidertypes.TextSliderView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private static final String TAG_FIRSTNAME = "firstname";
    private static final String TAG_LASTNAME = "lastname";
    private FragmentManager fragmentManager;
    NavigationView navigationView;
    Toolbar toolbar;
    private static PrefManager prf;
    boolean doubleBackToExitPressedOnce = false;
    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    List<MenuModel> headerList = new ArrayList<>();
    HashMap<MenuModel, List<MenuModel>> childList = new HashMap<>();
    TextView txtCartItem, header_name;
    private PageViewModel pageViewModel;


    @SuppressLint("SetTextI18n")
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
        View hView = navigationView.getHeaderView(0);
        header_name = hView.findViewById(R.id.header_name);
        drawerLayout = findViewById(R.id.drawer_layout);
        prf = new PrefManager(MainActivity.this);
        txtCartItem = findViewById(R.id.cart_item);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);

        Log.d("TAG", "session " + prf.getString("session"));


        findViewById(R.id.cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
            }
        });

        expandableListView = findViewById(R.id.expandableListView);
        prepareMenuData();
        populateExpandableList();

        HomeFragment homeFragment = new HomeFragment();
        loadFrag(homeFragment, prf.getString("pincode"), fragmentManager);

        header_name.setText(prf.getString(TAG_FIRSTNAME) + " " + prf.getString(TAG_LASTNAME));
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
        float density = getResources().getDisplayMetrics().density;
        //  float height =  displayMetrics.heightPixels/density;
        float width = displayMetrics.widthPixels / density;
        int a = (int) (width * density / 3.189);  // 1920*602
        int b = (int) (width * density / 1.66); // 800*480

        prf.setInt("banner_height", a + 20);
        prf.setInt("pbanner_height", b + 20);

        PageViewModel.setitemIndex(prf.getInt("cart_item"));

        pageViewModel.getItemText().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer s) {
                if (s == 0) {
                    txtCartItem.setVisibility(View.GONE);
                } else {
                    txtCartItem.setVisibility(View.VISIBLE);
                }
                txtCartItem.setText(String.valueOf(s));
            }
        });

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
        menuModel = new MenuModel("Address", true, false, null); //Menu of Android Tutorial. No sub menus
        headerList.add(menuModel);

        if (!menuModel.hasChildren) {
            childList.put(menuModel, null);
        }
        menuModel = new MenuModel("Shop by Category", true, true, ""); //Menu of Java Tutorials
        headerList.add(menuModel);
        List<MenuModel> childModelsList = new ArrayList<>();
        MenuModel childModel;

        for (int i = 0; i < Global.ongoingList.size(); i++) {
            childModel = new MenuModel(Global.ongoingList.get(i).getName(), false, false, null);
            childModelsList.add(childModel);
        }

        if (menuModel.hasChildren) {
            Log.d("API123", "here");
            childList.put(menuModel, childModelsList);
        }

        childModelsList = new ArrayList<>();
        menuModel = new MenuModel("My Account", true, true, ""); //Menu of Python Tutorials
        headerList.add(menuModel);
        childModel = new MenuModel("My Order", false, false, null);
        childModelsList.add(childModel);
//        childModel = new MenuModel("Refund History", false, false, null);
//        childModelsList.add(childModel);
        childModel = new MenuModel("Return History", false, false, null);
        childModelsList.add(childModel);
        childModel = new MenuModel("Edit Profile", false, false, null);
        childModelsList.add(childModel);
        childModel = new MenuModel("Rate Our App", false, false, null);
        childModelsList.add(childModel);

        if (menuModel.hasChildren) {
            childList.put(menuModel, childModelsList);
        }
        menuModel = new MenuModel("Call Us", true, false, null); //Menu of Android Tutorial. No sub menus
        headerList.add(menuModel);
        if (!menuModel.hasChildren) {
            childList.put(menuModel, null);
        }
        menuModel = new MenuModel("Share App", true, false, null); //Menu of Android Tutorial. No sub menus
        headerList.add(menuModel);
        if (!menuModel.hasChildren) {
            childList.put(menuModel, null);
        }
        menuModel = new MenuModel("Contact Us", true, false, null); //Menu of Android Tutorial. No sub menus
        headerList.add(menuModel);
        if (!menuModel.hasChildren) {
            childList.put(menuModel, null);
        }
        menuModel = new MenuModel("Terms & Conditions", true, false, null); //Menu of Android Tutorial. No sub menus
        headerList.add(menuModel);
        if (!menuModel.hasChildren) {
            childList.put(menuModel, null);
        }
        menuModel = new MenuModel("Privacy Policy", true, false, null); //Menu of Android Tutorial. No sub menus
        headerList.add(menuModel);
        if (!menuModel.hasChildren) {
            childList.put(menuModel, null);
        }
        menuModel = new MenuModel("Return Policy", true, false, null); //Menu of Android Tutorial. No sub menus
        headerList.add(menuModel);
        if (!menuModel.hasChildren) {
            childList.put(menuModel, null);
        }
        if (prf.getString("customer_id").equals("")) {
            menuModel = new MenuModel("Login", true, false, null); //Menu of Android Tutorial. No sub menus
        } else {
            menuModel = new MenuModel("Logout", true, false, null); //Menu of Android Tutorial. No sub menus
        }
        headerList.add(menuModel);
        if (!menuModel.hasChildren) {
            childList.put(menuModel, null);
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
                        switch (groupPosition) {
                            case 0:
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                                break;
                            case 4:
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:"+prf.getString("call_us")));
                                startActivity(intent);
                                break;
                            case 5:
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT,
                                        "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                                sendIntent.setType("text/plain");
                                startActivity(sendIntent);
                                break;
                            case 1:
                                if (prf.getString("session").equals(""))
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                else
                                    startActivity(new Intent(getApplicationContext(), AddressActivity.class));
                                break;
                            case 6:
                                startActivity(new Intent(getApplicationContext(), ContactUsActivity.class));
                                break;
                            case 7:
                                startActivity(new Intent(getApplicationContext(), WebViewActivity.class)
                                        .putExtra("link", ServiceNames.TERMS_CONDITIONS));
                                break;
                            case 8:
                                startActivity(new Intent(getApplicationContext(), WebViewActivity.class)
                                        .putExtra("link", ServiceNames.PRIVACY_POLICY));
                                break;
                            case 9:
                                startActivity(new Intent(getApplicationContext(), WebViewActivity.class)
                                        .putExtra("link", ServiceNames.RETURN_POLICY));
                                break;
                            case 10:
                                if (prf.getString("customer_id").equals(""))
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                else
                                    logOut();
                                break;
                            default:
                                break;
                        }
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
                    if (groupPosition == 2) {
                        Intent in = new Intent(getApplicationContext(), ProductListActivity.class);
                        in.putExtra("category_id", Global.ongoingList.get(childPosition).getId());
                        in.putExtra("index", childPosition);
                        startActivity(in);
                        onBackPressed();
                    } else if (childPosition == 2 && groupPosition == 3) {
                        if (prf.getString("session").equals(""))
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        else
                            startActivity(new Intent(getApplicationContext(), EditProfileActivity.class));
                    } else if (childPosition == 3 && groupPosition == 3) {
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    } else if (childPosition == 0 && groupPosition == 3) {
                        if (prf.getString("session").equals(""))
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        else
                            startActivity(new Intent(getApplicationContext(), MyOrdersActivity.class));
                    } else if (childPosition == 1 && groupPosition == 3) {
                        if (prf.getString("session").equals(""))
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        else
                            startActivity(new Intent(getApplicationContext(), ReturnHistoryActivity.class));
                    }
                }

                return false;
            }
        });
    }

    private void logOut() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, Global.base_url+ServiceNames.LOGOUT, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response.optInt("success") == 1) {
                            prf.setString("session", "");
                            prf.setString("customer_id", "");
                            prf.setInt("cart_item", 0);
                            PageViewModel.setitemIndex(0);
                            Global.cartTotalItem = 0;
                            Toast.makeText(MainActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            finish();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray jsonArray = jsonObject.optJSONArray("error");
                    String err = jsonArray.optString(0);
                    Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    //Handle a malformed json response
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("X-Oc-Merchant-Id", prf.getString("s_key"));
                headers.put("X-Oc-Session", prf.getString("session"));
                return headers;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjReq.setShouldCache(false);
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
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

    public void cartClick(View view) {
        startActivity(new Intent(getApplicationContext(), CartActivity.class));
    }

}
