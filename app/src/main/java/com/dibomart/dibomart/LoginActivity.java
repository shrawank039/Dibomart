package com.dibomart.dibomart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object

    private ArrayList<HashMap<String, String>> offersList;


    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    //user
    private static final String TAG_USERID = "userid";
    private static final String TAG_FIRSTNAME = "firstname";
    private static final String TAG_LASTNAME = "lastname";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_MOBILE = "mobile";
    private static final String TAG_PASSWORD = "password";

    //balance
    private static final String TAG_USERBALANCE = "balance";

    //matchdetail
    private static final String TAG_WONAMOUNT = "wonamount";

    // products JSONArray
    private JSONArray jsonarray = null;

    //Prefrance
    private static PrefManager prf;

    //Textbox
    private EditText phone;
    private EditText password;
    private Button signup;
    private Button signin;
    private TextView resetnow;

    private int success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prf = new PrefManager(LoginActivity.this);

        // Hashmap for ListView
        offersList = new ArrayList<>();

        phone = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        resetnow = (TextView) findViewById(R.id.resetnow);

        signup = (Button) findViewById(R.id.registerFromLogin);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        signin = (Button) findViewById(R.id.signinbtn);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkdetails()) {
                    // Loading jsonarray in Background Thread
                    loginSubmit();
                }
            }
        });

        resetnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(LoginActivity.this,"Email us from your registed email id",Toast.LENGTH_LONG).show();
                // Loading offers in Background Thread
                Intent intent = new Intent(LoginActivity.this, MobileVerifyActivity.class);
                intent.putExtra("password", "password");
                startActivity(intent);
            }
        });
    }

    private boolean checkdetails() {

        if (phone.getText().toString().trim().isEmpty()) {
            Toast.makeText(LoginActivity.this, "Enter Value for Mobile", Toast.LENGTH_SHORT).show();
            phone.requestFocus();
        }  else if (password.getText().toString().trim().isEmpty()) {
            Toast.makeText(LoginActivity.this, "Enter Value for Password", Toast.LENGTH_SHORT).show();
            password.requestFocus();
            return false;
        }


        return true;
    }

    private void loginSubmit() {
        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage("Loading Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServiceNames.USER_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        if (success == 1) {
                            // jsonarray found
                            // Getting Array of jsonarray
                            System.out.println("Rjn_user_created");

                        /*
                          Updating parsed JSON data into ListView
                         */
                            for (int i = 0; i < offersList.size(); i++) {

                                // preference and set username for session
                                prf.setString(TAG_USERID, offersList.get(i).get(TAG_USERID));
                                prf.setString(TAG_FIRSTNAME, offersList.get(i).get(TAG_FIRSTNAME));
                                prf.setString(TAG_LASTNAME, offersList.get(i).get(TAG_LASTNAME));
                                prf.setString(TAG_EMAIL, offersList.get(i).get(TAG_EMAIL));
                                prf.setString(TAG_MOBILE, offersList.get(i).get(TAG_MOBILE));

                                //balance
                                prf.setString(TAG_USERBALANCE, offersList.get(i).get(TAG_USERBALANCE));

                                //matchdetail
                                prf.setString(TAG_WONAMOUNT, offersList.get(i).get(TAG_WONAMOUNT));

                            }

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                            Toast.makeText(LoginActivity.this,"Signin done Succsessfully",Toast.LENGTH_LONG).show();

                        } else if(success == 2){
                            // no jsonarray found
                            Toast.makeText(LoginActivity.this,"Username or password is not valid",Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(LoginActivity.this,"Something went wrong. Try again!",Toast.LENGTH_LONG).show();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("phone", phone.getText().toString());
                params.put(TAG_PASSWORD, password.getText().toString());
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

}
