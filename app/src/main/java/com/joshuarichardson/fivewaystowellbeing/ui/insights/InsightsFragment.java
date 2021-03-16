package com.joshuarichardson.fivewaystowellbeing.ui.insights;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.TimeFormatter;
import com.joshuarichardson.fivewaystowellbeing.TimeHelper;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingValues;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingResult;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class InsightsFragment extends Fragment implements InsightsAdapter.DateClickListener {

    public static final long MILLIS_PER_DAY = 86400000;
    MutableLiveData<Integer> daysLive = new MutableLiveData<>(7);
    Date selectedTime = new Date();

    @Inject
    WellbeingDatabase db;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parentView, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_insights, parentView, false);

        Observer<Integer> daysObserver = days -> {
            long thisMorning = TimeHelper.getStartOfDay(this.selectedTime.getTime());
            long tonight = TimeHelper.getEndOfDay(this.selectedTime.getTime());

            long finalStartTime = thisMorning - (MILLIS_PER_DAY * (days - 1));

            // End of previous period
            long previousPeriodEndTime = tonight - (MILLIS_PER_DAY * days);
            // Number of days earlier
            long previousPeriodStartTime = finalStartTime - (MILLIS_PER_DAY * days);

            String timeText = String.format(Locale.getDefault(), "%s - %s", TimeFormatter.formatTimeAsDayMonthString(finalStartTime), TimeFormatter.formatTimeAsDayMonthString(tonight));

            WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                List<WellbeingResult> currentWellbeingResults = db.wellbeingResultsDao().getResultsByTimestampRange(finalStartTime, tonight);
                List<WellbeingResult> previousWellbeingResults = db.wellbeingResultsDao().getResultsByTimestampRange(previousPeriodStartTime, previousPeriodEndTime);

                WellbeingValues currentValues = new WellbeingValues(currentWellbeingResults, finalStartTime, tonight);
                WellbeingValues previousValues = new WellbeingValues(previousWellbeingResults, previousPeriodStartTime, previousPeriodEndTime);

                List<InsightsItem> insights = Arrays.asList(
                    new InsightsItem(timeText, "", 2, null, InsightType.DATE_PICKER_CARD, null),
                    new InsightsItem(getContext().getString(R.string.wellbeing_insights_graph), "", 2, null, InsightType.DOUBLE_GRAPH, currentValues),
                    new InsightsItem(getString(R.string.wellbeing_connect), WaysToWellbeing.CONNECT, currentValues.getAchievedConnectNumber(), previousValues.getAchievedConnectNumber(), InsightType.SINGLE_INSIGHT_CARD),
                    new InsightsItem(getString(R.string.wellbeing_be_active), WaysToWellbeing.BE_ACTIVE, currentValues.getAchievedBeActiveNumber(), previousValues.getAchievedBeActiveNumber(), InsightType.SINGLE_INSIGHT_CARD),
                    new InsightsItem(getString(R.string.wellbeing_keep_learning), WaysToWellbeing.KEEP_LEARNING, currentValues.getAchievedKeepLearningNumber(), previousValues.getAchievedKeepLearningNumber(), InsightType.SINGLE_INSIGHT_CARD),
                    new InsightsItem(getString(R.string.wellbeing_take_notice), WaysToWellbeing.TAKE_NOTICE, currentValues.getAchievedTakeNoticeNumber(), previousValues.getAchievedTakeNoticeNumber(), InsightType.SINGLE_INSIGHT_CARD),
                    new InsightsItem(getString(R.string.wellbeing_give), WaysToWellbeing.GIVE, currentValues.getAchievedGiveNumber(), previousValues.getAchievedGiveNumber(), InsightType.SINGLE_INSIGHT_CARD)
                );

                getActivity().runOnUiThread(() -> {
                    GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);

                    layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            return insights.get(position).getColumnWidth();
                        }
                    });

                    InsightsAdapter adapter = new InsightsAdapter(getActivity(), insights, this, getParentFragmentManager());

                    RecyclerView recycler = root.findViewById(R.id.insights_recycler_view);
                    recycler.setLayoutManager(layoutManager);
                    recycler.setAdapter(adapter);

                });
            });
        };

        this.daysLive.observe(requireActivity(), daysObserver);

        return root;
    }

    @Override
    public void updateInsights(View view, long startTime, long endTime) {
        long difference = endTime - startTime;
        int days = (int) (difference/MILLIS_PER_DAY) + 1;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(endTime);
        this.selectedTime = cal.getTime();
        this.daysLive.postValue(days);
    }
}
