package com.joshuarichardson.fivewaystowellbeing.ui.individual_surveys;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.joshuarichardson.fivewaystowellbeing.ActivityTypeImageHelper;
import com.joshuarichardson.fivewaystowellbeing.DisplayHelper;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.TimeFormatter;
import com.joshuarichardson.fivewaystowellbeing.TimeHelper;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.WellbeingHelper;
import com.joshuarichardson.fivewaystowellbeing.analytics.LogAnalyticEventHelper;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingQuestion;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingRecord;
import com.joshuarichardson.fivewaystowellbeing.surveys.Passtime;
import com.joshuarichardson.fivewaystowellbeing.surveys.Question;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyDay;
import com.joshuarichardson.fivewaystowellbeing.ui.insights.WayToWellbeingImageColorizer;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import static java.lang.Math.min;

public class ActivityViewHelper {
    public static void displaySurveyItems(Activity activity, SurveyDay surveyData, WellbeingDatabase db, FragmentManager fragmentManager, LogAnalyticEventHelper analyticsHelper) {
        if(surveyData == null) {
            return;
        }

        LinearLayout layout = activity.findViewById(R.id.survey_item_container);
        activity.runOnUiThread(layout::removeAllViews);

        for(long key : surveyData.getPasstimeMap().keySet()) {
            Passtime passtime = surveyData.getPasstimeMap().get(key);
            if(passtime == null) {
                continue;
            }

            createPasstimeItem(activity, layout, passtime, db, fragmentManager, analyticsHelper, false);
        }

        // This only needs to run once, after everything else
        activity.runOnUiThread(() -> {
            View todaySurveyContainer = activity.findViewById(R.id.survey_summary_item_container);

            // If the container tries to populate after navigating away it would be null
            if(todaySurveyContainer == null) {
                return;
            }

            TextView surveyNote = todaySurveyContainer.findViewById(R.id.survey_list_description);

            long surveyTime = TimeHelper.getStartOfDay(surveyData.getTimestamp());
            long currentTime = TimeHelper.getStartOfDay(new Date().getTime());

            if (surveyTime != currentTime && surveyTime >= 0) {
                TextView surveyTitle = todaySurveyContainer.findViewById(R.id.survey_list_title);
                surveyTitle.setText(surveyData.getTitle());
            }

            if(surveyData.getNote().length() > 0) {
                surveyNote.setText(surveyData.getNote());
            }
        });
    }

