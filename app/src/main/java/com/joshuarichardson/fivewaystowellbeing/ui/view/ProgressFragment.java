package com.joshuarichardson.fivewaystowellbeing.ui.view;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.joshuarichardson.fivewaystowellbeing.ActivityTypeImageHelper;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.TimeFormatter;
import com.joshuarichardson.fivewaystowellbeing.TimeHelper;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.analytics.LogAnalyticEventHelper;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.notifications.SendCompleteSurveyNotificationBroadcastReceiver;
import com.joshuarichardson.fivewaystowellbeing.storage.LimitedRawSurveyData;
import com.joshuarichardson.fivewaystowellbeing.storage.RawSurveyData;
import com.joshuarichardson.fivewaystowellbeing.storage.SentimentItem;
import com.joshuarichardson.fivewaystowellbeing.storage.SurveyCountItem;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphItem;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphValueHelper;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.PhysicalActivity;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingResult;
import com.joshuarichardson.fivewaystowellbeing.surveys.Passtime;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyDataHelper;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyDay;
import com.joshuarichardson.fivewaystowellbeing.ui.graphs.WellbeingGraphView;
import com.joshuarichardson.fivewaystowellbeing.ui.individual_surveys.ActivityViewHelper;
import com.joshuarichardson.fivewaystowellbeing.ui.individual_surveys.WellbeingRecordInsertionHelper;
import com.joshuarichardson.fivewaystowellbeing.ui.insights.WayToWellbeingImageColorizer;
import com.joshuarichardson.fivewaystowellbeing.ui.pass_times.edit.ViewPassTimesActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import dagger.hilt.android.AndroidEntryPoint;

import static com.joshuarichardson.fivewaystowellbeing.DisplayHelper.getSmallestMaxDimension;
import static com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing.UNASSIGNED;
import static com.joshuarichardson.fivewaystowellbeing.ui.individual_surveys.ActivityViewHelper.createInsightCards;

@AndroidEntryPoint
public class ProgressFragment extends Fragment {
    private static final int ACTIVITY_REQUEST_CODE = 1;

    @Inject
    WellbeingDatabase db;

    @Inject
    public LogAnalyticEventHelper analyticsHelper;

