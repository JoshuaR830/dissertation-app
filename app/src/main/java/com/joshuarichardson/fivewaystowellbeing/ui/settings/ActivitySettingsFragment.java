package com.joshuarichardson.fivewaystowellbeing.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.joshuarichardson.fivewaystowellbeing.physical_activity_tracking.PhysicalActivityTypes;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ActivitySettingsFragment extends PreferenceFragmentCompat {
    @Inject
    WellbeingDatabase db;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.auto_settings, rootKey);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        ListPreference walkActivityList = (ListPreference) findPreference("notification_auto_tracking_list_walk");
        ListPreference runActivityList = (ListPreference) findPreference("notification_auto_tracking_list_run");
        ListPreference cycleActivityList = (ListPreference) findPreference("notification_auto_tracking_list_cycle");
        ListPreference vehicleActivityList = (ListPreference) findPreference("notification_auto_tracking_list_vehicle");

        walkActivityList.setSummaryProvider(preference -> preferences.getString("notification_auto_tracking_list_walk", ""));
        runActivityList.setSummaryProvider(preference -> preferences.getString("notification_auto_tracking_list_run", ""));
        cycleActivityList.setSummaryProvider(preference -> preferences.getString("notification_auto_tracking_list_cycle", ""));
        vehicleActivityList.setSummaryProvider(preference -> preferences.getString("notification_auto_tracking_list_vehicle", ""));

        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> ids = new ArrayList<>();

        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
            List<ActivityRecord> activities = this.db.activityRecordDao().getAllActivitiesNotLive();

            for(ActivityRecord activity : activities) {
                names.add(activity.getActivityName());
                ids.add(String.valueOf(activity.getActivityRecordId()));
            }

            requireActivity().runOnUiThread(() -> {

                String[] nameArray = names.toArray(new String[0]);
                walkActivityList.setEntries(nameArray);
                walkActivityList.setEntryValues(ids.toArray(new String[0]));
                runActivityList.setEntries(nameArray);
                runActivityList.setEntryValues(ids.toArray(new String[0]));
                cycleActivityList.setEntries(nameArray);
                cycleActivityList.setEntryValues(ids.toArray(new String[0]));
                vehicleActivityList.setEntries(nameArray);
                vehicleActivityList.setEntryValues(ids.toArray(new String[0]));
            });
        });

        setPreferenceListener(walkActivityList, preferences, PhysicalActivityTypes.WALK);
        setPreferenceListener(runActivityList, preferences, PhysicalActivityTypes.RUN);
        setPreferenceListener(cycleActivityList, preferences, PhysicalActivityTypes.CYCLE);
        setPreferenceListener(vehicleActivityList, preferences, PhysicalActivityTypes.VEHICLE);
    }

    private void setPreferenceListener(ListPreference listPreference, SharedPreferences preferences, String typeName) {
        listPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong("notification_auto_tracking_list_" + typeName.toLowerCase() + "_id", Long.parseLong(String.valueOf(newValue)));
            editor.apply();
            WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                this.db.physicalActivityDao().updateActivityId(typeName, Long.parseLong(String.valueOf(newValue)));

                ActivityRecord record = this.db.activityRecordDao().getActivityRecordById(Long.parseLong(String.valueOf(newValue)));
                editor.putString("notification_auto_tracking_list_"+ typeName.toLowerCase(), record.getActivityName());
                editor.apply();

                requireActivity().runOnUiThread(() -> {
                    listPreference.setSummaryProvider(pref -> record.getActivityName());
                });

            });

            return true;
        });
    }
}
