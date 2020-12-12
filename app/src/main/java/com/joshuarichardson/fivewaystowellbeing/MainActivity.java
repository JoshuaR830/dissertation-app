package com.joshuarichardson.fivewaystowellbeing;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButtonClick(View v) {
        mFirebaseAnalytics.setUserId("Joshua");
        mFirebaseAnalytics.setUserProperty("custom", "somethingCustomJoshua");
        Log.d("joshua", "It worked");
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Test_Button_Click");
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "Test");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle);
    }
}