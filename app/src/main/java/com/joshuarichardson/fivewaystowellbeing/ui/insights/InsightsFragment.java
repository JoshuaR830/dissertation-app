package com.joshuarichardson.fivewaystowellbeing.ui.insights;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.ui.view.ViewPassTimesActivity;

import java.util.Arrays;
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

        String prefix = getString(R.string.wellbeing_insight_prefix) + " ";

        MaterialButton activityButton = new MaterialButton(getActivity(), null, R.attr.materialButtonOutlinedStyle);
        activityButton.setText(R.string.launch_view_pass_time);
        activityButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ViewPassTimesActivity.class);
            startActivity(intent);
        });

        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
            List<InsightsItem> insights = Arrays.asList(
                new InsightsItem(getString(R.string.wellbeing_insight_activities), "", 2, activityButton),
                new InsightsItem(prefix + getString(R.string.wellbeing_connect), String.format(Locale.getDefault(), "%d", db.surveyResponseDao().getInsights(WaysToWellbeing.CONNECT.toString()))),
                new InsightsItem(prefix + getString(R.string.wellbeing_be_active), String.format(Locale.getDefault(), "%d", db.surveyResponseDao().getInsights(WaysToWellbeing.BE_ACTIVE.toString()))),
                new InsightsItem(prefix + getString(R.string.wellbeing_keep_learning), String.format(Locale.getDefault(), "%d", db.surveyResponseDao().getInsights(WaysToWellbeing.KEEP_LEARNING.toString()))),
                new InsightsItem(prefix + getString(R.string.wellbeing_take_notice), String.format(Locale.getDefault(), "%d", db.surveyResponseDao().getInsights(WaysToWellbeing.TAKE_NOTICE.toString()))),
                new InsightsItem(prefix + getString(R.string.wellbeing_give), String.format(Locale.getDefault(), "%d", db.surveyResponseDao().getInsights(WaysToWellbeing.GIVE.toString())))
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