    private Observer<List<SurveyResponse>> surveyResponsesObserver;
    private LiveData<List<SurveyResponse>> surveyResponseItems;
    private Observer<List<WellbeingGraphItem>> wholeGraphUpdate;
    private LiveData<List<WellbeingGraphItem>> graphUpdateValues;
    private LiveData<SurveyCountItem> emotionUpdateValues;
    private long surveyId;
    private Observer<SurveyCountItem> emotionUpdateObserver;
    private boolean isDeletable;
    private boolean shouldUpdate = true;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parentView, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, parentView, false);

        Button addActivityButton = view.findViewById(R.id.add_activity_button);
        addActivityButton.setOnClickListener(v -> {
            this.shouldUpdate = false;
            Intent activityIntent = new Intent(requireActivity(), ViewPassTimesActivity.class);
            startActivityForResult(activityIntent, ACTIVITY_REQUEST_CODE);

            // When activity button clicked, clear pending notifications
            NotificationManager notification = (NotificationManager) requireContext().getSystemService(Service.NOTIFICATION_SERVICE);
            notification.cancel(SendCompleteSurveyNotificationBroadcastReceiver.SURVEY_REMINDER);
        });

        this.emotionUpdateObserver = sentiment -> {
            SentimentItem emotionValues = sentiment.getResourcesForAverage();
            int color = requireContext().getColor(emotionValues.getColorResource());
            ImageView image = requireActivity().findViewById(R.id.surveys_completed_image);
            image.setImageResource(emotionValues.getImageResource());
            image.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        };

        // Reference https://stackoverflow.com/a/47531110/13496270
        setHasOptionsMenu(true);

        return view;
    }

    // Whenever something needs to be updated - do this
    public void updateSurveyItems() {
        SurveyResponseDao surveyDao = db.surveyResponseDao();
        LinearLayout passTimeContainer = requireActivity().findViewById(R.id.survey_item_container);

        this.surveyResponsesObserver = surveys -> {
            passTimeContainer.removeAllViews();
            if (surveys.size() == 0) {
                long startTime = TimeHelper.getStartOfDay(new Date().getTime());
                // Adding the new survey should trigger the live data to update
                WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                    long newSurveyId = this.db.surveyResponseDao().insert(new SurveyResponse(startTime, UNASSIGNED, "", ""));
                    this.db.wellbeingResultsDao().insert(new WellbeingResult(newSurveyId, startTime, 0, 0, 0, 0, 0));
                });

                return;
            }

            this.surveyId = surveys.get(0).getSurveyResponseId();
            this.emotionUpdateValues = this.db.surveyResponseActivityRecordDao().getEmotions(this.surveyId);
            this.emotionUpdateValues.observe(requireActivity(), this.emotionUpdateObserver);
            WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                List<RawSurveyData> rawSurveyDataList = this.db.wellbeingRecordDao().getDataBySurvey(this.surveyId);
                if (rawSurveyDataList == null || rawSurveyDataList.size() == 0) {
                    // This converts a limited response to an entire RawSurveyData response
                    List<LimitedRawSurveyData> limitedData = this.db.wellbeingRecordDao().getLimitedDataBySurvey(this.surveyId);
                    rawSurveyDataList = LimitedRawSurveyData.convertToRawSurveyDataList(limitedData);
                }

                SurveyDay surveyData = SurveyDataHelper.transform(rawSurveyDataList);
                ActivityViewHelper.displaySurveyItems(requireActivity(), surveyData, this.db, getParentFragmentManager(), analyticsHelper);

                // Get all pending activities
                List<PhysicalActivity> list = this.db.physicalActivityDao().getPending();

                // Loop through each pending activity
                for(PhysicalActivity item : list) {

                    ActivityRecord record = this.db.activityRecordDao().getActivityRecordById(item.getActivityId());

                    if (record == null) {
                        continue;
                    }

                    requireActivity().runOnUiThread(() -> {
                        // Create a temporary record allowing users to select yes or no
                        View tempActivity = LayoutInflater.from(requireActivity()).inflate(R.layout.pass_time_temporary_item, null, false);
                        TextView title = tempActivity.findViewById(R.id.activity_text);
                        TextView timeText = tempActivity.findViewById(R.id.activity_time_text);
                        FrameLayout imageFrame = tempActivity.findViewById(R.id.activity_image_frame);
                        ImageView image = tempActivity.findViewById(R.id.activity_image);

                        title.setText(record.getActivityName());
                        long startTime = TimeHelper.getStartOfDay(new Date().getTime());
                        String textStartTime = TimeFormatter.formatTimeAsHourMinuteString(item.getStartTime() - startTime);
                        String textEndTime = TimeFormatter.formatTimeAsHourMinuteString(item.getEndTime() - startTime);

                        if(textStartTime != null && textEndTime != null) {
                            timeText.setText(String.format(Locale.getDefault(), "%s - %s", textStartTime, textEndTime));
                        }

                        WayToWellbeingImageColorizer.colorizeFrame(requireContext(), imageFrame, WaysToWellbeing.valueOf(record.getActivityWayToWellbeing()));
                        image.setImageResource(ActivityTypeImageHelper.getActivityImage(record.getActivityType()));

                        passTimeContainer.addView(tempActivity);

                        // Yes and no buttons - hide the temporary thing
                        Button yesButton = tempActivity.findViewById(R.id.yes_button);
                        Button noButton = tempActivity.findViewById(R.id.no_button);

                        yesButton.setOnClickListener((v) -> {
                            WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                                // Insert that activity into the table
                                int sequenceNumber = this.db.surveyResponseActivityRecordDao().getItemCount(surveyId) + 1;
                                long activitySurveyId = this.db.surveyResponseActivityRecordDao().insert(new SurveyResponseActivityRecord(surveyId, item.getActivityId(), sequenceNumber, "", item.getStartTime() - startTime, item.getEndTime() - startTime, 0, false));
                                Passtime passtime = new Passtime(record.getActivityName(), "", record.getActivityType(), record.getActivityWayToWellbeing(), activitySurveyId, item.getStartTime() - startTime, item.getEndTime() - startTime, 0, false);
                                Passtime updatedPasstime = WellbeingRecordInsertionHelper.addPasstimeQuestions(this.db, activitySurveyId, record.getActivityType(), passtime, new Date().getTime());
                                this.db.physicalActivityDao().updateIsPendingStatus(item.getActivityType(), false);
                                this.db.physicalActivityDao().updateIsNotificationConfirmedStatus(item.getActivityType(), true);

                                // Display the item on the screen to the user
                                requireActivity().runOnUiThread(() -> {
                                    // Time the scroll animation so that you never notice the item get deleted
                                    ActivityViewHelper.createPasstimeItem(requireActivity(), passTimeContainer, updatedPasstime, this.db, getParentFragmentManager(), analyticsHelper, true);
                                    passTimeContainer.removeView(tempActivity);
                                });
                            });
                        });

                        noButton.setOnClickListener((v) -> {
                            passTimeContainer.removeView(tempActivity);
                            // We know the outcome - it should therefore no-longer be pending
                            WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                                this.db.physicalActivityDao().updateIsNotificationConfirmedStatus(item.getActivityType(), true);
                                this.db.physicalActivityDao().updateIsPendingStatus(item.getActivityType(), false);
                            });
                        });
                    });
                }
            });
        };

        long time = new Date().getTime();
        long thisMorning = TimeHelper.getStartOfDay(time);
        long tonight = TimeHelper.getEndOfDay(time);

        requireActivity().runOnUiThread(() -> {
            // Epoch seconds give a 24 hour time frame - any new surveys added will get updated live (using now meant that future surveys today didn't get shown)
            this.surveyResponseItems = surveyDao.getSurveyResponsesByTimestampRange(thisMorning, tonight);
            this.surveyResponseItems.observe(requireActivity(), this.surveyResponsesObserver);
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        long time = new Date().getTime();
        long thisMorning = TimeHelper.getStartOfDay(time);
        long tonight = TimeHelper.getEndOfDay(time);

        CardView container = view.findViewById(R.id.graph_card);
        FrameLayout canvasContainer = container.findViewById(R.id.graph_card_container);

        WellbeingGraphView  graphView = new WellbeingGraphView(getActivity(), (int)(getSmallestMaxDimension(requireContext())/1.5), new WellbeingGraphValueHelper(0, 0,0 ,0, 0), true);

        this.wholeGraphUpdate = graphValues -> {
            // ToDo - get the values on fragment launch - save them - compare them to the new ones - is a new 100% - then you have achieved!
            WellbeingGraphValueHelper values = WellbeingGraphValueHelper.getWellbeingGraphValues(graphValues);
            graphView.updateValues(values);
            WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                db.wellbeingResultsDao().updateWaysToWellbeing(this.surveyId, values.getConnectValue(), values.getBeActiveValue(), values.getKeepLearningValue(), values.getTakeNoticeValue(), values.getGiveValue());
            });
        };

        this.graphUpdateValues = this.db.wellbeingQuestionDao().getWaysToWellbeingBetweenTimes(thisMorning, tonight);
        this.graphUpdateValues.observe(requireActivity(), this.wholeGraphUpdate);
        canvasContainer.addView(graphView);

        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
            Calendar cal = Calendar.getInstance();
            boolean doesExist;

            do {
                // Add all activities that were missed between now and the last added one
                doesExist = true;
                cal.add(Calendar.DATE, -1);

                long morning = TimeHelper.getStartOfDay(cal.getTimeInMillis());
                long night = TimeHelper.getEndOfDay(cal.getTimeInMillis());

                List<SurveyResponse> surveyResponsesNotLive = this.db.surveyResponseDao().getSurveyResponsesByTimestampRangeNotLive(morning, night);

                if (surveyResponsesNotLive.size() == 0) {
                    doesExist = false;
                    // If missed - add it
                    long newSurveyId = this.db.surveyResponseDao().insert(new SurveyResponse(morning, UNASSIGNED, "", ""));
                    this.db.wellbeingResultsDao().insert(new WellbeingResult(newSurveyId, morning, 0, 0, 0, 0, 0));
                }
            } while(!doesExist && cal.getTimeInMillis() > 1613509560000L);
        });


        ChipGroup group = view.findViewById(R.id.wellbeing_chip_group);
        LinearLayout helpContainer = view.findViewById(R.id.way_to_wellbeing_help_container);
        group.setOnCheckedChangeListener((groupId, checkedId) -> {
            helpContainer.removeAllViews();
            switch (checkedId) {
                case R.id.chip_connect:
                    graphView.highlightBar(WaysToWellbeing.CONNECT);
                    LayoutInflater.from(getContext()).inflate(R.layout.card_connect, helpContainer);
                    createInsightCards(helpContainer, WaysToWellbeing.CONNECT, thisMorning, tonight, requireActivity(), requireActivity(), this.db);
                    helpContainer.setVisibility(View.VISIBLE);
                    break;
                case R.id.chip_be_active:
                    graphView.highlightBar(WaysToWellbeing.BE_ACTIVE);
                    LayoutInflater.from(getContext()).inflate(R.layout.card_be_active, helpContainer);
                    createInsightCards(helpContainer, WaysToWellbeing.BE_ACTIVE, thisMorning, tonight, requireActivity(), requireActivity(), this.db);
                    helpContainer.setVisibility(View.VISIBLE);
                    break;
                case R.id.chip_keep_learning:
                    graphView.highlightBar(WaysToWellbeing.KEEP_LEARNING);
                    LayoutInflater.from(getContext()).inflate(R.layout.card_keep_learning, helpContainer);
                    createInsightCards(helpContainer, WaysToWellbeing.KEEP_LEARNING, thisMorning, tonight, requireActivity(), requireActivity(), this.db);
                    helpContainer.setVisibility(View.VISIBLE);
                    break;
                case R.id.chip_take_notice:
                    graphView.highlightBar(WaysToWellbeing.TAKE_NOTICE);
                    LayoutInflater.from(getContext()).inflate(R.layout.card_take_notice, helpContainer);
                    createInsightCards(helpContainer, WaysToWellbeing.TAKE_NOTICE, thisMorning, tonight, requireActivity(), requireActivity(), this.db);
                    helpContainer.setVisibility(View.VISIBLE);
                    break;
                case R.id.chip_give:
                    graphView.highlightBar(WaysToWellbeing.GIVE);
                    LayoutInflater.from(getContext()).inflate(R.layout.card_give, helpContainer);
                    createInsightCards(helpContainer, WaysToWellbeing.GIVE, thisMorning, tonight, requireActivity(), requireActivity(), this.db);
                    helpContainer.setVisibility(View.VISIBLE);
                    break;
                default:
                    helpContainer.setVisibility(View.GONE);
                    graphView.resetColors();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            updateSurveyItems();
            return;
        }

        if (data == null) {
            return;
        }

        if (requestCode == ACTIVITY_REQUEST_CODE) {
            if (data.getExtras() == null || !data.hasExtra("activity_id")) {
                return;
            }

            long activityId = data.getExtras().getLong("activity_id", -1);
            String activityType = data.getExtras().getString("activity_type", "");
            String activityName = data.getExtras().getString("activity_name", "");
            String wayToWellbeing = data.getExtras().getString("activity_way_to_wellbeing", "UNASSIGNED");

            if (activityId == -1) {
                return;
            }

            analyticsHelper.logWayToWellbeingActivity(this, WaysToWellbeing.valueOf(wayToWellbeing));

            // Sequence number based on number of children in the linear layout
            LinearLayout passtimeContainer = requireActivity().findViewById(R.id.survey_item_container);

            WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                int sequenceNumber = this.db.surveyResponseActivityRecordDao().getItemCount(surveyId) + 1;
                long activitySurveyId = this.db.surveyResponseActivityRecordDao().insert(new SurveyResponseActivityRecord(surveyId, activityId, sequenceNumber, "", -1, -1, 0, false));
                Passtime passtime = new Passtime(activityName, "", activityType, wayToWellbeing, activitySurveyId, -1, -1, 0, false);
                Passtime updatedPasstime = WellbeingRecordInsertionHelper.addPasstimeQuestions(this.db, activitySurveyId, activityType, passtime, new Date().getTime());

                // If it has been edited, the page will reload everything
                boolean isEdited = data.getExtras().getBoolean("is_edited", false);
                if(isEdited) {
                    updateSurveyItems();
                } else {
                    ActivityViewHelper.createPasstimeItem(requireActivity(), passtimeContainer, updatedPasstime, this.db, getParentFragmentManager(), analyticsHelper, true);
                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    // Reference: https://stackoverflow.com/a/47531110/13496270
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_delete).setVisible(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.surveyResponseItems != null) {
            this.surveyResponseItems.removeObserver(this.surveyResponsesObserver);
        }

        if(this.emotionUpdateValues != null) {
            this.emotionUpdateValues.removeObserver(this.emotionUpdateObserver);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(this.emotionUpdateValues != null) {
            this.emotionUpdateValues.removeObserver(this.emotionUpdateObserver);
        }
        this.graphUpdateValues.removeObserver(this.wholeGraphUpdate);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.isDeletable) {
            toggleDeletable();
        }

        if(this.shouldUpdate) {
            updateSurveyItems();
        }

        // Update this so that it updates next time - unless explicitly disabled
        this.shouldUpdate = true;

        // This starts observing again so that after adding an activity the emotions still update
        if(this.emotionUpdateValues != null) {
            this.emotionUpdateValues.observe(requireActivity(), this.emotionUpdateObserver);
        }
    }

    public void toggleDeletable() {
        LinearLayout layout = requireActivity().findViewById(R.id.survey_item_container);
        int counter = layout.getChildCount();
        this.isDeletable = !this.isDeletable;
        for (int i = 0; i < counter; i++) {
            View child = layout.getChildAt(i);
            ImageButton expandButton = child.findViewById(R.id.expand_options_button);
            MaterialButton deleteButton = child.findViewById(R.id.delete_options_button);

            if(expandButton == null || deleteButton == null) {
                return;
            }

            if (this.isDeletable) {
                expandButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.VISIBLE);
            } else{
                expandButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.GONE);
            }
        }
    }
}
