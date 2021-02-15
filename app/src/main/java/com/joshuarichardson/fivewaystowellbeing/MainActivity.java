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
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.ui.settings.SettingsActivity;
import com.joshuarichardson.fivewaystowellbeing.ui.view.ViewPassTimesActivity;
import com.joshuarichardson.fivewaystowellbeing.ui.wellbeing_support.WellbeingSupportActivity;

import javax.inject.Inject;

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

    @Inject
    WellbeingDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        // Switch to the theme chosen in settings
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = preferences.getString("theme_settings_list", "SYSTEM");
        ThemeHelper.setTheme(theme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_progress, R.id.navigation_view_survey_responses, R.id.navigation_insights)
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
            case R.id.menu_learn_more:
                Intent learnMoreIntent = new Intent(MainActivity.this, LearnMoreAboutFiveWaysActivity.class);
                startActivity(learnMoreIntent);
                break;
            default:
                Log.d("Menu", "Menu");
        }

        return true;
    }

    public void onCreatePassTimeButtonClicked(View v) {
        Intent answerSurveyIntent = new Intent(MainActivity.this, CreatePassTimeActivity.class);
        startActivity(answerSurveyIntent);
    }

    public void onLaunchActivitiesActivity(View v) {
        Intent activityViewIntent = new Intent(this, ViewPassTimesActivity.class);
        startActivity(activityViewIntent);
    }

    public void onLearnMoreButtonClicked(View v) {
        Intent learnMoreIntent = new Intent(this, LearnMoreAboutFiveWaysActivity.class);
        startActivity(learnMoreIntent);
    }
}