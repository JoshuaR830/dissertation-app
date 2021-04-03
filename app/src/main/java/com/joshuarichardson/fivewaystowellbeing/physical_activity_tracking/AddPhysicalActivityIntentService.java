package com.joshuarichardson.fivewaystowellbeing.physical_activity_tracking;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;

import com.joshuarichardson.fivewaystowellbeing.TimeHelper;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.analytics.LogAnalyticEventHelper;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.surveys.Passtime;
import com.joshuarichardson.fivewaystowellbeing.ui.individual_surveys.WellbeingRecordInsertionHelper;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.joshuarichardson.fivewaystowellbeing.physical_activity_tracking.ActivityTracking.PHYSICAL_ACTIVITY_NOTIFICATION_CYCLE;
import static com.joshuarichardson.fivewaystowellbeing.physical_activity_tracking.ActivityTracking.PHYSICAL_ACTIVITY_NOTIFICATION_RUN;
import static com.joshuarichardson.fivewaystowellbeing.physical_activity_tracking.ActivityTracking.PHYSICAL_ACTIVITY_NOTIFICATION_VEHICLE;
import static com.joshuarichardson.fivewaystowellbeing.physical_activity_tracking.ActivityTracking.PHYSICAL_ACTIVITY_NOTIFICATION_WALK;
import static com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing.UNASSIGNED;

@AndroidEntryPoint
// ToDo - intent service is deprecated in new versions of Android - but it works for now
public class AddPhysicalActivityIntentService extends IntentService {

    @Inject
    WellbeingDatabase db;

    @Inject
    LogAnalyticEventHelper analyticsHelper;

    public AddPhysicalActivityIntentService() {
        super("name");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Cancel the notification
        NotificationManager notification = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        notification.cancel(PHYSICAL_ACTIVITY_NOTIFICATION_WALK);
        notification.cancel(PHYSICAL_ACTIVITY_NOTIFICATION_RUN);
        notification.cancel(PHYSICAL_ACTIVITY_NOTIFICATION_CYCLE);
        notification.cancel(PHYSICAL_ACTIVITY_NOTIFICATION_VEHICLE);

        if (intent.getExtras() == null || !intent.hasExtra("activity_id")) {
            return;
        }

        long activityId = intent.getExtras().getLong("activity_id", -1);

        if (activityId == -1) {
            return;
        }

        long activityStartTime = intent.getExtras().getLong("start_time", 0);
        long activityEndTime = intent.getExtras().getLong("end_time", 0);
        String eventType = intent.getExtras().getString("event_type", null);

        long currentTime = new Date().getTime();
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
            Passtime passtime = new Passtime(activityDetails.getActivityName(), "", activityDetails.getActivityType(), activityDetails.getActivityWayToWellbeing(), activitySurveyId, activityStartTime - startTime, activityEndTime - startTime, 0, false);
            WellbeingRecordInsertionHelper.addPasstimeQuestions(this.db, activitySurveyId, activityDetails.getActivityType(), passtime, currentTime);
            this.db.physicalActivityDao().updateIsPendingStatus(eventType, false);
        });
    }
}