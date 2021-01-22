package com.joshuarichardson.fivewaystowellbeing;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.surveys.QuestionBuilder;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyBuilder;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyQuestion;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import dagger.hilt.android.AndroidEntryPoint;

import static com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes.DROP_DOWN_LIST;
import static com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes.TEXT;

@AndroidEntryPoint
public class AnswerSurveyActivity extends AppCompatActivity {

    private SurveyResponseDao surveyResponseDao;

    @Inject
    WellbeingDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_survey);

        this.surveyResponseDao = this.db.surveyResponseDao();


        ArrayList<String> listItems = new ArrayList<>();
        listItems.add("Happy");
        listItems.add("Moderate");
        listItems.add("Sad");

        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
            List<ActivityRecord> activities = this.db.activityRecordDao().getAllActivitiesNotLive();

            ArrayList<String> apps = new ArrayList<>();
            if (activities != null) {
                for (ActivityRecord activity : activities) {
                    apps.add(activity.getActivityName());
                }
            }

            SurveyQuestion title = new QuestionBuilder()
                .withQuestionText("Add a title")
                .withType(TEXT)
                .build();

            SurveyQuestion description = new QuestionBuilder()
                .withQuestionText("Set a description")
                .withType(TEXT)
                .build();

            SurveyQuestion firstQuestion = new QuestionBuilder()
                .withQuestionText("What activity have you been doing?")
                .withOptionsList(apps)
                .withType(DROP_DOWN_LIST)
                .build();

            SurveyQuestion secondQuestion = new QuestionBuilder()
                .withQuestionText("How are you feeling?")
                .withOptionsList(listItems)
                .withType(DROP_DOWN_LIST)
                .build();

            runOnUiThread(() -> {
                LinearLayout surveyBuilder = new SurveyBuilder(AnswerSurveyActivity.this)
                        .withQuestion(title)
                        .withQuestion(description)
                        .withQuestion(firstQuestion)
                        .withQuestion(secondQuestion)
                        .build();

                ScrollView view = findViewById(R.id.survey_items_scroll_view);
                view.removeAllViews();
                view.addView(surveyBuilder);
            });


        });


//        AutoCompleteTextView answerInputBox2 = findViewById(R.id.drop_down_input);

        Log.d("Success", "Yay");

//        ArrayAdapter<String> adapter = new ArrayAdapter<>(AnswerSurveyActivity.this, R.layout.item_list_text, listItems);
//        answerInputBox2.setAdapter(adapter);
        Log.d("View", String.valueOf(findViewById(0)));
    }

    public void onSubmit(View v) {
        // Get the layout that contains all of the survey items
        ScrollView scrollView = findViewById(R.id.survey_items_scroll_view);
        LinearLayout layout = (LinearLayout) scrollView.getChildAt(0);

        // Process each item
        for(int i = 0; i < layout.getChildCount(); i++) {
            // Get the question item
            View child = layout.getChildAt(i);

            // Get the question title
            TextView questionTitleView = child.findViewById(R.id.question_title);
            String questionTitle = questionTitleView.getText().toString();

            // Get the question type
            SurveyItemTypes questionType = (SurveyItemTypes) child.getTag();

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
            switch(questionType) {
                // ToDo add all of the questions to database with the survey ID as a foreign key
                // ToDo Survey sequence order would be useful to remember for reconstructing
                // ToDo Survey
                case DROP_DOWN_LIST:
                    AutoCompleteTextView dropDown = child.findViewById(R.id.drop_down_input);
                    Log.d("Selected item", String.valueOf(dropDown.getText()));
                    break;
                case TEXT:
                    EditText inputText = child.findViewById(R.id.text_input);
                    Log.d("Selected item", String.valueOf(inputText.getText()));
            }
        }



//        EditText answerInputBox1 = findViewById(0).findViewById(R.id.text_input);
//        AutoCompleteTextView answerInputBox2 = findViewById(R.id.drop_down_input);
//        EditText answerInputBox3 = findViewById(R.id.slider_input);
//
//        String answer1 = answerInputBox1.getText().toString();
//        String answer2 = answerInputBox2.getText().toString();
//        String answer3 = answerInputBox3.getText().toString();
////
//        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
//            this.surveyResponseDao.insert(new SurveyResponse(478653, WaysToWellbeing.BE_ACTIVE));
//            this.surveyResponseDao.insert(new SurveyResponse(478657, WaysToWellbeing.GIVE));
//            this.surveyResponseDao.insert(new SurveyResponse(478656, WaysToWellbeing.CONNECT));
//            finish();
//        });

        // ToDo some database stuff here - but need the database first which is on another branch
    }
}