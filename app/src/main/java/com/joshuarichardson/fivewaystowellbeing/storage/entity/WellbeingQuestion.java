package com.joshuarichardson.fivewaystowellbeing.storage.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.WELLBEING_QUESTIONS_ACTIVITY_TYPE;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.WELLBEING_QUESTIONS_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.WELLBEING_QUESTIONS_INPUT_TYPE;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.WELLBEING_QUESTIONS_NEGATIVE_MESSAGE;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.WELLBEING_QUESTIONS_POSITIVE_MESSAGE;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.WELLBEING_QUESTIONS_QUESTION;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.WELLBEING_QUESTIONS_TABLE_NAME;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.WELLBEING_QUESTIONS_WAY_TO_WELLBEING;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.WELLBEING_QUESTIONS_WEIGHTING;

@Entity(tableName = WELLBEING_QUESTIONS_TABLE_NAME)
public class WellbeingQuestion {

    @ColumnInfo(name = WELLBEING_QUESTIONS_ID)
    @PrimaryKey
    private long id;

    @NonNull
    @ColumnInfo(name = WELLBEING_QUESTIONS_QUESTION)
    private String question;

    @NonNull
    @ColumnInfo(name = WELLBEING_QUESTIONS_POSITIVE_MESSAGE)
    private String positiveMessage;

    @NonNull
    @ColumnInfo(name = WELLBEING_QUESTIONS_NEGATIVE_MESSAGE)
    private String negativeMessage;

    @NonNull
    @ColumnInfo(name = WELLBEING_QUESTIONS_WAY_TO_WELLBEING)
    private String wayToWellbeing;

    @ColumnInfo(name = WELLBEING_QUESTIONS_WEIGHTING)
    private int weighting;

    @NonNull
    @ColumnInfo(name = WELLBEING_QUESTIONS_ACTIVITY_TYPE)
    private String activityType;

    @NonNull
    @ColumnInfo(name = WELLBEING_QUESTIONS_INPUT_TYPE)
    private String inputType;

    public WellbeingQuestion(long id, String question, String positiveMessage, String negativeMessage, String wayToWellbeing, int weighting, String activityType, String inputType) {
        // ToDo - implement this
        this.id = id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    @NonNull
    public String getQuestion() {
        return this.question;
    }

    @NonNull
    public String getActivityType() {
        return this.activityType;
    }

    public int getWeighting() {
        return this.weighting;
    }

    @NonNull
    public String getInputType() {
        return this.inputType;
    }

    @NonNull
    public String getNegativeMessage() {
        return this.negativeMessage;
    }

    @NonNull
    public String getPositiveMessage() {
        return this.positiveMessage;
    }

    @NonNull
    public String getWayToWellbeing() {
        return this.wayToWellbeing;
    }
}
