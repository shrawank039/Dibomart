package com.dibomart.dibomart;

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

        url = getIntent().getStringExtra("link");

        myWebView = (WebView) findViewById(R.id.webview);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
        //  myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.loadUrl(url);


        // Create an unencoded HTML string
// then convert the unencoded HTML string into bytes, encode
// it with Base64, and load the data.
        String unencodedHtml = "<link rel=\"stylesheet\" href=\"https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/themes/smoothness/jquery-ui.css\">\n" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"../../catalog/view/theme/default/stylesheet/stylesheet.css\" />\n" +
                "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js\"></script>\n" +
                "<script src=\"https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js\"></script>\n" +
                "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"\n" +
                "\tintegrity=\"sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa\" crossorigin=\"anonymous\">\n" +
                "</script>\n" +
                "\n" +
                "<div class=\"payment\" style=\"visibility: hidden\">\n" +
                "\t<form action=\"https://securegw.paytm.in/order/process\" method=\"POST\" class=\"form-horizontal\"\n" +
                "\t\tid=\"paytm_form_redirect\">\n" +
                "\t\t<input type=\"hidden\" name=\"MID\" value=\"jXhxQZ97056722476550\" />\n" +
                "\t\t<input type=\"hidden\" name=\"WEBSITE\" value=\"DEFAULT\" />\n" +
                "\t\t<input type=\"hidden\" name=\"INDUSTRY_TYPE_ID\" value=\"Retail\" />\n" +
                "\t\t<input type=\"hidden\" name=\"CALLBACK_URL\" value=\"http://www.dibomart.in/index.php?route=extension/payment/paytm/callback\" />\n" +
                "\t\t<input type=\"hidden\" name=\"ORDER_ID\" value=\"123\" />\n" +
                "\t\t<input type=\"hidden\" name=\"CHANNEL_ID\" value=\"WEB\" />\n" +
                "\t\t<input type=\"hidden\" name=\"CUST_ID\" value=\"shrawank039@gmail.com\" />\n" +
                "\t\t<input type=\"hidden\" name=\"TXN_AMOUNT\" value=\"1035\" />\n" +
                "\t\t<input type=\"hidden\" name=\"MOBILE_NO\" value=\"1541754\" />\n" +
                "\t\t<input type=\"hidden\" name=\"EMAIL\" value=\"shrawank039@gmail.com\" />\n" +
                "\t\t<input type=\"hidden\" name=\"CHECKSUMHASH\" value=\"3JspxD4fbtPeEYYbz1R5ykSdNJuQq6RAUxIxX5Ly+K26G51nNAYh1vcFPKXWxS2WZiLDEJd0c9J3GoQKw69XNxUCWaAU8C/WdXamowbZ4YA=\" />\n" +
                "\t\t<input type=\"hidden\" name=\"X-REQUEST-ID\" value=\"PLUGIN_OPENCART_3.0.3.3\" />\n" +
                "\t\t<div class=\"buttons\">\n" +
                "\t\t\t<div class=\"pull-right\">\n" +
                "\t\t\t\t<input type=\"submit\" value=\"Confirm Order\" id=\"button-confirm\" class=\"btn btn-primary\" />\n" +
                "\t\t</div>\n" +
                "\t\t\t</div>\n" +
                "\t</form>\n" +
                "</div>\n" +
                "\n" +
                "<script type=\"text/javascript\">\n" +
                "\t$(document).ready(function () {\n" +
                "                $(\".btn\").click();\n" +
                "                if ($('.btn').attr('href')) {\n" +
                "                    window.location.href = $('.btn').attr('href');\n" +
                "                }\n" +
                "            });\n" +
                "</script>";

