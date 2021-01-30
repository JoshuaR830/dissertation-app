package com.joshuarichardson.fivewaystowellbeing.ui.individual_surveys;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joshuarichardson.fivewaystowellbeing.ActivityTypeImageHelper;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.WellbeingHelper;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseElement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class IndividualSurveyActivity extends AppCompatActivity {

    @Inject
    public WellbeingDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invividual_survey);

        Intent surveyIntent = getIntent();
        if(surveyIntent == null || surveyIntent.getExtras() == null) {
            finish();
            return;
        }

        long surveyId = surveyIntent.getExtras().getLong("survey_id", -1);

        if(surveyId < 0) {
            finish();
            return;
        }

        RecyclerView recycler = findViewById(R.id.survey_summary_recycler_view);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        Observer<List<SurveyResponseElement>> observer = list -> {
            // This hides the recycler view to stop it from scrolling when there is no content
            if(list.size() == 0) {
                recycler.setVisibility(View.INVISIBLE);
            } else {
                recycler.setVisibility(View.VISIBLE);
            }
            recycler.setAdapter(new IndividualSurveyAdapter(this, list));
        };

        LiveData<List<SurveyResponseElement>> data = this.db.surveyResponseElementDao().getSurveyResponseElementBySurveyResponseId(surveyId);
        data.observe(this, observer);


        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
            SurveyResponse surveyResponse = this.db.surveyResponseDao().getSurveyResponseById(surveyId);
            List<ActivityRecord> activities = this.db.surveyResponseActivityRecordDao().getActivitiesBySurveyId(surveyId);

            runOnUiThread(() -> {

                TextView title = findViewById(R.id.individual_survey_title);
                TextView description = findViewById(R.id.individual_survey_description);
                TextView date = findViewById(R.id.individual_survey_time);
                ImageView image = findViewById(R.id.individual_survey_image);

                title.setText(surveyResponse.getTitle());
                description.setText(surveyResponse.getDescription());


                // Catch the exception if the user does not set a value
                WaysToWellbeing way;
                try {
                    way = WaysToWellbeing.valueOf(surveyResponse.getSurveyResponseWayToWellbeing());
                } catch(IllegalArgumentException e) {
                    way = WaysToWellbeing.UNASSIGNED;
                }

                image.setImageResource(WellbeingHelper.getImage(way));

                Date dateTime = new Date(surveyResponse.getSurveyResponseTimestamp());
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
                date.setText(dateFormatter.format(dateTime));

                if(activities.size() > 0) {
                    ActivityRecord pastimeActivity = activities.get(0);
                    LinearLayout activity = findViewById(R.id.individual_survey_activity);
                    View view = LayoutInflater.from(this).inflate(R.layout.pass_time_record_list_item, activity, false);
                    ((TextView)view.findViewById(R.id.nameTextView)).setText(pastimeActivity.getActivityName());
                    Date activityTime = new Date(pastimeActivity.getActivityTimestamp());
                    // ToDo - Create a time helper
                    ((TextView)view.findViewById(R.id.survey_list_title)).setText(dateFormatter.format(activityTime));
                    ((TextView)view.findViewById(R.id.typeTextView)).setText(pastimeActivity.getActivityType());
                    // ToDo - create a time helper
                    ((TextView)view.findViewById(R.id.durationTextView)).setText(String.format(Locale.getDefault(),"%d %s", pastimeActivity.getActivityDuration() / (1000 * 60), getString(R.string.minutes)));
                    TextView wayToWellbeingTextView = view.findViewById(R.id.wayToWellbeingTextView);

                    if(pastimeActivity.getActivityWayToWellbeing().equals("UNASSIGNED")) {
                        wayToWellbeingTextView.setVisibility(View.GONE);
                    } else {
                        wayToWellbeingTextView.setText(pastimeActivity.getActivityWayToWellbeing());
                        wayToWellbeingTextView.setVisibility(View.VISIBLE);
                    }

                    ((ImageView)view.findViewById(R.id.list_item_image)).setImageResource(ActivityTypeImageHelper.getActivityImage(pastimeActivity.getActivityType()));
                    activity.addView(view);
                }
            });
        });
    }
}