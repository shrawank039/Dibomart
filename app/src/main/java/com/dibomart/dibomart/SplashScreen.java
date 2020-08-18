package com.dibomart.dibomart;


import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.dibomart.dibomart.net.ServiceNames;


public class SplashScreen extends Activity {

    private static final int SPLASH_SHOW_TIME = 1000;

    //Prefrance
    private static PrefManager prf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        prf = new PrefManager(SplashScreen.this);

        new BackgroundSplashTask().execute();

    }

    /**
     * Async Task: can be used to load DB, images during which the splash screen
     * is shown to user
     */
    private class BackgroundSplashTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                Thread.sleep(SPLASH_SHOW_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


                // Create a new boolean and preference and set it to true
                String isSignedin = prf.getString("pincode");

                if(!isSignedin.equalsIgnoreCase("")) {
                    //user signedin
                  //  ServiceNames.PRODUCTION_API = prf.getString("url");
                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);
                } else {
                    //user not signedin
                    Intent i = new Intent(SplashScreen.this, PincodeActivity.class);
                    startActivity(i);
                }

            finish();
        }

    }
}
