package com.joshuarichardson.fivewaystowellbeing;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class LearnMoreAboutFiveWaysActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_more_about_five_ways);
    }

    public void onLearnMoreButtonClicked(View v) {
        // Open research PDF in Chrome
        Intent webViewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://neweconomics.org/uploads/files/five-ways-to-wellbeing-1.pdf"));
        startActivity(webViewIntent);
    }
}