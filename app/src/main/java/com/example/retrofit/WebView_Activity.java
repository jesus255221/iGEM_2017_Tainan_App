package com.example.retrofit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebView_Activity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        //Intent intent = getIntent();
        WebView webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        setContentView(webView);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://yanbo.ee.ncku.edu.tw/home");
    }
}
