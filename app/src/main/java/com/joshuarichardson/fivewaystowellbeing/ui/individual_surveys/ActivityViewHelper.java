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
import com.joshuarichardson.fivewaystowellbeing.surveys.Question;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyDay;
import com.joshuarichardson.fivewaystowellbeing.surveys.ActivityInstance;
import com.joshuarichardson.fivewaystowellbeing.ui.insights.WayToWellbeingImageColorizer;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import static java.lang.Math.min;

/**
 * A class that helps to display all of the content for surveys
 * Takes survey data and uses it to populate all of the survey views.
 */
public class ActivityViewHelper {
    /**
     * Display the daily activity log to users
     *
     * @param activity The activity that contains the fragment
     * @param surveyData The data that needs to be displayed
     * @param db Access to the database
     * @param fragmentManager The fragment manager
     * @param analyticsHelper An instance of the analytics helper to be able to log events
     */
    public static void displaySurveyItems(Activity activity, SurveyDay surveyData, WellbeingDatabase db, FragmentManager fragmentManager, LogAnalyticEventHelper analyticsHelper) {
        if(surveyData == null) {
            return;
        }

        LinearLayout layout = activity.findViewById(R.id.survey_item_container);
        activity.runOnUiThread(layout::removeAllViews);

        for(long key : surveyData.getActivityMap().keySet()) {
            ActivityInstance activityInstance = surveyData.getActivityMap().get(key);
            if(activityInstance == null) {
                continue;
            }

            createActivityItem(activity, layout, activityInstance, db, fragmentManager, analyticsHelper, false);
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
            long currentTime = TimeHelper.getStartOfDay(Calendar.getInstance().getTimeInMillis());

            if (surveyTime != currentTime && surveyTime >= 0) {
                TextView surveyTitle = todaySurveyContainer.findViewById(R.id.survey_list_title);
                surveyTitle.setText(surveyData.getTitle());
            }

            if(surveyData.getNote().length() > 0) {
                surveyNote.setText(surveyData.getNote());
            }
        });
    }

    /**
     * Create the instance of the activity to populate the daily survey
     *
     * @param activity The page to display it on
     * @param layout The layout to populate
     * @param activityInstance The activity details to display
     * @param db Access to the database
     * @param fragmentManager The fragment manager
     * @param analyticsHelper The analytics helper
     * @param isAddedIn The indicator of whether the activity has just been added
     */
    public static void createActivityItem(Activity activity, LinearLayout layout, ActivityInstance activityInstance, WellbeingDatabase db, FragmentManager fragmentManager, LogAnalyticEventHelper analyticsHelper, boolean isAddedIn) {
        // Get the Activity template
        View view = LayoutInflater.from(activity).inflate(R.layout.activity_item, layout, false);
        TextView title = view.findViewById(R.id.activity_text);
        TextView note = view.findViewById(R.id.activity_note_text);
        ImageView image = view.findViewById(R.id.activity_image);
        FrameLayout imageFrame = view.findViewById(R.id.activity_image_frame);

        GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(activity, R.drawable.frame_circle);
        if (drawable != null) {
            drawable = (GradientDrawable) drawable.mutate();
            drawable.setStroke(DisplayHelper.dpToPx(activity, 4), WellbeingHelper.getColor(activity, activityInstance.getWayToWellbeing()));
            imageFrame.setBackground(drawable);
        }

        View topLevelDetails = view.findViewById(R.id.activity_top_level_details);
        ImageButton expandButton = view.findViewById(R.id.expand_options_button);
        MaterialButton deleteButton = view.findViewById(R.id.delete_options_button);
        View checkboxView = view.findViewById(R.id.activity_checkbox_container);

        view.setTag(activityInstance.getWayToWellbeing());

        MaterialButton addNoteButton = view.findViewById(R.id.add_note_button);
        TextInputLayout noteTextInputContainer = view.findViewById(R.id.note_input_container);

        MaterialButton startTimeButton = view.findViewById(R.id.add_start_time);
        MaterialButton endTimeButton = view.findViewById(R.id.add_end_time);

        // Allow the user to select a start time
        startTimeButton.setOnClickListener(v -> {
            long myHours = activityInstance.getStartTime() / 3600000;
            long myMinutes = (activityInstance.getStartTime() - (myHours * 3600000)) / 60000;

            MaterialTimePicker startTimePicker = new MaterialTimePicker.Builder()
                .setTitleText(R.string.title_start_time)
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour((int)(activityInstance.getStartTime() > 0 ? myHours : 12))
                .setMinute((int)(activityInstance.getStartTime() > 0 ? myMinutes : 0))
                .build();

            startTimePicker.show(fragmentManager, "start");

            startTimePicker.addOnPositiveButtonClickListener(time -> {
                int hour = startTimePicker.getHour();
                int minute = startTimePicker.getMinute();

                long startTimeMillis = (hour * 3600000) + (minute * 60000);

                activityInstance.setStartTime(startTimeMillis);
                WellbeingDatabaseModule.databaseExecutor.execute(() -> {
                    db.surveyResponseActivityRecordDao().updateStartTime(activityInstance.getActivitySurveyId(), startTimeMillis);
                });
                activity.runOnUiThread(() -> {
                    TextView timeText = view.findViewById(R.id.activity_time_text);
                    displayTimes(timeText, activityInstance.getStartTime(), activityInstance.getEndTime());
                });
            });
        });

        // Allow a user to select an end time
        endTimeButton.setOnClickListener(v -> {

            long myHours = activityInstance.getEndTime() / 3600000;
            long myMinutes = (activityInstance.getEndTime() - (myHours * 3600000)) / 60000;

            MaterialTimePicker endTimePicker = new MaterialTimePicker.Builder()
                .setTitleText(R.string.title_end_time)
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour((int)(activityInstance.getEndTime() > 0 ? myHours : 12))
                .setMinute((int)(activityInstance.getEndTime() > 0 ? myMinutes : 0))
                .build();

            endTimePicker.show(fragmentManager, "end");

            endTimePicker.addOnPositiveButtonClickListener(time -> {
                int hour = endTimePicker.getHour();
                int minute = endTimePicker.getMinute();
                long endTimeMillis = (hour * 3600000) + (minute * 60000);
                activityInstance.setEndTime(endTimeMillis);
                WellbeingDatabaseModule.databaseExecutor.execute(() -> {
                    db.surveyResponseActivityRecordDao().updateEndTime(activityInstance.getActivitySurveyId(), endTimeMillis);
                });
                activity.runOnUiThread(() -> {
                    TextView timeText = view.findViewById(R.id.activity_time_text);
                    displayTimes(timeText, activityInstance.getStartTime(), activityInstance.getEndTime());
                });
            });
        });


        // Add emotions
        List<Integer> colorList = Arrays.asList(R.color.translucent_sentiment_worst, R.color.translucent_sentiment_bad, R.color.translucent_sentiment_neutral, R.color.translucent_sentiment_good, R.color.translucent_sentiment_best);

        LinearLayout emotionsContainer = view.findViewById(R.id.emotions_container);

        // Set all to transparent so it doesn't end up the colour of the last background
        removeEmotionSelection(view, activity);

        if (activityInstance.getEmotion() != 0) {
            // Remember to -1 because index is the value - 1
            int index = activityInstance.getEmotion() - 1;
            View sentimentItem = emotionsContainer.getChildAt(index);
            sentimentItem.getBackground().setTint(activity.getColor(colorList.get(index)));
        }

        for (int i = 0; i < emotionsContainer.getChildCount(); i++) {
            View sentimentItem = emotionsContainer.getChildAt(i);
            final int index = i;
            sentimentItem.setOnClickListener(v -> {
                removeEmotionSelection(view, activity);
                WellbeingDatabaseModule.databaseExecutor.execute(() -> {
                    db.surveyResponseActivityRecordDao().updateEmotion(activityInstance.getActivitySurveyId(), index + 1);
                });
                sentimentItem.getBackground().setTint(activity.getColor(colorList.get(index)));
            });
        }

        EditText noteTextInput = view.findViewById(R.id.note_input);

        // Show note box and allow user to edit it or delete it if they press it again
        addNoteButton.setOnClickListener(v -> {
            if(noteTextInputContainer.getVisibility() == View.GONE) {
                noteTextInputContainer.setVisibility(View.VISIBLE);
                addNoteButton.setIconResource(R.drawable.icon_close);
            } else {
                // Confirm that users are happy to delete the note
                new MaterialAlertDialogBuilder(activity)
                        .setTitle(R.string.title_delete_note)
                        .setMessage(R.string.body_delete_note)
                        .setIcon(R.drawable.icon_close)
                        .setPositiveButton(R.string.button_delete, (dialog, which) -> {
                            note.setText("");
                            noteTextInput.setText("");
                            note.setVisibility(View.GONE);
                            WellbeingDatabaseModule.databaseExecutor.execute(() -> {
                                db.surveyResponseActivityRecordDao().updateNote(activityInstance.getActivitySurveyId(), "");
                                noteTextInputContainer.setHelperText(activity.getText(R.string.helper_saved_note));
                            });

                            noteTextInputContainer.setVisibility(View.GONE);
                            addNoteButton.setIconResource(R.drawable.icon_add);
                        })
                        .setNegativeButton(R.string.button_cancel, (dialog, which) -> {})
                        .show();
            }
        });

        // Respond to changes in note text to update the hint
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

            for (Question question : activityInstance.getQuestions()) {
                CheckBox checkBox = (CheckBox) LayoutInflater.from(activity).inflate(R.layout.item_check_box, checkboxContainer, false);
                checkBox.setChecked(question.getUserResponse());
                checkBox.setText(question.getQuestion());
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    WellbeingDatabaseModule.databaseExecutor.execute(() -> {
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

                        WellbeingDatabaseModule.databaseExecutor.execute(() -> {
                            db.surveyResponseActivityRecordDao().updateNote(activityInstance.getActivitySurveyId(), noteText);
                            noteTextInputContainer.setHelperText(activity.getText(R.string.helper_saved_note));
                        });
                    }

                    // Remember that the activity details have been filled in
                    WellbeingDatabaseModule.databaseExecutor.execute(() -> {
                        db.surveyResponseActivityRecordDao().updateIsDone(activityInstance.getActivitySurveyId(), true);
                    });

                    checkboxView.setVisibility(View.GONE);
                    expandButton.setImageResource(R.drawable.button_expand);
                }
            });

            MaterialButton doneButton = view.findViewById(R.id.done_button);

            expandButton.setOnClickListener(expandClickListener);
            doneButton.setOnClickListener(expandClickListener);

            // Hide the checkboxes if they have been hidden before
            if(activityInstance.getIsDone()) {
                checkboxView.setVisibility(View.GONE);
                expandButton.setImageResource(R.drawable.button_expand);
            } else {
                expandButton.setImageResource(R.drawable.button_collapse);
            }

            if (activityInstance.getQuestions().size() == 0) {
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
                        WellbeingDatabaseModule.databaseExecutor.execute(() -> {
                            db.surveyResponseActivityRecordDao().deleteById(activityInstance.getActivitySurveyId());
                        });
                        layout.removeView(view);
                    })
                    .setNegativeButton(R.string.button_cancel, (dialog, which) -> {})
                    .create()
                    .show();
            });

            // Display details
            noteTextInput.setText(activityInstance.getNote());
            TextView timeText = view.findViewById(R.id.activity_time_text);
            displayTimes(timeText, activityInstance.getStartTime(), activityInstance.getEndTime());

            title.setText(activityInstance.getName());

            if (activityInstance.getNote() == null || activityInstance.getNote().length() == 0) {
                note.setVisibility(View.GONE);
            } else {
                note.setText(activityInstance.getNote());
                note.setVisibility(View.VISIBLE);
            }

            image.setImageResource(ActivityTypeImageHelper.getActivityImage(activityInstance.getType()));

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

    /**
     * Create a progress insights card that displays the sub-activities that users are doing best at
     * and sub-activities that users could improve
     *
     * @param helpContainer The container to display help in
     * @param wayToWellbeing The way to wellbeing that is being displayed
     * @param startTime The start time in milliseconds
     * @param endTime The end time in milliseconds
     * @param context The application context
     * @param activity The lifecycle owner
     * @param db A way to access the database
     */
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

            // Ensure that they are displayed in a random order
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

            // Ensure that they are displayed in a random order
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

        db.wellbeingRecordDao().getTrueWellbeingRecordsByTimestampRangeAndWayToWellbeingType(startTime, endTime, wayToWellbeing.toString()).observe(activity, goodObserver);
        db.wellbeingRecordDao().getFalseWellbeingRecordsByTimestampRangeAndWayToWellbeingType(startTime, endTime, wayToWellbeing.toString()).observe(activity, suggestionObserver);

        helpContainer.addView(goodInsights);
        helpContainer.addView(suggestionInsights);
    }

    /**
     * When changing the selected emotion, need to remove the background tint from all others
     *
     * @param view A view that contains the emotions
     * @param context The application context
     */
    private static void removeEmotionSelection(View view, Context context) {
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

    /**
     * Add the start and end times to the instance of the activity
     *
     * @param timeText The text view that should contain the time
     * @param startTimeMillis The start time in milliseconds
     * @param endTimeMillis The end time in milliseconds
     */
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
