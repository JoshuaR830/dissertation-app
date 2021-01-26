package com.joshuarichardson.fivewaystowellbeing;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.joshuarichardson.fivewaystowellbeing.analytics.LogAnalyticEventHelper;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseElementDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.QuestionsToAsk;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyQuestionSet;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseElement;
import com.joshuarichardson.fivewaystowellbeing.surveys.DropDownListOptionWrapper;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyBuilder;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import dagger.hilt.android.AndroidEntryPoint;

import static com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes.BASIC_SURVEY;

@AndroidEntryPoint
public class AnswerSurveyActivity extends AppCompatActivity {

    private SurveyResponseDao surveyResponseDao;
    private SurveyResponseElementDao surveyResponseElementDao;

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


        ArrayList<String> listItems = new ArrayList<>();
        listItems.add("Happy");
        listItems.add("Moderate");
        listItems.add("Sad");

        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
            List<ActivityRecord> activities = this.db.activityRecordDao().getAllActivitiesNotLive();

            // ToDo - work out how to get the stuff in here
            // This gets all unanswered question sets - these have IDs - these Ids are the set ids used to get the question sets
            LiveData<List<SurveyQuestionSet>> questionSets = this.db.surveyQuestionSetDao().getUnansweredSurveyQuestionSets();

            List<SurveyQuestionSet> something = questionSets.getValue();

            long setId = 0;
            if(something != null) {
                // This gets the id from the data
                // ToDo - if there are multiple surveys - probably want to have a way to deal with that
                setId = something.get(0).getId();
            }

            // This is a list of questions - should all be from unanswered surveys
            LiveData<List<QuestionsToAsk>> questions = this.db.questionsToAskDao().getQuestionsBySetId(setId);

            List<QuestionsToAsk> questionList = new ArrayList<>();

            if(questions != null && questions.getValue() != null) {
                questionList = questions.getValue();
            }

            DropDownListOptionWrapper data = new DropDownListOptionWrapper(Arrays.asList("1", "2", "3"));
            Gson gson = new Gson();

//            questionList = Arrays.asList(
//                new QuestionsToAsk("Question 1", "Reason 1", 1, BASIC_SURVEY.name(), 0, gson.toJson(data)),
//                new QuestionsToAsk("Question 2", "Reason 2", 1, DROP_DOWN_LIST.name(), 0, gson.toJson(data))
//            );



            // ToDo - the survey builder should use this list instead of building a question item - that just makes no sense - instead - pass in this list - use that instead of the previous item
            // ToDo - edit some tests before undertaking this work so that I know that it does infact work

//            for(QuestionsToAsk question : questionList) {
//                question.getQuestion();
//                question.getReason();
//                question.getType();
//                question.getExtraData();
//            }

            // ToDo remove old

            ArrayList<String> apps = new ArrayList<>();
            if (activities != null) {
                for (ActivityRecord activity : activities) {
                    apps.add(activity.getActivityName());
                }
            }
            // ToDo - need to implement the table to save this to
            // ToDo - need a way to generate the questions to ask
//            SurveyQuestion title = new QuestionBuilder()
//                .withQuestionText("Add a title")
//                .withType(TEXT)
//                .build();
//
//            SurveyQuestion description = new QuestionBuilder()
//                .withQuestionText("Set a description")
//                .withType(TEXT)
//                .build();
//
//            SurveyQuestion firstQuestion = new QuestionBuilder()
//                .withQuestionText("What activity have you been doing?")
//                .withOptionsList(apps)
//                .withType(DROP_DOWN_LIST)
//                .build();
//
//            SurveyQuestion secondQuestion = new QuestionBuilder()
//                .withQuestionText("How are you feeling?")
//                .withOptionsList(listItems)
//                .withType(DROP_DOWN_LIST)
//                .build();

            List<QuestionsToAsk> finalQuestionList = questionList;

