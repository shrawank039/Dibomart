package com.dibomart.dibomart;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dibomart.dibomart.WebView.WebAppInterface;

public class PaymentWebViewActivity extends AppCompatActivity implements View.OnTouchListener, Handler.Callback {

    WebView myWebView;
    String url;
    private WebViewClient client;
    private static final int CLICK_ON_WEBVIEW = 1;
    private static final int CLICK_ON_URL = 2;
    private final Handler handler = new Handler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        url = getIntent().getStringExtra("data");

        myWebView = (WebView) findViewById(R.id.webview);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String urll) {
                if (urll.contains("checkout/success")) {
                    startActivity(new Intent(getApplicationContext(), OrderSuccessActivity.class)
                    .putExtra("status","1"));
                    return true;
                } else if (urll.contains("checkout/cart")){
                    startActivity(new Intent(getApplicationContext(), OrderSuccessActivity.class)
                            .putExtra("status","0"));
                    return true;
                }
                return false;
            }
        });


        // Create an unencoded HTML string
// then convert the unencoded HTML string into bytes, encode
// it with Base64, and load the data.
        String unencodedHtml = url;
        String encodedHtml = Base64.encodeToString(unencodedHtml.getBytes(),
                Base64.NO_PADDING);
        myWebView.loadData(encodedHtml, "text/html", "base64");

//        myWebView.setOnTouchListener(this);

        client = new WebViewClient(){
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                handler.sendEmptyMessage(CLICK_ON_URL);
                return false;
            }
        };
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.webview && event.getAction() == MotionEvent.ACTION_DOWN){
            handler.sendEmptyMessageDelayed(CLICK_ON_WEBVIEW, 500);
        }
        return false;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == CLICK_ON_URL){
            handler.removeMessages(CLICK_ON_WEBVIEW);
            return true;
        }
        if (msg.what == CLICK_ON_WEBVIEW){

            return true;
        }
        return false;
    }
}