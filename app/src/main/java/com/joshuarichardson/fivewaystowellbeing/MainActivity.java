package com.joshuarichardson.fivewaystowellbeing;

import android.Manifest;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.joshuarichardson.fivewaystowellbeing.automated_activity_tracking.app_usage_tracking.AppUsageActivityTrackingService;
import com.joshuarichardson.fivewaystowellbeing.automated_activity_tracking.physical_activity_tracking.PhysicalActivityTracking;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.notifications.AlarmHelper;
import com.joshuarichardson.fivewaystowellbeing.automated_activity_tracking.AutomaticActivityTypes;
import com.joshuarichardson.fivewaystowellbeing.storage.DatabaseQuestionHelper;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphItem;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphValueHelper;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.AutomaticActivityDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingQuestionDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.AutomaticActivity;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingQuestion;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingResult;
import com.joshuarichardson.fivewaystowellbeing.ui.history.HistoryParentFragment;
import com.joshuarichardson.fivewaystowellbeing.ui.activities.edit.CreateOrUpdateActivityActivity;
import com.joshuarichardson.fivewaystowellbeing.ui.progress.ProgressFragment;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;
import dagger.hilt.android.AndroidEntryPoint;

import static com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase.DATABASE_VERSION_CODE;

/**
 * Hello and welcome to the five ways to wellbeing app.
 * This app provides users with different pages to help them to learn about their wellbeing.
 * The first item in the bottom navigation bar is the ProgressFragment which helps users to focus on a daily goal
 * The second item in the bottom navigation bar is an insights page which provides a collated view of the data over time
 * The third item in the bottom navigation bar is history which provides a more granular view.
 * The settings page can be accessed from the overflow menu and provides a range of options to customise the app to meet your needs.
 *
 * The main activity in the app, does some initialisation and sets up the bottom navigation
 */
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private static final int ACTIVITY_TRACKING_CODE = 1;
    private AlertDialog dialog;

    @Inject
    WellbeingDatabase db;

    @Inject
    PhysicalActivityTracking activityTracker;

    @Inject
    AlarmHelper alarmHelper;

    @Inject
    InitialPreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        // Switch to the theme chosen in settings
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor preferenceEditor = preferences.edit();

        themePreferences(preferences);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Display a welcome screen for first time users of this version
        if(preferences.getInt("app_version", 0) < 6) {
            this.dialog = new MaterialAlertDialogBuilder(this)
                .setView(R.layout.new_features_auto_tracking)
                .setPositiveButton(getString(R.string.tracking_dialog_positive_button), (dialog, which) -> {
                    acceptPermissions();
                    preferenceEditor.putInt("app_version", 6);
                    preferenceEditor.apply();
                }).show();
        } else {
            acceptPermissions();
        }

        // The database version will only be updated after this so when migrating from 5 to 6 this will run
        if (preferences.getInt("database_version", 0) < 6) {
            backdateWellbeingResults();
        }

        // Add the physical activities to the database
        if (preferences.getInt("database_version", 0) < 7) {
            addAutomatedTrackingActivities();
        }

        if (preferences.getInt("database_version", 0) < 9) {
            backdateActivitiesWithoutWellbeingRecords();
        }

        preferenceEditor.putInt("database_version", DATABASE_VERSION_CODE);
        preferenceEditor.apply();

        preferencesHelper.setInitialNotificationPreferences(alarmHelper);
        preferencesHelper.setInitialPhysicalActivitySettings();
        DatabaseQuestionHelper.updateQuestions(preferences, db.wellbeingQuestionDao());

        startServiceAppTracking(preferences.getBoolean("notification_app_enabled", false));

        // Bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_progress, R.id.navigation_view_survey_responses, R.id.navigation_insights)
            .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNav, navController);
    }

    /**
     * Set the theme based on the preferences and set initial preference to system default
     */
    public void themePreferences(SharedPreferences preferences) {

        if(!preferences.contains("theme_settings_list")) {
            // Reference https://stackoverflow.com/a/552380/13496270
            // Now before selecting a preference an options will be selected automatically
            SharedPreferences.Editor preferenceEditor = preferences.edit();
            preferenceEditor.putString("theme_settings_list", "SYSTEM");
            preferenceEditor.apply();
        }

        String theme = preferences.getString("theme_settings_list", "SYSTEM");
        if(theme == null) {
            theme = "SYSTEM";
        }
        ThemeHelper.setTheme(theme);
    }

    /**
     * When the wellbeing results were implemented, the data needed to be backdated so that all compatible surveys were included
     * This allows for looking at historic data in insights
     */
    private void backdateWellbeingResults() {
        WellbeingDatabaseModule.databaseExecutor.execute(() -> {
            // Get all surveys since time of the update that made it work
            List<SurveyResponse> surveyResponses = this.db.surveyResponseDao().getSurveyResponsesByTimestampRangeNotLive(1613509560000L, Calendar.getInstance().getTimeInMillis());

            // For each survey update the database
            for(SurveyResponse response : surveyResponses) {
                List<WellbeingGraphItem> wellbeingValues = db.wellbeingQuestionDao().getWaysToWellbeingBetweenTimesNotLive(TimeHelper.getStartOfDay(response.getSurveyResponseTimestamp()), TimeHelper.getEndOfDay(response.getSurveyResponseTimestamp()));
                WellbeingGraphValueHelper values = WellbeingGraphValueHelper.getWellbeingGraphValues(wellbeingValues);
                db.wellbeingResultsDao().insert(new WellbeingResult(response.getSurveyResponseId(), response.getSurveyResponseTimestamp(), values.getConnectValue(), values.getBeActiveValue(), values.getKeepLearningValue(), values.getTakeNoticeValue(), values.getGiveValue()));
            }
        });
    }

    /**
     * Add the automated physical activity tracking types to the database so that they can be updated
     */
    private void addAutomatedTrackingActivities() {
        AutomaticActivityDao automaticActivityDao = this.db.physicalActivityDao();
        WellbeingDatabaseModule.databaseExecutor.execute(() -> {
            automaticActivityDao.insert(new AutomaticActivity(AutomaticActivityTypes.WALK, null, 0, 0, 0, false, false));
            automaticActivityDao.insert(new AutomaticActivity(AutomaticActivityTypes.RUN, null, 0, 0, 0, false, false));
            automaticActivityDao.insert(new AutomaticActivity(AutomaticActivityTypes.CYCLE, null, 0, 0, 0, false, false));
            automaticActivityDao.insert(new AutomaticActivity(AutomaticActivityTypes.VEHICLE, null, 0, 0, 0, false, false));
        });
    }

    /**
     * Start the app tracking service if enabled
     *
     * @param isEnabled The enabled status of app tracking
     */
    private void startServiceAppTracking(boolean isEnabled) {
        if (isEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Reference startForegroundService https://stackoverflow.com/a/7690600/13496270
                startForegroundService(new Intent(this, AppUsageActivityTrackingService.class));
            } else {
                startService(new Intent(this, AppUsageActivityTrackingService.class));
            }
        }
    }

    /**
     * Automatic activity tracking initially didn't add wellbeing records if the app wasn't launched
     * This meant that insights didn't show up some of the days
     * This overcomes this by correcting all wellbeing results until first app launch after updating
     */
    private void backdateActivitiesWithoutWellbeingRecords() {
        Calendar cal = Calendar.getInstance();
        long endTime = TimeHelper.getEndOfDay(cal.getTimeInMillis());

        // This gets midnight on 3rd April
        cal.setTimeInMillis(1617404400000L);

        // Get start and end time
        long startTime = TimeHelper.getStartOfDay(cal.getTimeInMillis());

        WellbeingDatabaseModule.databaseExecutor.execute(() -> {
            // This gets all of the surveys between then and now
            List<SurveyResponse> surveyResponses = this.db.surveyResponseDao().getSurveyResponsesByTimestampRangeNotLive(startTime, endTime);

            // Update the wellbeing results so that they are accounted for
            for(SurveyResponse response : surveyResponses) {
                // Use the start time to get the start and end of the day
                long time = response.getSurveyResponseTimestamp();
                long wellbeingStartTime = TimeHelper.getStartOfDay(time);
                long wellbeingEndTime = TimeHelper.getEndOfDay(time);
                // Get the ways to wellbeing for that day
                List<WellbeingGraphItem> wayToWellbeingValues = this.db.wellbeingQuestionDao().getWaysToWellbeingBetweenTimesNotLive(wellbeingStartTime, wellbeingEndTime);
                WellbeingGraphValueHelper values = WellbeingGraphValueHelper.getWellbeingGraphValues(wayToWellbeingValues);
                // Create a new item if it doesn't already exist
                this.db.wellbeingResultsDao().insert(new WellbeingResult(response.getSurveyResponseId(), wellbeingStartTime, values.getConnectValue(), values.getBeActiveValue(), values.getKeepLearningValue(), values.getTakeNoticeValue(), values.getGiveValue()));
            }
        });
    }

    /**
     * Prompt user to accept permissions.
     * To track physical activities on Android Q+ users must allow the permission
     * If users don't allow it the first time, they will be shown a dialog that justifies why
     */
    private void acceptPermissions() {
        // Permission reference https://developer.android.com/training/permissions/requesting#allow-system-manage-request-code
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
                this.activityTracker.initialiseTracking(getApplicationContext());
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACTIVITY_RECOGNITION)) {
                new MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.tracking_dialog_title))
                    .setMessage(getString(R.string.tracking_dialog_body))
                    .setIcon(R.drawable.notification_icon_walk)
                    .setPositiveButton(getString(R.string.tracking_dialog_positive_button), (dialog, which) -> {
                        requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, ACTIVITY_TRACKING_CODE);
                    })
                    .show();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, ACTIVITY_TRACKING_CODE);
            }
        } else {
            this.activityTracker.initialiseTracking(getApplicationContext());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Start tracking activities if the permission is enabled
        if(requestCode == ACTIVITY_TRACKING_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.activityTracker.initialiseTracking(getApplicationContext());
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Hide the dialog if it is displayed
        if(this.dialog != null) {
            if(this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cancel the notifications
        NotificationManager notification = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        notification.cancel(NotificationConfiguration.NotificationsId.AUTOMATIC_ACTIVITY_NOTIFICATION_WALK);
        notification.cancel(NotificationConfiguration.NotificationsId.AUTOMATIC_ACTIVITY_NOTIFICATION_RUN);
        notification.cancel(NotificationConfiguration.NotificationsId.AUTOMATIC_ACTIVITY_NOTIFICATION_CYCLE);
        notification.cancel(NotificationConfiguration.NotificationsId.AUTOMATIC_ACTIVITY_NOTIFICATION_VEHICLE);
        notification.cancel(NotificationConfiguration.NotificationsId.AUTOMATIC_ACTIVITY_NOTIFICATION_APP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.help_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Delete action for progress fragment
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

        // Edit action for activity fragment
        if (item.getItemId() == R.id.action_edit) {
            Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

            if(navHostFragment == null) {
                return false;
            }

            if(navHostFragment.getChildFragmentManager().getFragments().get(0).getClass() == HistoryParentFragment.class) {
                HistoryParentFragment viewSurveyResponseFragment = (HistoryParentFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
                viewSurveyResponseFragment.instantiateEditable();
            }
            return true;
        }

        Intent intent = MenuItemHelper.handleOverflowMenuClick(this, item);

        // If there is no intent to launch, return
        if(intent == null) {
            return false;
        }

        // This only runs if an intent was set
        startActivity(intent);
        return true;
    }

    /** Launch the activity to create a user activity on button click
     * @param view The instance of the button clicked.
     */
    public void onCreateActivityButtonClicked(View view) {
        Intent answerSurveyIntent = new Intent(MainActivity.this, CreateOrUpdateActivityActivity.class);
        startActivity(answerSurveyIntent);
    }


    /** Launch the learn more activity on button click
     * @param view The instance of the button clicked.
     */
    public void onLearnMoreButtonClicked(View view) {
        Intent learnMoreIntent = new Intent(this, LearnMoreAboutFiveWaysActivity.class);
        startActivity(learnMoreIntent);
    }
}