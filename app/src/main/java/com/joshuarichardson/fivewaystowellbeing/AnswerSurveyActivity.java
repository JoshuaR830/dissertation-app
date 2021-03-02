package com.joshuarichardson.fivewaystowellbeing;

import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.joshuarichardson.fivewaystowellbeing.analytics.LogAnalyticEventHelper;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseElementDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.QuestionsToAsk;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyQuestionSet;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseElement;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyBuilder;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import dagger.hilt.android.AndroidEntryPoint;

import static com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes.BASIC_SURVEY;
import static java.lang.Long.parseLong;

@AndroidEntryPoint
public class AnswerSurveyActivity extends AppCompatActivity {

    private SurveyResponseDao surveyResponseDao;
    private SurveyResponseElementDao surveyResponseElementDao;
    private long setId;

    @Inject
    WellbeingDatabase db;

    @Inject
    public LogAnalyticEventHelper analyticsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_survey);

        this.surveyResponseDao = this.db.surveyResponseDao();
        this.surveyResponseElementDao = this.db.surveyResponseElementDao();

        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
            List<ActivityRecord> activities = this.db.activityRecordDao().getAllActivitiesNotLive();
            List<SurveyQuestionSet> questionSets = this.db.surveyQuestionSetDao().getUnansweredSurveyQuestionSets();

            // Cope with potentially null values
            List<SurveyQuestionSet> questionSetValues = new ArrayList<>();
            if(questionSets != null && questionSets != null) {
                questionSetValues = questionSets;
            }

            // Cope with null values
            this.setId = 0;
            if(questionSetValues != null && questionSetValues.size() > 0) {
                // This gets the id from the data
                // ToDo - if there are multiple surveys - probably want to have a way to deal with that
                this.setId = questionSetValues.get(0).getId();
            }

            // This is a list of questions - should all be from unanswered surveys
            List<QuestionsToAsk> questions = this.db.questionsToAskDao().getQuestionsBySetId(setId);

            List<QuestionsToAsk> questionList = new ArrayList<>();

            // Cope with null values
            if(questions != null && questions != null) {
                questionList = questions;
            }

            // ToDo - might be better if this didn't have to be passed in separately
            HashMap<Long, String> apps = new HashMap<>();
            if (activities != null) {
                for (ActivityRecord activity : activities) {
                    apps.put(activity.getActivityRecordId(), activity.getActivityName());
                }
            }


            // ToDo - need a way to populate the database with questions
            List<QuestionsToAsk> finalQuestionList = questionList;

            runOnUiThread(() -> {
                LinearLayout surveyBuilder = new SurveyBuilder(AnswerSurveyActivity.this)
                        .withBasicSurvey(apps)
                        .withQuestions(finalQuestionList)
                        .build();

                ScrollView view = findViewById(R.id.survey_items_scroll_view);
                view.removeAllViews();
                view.addView(surveyBuilder);
            });


        });
    }

    Date now = new Date();

    // ToDo - what to do about this if there is no basic survey?
    SurveyResponse surveyResponse = new SurveyResponse(now.getTime(), WaysToWellbeing.UNASSIGNED, "", "");

    public void onSubmit(View v) {
        // Log the survey completion
        analyticsHelper.logCreateSurveyEvent(this);

        // Get the layout that contains all of the survey items
        ScrollView scrollView = findViewById(R.id.survey_items_scroll_view);
        LinearLayout layout = (LinearLayout) scrollView.getChildAt(0);

        ArrayList<String> questionTitles = new ArrayList<>();
        ArrayList<String> questionAnswers = new ArrayList<>();

        long activityId = -1;

        // Process each item
        for(int i = 0; i < layout.getChildCount(); i++) {
            // Get the question item
            View child = layout.getChildAt(i);

            // Get the question type
            SurveyItemTypes questionType = (SurveyItemTypes) child.getTag();

            if(questionType == BASIC_SURVEY) {
                EditText titleView = child.findViewById(R.id.survey_title_input);
                EditText descriptionView = child.findViewById(R.id.survey_description_input);

                TextView activityIdSelected = child.findViewById(R.id.survey_activity_record_id);
                activityId = parseLong(activityIdSelected.getText().toString());

                AutoCompleteTextView waysToWellbeingView = child.findViewById(R.id.way_to_wellbeing_input);

                surveyResponse.setTitle(titleView.getText().toString());
                surveyResponse.setDescription(descriptionView.getText().toString());

                // ToDo - have constant string in place of the enum - rather than having to convert everywhere have a fixed, human readable value
                // Convert between the drop down value and the enum
                String wayToWellbeing = "";
                switch(waysToWellbeingView.getText().toString().toLowerCase()) {
                    case "connect":
                        wayToWellbeing = WaysToWellbeing.CONNECT.toString();
                        break;
                    case "be active":
                        wayToWellbeing = WaysToWellbeing.BE_ACTIVE.toString();
                        break;
                    case "keep learning":
                        wayToWellbeing = WaysToWellbeing.KEEP_LEARNING.toString();
                        break;
                    case "take notice":
                        wayToWellbeing = WaysToWellbeing.TAKE_NOTICE.toString();
                        break;
                    case "give":
                        wayToWellbeing = WaysToWellbeing.GIVE.toString();
                        break;
                    default:
                        wayToWellbeing = WaysToWellbeing.UNASSIGNED.toString();
                }

                surveyResponse.setSurveyResponseWayToWellbeing(wayToWellbeing);
                break;
            }

            String questionAnswer = "";

            // Process the question types appropriately to get the responses
            switch(questionType) {
                // ToDo add all of the questions to database with the survey ID as a foreign key
                // ToDo Survey sequence order would be useful to remember for reconstructing
                case DROP_DOWN_LIST:
                    AutoCompleteTextView dropDown = child.findViewById(R.id.drop_down_input);
                    questionAnswer = dropDown.getText().toString();
                    break;
                case TEXT:
                    EditText inputText = child.findViewById(R.id.text_input);
                    questionAnswer = inputText.getText().toString();
                    break;
                default:
                   break;
            }

            // Get the question title
            TextView questionTitleView = child.findViewById(R.id.question_title);
            questionTitles.add(questionTitleView.getText().toString());
            questionAnswers.add(questionAnswer);
        }

        long finalActivityId = activityId;
        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
            long surveyId = this.surveyResponseDao.insert(surveyResponse);

            for(int i = 0; i < questionAnswers.size(); i++) {
                SurveyResponseElement surveyResponseElement = new SurveyResponseElement(surveyId, questionTitles.get(i), questionAnswers.get(i));
                long elementId = this.surveyResponseElementDao.insert(surveyResponseElement);
            }

            // Link activity to a survey
            if(finalActivityId != -1) {
                this.db.surveyResponseActivityRecordDao().insert(new SurveyResponseActivityRecord(surveyId, finalActivityId, 1, "note 1",  1612427791, 1612427795, 0, false));
            }

            // ToDo - this is right - but not yet - because I can't auto generate questions
//            this.db.surveyQuestionSetDao().updateSetWithCompletedSurveyId(this.setId, surveyId);
            finish();
        });
    }
}