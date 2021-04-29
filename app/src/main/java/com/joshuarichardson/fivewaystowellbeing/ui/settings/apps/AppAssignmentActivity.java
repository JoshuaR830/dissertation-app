package com.joshuarichardson.fivewaystowellbeing.ui.settings.apps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.hilt.android.AndroidEntryPoint;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;

import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.TimeHelper;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.AutomaticActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

@AndroidEntryPoint
public class AppAssignmentActivity extends AppCompatActivity implements AppRecyclerViewAdapter.OnDropdownClick {

    private long currentTime;
    private long startTime;
    private UsageStatsManager usage;
    private PackageManager packageManager;
    private boolean wasPaused = false;

    @Inject
    WellbeingDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_assignment);
        this.currentTime = Calendar.getInstance().getTimeInMillis();
        this.startTime = TimeHelper.getStartOfDay(this.currentTime);
        this.usage = (UsageStatsManager) getSystemService(Service.USAGE_STATS_SERVICE);
        this.packageManager = getPackageManager();
        List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, this.currentTime);
        if (stats.size() == 0) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        displayStatsScreen();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.wasPaused = true;
    }

    private void displayStatsScreen() {
        this.usage = (UsageStatsManager) getSystemService(Service.USAGE_STATS_SERVICE);
        List<UsageStats> stats = this.usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, this.currentTime);
        if (stats.size() == 0 && wasPaused) {
            finish();
        }

        for(UsageStats stat : stats) {
            ApplicationInfo info;
            try {
                info = packageManager.getApplicationInfo(stat.getPackageName(), PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }

            String name = (String) packageManager.getApplicationLabel(info);

            // Add the item to the database when known that it exists
            WellbeingDatabaseModule.databaseExecutor.execute(() -> {
                this.db.physicalActivityDao().insert(new AutomaticActivity(stat.getPackageName(), name, 0, 0, 0, false, false));
            });
        }

        ArrayList<Long> activityIds = new ArrayList<>();
        ArrayList<String> activityNames = new ArrayList<>();
        ArrayList<String> activityWaysToWellbeing = new ArrayList<>();

        // Set the none values
        activityIds.add(0, 0L);
        activityNames.add(0, getString(R.string.no_selection));
        activityWaysToWellbeing.add(0, getString(R.string.no_ways_to_wellbeing));

        AppRecyclerViewAdapter adapter = new AppRecyclerViewAdapter(this, new ArrayList<>(), this);
        // Get the recycler view to populate
        RecyclerView recycler = findViewById(R.id.app_assignment_recycler);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        WellbeingDatabaseModule.databaseExecutor.execute(() -> {
            List<ActivityRecord> activities = db.activityRecordDao().getAllVisibleActivitiesNotLive();

            for(ActivityRecord activity : activities) {
                activityIds.add(activity.getActivityRecordId());
                activityNames.add(activity.getActivityName());
                activityWaysToWellbeing.add(activity.getActivityWayToWellbeing());
            }

            adapter.setListValues(activityIds, activityNames, activityWaysToWellbeing);
        });

        WellbeingDatabaseModule.databaseExecutor.execute(() -> {
            List<AutomaticActivity> automaticActivities = this.db.physicalActivityDao().getAllPhysicalActivitiesWithNames();
            List<AppItem> appItems = new ArrayList<>();

            for(AutomaticActivity activity : automaticActivities) {
                ActivityRecord activityRecord = this.db.activityRecordDao().getActivityRecordById(activity.getActivityId());

                if(activityRecord == null) {
                    appItems.add(new AppItem(activity.getActivityType(), activity.getName(),  getString(R.string.no_activity_assigned),  getString(R.string.no_ways_to_wellbeing), 0));
                    continue;
                }

                appItems.add(new AppItem(activity.getActivityType(), activity.getName(), activityRecord.getActivityName(), activityRecord.getActivityWayToWellbeing(), activity.getActivityId()));
            }

            runOnUiThread(() -> {
                adapter.setValues(appItems);
            });
        });
    }

    @Override
    public void onItemSelected(long activityId, String packageName) {
        WellbeingDatabaseModule.databaseExecutor.execute(() -> {
            this.db.physicalActivityDao().updateActivityId(packageName, activityId);
        });
    }
}