package com.joshuarichardson.fivewaystowellbeing.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    private SurveyResponseDao surveyDao;
    private LiveData<List<SurveyResponse>> surveyResponseItems;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parentView, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_progress, parentView, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.surveyDao = db.surveyResponseDao();

        CardView container = view.findViewById(R.id.graph_card);
        FrameLayout canvasContainer = container.findViewById(R.id.graph_card_container);

        int[] values = new int[]{0, 0, 0, 0, 0};
        WellbeingGraphView graphView = new WellbeingGraphView(getActivity(), 600, values);
        tempObserveValues(graphView, values);
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
        this.surveyResponseItems = this.surveyDao.getSurveyResponsesByTimestampRange(epochSecondsToday, epochSecondsTomorrow);
        this.surveyResponseItems.observe(requireActivity(), this.surveyResponsesObserver);
    }

    public void tempObserveValues(WellbeingGraphView graphView, int[] values) {
        Observer<Integer> giveObserver = giveNum -> {
            values[0] = giveNum*20;
            graphView.updateValues(values);
        };

        Observer<Integer> connectObserver = connectNum -> {
            values[1] = connectNum*20;
            graphView.updateValues(values);
        };

        Observer<Integer> beActiveObserver = beActiveNum -> {
            values[2] = beActiveNum*20;
            graphView.updateValues(values);
        };

        Observer<Integer> takeNoticeObserver = takeNoticeNum -> {
            values[3] = takeNoticeNum*20;
            graphView.updateValues(values);
        };

        Observer<Integer> keepLearningObserver = keepLearningNum -> {
            values[4] = keepLearningNum*20;
            graphView.updateValues(values);
        };


        this.db.surveyResponseDao().getLiveInsights(WaysToWellbeing.GIVE.toString()).observe(requireActivity(), giveObserver);
        this.db.surveyResponseDao().getLiveInsights(WaysToWellbeing.CONNECT.toString()).observe(requireActivity(), connectObserver);
        this.db.surveyResponseDao().getLiveInsights(WaysToWellbeing.BE_ACTIVE.toString()).observe(requireActivity(), beActiveObserver);
        this.db.surveyResponseDao().getLiveInsights(WaysToWellbeing.TAKE_NOTICE.toString()).observe(requireActivity(), takeNoticeObserver);
        this.db.surveyResponseDao().getLiveInsights(WaysToWellbeing.KEEP_LEARNING.toString()).observe(requireActivity(), keepLearningObserver);

        // ToDo - in some places I have removed the observers - should I not do this here too?
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.surveyResponseItems.removeObserver(this.surveyResponsesObserver);
    }
}
