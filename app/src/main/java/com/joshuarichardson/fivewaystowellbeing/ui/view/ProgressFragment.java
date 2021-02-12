package com.joshuarichardson.fivewaystowellbeing.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.TimeHelper;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.WellbeingHelper;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.LimitedRawSurveyData;
import com.joshuarichardson.fivewaystowellbeing.storage.RawSurveyData;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphItem;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphValueHelper;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyDataHelper;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyDay;
import com.joshuarichardson.fivewaystowellbeing.ui.graphs.WellbeingGraphView;
import com.joshuarichardson.fivewaystowellbeing.ui.individual_surveys.ActivityViewHelper;
import com.joshuarichardson.fivewaystowellbeing.ui.individual_surveys.IndividualSurveyActivity;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProgressFragment extends Fragment {

    @Inject
    WellbeingDatabase db;

    private Observer<List<SurveyResponse>> surveyResponsesObserver;
    private LiveData<List<SurveyResponse>> surveyResponseItems;
    private Observer<List<WellbeingGraphItem>> wholeGraphUpdate;
    private LiveData<List<WellbeingGraphItem>> graphUpdateValues;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parentView, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_progress, parentView, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SurveyResponseDao surveyDao = db.surveyResponseDao();

        // Found out how to get today midnight from https://stackoverflow.com/a/6850919/13496270
        Calendar thisMorning = GregorianCalendar.getInstance();
        thisMorning.set(Calendar.MINUTE, 0);
        thisMorning.set(Calendar.HOUR_OF_DAY, 0);
        thisMorning.set(Calendar.SECOND, 0);
        thisMorning.set(Calendar.MILLISECOND, 0);

        Calendar tonight = GregorianCalendar.getInstance();
        tonight.set(Calendar.HOUR_OF_DAY, 23);
        tonight.set(Calendar.MINUTE, 59);
        tonight.set(Calendar.SECOND, 59);
        tonight.set(Calendar.MILLISECOND, 999);

        CardView container = view.findViewById(R.id.graph_card);
        FrameLayout canvasContainer = container.findViewById(R.id.graph_card_container);

        WellbeingGraphView graphView = new WellbeingGraphView(getActivity(), 600, new WellbeingGraphValueHelper(0, 0,0 ,0, 0));

        this.wholeGraphUpdate = graphValues -> {
            WellbeingGraphValueHelper values = WellbeingGraphValueHelper.getWellbeingGraphValues(graphValues);
            graphView.updateValues(values);
        };

        this.graphUpdateValues = this.db.wellbeingQuestionDao().getWaysToWellbeingBetweenTimes(thisMorning.getTimeInMillis(), tonight.getTimeInMillis());
        this.graphUpdateValues.observe(requireActivity(), this.wholeGraphUpdate);
        canvasContainer.addView(graphView);

//        // Found out how to get today midnight from https://stackoverflow.com/a/6850919/13496270
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
//
//        TimeHelper.getStartOfDay(GregorianCalendar.getInstance().getTimeInMillis());
//
//        // Calculate the epoch timestamp for 12am today
//        Date todayMidnight = calendar.getTime();

        // Calculate time for today midnight
        Calendar calendar = GregorianCalendar.getInstance();
        long epochSecondsToday = TimeHelper.getStartOfDay(calendar.getTimeInMillis());
        long epochSecondsTonight = TimeHelper.getEndOfDay(calendar.getTimeInMillis());

        // Calculate the epoch time for 12am tomorrow
//        calendar.add(Calendar.DATE, 1);
//        long epochSecondsTomorrow = TimeHelper.getStartOfDay(calendar.getTimeInMillis());

        LinearLayout newView = view.findViewById(R.id.surveys_completed_today);

        this.surveyResponsesObserver = surveys -> {
            if(surveys.size() == 0) {

                // ToDo: create a new survey
                return;
            }

            // ToDo - in the new world there should only be 1 survey
            long id = surveys.get(0).getSurveyResponseId();
            // ToDo - get activities for the specific survey
            WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
//                List<ActivityRecord> activities = this.db.surveyResponseActivityRecordDao().getActivitiesBySurveyId(id);
                LinearLayout layout = requireActivity().findViewById(R.id.survey_item_container);

                List<RawSurveyData> rawSurveyDataList = this.db.wellbeingRecordDao().getDataBySurvey(id);
                if(rawSurveyDataList == null || rawSurveyDataList.size() == 0) {
                    // This converts a limited response to an entire RawSurveyData response
                    List<LimitedRawSurveyData> limitedData = this.db.wellbeingRecordDao().getLimitedDataBySurvey(id);
                    rawSurveyDataList = LimitedRawSurveyData.convertToRawSurveyDataList(limitedData);
                }

                SurveyDay surveyData = SurveyDataHelper.transform(rawSurveyDataList);

                if(surveyData == null) {
                    return;
                }

                requireActivity().runOnUiThread(() -> {
                    // ToDo - this needs to display a whole activity - not just the name

                    ActivityViewHelper.displayStuff(requireActivity(), surveyData);
//                    for(long passtimeId : surveyData.getPasstimeMap().keySet()) {
//                        Passtime passtime = surveyData.getPasstimeMap().get(passtimeId);
//                        if(passtime == null) {
//                            continue;
//                        }
//                        View passtimeItem = LayoutInflater.from(requireActivity()).inflate(R.layout.pass_time_item, layout, false);
//                        TextView title = passtimeItem.findViewById(R.id.activity_text);
//                        TextView note = passtimeItem.findViewById(R.id.activity_note_text);
//                        title.setText(passtime.getName());
//                        note.setText(passtime.getNote());
//                        TextView textView = new TextView(requireActivity());
//                        textView.setText(passtime.getName());
//                        layout.addView(passtimeItem);
//                    }
                });
            });





            // Hide the not displayed text
            newView.removeAllViews();

            int counter = 0;
            for (SurveyResponse survey : surveys) {
                LayoutInflater surveyInflater = LayoutInflater.from(getContext());

                View surveyItemToDisplay = surveyInflater.inflate(R.layout.surveys_completed_today_item, newView, false);

                surveyItemToDisplay.setId(counter);
                counter ++;

                // Find views to update
                TextView title = surveyItemToDisplay.findViewById(R.id.today_survey_item_title);
                TextView description = surveyItemToDisplay.findViewById(R.id.today_survey_item_description);
                ImageView surveyImage = surveyItemToDisplay.findViewById(R.id.today_survey_item_image);
                ImageButton button = surveyItemToDisplay.findViewById(R.id.today_survey_item_image_button);

                // Set values
                title.setText(survey.getTitle());
                description.setText(survey.getDescription());

                // Catch the exception if the user does not set a value
                WaysToWellbeing way;
                try {
                    way = WaysToWellbeing.valueOf(survey.getSurveyResponseWayToWellbeing());
                } catch(IllegalArgumentException e) {
                    way = WaysToWellbeing.UNASSIGNED;
                }

                surveyImage.setImageResource(WellbeingHelper.getImage(way));

                // Launch an activity to view a survey
                View.OnClickListener listener = v -> {
                    Intent surveyIntent = new Intent(getActivity(), IndividualSurveyActivity.class);

                    Bundle surveyBundle = new Bundle();
                    surveyBundle.putLong("survey_id", survey.getSurveyResponseId());
                    surveyIntent.putExtras(surveyBundle);
                    startActivity(surveyIntent);
                };

                surveyItemToDisplay.setOnClickListener(listener);
                button.setOnClickListener(listener);

                newView.addView(surveyItemToDisplay);

            }
        };

        // Epoch seconds give a 24 hour time frame - any new surveys added will get updated live (using now meant that future surveys today didn't get shown)
        this.surveyResponseItems = surveyDao.getSurveyResponsesByTimestampRange(epochSecondsToday, epochSecondsTonight);
        this.surveyResponseItems.observe(requireActivity(), this.surveyResponsesObserver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.surveyResponseItems.removeObserver(this.surveyResponsesObserver);
        this.graphUpdateValues.removeObserver(this.wholeGraphUpdate);
    }
}
