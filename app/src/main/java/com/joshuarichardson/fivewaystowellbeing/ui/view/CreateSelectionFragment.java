package com.joshuarichardson.fivewaystowellbeing.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.WellbeingHelper;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CreateSelectionFragment extends Fragment {

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

                // Set values
                title.setText(survey.getTitle());
                description.setText(survey.getDescription());
                surveyImage.setImageResource(WellbeingHelper.getImage(WaysToWellbeing.valueOf(survey.getSurveyResponseWayToWellbeing())));

                newView.addView(surveyItemToDisplay);

            }
        };

        // Epoch seconds give a 24 hour time frame - any new surveys added will get updated live (using now meant that future surveys today didn't get shown)
        this.surveyResponseItems = this.surveyDao.getSurveyResponsesByTimestampRange(epochSecondsToday, epochSecondsTomorrow);
        this.surveyResponseItems.observe(requireActivity(), this.surveyResponsesObserver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.surveyResponseItems.removeObserver(this.surveyResponsesObserver);
    }
}
