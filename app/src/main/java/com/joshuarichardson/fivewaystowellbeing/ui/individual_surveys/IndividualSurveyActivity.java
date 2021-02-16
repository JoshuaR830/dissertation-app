package com.joshuarichardson.fivewaystowellbeing.ui.individual_surveys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.TimeFormatter;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.WellbeingHelper;
import com.joshuarichardson.fivewaystowellbeing.analytics.LogAnalyticEventHelper;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.LimitedRawSurveyData;
import com.joshuarichardson.fivewaystowellbeing.storage.RawSurveyData;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphItem;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphValueHelper;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyDataHelper;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyDay;
import com.joshuarichardson.fivewaystowellbeing.ui.graphs.WellbeingGraphView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class IndividualSurveyActivity extends AppCompatActivity {

    @Inject
    public WellbeingDatabase db;

    @Inject
    public LogAnalyticEventHelper analyticsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_survey);

        Intent surveyIntent = getIntent();
        if(surveyIntent == null || surveyIntent.getExtras() == null) {
            finish();
            return;
        }

        long surveyId = surveyIntent.getExtras().getLong("survey_id", -1);
        long startTime = surveyIntent.getExtras().getLong("start_time", -1);

        if(surveyId < 0) {
            finish();
            return;
        }

        CardView container = findViewById(R.id.graph_card);
        FrameLayout canvasContainer = container.findViewById(R.id.graph_card_container);

        WellbeingGraphView graphView = new WellbeingGraphView(this, 600, new WellbeingGraphValueHelper(0, 0, 0, 0, 0));
        canvasContainer.addView(graphView);

        Observer<List<WellbeingGraphItem>> wholeGraphUpdate  = graphValues -> {
            WellbeingGraphValueHelper values = WellbeingGraphValueHelper.getWellbeingGraphValues(graphValues);
            graphView.updateValues(values);
        };

        // ToDo - at some point make this visible and allow it to be edited
        Button addActivityButton = findViewById(R.id.add_activity_button);
        addActivityButton.setVisibility(View.GONE);

        if(startTime > -1) {
            Date date = new Date(startTime);
            Calendar morning = new GregorianCalendar();
            Calendar night = new GregorianCalendar();
            morning.setTime(date);
            night.setTime(date);

            night.set(Calendar.HOUR_OF_DAY, 23);
            night.set(Calendar.MINUTE, 59);
            night.set(Calendar.SECOND, 59);
            night.set(Calendar.MILLISECOND, 999);
            this.db.wellbeingQuestionDao().getWaysToWellbeingBetweenTimes(morning.getTimeInMillis(), night.getTimeInMillis()).observe(this, wholeGraphUpdate);
        }

        // ToDo: should probably turn this to live data at some point
        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
            List<RawSurveyData> rawSurveyDataList = this.db.wellbeingRecordDao().getDataBySurvey(surveyId);

            if(rawSurveyDataList == null || rawSurveyDataList.size() == 0) {
                // This converts a limited response to an entire RawSurveyData response
                List<LimitedRawSurveyData> limitedData = this.db.wellbeingRecordDao().getLimitedDataBySurvey(surveyId);
                rawSurveyDataList = LimitedRawSurveyData.convertToRawSurveyDataList(limitedData);
            }

            SurveyDay surveyData = SurveyDataHelper.transform(rawSurveyDataList);


            ActivityViewHelper.displaySurveyItems(this, surveyData, this.db, getSupportFragmentManager(), analyticsHelper);

            SurveyResponse surveyResponse = this.db.surveyResponseDao().getSurveyResponseById(surveyId);

            runOnUiThread(() -> {

                TextView summaryTitle = findViewById(R.id.individual_survey_title);
                TextView description = findViewById(R.id.individual_survey_description);
                TextView date = findViewById(R.id.individual_survey_time);
                ImageView image = findViewById(R.id.individual_survey_image);

                summaryTitle.setText(surveyResponse.getTitle());
                description.setText(surveyResponse.getDescription());

                // Catch the exception if the user does not set a value
                WaysToWellbeing way;
                try {
                    way = WaysToWellbeing.valueOf(surveyResponse.getSurveyResponseWayToWellbeing());
                } catch(IllegalArgumentException e) {
                    way = WaysToWellbeing.UNASSIGNED;
                }

                image.setImageResource(WellbeingHelper.getImage(way));

                date.setText(TimeFormatter.formatTimeAsDayMonthYearString(surveyResponse.getSurveyResponseTimestamp()));
            });
        });
    }
}