package com.joshuarichardson.fivewaystowellbeing.ui.insights;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.TimeFormatter;
import com.joshuarichardson.fivewaystowellbeing.TimeHelper;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingValues;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingResult;
import com.joshuarichardson.fivewaystowellbeing.ui.pass_times.edit.ViewPassTimesActivity;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class InsightsFragment extends Fragment {

    @Inject
    WellbeingDatabase db;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parentView, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_insights, parentView, false);

        MaterialButton activityButton = new MaterialButton(getActivity(), null, R.attr.materialTextButton);
        activityButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        activityButton.setText(R.string.launch_view_pass_time);
        activityButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ViewPassTimesActivity.class);
            startActivity(intent);
        });

        // ToDo - could this use live data
        long millisPerDay = 86400000;
        int days = 7;
        Date now = new Date();
        long thisMorning = TimeHelper.getStartOfDay(now.getTime());
        long tonight = TimeHelper.getEndOfDay(now.getTime());

        long finalStartTime = thisMorning - (millisPerDay * (days - 1));

        // End of previous period
        long previousPeriodEndTime = tonight - (millisPerDay * days);
        // Number of days earlier
        long previousPeriodStartTime = finalStartTime - (millisPerDay * days);

        TextView startDayText = root.findViewById(R.id.start_day);
        TextView endDayText = root.findViewById(R.id.end_day);
        startDayText.setText(TimeFormatter.formatTimeAsDayMonthString(finalStartTime));
        endDayText.setText(TimeFormatter.formatTimeAsDayMonthString(tonight));

        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
            List<WellbeingResult> currentWellbeingResults = db.wellbeingResultsDao().getResultsByTimestampRange(finalStartTime, tonight);
            List<WellbeingResult> previousWellbeingResults = db.wellbeingResultsDao().getResultsByTimestampRange(previousPeriodStartTime, previousPeriodEndTime);
            WellbeingValues currentValues = new WellbeingValues(currentWellbeingResults);
            WellbeingValues previousValues = new WellbeingValues(previousWellbeingResults);

            List<InsightsItem> insights = Arrays.asList(
                new InsightsItem(getString(R.string.title_weekly_insights_overview), getString(R.string.description_weekly_insights_overview), 2, null),
                new InsightsItem(getString(R.string.wellbeing_connect), WaysToWellbeing.CONNECT, currentValues.getAchievedConnectNumber(), previousValues.getAchievedConnectNumber()),
                new InsightsItem(getString(R.string.wellbeing_be_active), WaysToWellbeing.BE_ACTIVE, currentValues.getAchievedBeActiveNumber(), previousValues.getAchievedBeActiveNumber()),
                new InsightsItem(getString(R.string.wellbeing_keep_learning), WaysToWellbeing.KEEP_LEARNING, currentValues.getAchievedKeepLearningNumber(), previousValues.getAchievedKeepLearningNumber()),
                new InsightsItem(getString(R.string.wellbeing_take_notice), WaysToWellbeing.TAKE_NOTICE, currentValues.getAchievedTakeNoticeNumber(), previousValues.getAchievedTakeNoticeNumber()),
                new InsightsItem(getString(R.string.wellbeing_give), WaysToWellbeing.GIVE, currentValues.getAchievedGiveNumber(), previousValues.getAchievedGiveNumber())
            );

            getActivity().runOnUiThread(() -> {
                GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);

                layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return insights.get(position).getColumnWidth();
                    }
                });

                InsightsAdapter adapter = new InsightsAdapter(getActivity(), insights);

                RecyclerView recycler = root.findViewById(R.id.insights_recycler_view);
                recycler.setLayoutManager(layoutManager);
                recycler.setAdapter(adapter);

            });
        });
        return root;
    }
}
