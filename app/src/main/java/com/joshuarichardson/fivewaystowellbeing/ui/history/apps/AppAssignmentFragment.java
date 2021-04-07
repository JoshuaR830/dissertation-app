package com.joshuarichardson.fivewaystowellbeing.ui.history.apps;

import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.TimeHelper;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.PhysicalActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AppAssignmentFragment extends Fragment {

    @Inject
    WellbeingDatabase db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_history, container, false);

//        AppActivity appActivity = this.db.physicalActivityDao().getPhysicalActivityByType(event.getPackageName());

        UsageStatsManager usage = (UsageStatsManager) requireContext().getSystemService(Service.USAGE_STATS_SERVICE);
        PackageManager packageManager = requireContext().getPackageManager();

        long currentTime = Calendar.getInstance().getTimeInMillis();
        long startTime = TimeHelper.getStartOfDay(currentTime);
        UsageEvents stats = usage.queryEvents(startTime, currentTime);

        // ToDo - this should probably be a job scheduled thing that happens each day
        // This basically gets all of the info required to display the data
        while (stats.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            stats.getNextEvent(event);

            event.getPackageName();

            ApplicationInfo info;
            try {
                info = packageManager.getApplicationInfo(event.getPackageName(), PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }

            String name = (String) packageManager.getApplicationLabel(info);

            // Add the item to the database when known that it exists
            WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                this.db.physicalActivityDao().insert(new PhysicalActivity(event.getPackageName(), name, 0, 0, 0, false));
            });
        }

        // Get the recycler view to populate
        RecyclerView recycler = root.findViewById(R.id.surveyRecyclerView);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        AppRecyclerViewAdapter adapter = new AppRecyclerViewAdapter(getActivity(), new ArrayList<>());
        recycler.setAdapter(adapter);

        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
            List<PhysicalActivity> automaticActivities = this.db.physicalActivityDao().getAllPhysicalActivitiesWithNamesAndAssociatedActivities();
            List<AppItem> appItems = new ArrayList<>();
            for(PhysicalActivity activity : automaticActivities) {
                ActivityRecord activityRecord = this.db.activityRecordDao().getActivityRecordById(activity.getActivityId());

                appItems.add(new AppItem(activity.getActivityType(), activity.getName(), activityRecord.getActivityName(), activityRecord.getActivityWayToWellbeing()));
            }

            Log.d("AUTOMATIC", String.valueOf(appItems.size()));
            requireActivity().runOnUiThread(() -> {
                adapter.setValues(appItems);
            });
        });

        return root;
    }
}
