package com.joshuarichardson.fivewaystowellbeing;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class AnswerSurveyActivity extends AppCompatActivity {

    private SurveyResponseDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_survey);

        WellbeingDatabase db = WellbeingDatabase.getWellbeingDatabase(AnswerSurveyActivity.this);
        this.dao = db.surveyResponseDao();


        ArrayList<String> listItems = new ArrayList<>();
        listItems.add("Running");
        listItems.add("Jumping");
        listItems.add("Fishing");

        AutoCompleteTextView answerInputBox2 = findViewById(R.id.drop_down_input);

        Log.d("Success", "Yay");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(AnswerSurveyActivity.this, R.layout.item_list_text, listItems);
        answerInputBox2.setAdapter(adapter);
    }

    public void onSubmit(View v) {
        EditText answerInputBox1 = findViewById(R.id.text_input);
        AutoCompleteTextView answerInputBox2 = findViewById(R.id.drop_down_input);
        EditText answerInputBox3 = findViewById(R.id.slider_input);

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