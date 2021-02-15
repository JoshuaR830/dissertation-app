package com.joshuarichardson.fivewaystowellbeing.ui.individual_surveys;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joshuarichardson.fivewaystowellbeing.ActivityTypeImageHelper;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.TimeHelper;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.surveys.Passtime;
import com.joshuarichardson.fivewaystowellbeing.surveys.Question;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyDay;

import java.util.Date;

public class ActivityViewHelper {
    public static void displaySurveyItems(Activity activity, SurveyDay surveyData, WellbeingDatabase db) {
        if(surveyData == null) {
            return;
        }

        LinearLayout layout = activity.findViewById(R.id.survey_item_container);
        for(long key : surveyData.getPasstimeMap().keySet()) {
            Passtime passtime = surveyData.getPasstimeMap().get(key);
            if(passtime == null) {
                continue;
            }

            createPasstimeItem(activity, layout, passtime, db);
        }

        // This only needs to run once, after everything else
        activity.runOnUiThread(() -> {
            View todaySurveyContainer = activity.findViewById(R.id.survey_summary_item_container);
            TextView surveyNote = todaySurveyContainer.findViewById(R.id.survey_list_description);

            long surveyTime = TimeHelper.getStartOfDay(surveyData.getTimestamp());
            long currentTime = TimeHelper.getStartOfDay(new Date().getTime());

            if (surveyTime != currentTime && surveyTime >= 0) {
                TextView surveyTitle = todaySurveyContainer.findViewById(R.id.survey_list_title);
                surveyTitle.setText(surveyData.getTitle());
            }

            surveyNote.setText(surveyData.getNote());
        });
    }

    public static void createPasstimeItem(Activity activity, LinearLayout layout, Passtime passtime, WellbeingDatabase db) {
        // Get the passtime template
        View view = LayoutInflater.from(activity).inflate(R.layout.pass_time_item, layout, false);
        TextView title = view.findViewById(R.id.activity_text);
        TextView note = view.findViewById(R.id.activity_note_text);
        ImageView image = view.findViewById(R.id.activity_image);

        ImageButton expandButton = view.findViewById(R.id.expand_options_button);
        View checkboxView = view.findViewById(R.id.pass_time_checkbox_container);

        // Start to populate the template
        activity.runOnUiThread(() -> {
            LinearLayout checkboxContainer = checkboxView.findViewById(R.id.check_box_container);

            for (Question question : passtime.getQuestions()) {
                CheckBox checkBox = (CheckBox) LayoutInflater.from(activity).inflate(R.layout.item_check_box, checkboxContainer, false);
                checkBox.setChecked(question.getUserResponse());
                checkBox.setText(question.getQuestion());
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                        db.wellbeingRecordDao().checkItem(question.getWellbeingRecordId(), isChecked);
                    });
                });
                checkboxContainer.addView(checkBox);
            }

            expandButton.setOnClickListener(v -> {
                if (checkboxView.getVisibility() == View.GONE) {
                    checkboxView.setVisibility(View.VISIBLE);
                    checkboxView.getVisibility();
                    expandButton.setImageResource(R.drawable.button_collapse);
                } else {
                    checkboxView.setVisibility(View.GONE);
                    expandButton.setImageResource(R.drawable.button_expand);
                }
            });

            if (passtime.getQuestions().size() == 0) {
                expandButton.setVisibility(View.GONE);
            }

            title.setText(passtime.getName());

            if (passtime.getNote() == null || passtime.getNote().length() == 0) {
                note.setVisibility(View.GONE);
            } else {
                note.setText(passtime.getNote());
                note.setVisibility(View.VISIBLE);
            }

            image.setImageResource(ActivityTypeImageHelper.getActivityImage(passtime.getType()));

            layout.addView(view);
        });
    }
}
