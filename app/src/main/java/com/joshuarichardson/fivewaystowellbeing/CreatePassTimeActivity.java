package com.joshuarichardson.fivewaystowellbeing;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;

import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

public class CreatePassTimeActivity extends AppCompatActivity {


    ActivityRecordDao passTimeDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pass_time);

        WellbeingDatabase db = WellbeingDatabase.getWellbeingDatabase(CreatePassTimeActivity.this);
        this.passTimeDao = db.activityRecordDao();
    }

    public void onSubmit(View v) {
        EditText passTimeName = findViewById(R.id.passTimeAnswerInput1);
        EditText passTimeDuration = findViewById(R.id.passTimeAnswerInput2);
        EditText passTimeType = findViewById(R.id.passTimeAnswerInput3);

        String name = passTimeName.getText().toString();
        int duration = Integer.parseInt(passTimeDuration.getText().toString());
        String type = passTimeType.getText().toString();

        Date currentTime = new Date();
        long unixTime = currentTime.getTime();

        WellbeingDatabase.databaseWriteExecutor.execute(() -> {
            this.passTimeDao.insert(new ActivityRecord(name, duration, unixTime, type));
            finish();
        });
    }
}