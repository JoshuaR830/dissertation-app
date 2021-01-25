package com.joshuarichardson.fivewaystowellbeing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.joshuarichardson.fivewaystowellbeing.ui.settings.SettingsActivity;
import com.joshuarichardson.fivewaystowellbeing.ui.wellbeing_support.WellbeingSupportActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Switch to the theme chosen in settings
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = preferences.getString("theme_settings_list", "SYSTEM");
        ThemeHelper.setTheme(theme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_view_survey_responses, R.id.navigation_view_pass_times)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNav, navController);
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
            // I want to launch activities - wouldn't expect that the bottom bar would still show and it isn't top level
            case R.id.menu_settings:
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.menu_wellbeing_support:
                Intent wellbeingIntent = new Intent(MainActivity.this, WellbeingSupportActivity.class);
                startActivity(wellbeingIntent);
                break;
            default:
                Log.d("Menu", "Menu");
        }

        return true;
    }

    public void onAnswerSurveysButtonClicked(View v) {
        mFirebaseAnalytics.setUserId("Joshua");
        mFirebaseAnalytics.setUserProperty("custom", "somethingCustomJoshua");
        Log.d("joshua", "It worked");
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Survey_Answer_Clicked");
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "Answer_Survey");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle);

        Intent answerSurveyIntent = new Intent(MainActivity.this, AnswerSurveyActivity.class);
        startActivity(answerSurveyIntent);
    }

    public void onCreatePassTimeButtonClicked(View v) {
        Bundle viewCreatePassTime = new Bundle();
        viewCreatePassTime.putString(FirebaseAnalytics.Param.ITEM_ID, "Create_PassTime_Clicked");
        viewCreatePassTime.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "Create_PassTime");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, viewCreatePassTime);

        Intent answerSurveyIntent = new Intent(MainActivity.this, CreatePassTimeActivity.class);
        startActivity(answerSurveyIntent);
    }
}