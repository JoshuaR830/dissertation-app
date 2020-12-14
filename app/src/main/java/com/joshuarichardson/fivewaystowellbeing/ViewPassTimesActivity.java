package com.joshuarichardson.fivewaystowellbeing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ViewPassTimesActivity extends AppCompatActivity {

    private RecyclerView passTimeRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pass_times);

        WellbeingDatabase db = WellbeingDatabase.getWellbeingDatabase(ViewPassTimesActivity.this);
        ActivityRecordDao passTimeDao = db.activityRecordDao();

        LiveData<List<ActivityRecord>> passTimes = passTimeDao.getAllActivities();


        this.passTimeRecycler = findViewById(R.id.passTimeRecyclerView);
        this.passTimeRecycler.setLayoutManager(new LinearLayoutManager(ViewPassTimesActivity.this));

        Observer<List<ActivityRecord>> passTimeObserver = passTimeData -> {
            PassTimesAdapter passTimeAdapter = new PassTimesAdapter(ViewPassTimesActivity.this, passTimeData);
            this.passTimeRecycler.setAdapter(passTimeAdapter);
        };

        passTimes.observe(ViewPassTimesActivity.this, passTimeObserver);

    }

    public void onCreatePassTimeButtonClicked(View v) {
        Intent answerSurveyIntent = new Intent(ViewPassTimesActivity.this, CreatePassTimeActivity.class);
        startActivity(answerSurveyIntent);
    }
}