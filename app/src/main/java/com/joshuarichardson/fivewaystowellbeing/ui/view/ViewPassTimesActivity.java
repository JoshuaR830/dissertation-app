package com.joshuarichardson.fivewaystowellbeing.ui.view;

import android.os.Bundle;

import com.joshuarichardson.fivewaystowellbeing.PassTimesAdapter;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;

import java.util.List;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ViewPassTimesActivity extends AppCompatActivity {

    private RecyclerView passTimeRecycler;

    @Inject
    WellbeingDatabase db;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_pass_times);
        ActivityRecordDao passTimeDao = this.db.activityRecordDao();

        LiveData<List<ActivityRecord>> passTimes = passTimeDao.getAllActivities();

        this.passTimeRecycler = findViewById(R.id.passTimeRecyclerView);
        this.passTimeRecycler.setLayoutManager(new LinearLayoutManager(this));

        Observer<List<ActivityRecord>> passTimeObserver = passTimeData -> {
            PassTimesAdapter passTimeAdapter = new PassTimesAdapter(this, passTimeData);
            this.passTimeRecycler.setAdapter(passTimeAdapter);
        };

        passTimes.observe(this, passTimeObserver);
    }
}
