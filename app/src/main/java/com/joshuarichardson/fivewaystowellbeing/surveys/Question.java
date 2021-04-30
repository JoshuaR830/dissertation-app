package com.joshuarichardson.fivewaystowellbeing.surveys;

/**
 * Represents one of the checkbox question sub-activities
 */
public class Question {
    private final String question;
    private final long wellbeingRecordId;
    private final Boolean userResponse;

    public Question(String question, long wellbeingRecordId, Boolean userResponse) {
        this.question = question;
        this.wellbeingRecordId = wellbeingRecordId;
        this.userResponse = userResponse;
    }

    public String getQuestion() {
        return this.question;
    }

    public Boolean getUserResponse() {
        return this.userResponse;
    }

    public long getWellbeingRecordId() {
        return this.wellbeingRecordId;
    }
}