//        ""<link rel=\"stylesheet\" href=\"https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/themes/smoothness/jquery-ui.css\">" +
//                "    <link rel=\"stylesheet\" type=\"text/css\" href=\"../../catalog/view/theme/default/stylesheet/stylesheet.css\"/>" +
//                "        <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js\"></script>" +
//                "    <script src=\"https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js\"></script>" +
//                "    <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\" integrity=\"sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa\" crossorigin=\"anonymous\"></script>" +
//                  "  <script>$(document).ready(function(){ $(\"#paytm_form_redirect\").submit();});</script>  "
//                +
//                "    <div class=\"payment\" ><form action=\"https://securegw.paytm.in/order/process\" method=\"POST\" class=\"form-horizontal\" id=\"paytm_form_redirect\">" +
//                "        <input type=\"hidden\" name=\"MID\" value=\"jXhxQZ97056722476550\" />" +
//                "        <input type=\"hidden\" name=\"WEBSITE\" value=\"DEFAULT\" />" +
//                "        <input type=\"hidden\" name=\"INDUSTRY_TYPE_ID\" value=\"Retail\" />" +
//                "        <input type=\"hidden\" name=\"CALLBACK_URL\" value=\"http://www.dibomart.in/index.php?route=extension/payment/paytm/callback\" />" +
//                "        <input type=\"hidden\" name=\"ORDER_ID\" value=\"122\" />" +
//                "        <input type=\"hidden\" name=\"CHANNEL_ID\" value=\"WEB\" />" +
//                "        <input type=\"hidden\" name=\"CUST_ID\" value=\"smithdebnath2016@gmail.com\" />" +
//                "        <input type=\"hidden\" name=\"TXN_AMOUNT\" value=\"40\" />" +
//                "        <input type=\"hidden\" name=\"MOBILE_NO\" value=\"9836976462\" />" +
//                "        <input type=\"hidden\" name=\"EMAIL\" value=\"smithdebnath2016@gmail.com\" />" +
//                "        <input type=\"hidden\" name=\"CHECKSUMHASH\" value=\"M6CqNiel3DmfTaa4sbUGoeO63eMhapK6w+HNGtSiIoSAD9HEk0wptcRtKh2E5qRXofYcSELB4Cr+YLa4cZ1bvmOotCqoOfiVvFSNSF8kIxI=\" />" +
//                "        <input type=\"hidden\" name=\"X-REQUEST-ID\" value=\"PLUGIN_OPENCART_3.0.3.3\" />" +
//                "        <div class=\"buttons\">" +
//                "        <div class=\"pull-right\">" +
//                "            <input type=\"submit\" value=\"Confirm Order\" id=\"button-confirm\" class=\"btn btn-primary\"/>" +
//                "        </div>" +
//                "    </div>" +
//                "</form></div>" +
//                "            <script type=\"text/javascript\">" +
//                "            $('.checkout-content').slideDown(0);" +
//                "        </script>";

//                "<form action=\"https://securegw.paytm.in/order/process\" method=\"POST\" class=\"form-horizontal\" id=\"paytm_form_redirect\">\n" +
//                        "\t\t<input type=\"hidden\" name=\"MID\" value=\"jXhxQZ97056722476550\" />\n\t\t<input type=\"hidden\" name=\"WEBSITE\"" +
//                        " value=\"DEFAULT\" />\n\t\t<input type=\"hidden\" name=\"INDUSTRY_TYPE_ID\" value=\"Retail\" />\n" +
//                        "\t\t<input type=\"hidden\" name=\"CALLBACK_URL\" value=\"http://www.dibomart.in/index.php?route=extension/payment/paytm/callback\"" +
//                        " />\n\t\t<input type=\"hidden\" name=\"ORDER_ID\" value=\"113\" />\n\t\t<input type=\"hidden\" name=\"CHANNEL_ID\" value=\"WEB\"" +
//                        " />\n\t\t<input type=\"hidden\" name=\"CUST_ID\" value=\"smithdebnath2016@gmail.com\" />\n\t\t<input type=\"hidden\" name=\"TXN_AMOUNT\" " +
//                        "value=\"40\" />\n\t\t<input type=\"hidden\" name=\"MOBILE_NO\" value=\"9836976462\" />\n\t\t<input type=\"hidden\" name=\"EMAIL\" value=\"" +
//                        "smithdebnath2016@gmail.com\" />\n\t\t<input type=\"hidden\" name=\"CHECKSUMHASH\" value=\"" +
//                        "Uqs7/R2Pn4Z/oLKMs/HfLCh2NFG/WM/5RYJ99Y2oRIEe2uG3g5saHZf7ByqjIkW/ykvAOSiFdzEpCQkGhWuyCtcCqYtB1qYMcUnk9RSE47M=\" />\n\t\t<input type=\"" +
//                        "hidden\" name=\"X-REQUEST-ID\" value=\"PLUGIN_OPENCART_3.0.3.3\" />\n\t\t<div class=\"buttons\">\n\t\t<div class=\"pull-right\">\n\t\t\t" +
//                        "<input type=\"submit\" value=\"Confirm Order\" id=\"button-confirm\" class=\"btn btn-primary\" />\n\t\t</div>\n\t</div>\n</form>";

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
            Toast.makeText(this, "WebView clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}