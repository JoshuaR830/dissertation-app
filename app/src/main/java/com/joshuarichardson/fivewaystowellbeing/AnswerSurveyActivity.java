package com.joshuarichardson.fivewaystowellbeing;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;

import androidx.appcompat.app.AppCompatActivity;

public class AnswerSurveyActivity extends AppCompatActivity {

    private SurveyResponseDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_survey);

        WellbeingDatabase db = WellbeingDatabase.getWellbeingDatabase(AnswerSurveyActivity.this);
        this.dao = db.surveyResponseDao();
    }

    public void onSubmit(View v) {
        EditText answerInputBox1 = findViewById(R.id.answerInput1);
        EditText answerInputBox2 = findViewById(R.id.answerInput2);
        EditText answerInputBox3 = findViewById(R.id.answerInput3);

        String answer1 = answerInputBox1.getText().toString();
        String answer2 = answerInputBox2.getText().toString();
        String answer3 = answerInputBox3.getText().toString();

        WellbeingDatabase.databaseWriteExecutor.execute(() -> {
            this.dao.insert(new SurveyResponse(478653, WaysToWellbeing.BE_ACTIVE));
            this.dao.insert(new SurveyResponse(478657, WaysToWellbeing.GIVE));
            this.dao.insert(new SurveyResponse(478656, WaysToWellbeing.CONNECT));
            finish();
        });

        // ToDo some database stuff here - but need the database first which is on another branch
    }
}