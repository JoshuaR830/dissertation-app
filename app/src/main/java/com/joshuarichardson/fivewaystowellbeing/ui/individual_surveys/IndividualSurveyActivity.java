package com.joshuarichardson.fivewaystowellbeing.ui.individual_surveys;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.joshuarichardson.fivewaystowellbeing.LearnMoreAboutFiveWaysActivity;
import com.joshuarichardson.fivewaystowellbeing.MenuItemHelper;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.TimeFormatter;
import com.joshuarichardson.fivewaystowellbeing.TimeHelper;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.WellbeingHelper;
import com.joshuarichardson.fivewaystowellbeing.analytics.LogAnalyticEventHelper;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.LimitedRawSurveyData;
import com.joshuarichardson.fivewaystowellbeing.storage.RawSurveyData;
import com.joshuarichardson.fivewaystowellbeing.storage.SentimentItem;
import com.joshuarichardson.fivewaystowellbeing.storage.SurveyCountItem;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphItem;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphValueHelper;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyDataHelper;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyDay;
import com.joshuarichardson.fivewaystowellbeing.surveys.ActivityInstance;
import com.joshuarichardson.fivewaystowellbeing.ui.graphs.WellbeingGraphView;
import com.joshuarichardson.fivewaystowellbeing.ui.activities.edit.ViewActivitiesActivity;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import dagger.hilt.android.AndroidEntryPoint;

import static com.joshuarichardson.fivewaystowellbeing.DisplayHelper.getSmallestMaxDimension;
import static com.joshuarichardson.fivewaystowellbeing.ui.individual_surveys.ActivityViewHelper.createInsightCards;

/**
 * Activity to display a specific wellbeing log from a day in history
 */
@AndroidEntryPoint
public class IndividualSurveyActivity extends AppCompatActivity {
    private static final int ACTIVITY_REQUEST_CODE = 1;
    private long surveyId;
    private boolean isDeletable;
    private long startTime;

    @Inject
    public WellbeingDatabase db;

