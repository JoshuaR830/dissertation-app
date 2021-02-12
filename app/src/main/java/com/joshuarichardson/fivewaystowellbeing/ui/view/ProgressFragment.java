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
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.WellbeingHelper;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphValueHelper;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphItem;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.ui.graphs.WellbeingGraphView;
import com.joshuarichardson.fivewaystowellbeing.ui.individual_surveys.IndividualSurveyActivity;

import java.util.Calendar;
import java.util.Date;
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

        // Found out how to get today midnight from https://stackoverflow.com/a/6850919/13496270
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Calculate the epoch timestamp for 12am today
        Date todayMidnight = calendar.getTime();
        long epochSecondsToday = todayMidnight.getTime();

        // Calculate the epoch time for 12am tomorrow
        calendar.add(Calendar.DATE, 1);
        Date tomorrowMidnight = calendar.getTime();
        long epochSecondsTomorrow = tomorrowMidnight.getTime();

        LinearLayout newView = view.findViewById(R.id.surveys_completed_today);

        this.surveyResponsesObserver = surveys -> {
            if(surveys.size() == 0) {
                return;
            }

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
        this.surveyResponseItems = surveyDao.getSurveyResponsesByTimestampRange(epochSecondsToday, epochSecondsTomorrow);
        this.surveyResponseItems.observe(requireActivity(), this.surveyResponsesObserver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.surveyResponseItems.removeObserver(this.surveyResponsesObserver);
        this.graphUpdateValues.removeObserver(this.wholeGraphUpdate);
    }
}
