package com.joshuarichardson.fivewaystowellbeing.surveys;

import com.joshuarichardson.fivewaystowellbeing.TimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SurveyDay {

    private String title;
    private String note;
    private ArrayList<Long> activitySurveyKeys;
    private HashMap<Long, UserActivity> activityMap;
    private long timestamp;

    // ToDo - get the data about the ways to wellbeing achieved for the day
    private List<Integer> waysToWellbeingValues;

    public SurveyDay(long time, String surveyNote, ArrayList<Long> activityRecordIds, HashMap<Long, UserActivity> list) {
        this.title = TimeFormatter.formatTimeAsDayMonthYearString(time);
        this.timestamp = time;
        this.note = surveyNote;
        this.activitySurveyKeys = activityRecordIds;
        this.activityMap = list;
    }

    public String getTitle() {
        return this.title;
    }

    public String getNote() {
        return this.note;
    }

    public ArrayList<Long> getActivitySurveyKeys() {
        return this.activitySurveyKeys;
    }

    public HashMap<Long, UserActivity> getActivityMap() {
        return this.activityMap;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}