    @Inject
    public LogAnalyticEventHelper analyticsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_survey);

        Intent surveyIntent = getIntent();
        if(surveyIntent == null || surveyIntent.getExtras() == null) {
            finish();
            return;
        }

        this.surveyId = surveyIntent.getExtras().getLong("survey_id", -1);
        this.startTime = surveyIntent.getExtras().getLong("start_time", -1);

        if(surveyId < 0) {
            finish();
            return;
        }

        CardView container = findViewById(R.id.graph_card);
        FrameLayout canvasContainer = container.findViewById(R.id.graph_card_container);

        WellbeingGraphView graphView = new WellbeingGraphView(this, (int)(getSmallestMaxDimension(this)/1.5), new WellbeingGraphValueHelper(0, 0, 0, 0, 0), true);
        canvasContainer.addView(graphView);

        // Observe changes to the graph and update the values
        Observer<List<WellbeingGraphItem>> wholeGraphUpdate  = graphValues -> {
            WellbeingGraphValueHelper values = WellbeingGraphValueHelper.getWellbeingGraphValues(graphValues);
            graphView.updateValues(values);
            WellbeingDatabaseModule.databaseExecutor.execute(() -> {
                db.wellbeingResultsDao().updateWaysToWellbeing(this.surveyId, values.getConnectValue(), values.getBeActiveValue(), values.getKeepLearningValue(), values.getTakeNoticeValue(), values.getGiveValue());
            });
        };

        ChipGroup group = findViewById(R.id.wellbeing_chip_group);
        LinearLayout helpContainer = findViewById(R.id.way_to_wellbeing_help_container);

        // Add chip listeners which can be selected to display insights and wellbeing descriptions
        group.setOnCheckedChangeListener((groupId, checkedId) -> {
            helpContainer.removeAllViews();
            switch (checkedId) {
                case R.id.chip_connect:
                    graphView.highlightBar(WaysToWellbeing.CONNECT);
                    LayoutInflater.from(this).inflate(R.layout.card_connect, helpContainer);
                    createInsightCards(helpContainer, WaysToWellbeing.CONNECT, this.startTime, TimeHelper.getEndOfDay(this.startTime), this, this, this.db);
                    helpContainer.setVisibility(View.VISIBLE);
                    break;
                case R.id.chip_be_active:
                    graphView.highlightBar(WaysToWellbeing.BE_ACTIVE);
                    LayoutInflater.from(this).inflate(R.layout.card_be_active, helpContainer);
                    createInsightCards(helpContainer, WaysToWellbeing.BE_ACTIVE, this.startTime, TimeHelper.getEndOfDay(this.startTime), this, this, this.db);
                    helpContainer.setVisibility(View.VISIBLE);
                    break;
                case R.id.chip_keep_learning:
                    graphView.highlightBar(WaysToWellbeing.KEEP_LEARNING);
                    LayoutInflater.from(this).inflate(R.layout.card_keep_learning, helpContainer);
                    createInsightCards(helpContainer, WaysToWellbeing.KEEP_LEARNING, this.startTime, TimeHelper.getEndOfDay(this.startTime), this, this, this.db);
                    helpContainer.setVisibility(View.VISIBLE);
                    break;
                case R.id.chip_take_notice:
                    graphView.highlightBar(WaysToWellbeing.TAKE_NOTICE);
                    LayoutInflater.from(this).inflate(R.layout.card_take_notice, helpContainer);
                    createInsightCards(helpContainer, WaysToWellbeing.TAKE_NOTICE, this.startTime, TimeHelper.getEndOfDay(this.startTime), this, this, this.db);
                    helpContainer.setVisibility(View.VISIBLE);
                    break;
                case R.id.chip_give:
                    graphView.highlightBar(WaysToWellbeing.GIVE);
                    LayoutInflater.from(this).inflate(R.layout.card_give, helpContainer);
                    createInsightCards(helpContainer, WaysToWellbeing.GIVE, this.startTime, TimeHelper.getEndOfDay(this.startTime), this, this, this.db);
                    helpContainer.setVisibility(View.VISIBLE);
                    break;
                default:
                    helpContainer.setVisibility(View.GONE);
                    graphView.resetColors();
            }
        });

        // Allow activities to be added
        Button addActivityButton = findViewById(R.id.add_activity_button);
        addActivityButton.setOnClickListener(v -> {
            Intent activityIntent = new Intent(this, ViewActivitiesActivity.class);
            startActivityForResult(activityIntent, ACTIVITY_REQUEST_CODE);
        });

        if(startTime > -1) {
            long morning = TimeHelper.getStartOfDay(startTime);
            long night = TimeHelper.getEndOfDay(startTime);
            this.db.wellbeingQuestionDao().getWaysToWellbeingBetweenTimes(morning, night).observe(this, wholeGraphUpdate);
        }

        // Respond to changes in emotion for the survey
        Observer<SurveyCountItem> emotionUpdateObserver = sentiment -> {
            SentimentItem emotionValues = sentiment.getResourcesForAverage();
            int color = getColor(emotionValues.getColorResource());
            ImageView image = findViewById(R.id.surveys_completed_image);
            image.setImageResource(emotionValues.getImageResource());
            image.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        };

        this.db.surveyResponseActivityRecordDao().getEmotions(surveyId).observe(this, emotionUpdateObserver);

        // Update items will reload everything
        updateSurveyItems();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If the user goes back instead of adding a new item, they might have updated an activity instead so should reload for consistency
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
            LinearLayout activityContainer = this.findViewById(R.id.survey_item_container);

            WellbeingDatabaseModule.databaseExecutor.execute(() -> {
                int sequenceNumber = this.db.surveyResponseActivityRecordDao().getItemCount(surveyId) + 1;
                long activitySurveyId = this.db.surveyResponseActivityRecordDao().insert(new SurveyResponseActivityRecord(surveyId, activityId, sequenceNumber, "", -1, -1, 0, false));
                ActivityInstance activity = new ActivityInstance(activityName, "", activityType, wayToWellbeing, activitySurveyId, -1, -1, 0, false);

                long night = TimeHelper.getEndOfDay(startTime);
                ActivityInstance updatedActivity = WellbeingRecordInsertionHelper.addActivityQuestions(this.db, activitySurveyId, activityType, activity, night);

                // If it has been edited, the page will reload everything
                boolean isEdited = data.getExtras().getBoolean("is_edited", false);
                if(isEdited) {
                    updateSurveyItems();
                } else {
                    ActivityViewHelper.createActivityItem(this, activityContainer, updatedActivity, this.db, getSupportFragmentManager(), analyticsHelper, true);
                }
            });
        }
    }

    /**
     * Whenever the page needs to be updated call this and it will update the survey data displayed
     */
    public void updateSurveyItems() {
        WellbeingDatabaseModule.databaseExecutor.execute(() -> {
            List<RawSurveyData> rawSurveyDataList = this.db.wellbeingRecordDao().getDataBySurvey(surveyId);

            if(rawSurveyDataList == null || rawSurveyDataList.size() == 0) {
                // This converts a limited response to an entire RawSurveyData response
                List<LimitedRawSurveyData> limitedData = this.db.wellbeingRecordDao().getLimitedDataBySurvey(surveyId);
                rawSurveyDataList = LimitedRawSurveyData.convertToRawSurveyDataList(limitedData);
            }

            SurveyDay surveyData = SurveyDataHelper.transform(rawSurveyDataList);
            ActivityViewHelper.displaySurveyItems(this, surveyData, this.db, getSupportFragmentManager(), analyticsHelper);

            SurveyResponse surveyResponse = this.db.surveyResponseDao().getSurveyResponseById(surveyId);

            // Add the summary
            runOnUiThread(() -> {

                TextView summaryTitle = findViewById(R.id.individual_survey_title);
                TextView description = findViewById(R.id.individual_survey_description);
                TextView date = findViewById(R.id.individual_survey_time);
                ImageView image = findViewById(R.id.individual_survey_image);

                summaryTitle.setText(surveyResponse.getTitle());
                description.setText(surveyResponse.getDescription());

                // Catch the exception if the user does not set a value
                WaysToWellbeing way;
                try {
                    way = WaysToWellbeing.valueOf(surveyResponse.getSurveyResponseWayToWellbeing());
                } catch(IllegalArgumentException e) {
                    way = WaysToWellbeing.UNASSIGNED;
                }

                image.setImageResource(WellbeingHelper.getImage(way));

                date.setText(TimeFormatter.formatTimeAsDayMonthYearString(surveyResponse.getSurveyResponseTimestamp()));
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.help_menu, menu);
        menu.findItem(R.id.action_delete).setVisible(true);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // When the user clicks delete toggle it
        if (item.getItemId() == R.id.action_delete) {
            toggleDeletable();
            return true;
        }

        Intent intent = MenuItemHelper.handleOverflowMenuClick(this, item);

        if(intent == null) {
            return false;
        }

        // This only runs if an intent was set
        startActivity(intent);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.isDeletable) {
            toggleDeletable();
        }
    }

    /**
     * Toggle delete mode.
     * Allows activities to be deleted from a survey
     */
    public void toggleDeletable() {
        LinearLayout layout = this.findViewById(R.id.survey_item_container);
        int counter = layout.getChildCount();
        this.isDeletable = !this.isDeletable;
        for (int i = 0; i < counter; i++) {
            View child = layout.getChildAt(i);
            ImageButton expandButton = child.findViewById(R.id.expand_options_button);
            MaterialButton deleteButton = child.findViewById(R.id.delete_options_button);
            if (this.isDeletable) {
                expandButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.VISIBLE);
            } else{
                expandButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Take the use rto the view more page when clicking a button
     *
     * @param view The instance of the button clicked
     */
    public void onLearnMoreButtonClicked(View view) {
        Intent learnMoreIntent = new Intent(this, LearnMoreAboutFiveWaysActivity.class);
        startActivity(learnMoreIntent);
    }
}