package com.joshuarichardson.fivewaystowellbeing;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.joshuarichardson.fivewaystowellbeing.analytics.LogAnalyticEventHelper;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;

import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CreatePassTimeActivity extends AppCompatActivity {
    ActivityRecordDao passTimeDao;

    @Inject
    WellbeingDatabase db;

    @Inject
    LogAnalyticEventHelper analyticsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pass_time);

        AutoCompleteTextView dropDownInput = findViewById(R.id.pass_time_type_input);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(CreatePassTimeActivity.this, R.layout.item_list_text, DropDownHelper.getEnumStrings(ActivityType.values()));
        dropDownInput.setAdapter(adapter);

        this.passTimeDao = this.db.activityRecordDao();
    }

    public void onTimerButtonClicked(View view) {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(0)
            .setMinute(10)
            .setTitleText(R.string.passtime_duration)
            .build();

        timePicker.addOnPositiveButtonClickListener(v -> {
            int minutes = timePicker.getMinute();
            minutes += timePicker.getHour() * 60;

            long epoch = minutes*60*1000;
            TextView durationText = findViewById(R.id.pass_time_duration_input);
            TextView timeText = findViewById(R.id.time_text_view);
            durationText.setText(String.valueOf(epoch));
            timeText.setText(String.format(Locale.getDefault(), "%d%s : %d%s", timePicker.getHour(), getString(R.string.hours), timePicker.getMinute(), getString(R.string.minutes)));
        });

        timePicker.show(getSupportFragmentManager(), "time");
    }

    public void onSubmit(View v) {
        // Log the interaction
        this.analyticsHelper.logCreatePasstimeEvent(this);

        EditText passTimeName = findViewById(R.id.pass_time_name_input);
        EditText passTimeDuration = findViewById(R.id.pass_time_duration_input);
        EditText passTimeType = findViewById(R.id.pass_time_type_input);

        String name = passTimeName.getText().toString();
        int duration = Integer.parseInt(passTimeDuration.getText().toString());
        String type = passTimeType.getText().toString();

        Date currentTime = new Date();
        long unixTime = currentTime.getTime();

        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
            this.passTimeDao.insert(new ActivityRecord(name, duration, unixTime, type, WaysToWellbeing.UNASSIGNED.toString()));
            finish();
        });
    }
}