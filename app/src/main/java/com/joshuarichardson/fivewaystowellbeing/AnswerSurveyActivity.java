package com.joshuarichardson.fivewaystowellbeing;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.surveys.QuestionBuilder;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyBuilder;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyQuestion;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import static com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes.DROP_DOWN_LIST;

public class AnswerSurveyActivity extends AppCompatActivity {

    private SurveyResponseDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_survey);

        WellbeingDatabase db = WellbeingDatabase.getWellbeingDatabase(getApplicationContext());
        this.dao = db.surveyResponseDao();


        ArrayList<String> listItems = new ArrayList<>();
        listItems.add("Happy");
        listItems.add("Moderate");
        listItems.add("Sad");

        WellbeingDatabase.databaseWriteExecutor.execute(() -> {
            List<ActivityRecord> activities = db.activityRecordDao().getAllActivitiesNotLive();

            ArrayList<String> apps = new ArrayList<>();
            if (activities != null) {
                for (ActivityRecord activity : activities) {
                    apps.add(activity.getActivityName());
                }
            }

            SurveyQuestion firstQuestion = new QuestionBuilder()
                .withQuestionText("How are you feeling?")
                .withOptionsList(apps)
                .withType(DROP_DOWN_LIST)
                .build();

            SurveyQuestion secondQuestion = new QuestionBuilder()
                .withQuestionText("What activity have you been doing?")
                .withOptionsList(listItems)
                .withType(DROP_DOWN_LIST)
                .build();

            runOnUiThread(() -> {
                LinearLayout surveyBuilder = new SurveyBuilder(AnswerSurveyActivity.this)
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
            SurveyItemTypes tag = (SurveyItemTypes) child.getTag();

            // Process the question types appropriately to get the responses
            switch(tag) {
                case DROP_DOWN_LIST:
                    AutoCompleteTextView dropDown = child.findViewById(R.id.drop_down_input);
                    Log.d("Selected item", String.valueOf(dropDown.getText()));
            }
        }

//        EditText answerInputBox1 = findViewById(R.id.text_input);
//        AutoCompleteTextView answerInputBox2 = findViewById(R.id.drop_down_input);
//        EditText answerInputBox3 = findViewById(R.id.slider_input);
//
//        String answer1 = answerInputBox1.getText().toString();
//        String answer2 = answerInputBox2.getText().toString();
//        String answer3 = answerInputBox3.getText().toString();
//
//        WellbeingDatabase.databaseWriteExecutor.execute(() -> {
//            this.dao.insert(new SurveyResponse(478653, WaysToWellbeing.BE_ACTIVE));
//            this.dao.insert(new SurveyResponse(478657, WaysToWellbeing.GIVE));
//            this.dao.insert(new SurveyResponse(478656, WaysToWellbeing.CONNECT));
//            finish();
//        });

        // ToDo some database stuff here - but need the database first which is on another branch
    }
}