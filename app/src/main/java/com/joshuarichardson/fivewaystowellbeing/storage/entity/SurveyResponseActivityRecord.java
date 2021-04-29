package com.joshuarichardson.fivewaystowellbeing.storage.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.ACTIVITY_RECORD_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_ACTIVITY_RECORD_ACTIVITY_EMOTION;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_ACTIVITY_RECORD_ACTIVITY_END_TIME;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_ACTIVITY_RECORD_ACTIVITY_IS_DONE;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_ACTIVITY_RECORD_ACTIVITY_NOTE;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_ACTIVITY_RECORD_ACTIVITY_RECORD_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_ACTIVITY_RECORD_ACTIVITY_SEQUENCE_NUMBER;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_ACTIVITY_RECORD_ACTIVITY_START_TIME;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_ACTIVITY_RECORD_SURVEY_ACTIVITY_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_ACTIVITY_RECORD_SURVEY_RESPONSE_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_ACTIVITY_RECORD_TABLE_NAME;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_ID;

/**
 * The entity representing the instance of an activity and the survey it is part of.
 * The table is used to hold all the links between surveys, activities and the wider data.
 * This table facilitates the main functionality of the app
 */
@Entity(
        tableName = SURVEY_RESPONSE_ACTIVITY_RECORD_TABLE_NAME,
        foreignKeys = {
            @ForeignKey(entity = ActivityRecord.class, parentColumns = ACTIVITY_RECORD_ID, childColumns = SURVEY_RESPONSE_ACTIVITY_RECORD_ACTIVITY_RECORD_ID, onDelete = CASCADE),
            @ForeignKey(entity = SurveyResponse.class, parentColumns = SURVEY_RESPONSE_ID, childColumns = SURVEY_RESPONSE_ACTIVITY_RECORD_SURVEY_RESPONSE_ID, onDelete = CASCADE)
        })
public class SurveyResponseActivityRecord {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = SURVEY_RESPONSE_ACTIVITY_RECORD_SURVEY_ACTIVITY_ID)
    private long surveyActivityId;

    @NonNull
    @ColumnInfo(name = SURVEY_RESPONSE_ACTIVITY_RECORD_ACTIVITY_RECORD_ID)
    private long activityRecordId;

    @NonNull
    @ColumnInfo(name = SURVEY_RESPONSE_ACTIVITY_RECORD_SURVEY_RESPONSE_ID)
    private long surveyResponseId;

    @NonNull
    @ColumnInfo(name = SURVEY_RESPONSE_ACTIVITY_RECORD_ACTIVITY_SEQUENCE_NUMBER)
    private int sequenceNumber;

    @ColumnInfo(name = SURVEY_RESPONSE_ACTIVITY_RECORD_ACTIVITY_NOTE)
    private String note;

    @NonNull
    @ColumnInfo(name = SURVEY_RESPONSE_ACTIVITY_RECORD_ACTIVITY_START_TIME)
    private long startTime;

    @NonNull
    @ColumnInfo(name = SURVEY_RESPONSE_ACTIVITY_RECORD_ACTIVITY_END_TIME)
    private long endTime;

    @ColumnInfo(name = SURVEY_RESPONSE_ACTIVITY_RECORD_ACTIVITY_EMOTION)
    private int emotion;

    @ColumnInfo(name = SURVEY_RESPONSE_ACTIVITY_RECORD_ACTIVITY_IS_DONE)
    private boolean isDone;

    public SurveyResponseActivityRecord(long surveyResponseId, long activityRecordId, int sequenceNumber, String note, long startTime, long endTime, int emotion, boolean isDone) {
        this.setSurveyResponseId(surveyResponseId);
        this.setActivityRecordId(activityRecordId);
        this.setSequenceNumber(sequenceNumber);
        this.setNote(note);
        this.setStartTime(startTime);
        this.setEndTime(endTime);
        this.setEmotion(emotion);
        this.isDone(isDone);
    }

    private void isDone(boolean isDone) {
        this.isDone = isDone;
    }

    private void setEmotion(int emotion) {
        this.emotion = emotion;
    }

    public void setSurveyActivityId(long id) {
        this.surveyActivityId = id;
    }

    public void setActivityRecordId(long activityRecordId) {
        this.activityRecordId = activityRecordId;
    }

    public void setSurveyResponseId(long surveyResponseId) {
        this.surveyResponseId = surveyResponseId;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getActivityRecordId() {
        return this.activityRecordId;
    }

    public long getSurveyResponseId() {
        return this.surveyResponseId;
    }

    public long getSurveyActivityId() {
        return this.surveyActivityId;
    }

    public String getNote() {
        return this.note;
    }

    public int getSequenceNumber() {
        return this.sequenceNumber;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public int getEmotion() {
        return this.emotion;
    }

    public boolean getIsDone() {
        return this.isDone;
    }
}
