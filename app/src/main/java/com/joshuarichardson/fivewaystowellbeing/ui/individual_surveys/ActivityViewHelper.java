package com.joshuarichardson.fivewaystowellbeing.ui.individual_surveys;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.joshuarichardson.fivewaystowellbeing.ActivityTypeImageHelper;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.TimeFormatter;
import com.joshuarichardson.fivewaystowellbeing.TimeHelper;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.analytics.LogAnalyticEventHelper;
import com.joshuarichardson.fivewaystowellbeing.analytics.LogAnalyticEventHelper;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingQuestion;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingRecord;
import com.joshuarichardson.fivewaystowellbeing.surveys.Passtime;
import com.joshuarichardson.fivewaystowellbeing.surveys.Question;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyDay;

import java.util.Date;

import androidx.fragment.app.FragmentManager;

public class ActivityViewHelper {
    public static void displaySurveyItems(Activity activity, SurveyDay surveyData, WellbeingDatabase db, FragmentManager fragmentManager, LogAnalyticEventHelper analyticsHelper) {
        if(surveyData == null) {
            return;
        }

        LinearLayout layout = activity.findViewById(R.id.survey_item_container);
        for(long key : surveyData.getPasstimeMap().keySet()) {
            Passtime passtime = surveyData.getPasstimeMap().get(key);
            if(passtime == null) {
                continue;
            }

            createPasstimeItem(activity, layout, passtime, db, fragmentManager, analyticsHelper);
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

    public static void createPasstimeItem(Activity activity, LinearLayout layout, Passtime passtime, WellbeingDatabase db, FragmentManager fragmentManager, LogAnalyticEventHelper analyticsHelper) {
        // Get the passtime template
        View view = LayoutInflater.from(activity).inflate(R.layout.pass_time_item, layout, false);
        TextView title = view.findViewById(R.id.activity_text);
        TextView note = view.findViewById(R.id.activity_note_text);
        ImageView image = view.findViewById(R.id.activity_image);

        ImageButton expandButton = view.findViewById(R.id.expand_options_button);
        View checkboxView = view.findViewById(R.id.pass_time_checkbox_container);

        view.setTag(passtime.getWayToWellbeing());

        MaterialButton addNoteButton = view.findViewById(R.id.add_note_button);
        TextInputLayout noteTextInputContainer = view.findViewById(R.id.note_input_container);
        addNoteButton.setOnClickListener(v -> {
            if(noteTextInputContainer.getVisibility() == View.GONE) {
                noteTextInputContainer.setVisibility(View.VISIBLE);
                addNoteButton.setIconResource(R.drawable.icon_close);
            } else {
                new MaterialAlertDialogBuilder(activity)
                    .setTitle(R.string.title_delete_note)
                    .setMessage(R.string.body_delete_note)
                    .setIcon(R.drawable.icon_close)
                    .setPositiveButton(R.string.button_delete, (dialog, which) -> {
                        note.setText("");
                        note.setVisibility(View.GONE);
                        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                            db.surveyResponseActivityRecordDao().updateNote(passtime.getActivitySurveyId(), "");
                            noteTextInputContainer.setHelperText(activity.getText(R.string.helper_saved_note));
                        });

                        noteTextInputContainer.setVisibility(View.GONE);
                        addNoteButton.setIconResource(R.drawable.icon_add);
                    })
                    .setNegativeButton(R.string.button_cancel, (dialog, which) -> {})
                    .show();
            }
        });

        MaterialButton startTimeButton = view.findViewById(R.id.add_start_time);
        MaterialButton endTimeButton = view.findViewById(R.id.add_end_time);

        startTimeButton.setOnClickListener(v -> {
            long myHours = passtime.getStartTime() / 3600000;
            long myMinutes = (passtime.getStartTime() - (myHours * 3600000)) / 60000;

            MaterialTimePicker startTimePicker = new MaterialTimePicker.Builder()
                .setTitleText(R.string.title_start_time)
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour((int)(passtime.getStartTime() > 0 ? myHours : 12))
                .setMinute((int)(passtime.getStartTime() > 0 ? myMinutes : 0))
                .build();

            startTimePicker.show(fragmentManager, "start");

            startTimePicker.addOnPositiveButtonClickListener(time -> {
                int hour = startTimePicker.getHour();
                int minute = startTimePicker.getMinute();

                long startTimeMillis = (hour * 3600000) + (minute * 60000);

                passtime.setStartTime(startTimeMillis);
                WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                    db.surveyResponseActivityRecordDao().updateStartTime(passtime.getActivitySurveyId(), startTimeMillis);
                });
                activity.runOnUiThread(() -> {
                    TextView timeText = view.findViewById(R.id.activity_time_text);
                    displayTimes(timeText, passtime.getStartTime(), passtime.getEndTime());
                });
            });
        });

        endTimeButton.setOnClickListener(v -> {

            long myHours = passtime.getEndTime() / 3600000;
            long myMinutes = (passtime.getEndTime() - (myHours * 3600000)) / 60000;

            MaterialTimePicker endTimePicker = new MaterialTimePicker.Builder()
                .setTitleText(R.string.title_end_time)
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour((int)(passtime.getEndTime() > 0 ? myHours : 12))
                .setMinute((int)(passtime.getEndTime() > 0 ? myMinutes : 0))
                .build();

            endTimePicker.show(fragmentManager, "end");

            endTimePicker.addOnPositiveButtonClickListener(time -> {
                int hour = endTimePicker.getHour();
                int minute = endTimePicker.getMinute();
                long endTimeMillis = (hour * 3600000) + (minute * 60000);
                passtime.setEndTime(endTimeMillis);
                WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                    db.surveyResponseActivityRecordDao().updateEndTime(passtime.getActivitySurveyId(), endTimeMillis);
                });
                activity.runOnUiThread(() -> {
                    TextView timeText = view.findViewById(R.id.activity_time_text);
                    displayTimes(timeText, passtime.getStartTime(), passtime.getEndTime());
                });
            });
        });


        MaterialButton saveNoteButton = view.findViewById(R.id.save_note_button);
        EditText noteTextInput = view.findViewById(R.id.note_input);

        noteTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                noteTextInputContainer.setHelperText(activity.getText(R.string.helper_unsaved_note));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        saveNoteButton.setOnClickListener(v -> {
            String noteText = noteTextInput.getText().toString();
            if(noteText.equals("")) {
                return;
            }

            note.setText(noteText);
            note.setVisibility(View.VISIBLE);

            WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                db.surveyResponseActivityRecordDao().updateNote(passtime.getActivitySurveyId(), noteText);
                noteTextInputContainer.setHelperText(activity.getText(R.string.helper_saved_note));
            });
        });

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
                        WellbeingRecord wellbeingRecord = db.wellbeingRecordDao().getWellbeingRecordById(question.getWellbeingRecordId());
                        WellbeingQuestion wellbeingQuestion = db.wellbeingQuestionDao().getQuestionById(wellbeingRecord.getQuestionId());
                        analyticsHelper.logWayToWellbeingChecked(activity, WaysToWellbeing.valueOf(wellbeingQuestion.getWayToWellbeing()), isChecked);
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

            // Display details
            noteTextInput.setText(passtime.getNote());
            TextView timeText = view.findViewById(R.id.activity_time_text);
            displayTimes(timeText, passtime.getStartTime(), passtime.getEndTime());

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

    private static void displayTimes(TextView timeText, long startTimeMillis, long endTimeMillis) {

        String startTime = TimeFormatter.formatTimeAsHourMinuteString(startTimeMillis);
        String endTime = TimeFormatter.formatTimeAsHourMinuteString(endTimeMillis);

        if(startTime == null && endTime == null) {
            timeText.setText("");
        }
        else if(startTime != null && endTime == null) {
            timeText.setText(String.format("%s", startTime));
        } else if(startTime == null) {
            timeText.setText(String.format("%s", endTime));
        } else {
            timeText.setText(String.format("%s - %s", startTime, endTime));
        }
    }
}
