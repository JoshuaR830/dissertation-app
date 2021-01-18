package com.joshuarichardson.fivewaystowellbeing;

import android.os.Bundle;

import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ViewSurveysActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_surveys);

        RecyclerView recycler = findViewById(R.id.surveyRecyclerView);
        recycler.setLayoutManager(new LinearLayoutManager(ViewSurveysActivity.this));

        WellbeingDatabase db = WellbeingDatabase.getWellbeingDatabase(ViewSurveysActivity.this);
        SurveyResponseDao surveyResponseDao = db.surveyResponseDao();

        Observer<List<SurveyResponse>> responseObserver = surveyResponses -> {
            SurveyResponseAdapter adapter = new SurveyResponseAdapter(ViewSurveysActivity.this, surveyResponses);
            recycler.setAdapter(adapter);
        };

        surveyResponseDao.getAllSurveyResponses().observe(ViewSurveysActivity.this, responseObserver);

    }
}