package com.joshuarichardson.fivewaystowellbeing.storage.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import static androidx.room.ForeignKey.CASCADE;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.ACTIVITY_RECORD_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_ACTIVITY_RECORD_ACTIVITY_RECORD_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_ACTIVITY_RECORD_SURVEY_RESPONSE_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_ACTIVITY_RECORD_TABLE_NAME;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_ID;

@Entity(
        tableName = SURVEY_RESPONSE_ACTIVITY_RECORD_TABLE_NAME,
        primaryKeys = {SURVEY_RESPONSE_ACTIVITY_RECORD_ACTIVITY_RECORD_ID, SURVEY_RESPONSE_ACTIVITY_RECORD_SURVEY_RESPONSE_ID},
        foreignKeys = {
                @ForeignKey(entity = ActivityRecord.class, parentColumns = ACTIVITY_RECORD_ID, childColumns = SURVEY_RESPONSE_ACTIVITY_RECORD_ACTIVITY_RECORD_ID, onDelete = CASCADE),
                @ForeignKey(entity = SurveyResponse.class, parentColumns = SURVEY_RESPONSE_ID, childColumns = SURVEY_RESPONSE_ACTIVITY_RECORD_SURVEY_RESPONSE_ID, onDelete = CASCADE)
        })
public class SurveyResponseActivityRecord {

    @NonNull
    @ColumnInfo(name = SURVEY_RESPONSE_ACTIVITY_RECORD_ACTIVITY_RECORD_ID,  index = true)
    private long activityRecordId;

    @NonNull
    @ColumnInfo(name  = SURVEY_RESPONSE_ACTIVITY_RECORD_SURVEY_RESPONSE_ID,  index = true)
    private long surveyResponseId;

    public SurveyResponseActivityRecord(long surveyResponseId, long activityRecordId) {
        this.setSurveyResponseId(surveyResponseId);
        this.setActivityRecordId(activityRecordId);
    }

    public void setActivityRecordId(long activityRecordId) {
        this.activityRecordId = activityRecordId;
    }

    public void setSurveyResponseId(long surveyResponseId) {
        this.surveyResponseId = surveyResponseId;
    }

    public long getActivityRecordId() {
        return this.activityRecordId;
    }

    public long getSurveyResponseId() {
        return this.surveyResponseId;
    }
}
