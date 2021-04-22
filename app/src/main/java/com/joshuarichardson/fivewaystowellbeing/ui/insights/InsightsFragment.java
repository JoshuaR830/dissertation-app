package com.joshuarichardson.fivewaystowellbeing.ui.insights;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.ActivityTypeImageHelper;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.TimeFormatter;
import com.joshuarichardson.fivewaystowellbeing.TimeHelper;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.WellbeingHelper;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.ActivityStats;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingValues;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingResult;

import java.util.Arrays;
import java.util.Calendar;
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
public class InsightsFragment extends Fragment implements InsightsAdapter.DateClickListener, InsightsAdapter.ChipInfoCallback {

    public static final long MILLIS_PER_DAY = 86400000;
    MutableLiveData<Integer> daysLive = new MutableLiveData<>(7);
    long selectedTime = Calendar.getInstance().getTimeInMillis();

    @Inject
    WellbeingDatabase db;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parentView, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_insights, parentView, false);

        Observer<Integer> daysObserver = days -> {
            long thisMorning = TimeHelper.getStartOfDay(this.selectedTime);
            long tonight = TimeHelper.getEndOfDay(this.selectedTime);

            long finalStartTime = thisMorning - (MILLIS_PER_DAY * (days - 1));

            // End of previous period
            long previousPeriodEndTime = tonight - (MILLIS_PER_DAY * days);
            // Number of days earlier
            long previousPeriodStartTime = finalStartTime - (MILLIS_PER_DAY * days);

            String timeText = String.format(Locale.getDefault(), "%s - %s", TimeFormatter.formatTimeAsDayMonthString(finalStartTime), TimeFormatter.formatTimeAsDayMonthString(tonight));

            WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                boolean shouldShowBest = true;
                boolean shouldShowWorst = true;

                List<WellbeingResult> currentWellbeingResults = db.wellbeingResultsDao().getResultsByTimestampRange(finalStartTime, tonight);
                List<WellbeingResult> previousWellbeingResults = db.wellbeingResultsDao().getResultsByTimestampRange(previousPeriodStartTime, previousPeriodEndTime);
                int currentDaysActive = this.db.surveyResponseDao().getNumDaysWithWaysToWellbeingByDate(finalStartTime, tonight);
                int previousDaysActive = this.db.surveyResponseDao().getNumDaysWithWaysToWellbeingByDate(previousPeriodStartTime, previousPeriodEndTime);

                WellbeingValues currentValues = new WellbeingValues(currentWellbeingResults, finalStartTime, tonight);
                WellbeingValues previousValues = new WellbeingValues(previousWellbeingResults, previousPeriodStartTime, previousPeriodEndTime);

                WaysToWellbeing mostAchieved = currentValues.getMostAchieved();
                List<ActivityStats> activityBestStats = db.surveyResponseActivityRecordDao().getActivityFrequencyByWellbeingTypeBetweenTimes(finalStartTime, tonight, mostAchieved.toString());

                WaysToWellbeing leastAchieved = currentValues.getLeastAchieved();

                // If the values are the same or it is unassigned then it should not be displayed
                if((mostAchieved == leastAchieved && shouldShowBest) || leastAchieved == WaysToWellbeing.UNASSIGNED) {
                    shouldShowWorst = false;
                }

                // This looks to see what the least achieved activity of the least achieved way to wellbeing is from the last week - suggests you do it - but not regularly enough
                List<ActivityStats> activityWorstStats = db.surveyResponseActivityRecordDao().getActivityFrequencyByWellbeingTypeBetweenTimes(finalStartTime, tonight, leastAchieved.toString());

                // Get activities in forever if not available in the last week
                if (activityWorstStats.size() == 0) {
                    activityWorstStats = db.surveyResponseActivityRecordDao().getActivityFrequencyByWellbeingTypeBetweenTimes(0, tonight, leastAchieved.toString());
                }

                long activityBestId = InsightActivitySelectionHelper.selectMostAchieved(activityBestStats);
                long activityWorstId = InsightActivitySelectionHelper.selectLeastAchieved(activityWorstStats, false);

                ActivityRecord activityBest = db.activityRecordDao().getActivityRecordById(activityBestId);
                ActivityRecord activityWorst = db.activityRecordDao().getActivityRecordById(activityWorstId);

                if(activityBest == null) {
                    shouldShowBest = false;
                }

                if(activityWorst == null) {
                    activityWorst = getPlaceholderActivity(leastAchieved);
                }

                String improvementWellbeingString = getString(WellbeingHelper.getWellbeingStringResource(leastAchieved));
                String wellbeingImprovement = String.format(Locale.getDefault(), "%s %s %s", getString(R.string.suggestions_work_on), improvementWellbeingString, getString(R.string.suggestions_score));
                String activitySuggestion = String.format(Locale.getDefault(), "%s %s", getString(R.string.suggestions_activity), activityWorst.getActivityName());
                String improvementDescription = String.format(Locale.getDefault(), "%s %s %s", getString(R.string.suggestions_improve_description), improvementWellbeingString, getString(R.string.suggestions_score));

                String bestWellbeingString = getString(WellbeingHelper.getWellbeingStringResource(mostAchieved));
                String wellbeingBest = String.format(Locale.getDefault(), "%s %s", getString(R.string.suggestions_best), bestWellbeingString);
                String activityFavourite = String.format(Locale.getDefault(), "%s %s", getString(R.string.suggestions_favourite), (activityBest != null ? activityBest.getActivityName() : ""));
                String positiveDescription = getString(R.string.suggestions_positive_description);

                List<InsightsItem> insights = Arrays.asList(
                    new InsightsItem(timeText, "", 2, null, InsightType.DATE_PICKER_CARD, null),
                    new InsightsItem(getString(R.string.wellbeing_insights_graph), "", 2, null, InsightType.DOUBLE_GRAPH, currentValues),
                    new InsightsItem(wellbeingBest, activityFavourite, positiveDescription, mostAchieved, shouldShowBest),
                    new InsightsItem(wellbeingImprovement, activitySuggestion, improvementDescription, leastAchieved, shouldShowWorst),
                    new InsightsItem(getString(R.string.wellbeing_connect), WaysToWellbeing.CONNECT, currentValues.getAchievedConnectNumber(), previousValues.getAchievedConnectNumber(), InsightType.SINGLE_INSIGHT_CARD),
                    new InsightsItem(getString(R.string.wellbeing_be_active), WaysToWellbeing.BE_ACTIVE, currentValues.getAchievedBeActiveNumber(), previousValues.getAchievedBeActiveNumber(), InsightType.SINGLE_INSIGHT_CARD),
                    new InsightsItem(getString(R.string.wellbeing_keep_learning), WaysToWellbeing.KEEP_LEARNING, currentValues.getAchievedKeepLearningNumber(), previousValues.getAchievedKeepLearningNumber(), InsightType.SINGLE_INSIGHT_CARD),
                    new InsightsItem(getString(R.string.wellbeing_take_notice), WaysToWellbeing.TAKE_NOTICE, currentValues.getAchievedTakeNoticeNumber(), previousValues.getAchievedTakeNoticeNumber(), InsightType.SINGLE_INSIGHT_CARD),
                    new InsightsItem(getString(R.string.wellbeing_give), WaysToWellbeing.GIVE, currentValues.getAchievedGiveNumber(), previousValues.getAchievedGiveNumber(), InsightType.SINGLE_INSIGHT_CARD),
                    new InsightsItem(getString(R.string.days_active), WaysToWellbeing.UNASSIGNED, currentDaysActive, previousDaysActive, InsightType.SINGLE_INSIGHT_CARD)
                );

                getActivity().runOnUiThread(() -> {
                    GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);

                    layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            return insights.get(position).getColumnWidth();
                        }
                    });

                    InsightsAdapter adapter = new InsightsAdapter(getActivity(), insights, this, getParentFragmentManager(), this);

                    RecyclerView recycler = root.findViewById(R.id.insights_recycler_view);
                    recycler.setLayoutManager(layoutManager);
                    recycler.setAdapter(adapter);

                });
            });
        };

        this.daysLive.observe(requireActivity(), daysObserver);

        return root;
    }

    public ActivityRecord getPlaceholderActivity(WaysToWellbeing wellbeingType) {
        switch (wellbeingType) {
            case CONNECT:
                return new ActivityRecord(getString(R.string.connect_suggestion), 0, 0, ActivityType.PEOPLE, wellbeingType, false);
            case BE_ACTIVE:
                return new ActivityRecord(getString(R.string.be_active_suggestion), 0, 0, ActivityType.EXERCISE, wellbeingType, false);
            case KEEP_LEARNING:
                return new ActivityRecord(getString(R.string.keep_learning_suggestion), 0, 0, ActivityType.HOBBY, wellbeingType, false);
            case TAKE_NOTICE:
                return new ActivityRecord(getString(R.string.take_notice_suggestion), 0, 0, ActivityType.JOURNALING, wellbeingType, false);
            case GIVE:
                return new ActivityRecord(getString(R.string.give_suggestion), 0, 0, ActivityType.CHORES, wellbeingType, false);
            default:
                return new ActivityRecord(getString(R.string.default_suggestion), 0, 0, ActivityType.PEOPLE, WaysToWellbeing.CONNECT, false);
        }
    }

    @Override
    public void updateInsights(View view, long startTime, long endTime) {
        long difference = endTime - startTime;
        int days = (int) (difference/MILLIS_PER_DAY) + 1;
        if(days == 1) {
            return;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(endTime);
        this.selectedTime = cal.getTimeInMillis();
        this.daysLive.postValue(days);
    }

    private void populateInsightCard(ActivityRecord activityRecord, WaysToWellbeing wayToWellbeing, LinearLayout helpContainer, String title, String description) {
        View insightsCard = LayoutInflater.from(requireContext()).inflate(R.layout.insight_suggestion_card, null);

        ImageView bestImage = insightsCard.findViewById(R.id.wellbeing_type_image);
        TextView bestTitle = insightsCard.findViewById(R.id.way_to_wellbeing);
        TextView bestActivity = insightsCard.findViewById(R.id.insight_title_type);
        TextView bestDescription = insightsCard.findViewById(R.id.insight_description);

        String activityTitle = String.format(Locale.getDefault(), "%s %s", title, activityRecord.getActivityName());

        FrameLayout frame = insightsCard.findViewById(R.id.image_view_frame);
        ImageView activityImage = insightsCard.findViewById(R.id.activity_image);
        frame.setVisibility(View.VISIBLE);
        activityImage.setImageResource(ActivityTypeImageHelper.getActivityImage(activityRecord.getActivityType()));

        WayToWellbeingImageColorizer.colorize(requireContext(), bestImage, wayToWellbeing);
        bestTitle.setText(activityTitle);
        bestActivity.setText(description);
        bestDescription.setVisibility(View.GONE);
        helpContainer.addView(insightsCard);
    }

    // Get the info and display it when the user clicks on a chip
    public void displaySuggestionChip(View graphCard, long startTime, long endTime, WaysToWellbeing wayToWellbeing) {
        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
            LinearLayout helpContainer = graphCard.findViewById(R.id.way_to_wellbeing_help_container);

            List<ActivityStats> wellbeingSpecificStats = this.db.surveyResponseActivityRecordDao().getActivityFrequencyByWellbeingTypeBetweenTimes(startTime, endTime, wayToWellbeing.toString());

            ActivityRecord bestActivityRecord = null;
            ActivityRecord worstActivityRecord = null;

            long mostAchievedId = InsightActivitySelectionHelper.selectMostAchieved(wellbeingSpecificStats);
            long leastAchievedId = InsightActivitySelectionHelper.selectLeastAchieved(wellbeingSpecificStats, true);

            // Only get the activity if one exists
            if(mostAchievedId > 0) {
                bestActivityRecord = this.db.activityRecordDao().getActivityRecordById(mostAchievedId);
            }

            // If the worst activity is set, then get it, otherwise, get the placeholder if there is only 1 item in the list
            if(leastAchievedId > 0) {
                worstActivityRecord = this.db.activityRecordDao().getActivityRecordById(leastAchievedId);
            } else if(wellbeingSpecificStats.size() <= 1) {
                // If the list length is less than 1 and worst activity is not set then there should be a default
                worstActivityRecord = getPlaceholderActivity(wayToWellbeing);
            }

            ActivityRecord finalBestActivityRecord = bestActivityRecord;
            ActivityRecord finalWorstActivityRecord = worstActivityRecord;
            requireActivity().runOnUiThread(() -> {

                if(finalBestActivityRecord != null) {
                    // If available show the activity that the user has done most
                    populateInsightCard(finalBestActivityRecord, wayToWellbeing, helpContainer, getString(R.string.suggestions_best_activity), getString(R.string.suggestions_positive_description));
                }

                if(finalWorstActivityRecord != null) {
                    // Display the activity that could be improved
                    String description = getString(R.string.suggestions_improve_description) + " " + getString(WellbeingHelper.getWellbeingStringResource(wayToWellbeing));
                    populateInsightCard(finalWorstActivityRecord, wayToWellbeing, helpContainer, getString(R.string.suggestions_worst_activity), description);
                }
            });
        });
    }
}