    public static void createPasstimeItem(Activity activity, LinearLayout layout, Passtime passtime, WellbeingDatabase db, FragmentManager fragmentManager, LogAnalyticEventHelper analyticsHelper, boolean isAddedIn) {
        // Get the passtime template
        View view = LayoutInflater.from(activity).inflate(R.layout.pass_time_item, layout, false);
        TextView title = view.findViewById(R.id.activity_text);
        TextView note = view.findViewById(R.id.activity_note_text);
        ImageView image = view.findViewById(R.id.activity_image);
        FrameLayout imageFrame = view.findViewById(R.id.activity_image_frame);

        GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(activity, R.drawable.frame_circle);
        if (drawable != null) {
            drawable = (GradientDrawable) drawable.mutate();
            drawable.setStroke(DisplayHelper.dpToPx(activity, 4), WellbeingHelper.getColor(activity, passtime.getWayToWellbeing()));
            imageFrame.setBackground(drawable);
        }

        View topLevelDetails = view.findViewById(R.id.activity_top_level_details);
        ImageButton expandButton = view.findViewById(R.id.expand_options_button);
        MaterialButton deleteButton = view.findViewById(R.id.delete_options_button);
        View checkboxView = view.findViewById(R.id.pass_time_checkbox_container);

        view.setTag(passtime.getWayToWellbeing());

        MaterialButton addNoteButton = view.findViewById(R.id.add_note_button);
        TextInputLayout noteTextInputContainer = view.findViewById(R.id.note_input_container);

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

        List<Integer> colorList = Arrays.asList(R.color.translucent_sentiment_worst, R.color.translucent_sentiment_bad, R.color.translucent_sentiment_neutral, R.color.translucent_sentiment_good, R.color.translucent_sentiment_best);

        LinearLayout emotionsContainer = view.findViewById(R.id.emotions_container);

        // Set all to transparent so it doesn't end up the colour of the last background
        removeSelection(view, activity);

        if (passtime.getEmotion() != 0) {
            // Remember to -1 because index is the value - 1
            int index = passtime.getEmotion() - 1;
            View sentimentItem = emotionsContainer.getChildAt(index);
            sentimentItem.getBackground().setTint(activity.getColor(colorList.get(index)));
        }

        for (int i = 0; i < emotionsContainer.getChildCount(); i++) {
            View sentimentItem = emotionsContainer.getChildAt(i);
            final int index = i;
            sentimentItem.setOnClickListener(v -> {
                removeSelection(view, activity);
                WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                    db.surveyResponseActivityRecordDao().updateEmotion(passtime.getActivitySurveyId(), index + 1);
                });
                sentimentItem.getBackground().setTint(activity.getColor(colorList.get(index)));
            });
        }

        MaterialButton doneButton = view.findViewById(R.id.done_button);
        EditText noteTextInput = view.findViewById(R.id.note_input);

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
                            noteTextInput.setText("");
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

            View.OnClickListener expandClickListener = (v -> {
                if (checkboxView.getVisibility() == View.GONE) {
                    checkboxView.setVisibility(View.VISIBLE);
                    expandButton.setImageResource(R.drawable.button_collapse);
                } else {
                    String noteText = noteTextInput.getText().toString();
                    if(!noteText.equals("")) {
                        note.setText(noteText);
                        note.setVisibility(View.VISIBLE);

                        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                            db.surveyResponseActivityRecordDao().updateNote(passtime.getActivitySurveyId(), noteText);
                            noteTextInputContainer.setHelperText(activity.getText(R.string.helper_saved_note));
                        });
                    }

                    // Remember that the activity details have been filled in
                    WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                        db.surveyResponseActivityRecordDao().updateIsDone(passtime.getActivitySurveyId(), true);
                    });

                    checkboxView.setVisibility(View.GONE);
                    expandButton.setImageResource(R.drawable.button_expand);
                }
            });

            expandButton.setOnClickListener(expandClickListener);
            doneButton.setOnClickListener(expandClickListener);

            // Hide the checkboxes if they have been hidden before
            if(passtime.getIsDone()) {
                checkboxView.setVisibility(View.GONE);
                expandButton.setImageResource(R.drawable.button_expand);
            } else {
                expandButton.setImageResource(R.drawable.button_collapse);
            }

            if (passtime.getQuestions().size() == 0) {
                expandButton.setVisibility(View.GONE);
            } else {
                topLevelDetails.setOnClickListener(expandClickListener);
            }

            deleteButton.setVisibility(View.GONE);
            deleteButton.setOnClickListener((v) -> {
                new MaterialAlertDialogBuilder(activity)
                    .setTitle(R.string.title_delete_activity)
                    .setMessage(R.string.body_delete_from_survey)
                    .setIcon(R.drawable.icon_close)
                    .setPositiveButton(R.string.button_delete, (dialog, which) -> {
                        // Delete the pass time from the survey
                        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                            db.surveyResponseActivityRecordDao().deleteById(passtime.getActivitySurveyId());
                        });
                        layout.removeView(view);
                    })
                    .setNegativeButton(R.string.button_cancel, (dialog, which) -> {})
                    .create()
                    .show();
            });

            // Display details
            noteTextInput.setText(passtime.getNote());
            TextView timeText = view.findViewById(R.id.activity_time_text);
            displayTimes(timeText, passtime.getStartTime(), passtime.getEndTime());

            title.setText(passtime.getName());

            if (passtime.getNote() == null || passtime.getNote().length() == 0) {
                note.setVisibility(View.GONE);
            } else {
                note.setText(passtime.getNote());
                note.setVisibility(View.VISIBLE);
            }

            image.setImageResource(ActivityTypeImageHelper.getActivityImage(passtime.getType()));

            layout.addView(view);

            if(isAddedIn) {
                ScrollView scrollView = activity.findViewById(R.id.progress_scroll_view);
                // Reference: https://stackoverflow.com/a/6831790/13496270
                scrollView.post(() -> {
                   scrollView.smoothScrollTo(0, view.getBottom());
                });
            }
        });
    }

    public static void createInsightCards(LinearLayout helpContainer, WaysToWellbeing wayToWellbeing, long startTime, long endTime, Context context, LifecycleOwner activity, WellbeingDatabase db) {

        View goodInsights = LayoutInflater.from(context).inflate(R.layout.progress_insight_card, null);
        View suggestionInsights = LayoutInflater.from(context).inflate(R.layout.progress_insight_card, null);

        ImageView goodImage = goodInsights.findViewById(R.id.wellbeing_type_image);
        WayToWellbeingImageColorizer.colorize(context, goodImage, wayToWellbeing);

        ImageView suggestionImage = suggestionInsights.findViewById(R.id.wellbeing_type_image);
        WayToWellbeingImageColorizer.colorize(context, suggestionImage, wayToWellbeing);

        TextView goodTitle = goodInsights.findViewById(R.id.progress_insight_title);
        goodTitle.setText(R.string.suggestions_insight_title_positive);

        TextView suggestionTitle = suggestionInsights.findViewById(R.id.progress_insight_title);
        suggestionTitle.setText(R.string.suggestions_insight_title_negative);

        LinearLayout goodItems = goodInsights.findViewById(R.id.item_container);
        LinearLayout suggestionItems = suggestionInsights.findViewById(R.id.item_container);

        Observer<List<WellbeingQuestion>> goodObserver = wellbeingQuestions -> {
            goodItems.removeAllViews();

            Collections.shuffle(wellbeingQuestions);

            boolean shouldDisplay = false;
            int counter = 3;
            for(int i = 0; i < min(counter, wellbeingQuestions.size()); i ++) {
                if(wellbeingQuestions.get(i).getPositiveMessage().length() == 0) {
                    counter ++;
                    continue;
                }
                TextView question = new TextView(context);
                question.setText(wellbeingQuestions.get(i).getPositiveMessage());
                question.setPadding(0, 16, 0, 0);
                goodItems.addView(question);
                shouldDisplay = true;
            }

            if(wellbeingQuestions.size() == 0 || !shouldDisplay) {
                goodInsights.setVisibility(View.GONE);
                return;
            } else {
                goodInsights.setVisibility(View.VISIBLE);
            }
        };

        Observer<List<WellbeingQuestion>> suggestionObserver = wellbeingQuestions -> {
            suggestionItems.removeAllViews();

            Collections.shuffle(wellbeingQuestions);

            boolean shouldDisplay = false;
            int counter = 3;
            for(int i = 0; i < min(counter, wellbeingQuestions.size()); i ++) {
                if(wellbeingQuestions.get(i).getNegativeMessage().length() == 0) {
                    counter ++;
                    continue;
                }
                TextView question = new TextView(context);
                question.setText(wellbeingQuestions.get(i).getNegativeMessage());
                question.setPadding(0, 16, 0, 0);
                suggestionItems.addView(question);
                shouldDisplay = true;
            }

            if(wellbeingQuestions.size() == 0 || !shouldDisplay) {
                suggestionInsights.setVisibility(View.GONE);
                return;
            } else {
                suggestionInsights.setVisibility(View.VISIBLE);
            }
        };

        db.wellbeingRecordDao().getTrueWellbeingRecordsByTimestampRange(startTime, endTime, wayToWellbeing.toString()).observe(activity, goodObserver);
        db.wellbeingRecordDao().getFalseWellbeingRecordsByTimestampRange(startTime, endTime, wayToWellbeing.toString()).observe(activity, suggestionObserver);

        helpContainer.addView(goodInsights);
        helpContainer.addView(suggestionInsights);
    }

    private static void removeSelection(View view, Context context) {
        FrameLayout layoutWorst = view.findViewById(R.id.sentiment_worst_frame);
        FrameLayout layoutBad = view.findViewById(R.id.sentiment_bad_frame);
        FrameLayout layoutNeutral = view.findViewById(R.id.sentiment_neutral_frame);
        FrameLayout layoutGood = view.findViewById(R.id.sentiment_good_frame);
        FrameLayout layoutBest = view.findViewById(R.id.sentiment_best_frame);

        layoutWorst.getBackground().setTint(context.getColor(android.R.color.transparent));
        layoutBad.getBackground().setTint(context.getColor(android.R.color.transparent));
        layoutNeutral.getBackground().setTint(context.getColor(android.R.color.transparent));
        layoutGood.getBackground().setTint(context.getColor(android.R.color.transparent));
        layoutBest.getBackground().setTint(context.getColor(android.R.color.transparent));
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
