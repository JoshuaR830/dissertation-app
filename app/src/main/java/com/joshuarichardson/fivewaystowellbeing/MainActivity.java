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
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        this.fragmentManager = getSupportFragmentManager();

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

        WellbeingSupportFragment supportFragment = new WellbeingSupportFragment();
        SettingsFragment settingsFragment = new SettingsFragment();

        switch(item.getItemId()) {
            case R.id.menu_settings:
                Log.d("Menu", "Settings");
                this.fragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, supportFragment, null)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.menu_wellbeing_support:
                Log.d("Menu", "Get wellbeing support");
                this.fragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, settingsFragment, null)
                        .addToBackStack(null)
                        .commit();
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