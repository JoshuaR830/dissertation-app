package com.joshuarichardson.fivewaystowellbeing;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.help_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case R.id.menu_settings:
                Log.d("Menu", "Settings");
                break;
            case R.id.menu_wellbeing_support:
                Log.d("Menu", "Get wellbeing support");
                break;
            default:
                Log.d("Menu", "Menu");
        }

        return true;
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