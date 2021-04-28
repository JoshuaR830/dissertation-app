package com.joshuarichardson.fivewaystowellbeing.automated_activity_tracking;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;

import com.joshuarichardson.fivewaystowellbeing.NotificationConfiguration;
import com.joshuarichardson.fivewaystowellbeing.TimeHelper;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.analytics.LogAnalyticEventHelper;
import com.joshuarichardson.fivewaystowellbeing.automated_activity_tracking.physical_activity_tracking.AutomaticActivityTypes;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphItem;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphValueHelper;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingResult;
import com.joshuarichardson.fivewaystowellbeing.surveys.UserActivity;
import com.joshuarichardson.fivewaystowellbeing.ui.individual_surveys.WellbeingRecordInsertionHelper;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing.UNASSIGNED;

@AndroidEntryPoint
public class AddAutomaticActivityIntentService extends IntentService {

    @Inject
    WellbeingDatabase db;

    @Inject
    LogAnalyticEventHelper analyticsHelper;

    public AddAutomaticActivityIntentService() {
        super("name");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Cancel the notification
        NotificationManager notification = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);

        if (intent.getExtras() == null || !intent.hasExtra("activity_id")) {
            return;
        }

        long activityId = intent.getExtras().getLong("activity_id", -1);
        String eventType = intent.getExtras().getString("event_type", null);

        switch (eventType) {
            case AutomaticActivityTypes.WALK:
                notification.cancel(NotificationConfiguration.NotificationsId.AUTOMATIC_ACTIVITY_NOTIFICATION_WALK);
                break;
            case AutomaticActivityTypes.RUN:
                notification.cancel(NotificationConfiguration.NotificationsId.AUTOMATIC_ACTIVITY_NOTIFICATION_RUN);
                break;
            case AutomaticActivityTypes.CYCLE:
                notification.cancel(NotificationConfiguration.NotificationsId.AUTOMATIC_ACTIVITY_NOTIFICATION_CYCLE);
                break;
            case AutomaticActivityTypes.VEHICLE:
                notification.cancel(NotificationConfiguration.NotificationsId.AUTOMATIC_ACTIVITY_NOTIFICATION_VEHICLE);
                break;
            default:
                notification.cancel(NotificationConfiguration.NotificationsId.AUTOMATIC_ACTIVITY_NOTIFICATION_APP);
        }

        if (activityId == -1) {
            return;
        }

        long activityStartTime = intent.getExtras().getLong("start_time", 0);
        long activityEndTime = intent.getExtras().getLong("end_time", 0);

        long currentTime = Calendar.getInstance().getTimeInMillis();
        long startTime = TimeHelper.getStartOfDay(currentTime);
        long endTime = TimeHelper.getEndOfDay(currentTime);

        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {

            ActivityRecord activityDetails = this.db.activityRecordDao().getActivityRecordById(activityId);

            if(activityDetails == null) {
                return;
            }

            this.analyticsHelper.logWayToWellbeingAutomaticActivity(this, WaysToWellbeing.valueOf(activityDetails.getActivityWayToWellbeing()));

            List<SurveyResponse> surveys = this.db.surveyResponseDao().getSurveyResponsesByTimestampRangeNotLive(startTime, endTime);

            long surveyId;

            // If no survey for today - make a new survey
            if(surveys.size() == 0) {
                surveyId = this.db.surveyResponseDao().insert(new SurveyResponse(startTime, UNASSIGNED, "", ""));
            } else {
                surveyId = surveys.get(0).getSurveyResponseId();
            }

            // If still no survey - return
            if(surveyId <= 0) {
                return;
            }

            int sequenceNumber = this.db.surveyResponseActivityRecordDao().getItemCount(surveyId) + 1;
            long activitySurveyId = this.db.surveyResponseActivityRecordDao().insert(new SurveyResponseActivityRecord(surveyId, activityId, sequenceNumber, "", activityStartTime - startTime, activityEndTime - startTime, 0, false));
            UserActivity userActivity = new UserActivity(activityDetails.getActivityName(), "", activityDetails.getActivityType(), activityDetails.getActivityWayToWellbeing(), activitySurveyId, activityStartTime - startTime, activityEndTime - startTime, 0, false);
            WellbeingRecordInsertionHelper.addActivityQuestions(this.db, activitySurveyId, activityDetails.getActivityType(), userActivity, currentTime);
            this.db.physicalActivityDao().updateIsPendingStatus(eventType, false);
            this.db.physicalActivityDao().updateStartTime(eventType, 0);

            // If it already exists, it won't get recreated, but if it doesn't exist and a survey exists, it can create it
            this.db.wellbeingResultsDao().insert(new WellbeingResult(surveyId, startTime, 0, 0, 0, 0, 0));

            // Update the values for the ways to wellbeing so that it is up-to-date
            List<WellbeingGraphItem> wayToWellbeingValues = this.db.wellbeingQuestionDao().getWaysToWellbeingBetweenTimesNotLive(startTime, endTime);
            WellbeingGraphValueHelper values = WellbeingGraphValueHelper.getWellbeingGraphValues(wayToWellbeingValues);
            this.db.wellbeingResultsDao().updateWaysToWellbeing(surveyId, values.getConnectValue(), values.getBeActiveValue(), values.getKeepLearningValue(), values.getTakeNoticeValue(), values.getGiveValue());
        });
    }
}