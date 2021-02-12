package com.joshuarichardson.fivewaystowellbeing.ui.individual_surveys;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.TimeFormatter;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.WellbeingHelper;
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


            ActivityViewHelper.displayStuff(this, surveyData);

//            if(surveyData == null) {
//                return;
//            }
//
//
//            LinearLayout layout = findViewById(R.id.survey_item_container);
//            for(long key : surveyData.getPasstimeMap().keySet()) {
//                Passtime passtime = surveyData.getPasstimeMap().get(key);
//                if(passtime == null) {
//                    continue;
//                }
//
//                View view = LayoutInflater.from(this).inflate(R.layout.pass_time_item, layout, false);
//                TextView title = view.findViewById(R.id.activity_text);
//                TextView note = view.findViewById(R.id.activity_note_text);
//                ImageView image = view.findViewById(R.id.activity_image);
//
//                ImageButton expandButton = view.findViewById(R.id.expand_options_button);
//                View checkboxView = view.findViewById(R.id.pass_time_checkbox_container);
//
//                runOnUiThread(() -> {
//                    LinearLayout checkboxContainer = checkboxView.findViewById(R.id.check_box_container);
//
//                    for(Question question : passtime.getQuestions()) {
//                        CheckBox checkBox = (CheckBox) LayoutInflater.from(this).inflate(R.layout.item_check_box, checkboxContainer, false);
//                        checkBox.setChecked(question.getUserResponse());
//                        checkBox.setText(question.getQuestion());
//                        checkboxContainer.addView(checkBox);
//                    }
//
//                    expandButton.setOnClickListener(v -> {
//                        if(checkboxView.getVisibility() == View.GONE) {
//                            checkboxView.setVisibility(View.VISIBLE);
//                            checkboxView.getVisibility();
//                            expandButton.setImageResource(R.drawable.button_collapse);
//                        } else {
//                            checkboxView.setVisibility(View.GONE);
//                            expandButton.setImageResource(R.drawable.button_expand);
//                        }
//                    });
//
//                    if(passtime.getQuestions().size() == 0) {
//                        expandButton.setVisibility(View.GONE);
//                    }
//
//                    title.setText(passtime.getName());
//                    note.setText(passtime.getNote());
//                    image.setImageResource(ActivityTypeImageHelper.getActivityImage(passtime.getType()));
//
//                    layout.addView(view);
//                });
//
//                TextView surveyTitle = findViewById(R.id.survey_list_title);
//                TextView surveyNote = findViewById(R.id.survey_list_description);
//
//                surveyTitle.setText(surveyData.getTitle());
//                surveyNote.setText(surveyData.getNote());
//            }

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