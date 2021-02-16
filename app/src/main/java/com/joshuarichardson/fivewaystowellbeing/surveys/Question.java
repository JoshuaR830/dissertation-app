package com.joshuarichardson.fivewaystowellbeing.surveys;

public class Question {
    private String question;
    private long wellbeingRecordId;
    private Boolean userResponse;
    private String wayToWellbeing;

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