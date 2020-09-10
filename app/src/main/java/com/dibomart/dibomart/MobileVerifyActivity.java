package com.dibomart.dibomart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.dibomart.dibomart.model.Merchant;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rilixtech.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MobileVerifyActivity extends AppCompatActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_FIRSTNAME = "firstname";
    private static final String TAG_LASTNAME = "lastname";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_MOBILE = "telephone";
    private static final String TAG_PASSWORD = "password";

    //Textbox
    private String firstname;
    private String lastname;
    private String email;
    private String countrycode;
    private String mobile;
    private String password;

    private String success;

    private boolean ispass;

    private TextInputEditText newPass;
    private Button resetPassButton;
    private TextInputEditText retypeNewPass;
    private static PrefManager prf;
    private static final String TAG_USERID = "customer_id";

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private CountryCodePicker ccp;
    private TextInputEditText phoneed;
    private TextInputEditText codeed;
    private FloatingActionButton fabbutton;
    private String mVerificationId;
    private TextView timertext;
    private Timer timer;
    private ImageView verifiedimg;
    private Boolean mVerified = false;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    String session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_verify);

        prf = new PrefManager(this);
        try {
            ispass = Objects.requireNonNull(getIntent().getStringExtra("password")).contains("password");
            if(!ispass) {
                firstname = getIntent().getStringExtra(TAG_FIRSTNAME);
                lastname = getIntent().getStringExtra(TAG_LASTNAME);
                email = getIntent().getStringExtra(TAG_EMAIL);
                mobile = getIntent().getStringExtra(TAG_MOBILE);
                password = getIntent().getStringExtra(TAG_PASSWORD);
            } else {

                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        phoneed = (TextInputEditText) this.findViewById(R.id.numbered);
   //     ccp.registerPhoneNumberTextView(phoneed);

//        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
//            @Override
//            public void onCountrySelected(Country selectedCountry) {
//                countrycode = selectedCountry.getPhoneCode();
//                Toast.makeText(MobileVerifyActivity.this, "Updated " + selectedCountry.getPhoneCode(), Toast.LENGTH_SHORT).show();
//            }
//        });

        codeed = (TextInputEditText) this.findViewById(R.id.verificationed);
        fabbutton = (FloatingActionButton) findViewById(R.id.sendverifybt);
        timertext = (TextView) findViewById(R.id.timertv);
        verifiedimg = (ImageView) findViewById(R.id.verifiedsign);

        newPass = (TextInputEditText) findViewById(R.id.newpass);
        retypeNewPass = (TextInputEditText) findViewById(R.id.retypeNewPass);
        resetPassButton = (Button) findViewById(R.id.changePassBtn);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        if(!ispass) {
            phoneed.setText(mobile.toString());
        } else {
            phoneed.setEnabled(true);
        }

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.
                Log.d("TAG", "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("TAG", "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Snackbar snackbar = Snackbar
                            .make((ConstraintLayout) findViewById(R.id.parentlayout), "Verification Failed !! Invalied verification Code", Snackbar.LENGTH_LONG);

                    snackbar.show();
                }
                else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar snackbar = Snackbar
                            .make((ConstraintLayout) findViewById(R.id.parentlayout), "Verification Failed !! Too many request. Try after some time. ", Snackbar.LENGTH_LONG);

                    snackbar.show();
                }

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("TAG", "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
        fabbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fabbutton.getTag().equals("send")) {
                    if (!phoneed.getText().toString().trim().isEmpty() && phoneed.getText().toString().trim().length() >= 5) {
                      //  Toast.makeText(MobileVerifyActivity.this, ccp.getSelectedCountryCodeWithPlus()+phoneed.getText().toString().trim(), Toast.LENGTH_SHORT).show();
                        startPhoneNumberVerification(ccp.getSelectedCountryCodeWithPlus()+phoneed.getText().toString().trim());
                        mVerified = false;
                        starttimer();
                        codeed.setVisibility(View.VISIBLE);
                        fabbutton.setImageResource(R.drawable.ic_arrow_forward_white_24dp);
                        fabbutton.setTag("verify");
                    }
                    else {
                        phoneed.setError("Please enter valid mobile number");
                    }
                }

                if (fabbutton.getTag().equals("verify")) {
                    if (!Objects.requireNonNull(codeed.getText()).toString().trim().isEmpty() && !mVerified) {
                        Snackbar snackbar = Snackbar
                                .make((ConstraintLayout) findViewById(R.id.parentlayout), "Please wait...", Snackbar.LENGTH_LONG);

                        snackbar.show();
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, codeed.getText().toString().trim());
                        signInWithPhoneAuthCredential(credential);
                    }
                    if (mVerified) {
                        if(!ispass) {
                           // new OneLoadAllProducts().execute();
                            Log.d("TAG","Verified : registerSubmit()");
                            registerSubmit();
                        } else {
                            ((LinearLayout) findViewById(R.id.entermobile)).setVisibility(View.GONE);
                            ((LinearLayout) findViewById(R.id.resetpass)).setVisibility(View.VISIBLE);
                        }
                    }

                }


            }
        });

        resetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newPass.getText().length()>1 && retypeNewPass.getText().length()>1) {
                    if (newPass.getText().toString().equals(retypeNewPass.getText().toString())) {
                        String code ="";
                        changePassword(newPass,code);
                    } else {
                        Toast.makeText(MobileVerifyActivity.this, "NewPassword And RetypePass is not Same", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MobileVerifyActivity.this, "Please enter value for all field", Toast.LENGTH_SHORT).show();
                }

            }
        });

        timertext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!phoneed.getText().toString().trim().isEmpty() && phoneed.getText().toString().trim().length() == 10) {
                    resendVerificationCode(phoneed.getText().toString().trim(), mResendToken);
                    mVerified = false;
                    starttimer();
                    codeed.setVisibility(View.VISIBLE);
                    fabbutton.setImageResource(R.drawable.ic_arrow_forward_white_24dp);
                    fabbutton.setTag("verify");
                    Snackbar snackbar = Snackbar
                            .make((ConstraintLayout) findViewById(R.id.parentlayout), "Resending verification code...", Snackbar.LENGTH_LONG);

                    snackbar.show();
                }
            }
        });
    }

    private void changePassword(TextInputEditText newPass, String code) {
        String form_action ="<form id=\"reset\" action=\"https://www.dibomart.in/index.php?route=account/reset&amp;code="+code+"\" method=\"post\" enctype=\"multipart/form-data\" class=\"form-horizontal\">\n" +
                "        <fieldset>\n" +
                "          <legend>Enter the new password you wish to use.</legend>\n" +
                "          <div class=\"form-group\">\n" +
                "            <label class=\"col-sm-2 control-label\" for=\"input-password\">Password</label>\n" +
                "            <div class=\"col-sm-10\">\n" +
                "              <input type=\"password\" name=\"password\" value=\""+newPass+"\" id=\"input-password\" class=\"form-control\" />\n" +
                "               </div>\n" +
                "          </div>\n" +
                "          <div class=\"form-group\">\n" +
                "            <label class=\"col-sm-2 control-label\" for=\"input-confirm\">Confirm</label>\n" +
                "            <div class=\"col-sm-10\">\n" +
                "              <input type=\"password\" name=\"confirm\" value=\""+newPass+"\" id=\"input-confirm\" class=\"form-control\" />\n" +
                "              </div>\n" +
                "          </div>\n" +
                "        </fieldset>\n" +
                "        <div class=\"buttons clearfix\">\n" +
                "          <div class=\"pull-left\"><a href=\"#\" class=\"btn btn-default\">Back</a></div>\n" +
                "          <div class=\"pull-right\">\n" +
                "            <button type=\"submit\" class=\"btn btn-primary\">Continue</button>\n" +
                "          </div>\n" +
                "        </div>\n" +
                "      </form>\n" +
                "<script type=\"text/javascript\">\n" +
                "        window.onload = function () {\n" +
                "        document.getElementById(\"reset\").submit();\n" +
                "        }\n" +
                "    </script>";

        startActivity(new Intent(getApplicationContext(),PaymentWebViewActivity.class)
                .putExtra("data", form_action));
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            mVerified = true;
                            timer.cancel();
                            verifiedimg.setVisibility(View.VISIBLE);
                            timertext.setVisibility(View.INVISIBLE);
                            phoneed.setEnabled(false);
                            ((TextInputLayout) findViewById(R.id.enterotp)).setVisibility(View.GONE);
                            codeed.setVisibility(View.INVISIBLE);
                            Snackbar snackbar = Snackbar
                                    .make((ConstraintLayout) findViewById(R.id.parentlayout), "Successfully Verified", Snackbar.LENGTH_LONG);

                            snackbar.show();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Snackbar snackbar = Snackbar
                                        .make((ConstraintLayout) findViewById(R.id.parentlayout), "Invalid OTP ! Please enter correct OTP", Snackbar.LENGTH_LONG);

                                snackbar.show();
                            }
                        }
                    }
                });
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

    }

    public void starttimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {

            int second = 60;

            @Override
            public void run() {
                if (second <= 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timertext.setText("RESEND CODE");
                            timer.cancel();
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timertext.setText("00:" + second--);
                        }
                    });
                }

            }
        }, 0, 1000);
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void registerSubmit() {
        pDialog = new ProgressDialog(MobileVerifyActivity.this);
        pDialog.setMessage("Loading Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject data= new JSONObject();
        try {
            data.put(TAG_FIRSTNAME, firstname);
            data.put(TAG_LASTNAME, lastname);
            data.put(TAG_EMAIL, email);
            data.put(TAG_MOBILE, mobile);
            data.put(TAG_PASSWORD, password);
            data.put("confirm", password);
            data.put("customer_group_id", "1");
            data.put("agree", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, ServiceNames.USER_REGISTRATION, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        success = jsonObject.optString("success");
                        if (success.equals("1")) {

                            JSONObject data = jsonObject.optJSONObject("data");
                            // preference and set username for session
                            prf.setString(TAG_USERID, data.optString(TAG_USERID));
                            prf.setString(TAG_FIRSTNAME, data.optString(TAG_FIRSTNAME));
                            prf.setString(TAG_LASTNAME, data.optString(TAG_LASTNAME));
                            prf.setString(TAG_EMAIL, data.optString(TAG_EMAIL));
                            prf.setString(TAG_MOBILE, data.optString("telephone"));
                            prf.setString("session", session);

                            Intent intent = new Intent(MobileVerifyActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                            Toast.makeText(MobileVerifyActivity.this, "Registration done Succsessfully", Toast.LENGTH_LONG).show();

                        } else {
                            try {
                                JSONArray jsonArray = jsonObject.getJSONArray("error");
                                String error = jsonArray.optString(1);
                                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        pDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("X-Oc-Merchant-Id", prf.getString("s_key"));
                headers.put("X-Oc-Session", session);
                return headers;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void resetSubmit() {
        pDialog = new ProgressDialog(MobileVerifyActivity.this);
        pDialog.setMessage("Loading Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServiceNames.CHANGE_PASS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        if (success.equals("1")) {
                            // offers found
                            // Getting Array of offers

                            Intent intent = new Intent(MobileVerifyActivity.this, LoginActivity.class);
                            startActivity(intent);

                            Toast.makeText(MobileVerifyActivity.this,"Password changed Succsessfully",Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(MobileVerifyActivity.this,"Something went wrong.Please try again!",Toast.LENGTH_LONG).show();

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
                params.put(TAG_MOBILE, phoneed.getText().toString().trim());
                params.put(TAG_PASSWORD, newPass.getText().toString());
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void createSession() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET, ServiceNames.SESSION, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response.optString("success").equals("1")) {
                            try {
                                JSONObject jsonObject = response.getJSONObject("data");
                                session = jsonObject.optString("session");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "03 " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("X-Oc-Merchant-Id", prf.getString("s_key"));
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

}