            runOnUiThread(() -> {
                LinearLayout surveyBuilder = new SurveyBuilder(AnswerSurveyActivity.this)
                        .withBasicSurvey(apps)
                        // ToDo - need to re-implement this when the database is ready
                        .withQuestions(finalQuestionList)
//                        .withQuestion(description)
//                        .withQuestion(firstQuestion)
//                        .withQuestion(secondQuestion)
                        .build();

                ScrollView view = findViewById(R.id.survey_items_scroll_view);
                view.removeAllViews();
                view.addView(surveyBuilder);
            });


        });
    }

    Date now = new Date();

    // ToDo - what to do about this if there is no basic survey?
    SurveyResponse surveyResponse = new SurveyResponse((int)now.getTime(), WaysToWellbeing.CONNECT, "", "");

    public void onSubmit(View v) {
        // Log the survey completion
        analyticsHelper.logCreateSurveyEvent(this);

        // Get the layout that contains all of the survey items
        ScrollView scrollView = findViewById(R.id.survey_items_scroll_view);
        LinearLayout layout = (LinearLayout) scrollView.getChildAt(0);

        ArrayList<String> questionTitles = new ArrayList<>();
        ArrayList<String> questionAnswers = new ArrayList<>();

        // Process each item
        for(int i = 0; i < layout.getChildCount(); i++) {
            // Get the question item
            View child = layout.getChildAt(i);

            // Get the question type
            SurveyItemTypes questionType = (SurveyItemTypes) child.getTag();

            if(questionType == BASIC_SURVEY) {
                EditText titleView = child.findViewById(R.id.survey_title_input);
                EditText descriptionView = child.findViewById(R.id.survey_description_input);

                // ToDo - do something with the activity
                AutoCompleteTextView activityView = child.findViewById(R.id.survey_activity_input);

                surveyResponse.setTitle(titleView.getText().toString());
                surveyResponse.setDescription(descriptionView.getText().toString());
                break;
            }


            // ToDo add a way to set specific elements with ids - these should always be added first - all other questions are generated - makes sense
            // The first item in the survey creation could have 4 things
                // Title
                // Description
                // Activity
                // Date time picker
            // The second item could be about mood - then that can be saved elsewhere in the database
            // All following items are generated numerically - question 0 -> question max -> only these are saved in the survey_response_element

            // Build the question and answer objects then push them all to the database at once - inserts allow lists
                // Question
                // Answer
                // Id - this can be an auto increment
                // FK - Survey_id -> clicking expand can do a get all where survey_id = the id of that survey

            // Process the question types appropriately to get the responses


            String questionAnswer = "";

            switch(questionType) {
                // ToDo add all of the questions to database with the survey ID as a foreign key
                // ToDo Survey sequence order would be useful to remember for reconstructing
                // ToDo Survey
                case DROP_DOWN_LIST:
                    AutoCompleteTextView dropDown = child.findViewById(R.id.drop_down_input);
                    questionAnswer = dropDown.getText().toString();
                    Log.d("Selected item", String.valueOf(dropDown.getText()));
                    break;
                case TEXT:
                    EditText inputText = child.findViewById(R.id.text_input);
                    questionAnswer = inputText.getText().toString();
                    Log.d("Selected item", String.valueOf(inputText.getText()));
                    break;
                default:
                   break;
            }

            // Get the question title
            TextView questionTitleView = child.findViewById(R.id.question_title);
            questionTitles.add(questionTitleView.getText().toString());
            questionAnswers.add(questionAnswer);
        }

        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
            long surveyId = this.surveyResponseDao.insert(surveyResponse);

            Log.d("Length", String.valueOf(questionAnswers.size()));

            for(int i = 0; i < questionAnswers.size(); i++) {
                SurveyResponseElement surveyResponseElement = new SurveyResponseElement(surveyId, questionTitles.get(i), questionAnswers.get(i));
                Log.d("Survey Id", String.valueOf(surveyId));
                long elementId = this.surveyResponseElementDao.insert(surveyResponseElement);
                Log.d("Element Id", String.valueOf(elementId));
            }

            finish();
        });
    }
}