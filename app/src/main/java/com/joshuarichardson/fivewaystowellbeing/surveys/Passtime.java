package com.joshuarichardson.fivewaystowellbeing.surveys;

import java.util.ArrayList;
import java.util.List;

public class Passtime {

    private final String name;
    private final String note;
    private final String type;
    private long startTime;
    private long endTime;
    private final long activitySurveyId;
    private final String wayToWellbeing;

    ArrayList<Question> questions;

    public Passtime(String name, String note, String type, String wayToWellbeing, long activitySurveyId, long startTime, long endTime) {
        this.questions = new ArrayList<>();
        this.name = name;
        this.note = note;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.activitySurveyId = activitySurveyId;
        this.wayToWellbeing = wayToWellbeing;
    }

    public void addQuestionToList(Question question) {
        this.questions.add(question);
    }

    public List<Question> getQuestions() {
        return this.questions;
    }

    public String getNote() {
        return this.note;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public String getWayToWellbeing() {
        return this.wayToWellbeing;
    }

    public long getActivitySurveyId() {
        return this.activitySurveyId;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public void setStartTime(long startTimeMillis) {
        this.startTime = startTimeMillis;
    }

    public void setEndTime(long endTimeMillis) {
        this.endTime = endTimeMillis;
    }
}
