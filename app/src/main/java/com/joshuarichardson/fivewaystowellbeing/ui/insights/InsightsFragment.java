package com.joshuarichardson.fivewaystowellbeing.ui.insights;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.TimeHelper;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphItem;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphValueHelper;
import com.joshuarichardson.fivewaystowellbeing.ui.pass_times.edit.ViewPassTimesActivity;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        Date now = new Date();
        long startTime = TimeHelper.getStartOfDay(now.getTime());
        long endTime = TimeHelper.getEndOfDay(now.getTime());


        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {

            int connect = 0;
            int beActive = 0;
            int keepLearning = 0;
            int takeNotice = 0;
            int give = 0;

            for(int i = 0; i < 7; i++) {
                List<WellbeingGraphItem> response = db.wellbeingQuestionDao().getWaysToWellbeingBetweenTimesNotLive(startTime - (millisPerDay * i), endTime - (millisPerDay * i));
                WellbeingGraphValueHelper values = WellbeingGraphValueHelper.getWellbeingGraphValues(response);

                if(values.getConnectValue() >= 100) {
                    connect ++;
                }
                if(values.getBeActiveValue() >= 100) {
                    beActive ++;
                }
                if(values.getKeepLearningValue() >= 100) {
                    keepLearning ++;
                }
                if(values.getTakeNoticeValue() >= 100) {
                    takeNotice ++;
                }
                if(values.getGiveValue() >= 100) {
                    give ++;
                }
            }

            List<InsightsItem> insights = Arrays.asList(
                new InsightsItem(getString(R.string.title_weekly_insights_overview), getString(R.string.description_weekly_insights_overview), 2, null),
                new InsightsItem(getString(R.string.wellbeing_connect), String.format(Locale.getDefault(), "%d", connect)),
                new InsightsItem(getString(R.string.wellbeing_be_active), String.format(Locale.getDefault(), "%d", beActive)),
                new InsightsItem(getString(R.string.wellbeing_keep_learning), String.format(Locale.getDefault(), "%d", keepLearning)),
                new InsightsItem(getString(R.string.wellbeing_take_notice), String.format(Locale.getDefault(), "%d", takeNotice)),
                new InsightsItem(getString(R.string.wellbeing_give), String.format(Locale.getDefault(), "%d", give))
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
