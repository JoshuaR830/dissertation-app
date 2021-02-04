package com.joshuarichardson.fivewaystowellbeing.storage.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_ACTIVITY_RECORD_SURVEY_ACTIVITY_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.WELLBEING_QUESTIONS_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.WELLBEING_RECORDS_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.WELLBEING_RECORDS_QUESTION_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.WELLBEING_RECORDS_SEQUENCE_NUMBER;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.WELLBEING_RECORDS_SURVEY_ACTIVITY_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.WELLBEING_RECORDS_TABLE_NAME;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.WELLBEING_RECORDS_TIME;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.WELLBEING_RECORDS_USER_INPUT;

@Entity(
    tableName = WELLBEING_RECORDS_TABLE_NAME,
    foreignKeys = {
        @ForeignKey(entity = WellbeingQuestion.class, parentColumns = WELLBEING_QUESTIONS_ID, childColumns = WELLBEING_RECORDS_QUESTION_ID, onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = SurveyResponseActivityRecord.class, parentColumns = SURVEY_RESPONSE_ACTIVITY_RECORD_SURVEY_ACTIVITY_ID, childColumns = WELLBEING_RECORDS_SURVEY_ACTIVITY_ID, onDelete = ForeignKey.CASCADE)
    })
public class WellbeingRecord {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = WELLBEING_RECORDS_ID)
    private long id;

    @NonNull
    @ColumnInfo(name = WELLBEING_RECORDS_USER_INPUT)
    private Boolean userInput;

    @ColumnInfo(name = WELLBEING_RECORDS_TIME)
    private long time;

    @ColumnInfo(name = WELLBEING_RECORDS_SURVEY_ACTIVITY_ID)
    private long surveyActivityId;

    @ColumnInfo(name = WELLBEING_RECORDS_SEQUENCE_NUMBER)
    private int sequenceNumber;

    @ColumnInfo(name = WELLBEING_RECORDS_QUESTION_ID)
    private long questionId;

    public WellbeingRecord(@NonNull Boolean userInput, long time, long surveyActivityId, int sequenceNumber, long questionId) {
        this.userInput = userInput;
        this.time = time;
        this.surveyActivityId = surveyActivityId;
        this.sequenceNumber = sequenceNumber;
        this.questionId = questionId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public int getSequenceNumber() {
        return this.sequenceNumber;
    }

    @NonNull
    public Boolean getUserInput() {
        return this.userInput;
    }

    public long getQuestionId() {
        return this.questionId;
    }

    public long getSurveyActivityId() {
        return this.surveyActivityId;
    }

    public long getTime() {
        return this.time;
    }
}
