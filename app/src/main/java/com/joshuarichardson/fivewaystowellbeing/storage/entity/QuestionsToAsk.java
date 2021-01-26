package com.joshuarichardson.fivewaystowellbeing.storage.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.QUESTIONS_TO_ASK_EXTRA_DATA;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.QUESTIONS_TO_ASK_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.QUESTIONS_TO_ASK_QUESTION;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.QUESTIONS_TO_ASK_REASON;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.QUESTIONS_TO_ASK_SEQUENCE_NUMBER;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.QUESTIONS_TO_ASK_SET_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.QUESTIONS_TO_ASK_TABLE_NAME;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.QUESTIONS_TO_ASK_TYPE;

@Entity(tableName = QUESTIONS_TO_ASK_TABLE_NAME)
public class QuestionsToAsk {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = QUESTIONS_TO_ASK_ID)
    public long id;

    @ColumnInfo(name = QUESTIONS_TO_ASK_QUESTION)
    public String question;

    @ColumnInfo(name = QUESTIONS_TO_ASK_REASON)
    public String reason;

    @NonNull
    @ColumnInfo(name = QUESTIONS_TO_ASK_SET_ID)
    public long setId;

    @ColumnInfo(name = QUESTIONS_TO_ASK_TYPE)
    public String type;

    @ColumnInfo(name = QUESTIONS_TO_ASK_SEQUENCE_NUMBER)
    public int sequenceNumber;

    @ColumnInfo(name = QUESTIONS_TO_ASK_EXTRA_DATA)
    public String extraData;

    public QuestionsToAsk(String question, String reason, long setId, String type, int sequenceNumber, String extraData) {
        this.question = question;
        this.reason = reason;
        this.setId = setId;
        this.type = type;
        this.sequenceNumber = sequenceNumber;
        this.extraData = extraData;
    }

    public long getId() {
        return this.id;
    }

    public String getQuestion() {
        return this.question;
    }

    public long getSetId() {
        return this.setId;
    }

    public String getReason() {
        return this.reason;
    }

    public String getType() {
        return this.type;
    }

    public int getSequenceNumber() {
        return this.sequenceNumber;
    }
}
