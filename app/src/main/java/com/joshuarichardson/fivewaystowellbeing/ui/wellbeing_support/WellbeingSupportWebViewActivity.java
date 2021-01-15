package com.joshuarichardson.fivewaystowellbeing.ui.wellbeing_support;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.joshuarichardson.fivewaystowellbeing.R;

import androidx.appcompat.app.AppCompatActivity;

public class WellbeingSupportWebViewActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellbeing_support_web_view);

        // An intent should be passed through with a url string
        Intent webIntent = getIntent();
        if(webIntent == null || webIntent.getExtras() == null) {
            return;
        }

        // Get the url string
        String url = webIntent.getExtras().getString("url");

        if (url == null) {
            return;
        }

        // Navigate to the link selected
        this.webView = findViewById(R.id.wellbeing_support_web_view);
        this.webView.setWebViewClient(new WebViewClient());
        this.webView.loadUrl(url);
        this.webView.requestFocus();
    }

    @Override
    public void onBackPressed() {
        if (this.webView.canGoBack()) {
            // Navigate back in the web-view
            this.webView.goBack();
        } else {
            // Navigate back to the previous page
            super.onBackPressed();
        }
    }
}