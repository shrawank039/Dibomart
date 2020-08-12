package com.dibomart.dibomart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_FIRSTNAME = "firstname";
    private static final String TAG_LASTNAME = "lastname";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_MOBILE = "mobile";
    private static final String TAG_PASSWORD = "password";

    //Textbox
    private EditText firstname;
    private EditText lastname;
    private EditText email;
    private EditText mobile;
    private EditText password;
    private Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstname = (EditText) findViewById(R.id.firstname);
        lastname = (EditText) findViewById(R.id.lastname);
        email = (EditText) findViewById(R.id.email);
        mobile = (EditText) findViewById(R.id.mobileNumber);
        password = (EditText) findViewById(R.id.password);

        signup = (Button) findViewById(R.id.registerBtn);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkdetails()) {
                    // Loading offers in Background Thread
                    Intent intent = new Intent(RegisterActivity.this, MobileVerifyActivity.class);
                    intent.putExtra(TAG_FIRSTNAME, firstname.getText().toString().trim());
                    intent.putExtra(TAG_LASTNAME, lastname.getText().toString().trim());
                    intent.putExtra(TAG_EMAIL, email.getText().toString().trim());
                    intent.putExtra(TAG_MOBILE, mobile.getText().toString().trim());
                    intent.putExtra(TAG_PASSWORD, password.getText().toString().trim());
                    startActivity(intent);
//                    new OneLoadAllProducts().execute();
                }
            }
        }); 
    }

    private boolean checkdetails() {

        if (email.getText().toString().trim().isEmpty()) {
//            Toast.makeText(RegisterActivity.this, "Enter Value for Email", Toast.LENGTH_SHORT).show();
//            email.requestFocus();
//            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()) {
                Toast.makeText(RegisterActivity.this, "Enter valid Value for Email", Toast.LENGTH_SHORT).show();
                email.requestFocus();
                return false;
        } else if (password.getText().toString().trim().isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Enter Value for Password", Toast.LENGTH_SHORT).show();
            password.requestFocus();
            return false;
        } else if (firstname.getText().toString().trim().isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Enter Value for FirstName", Toast.LENGTH_SHORT).show();
            firstname.requestFocus();
            return false;
        } else if (lastname.getText().toString().trim().isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Enter Value for LastName", Toast.LENGTH_SHORT).show();
            lastname.requestFocus();
            return false;
       } else if (mobile.getText().toString().trim().isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Enter Value for Mobile", Toast.LENGTH_SHORT).show();
            mobile.requestFocus();
            return false;
        } else if (!Patterns.PHONE.matcher(mobile.getText().toString().trim()).matches()) {
            Toast.makeText(RegisterActivity.this, "Enter Valid Value for MobileNumber", Toast.LENGTH_SHORT).show();
            mobile.requestFocus();
            return false;
        }

        return true;
    }
    
    public void signIn(View view)
    {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

}