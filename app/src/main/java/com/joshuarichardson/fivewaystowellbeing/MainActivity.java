package com.joshuarichardson.fivewaystowellbeing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.notifications.AlarmHelper;
import com.joshuarichardson.fivewaystowellbeing.storage.DatabaseQuestionHelper;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingQuestionDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingQuestion;
import com.joshuarichardson.fivewaystowellbeing.ui.pass_times.edit.CreateOrUpdatePassTimeActivity;
import com.joshuarichardson.fivewaystowellbeing.ui.pass_times.edit.ViewPassTimesActivity;
import com.joshuarichardson.fivewaystowellbeing.ui.view.ProgressFragment;
import com.joshuarichardson.fivewaystowellbeing.ui.view.ViewSurveyResponsesFragment;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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
        SharedPreferences.Editor preferenceEditor = preferences.edit();
        if(!preferences.contains("theme_settings_list")) {
            // Reference https://stackoverflow.com/a/552380/13496270
            // Now before selecting a preference an options will be selected automatically
            preferenceEditor.putString("theme_settings_list", "SYSTEM");
            preferenceEditor.apply();
        }

        String theme = preferences.getString("theme_settings_list", "SYSTEM");
        if(theme == null) {
            theme = "SYSTEM";
        }
        ThemeHelper.setTheme(theme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Schedule default notification and set values if not set
        if (!preferences.contains("notification_morning_time") || !preferences.contains("notification_morning_switch")) {
            preferenceEditor.putLong("notification_morning_time", 30600000); // 8:30
            preferenceEditor.putBoolean("notification_morning_switch", true);
            AlarmHelper.getInstance().scheduleNotification(getApplicationContext(), 8, 30, "morning", true);
        }

        if (!preferences.contains("notification_noon_time") || !preferences.contains("notification_noon_switch")) {
            preferenceEditor.putLong("notification_noon_time", 43200000); // 12:00
            preferenceEditor.putBoolean("notification_noon_switch", true);
            AlarmHelper.getInstance().scheduleNotification(getApplicationContext(), 12, 0, "noon", true);
        }

        if (!preferences.contains("notification_night_time") || !preferences.contains("notification_night_switch")) {
            preferenceEditor.putLong("notification_night_time", 73800000); // 20:30
            preferenceEditor.putBoolean("notification_night_switch", true);
            AlarmHelper.getInstance().scheduleNotification(getApplicationContext(), 20, 30, "night", true);
        }

        preferenceEditor.apply();

        // Put the questions in the database whenever the questions are updated
        int hasAddedQuestions = preferences.getInt("added_question_version", 0);
        if (hasAddedQuestions != DatabaseQuestionHelper.VERSION_NUMBER) {
            WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                for (WellbeingQuestion question : DatabaseQuestionHelper.getQuestions()) {
                    WellbeingQuestionDao questionDao = this.db.wellbeingQuestionDao();
                    questionDao.insert(question);
                }
            });
            preferenceEditor.putInt("added_question_version", DatabaseQuestionHelper.VERSION_NUMBER);
            preferenceEditor.apply();
        }

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

        if (item.getItemId() == R.id.action_delete) {
            Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

            if(navHostFragment == null) {
                return false;
            }

            if(navHostFragment.getChildFragmentManager().getFragments().get(0).getClass() == ProgressFragment.class) {
                ProgressFragment progressFragment = (ProgressFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
                progressFragment.toggleDeletable();
            }

            return true;
        }

        if (item.getItemId() == R.id.action_edit) {
            Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

            if(navHostFragment == null) {
                return false;
            }

            if(navHostFragment.getChildFragmentManager().getFragments().get(0).getClass() == ViewSurveyResponsesFragment.class) {
                ViewSurveyResponsesFragment viewSurveyResponseFragment = (ViewSurveyResponsesFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
                viewSurveyResponseFragment.instantiateEditable();
            }
            return true;
        }

        Intent intent = MenuItemHelper.handleMenuClick(this, item);

        if(intent == null) {
            return false;
        }

        // This only runs if an intent was set
        startActivity(intent);
        return true;
    }

    public void onCreatePassTimeButtonClicked(View v) {
        Intent answerSurveyIntent = new Intent(MainActivity.this, CreateOrUpdatePassTimeActivity.class);
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