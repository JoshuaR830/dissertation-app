package com.joshuarichardson.fivewaystowellbeing.storage;

public class RawSurveyData {

    private long wellbeingRecordId;
    private long date;
    private String surveyNote;
    private String activityNote;
    private String activityName;
    private long surveyActivityId;
    private String question;
    private Boolean userInput;
    private String activityType;
    private String wayToWellbeing;

    public RawSurveyData(long date, String surveyNote, String activityNote, String activityName, long surveyActivityId, String question, long wellbeingRecordId, Boolean userInput, String activityType, String wayToWellbeing) {
        this.date = date;
        this.surveyNote = surveyNote;
        this.activityNote = activityNote;
        this.activityName = activityName;
        this.surveyActivityId = surveyActivityId;
        this.question = question;
        this.userInput = userInput;
        this.wellbeingRecordId = wellbeingRecordId;
        this.activityType = activityType;
        this.wayToWellbeing = wayToWellbeing;
    }

    public long getSurveyActivityId() {
        return this.surveyActivityId;
    }

    public Boolean getUserInput() {
        return this.userInput;
    }

    public String getQuestion() {
        return this.question;
    }

    public long getDate() {
        return this.date;
    }

    public String getActivityName() {
        return this.activityName;
    }

    public String getActivityNote() {
        return this.activityNote;
    }

    public String getActivityType() {
        return this.activityType;
    }

    public String getSurveyNote() {
        return this.surveyNote;
    }

    public long getWellbeingRecordId() {
        return this.wellbeingRecordId;
    }

    public String getWayToWellbeing() {
        return this.wayToWellbeing;
    }
}