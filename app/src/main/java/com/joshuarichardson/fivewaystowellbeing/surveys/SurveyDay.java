package com.joshuarichardson.fivewaystowellbeing.surveys;

import com.joshuarichardson.fivewaystowellbeing.TimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SurveyDay {

    private String title;
    private String note;
    private ArrayList<Long> passtimeSurveyKeys;
    private HashMap<Long, Passtime> passtimeMap;
    private long timestamp;

    // ToDo - get the data about the ways to wellbeing achieved for the day
    private List<Integer> waysToWellbeingValues;

    public SurveyDay(long time, String surveyNote, ArrayList<Long> activityRecordIds, HashMap<Long, Passtime> list) {
        this.title = TimeFormatter.formatTimeAsDayMonthYearString(time);
        this.timestamp = time;
        this.note = surveyNote;
        this.passtimeSurveyKeys = activityRecordIds;
        this.passtimeMap = list;
    }

    public String getTitle() {
        return this.title;
    }

    public String getNote() {
        return this.note;
    }

    public ArrayList<Long> getPasstimeSurveyKeys() {
        return this.passtimeSurveyKeys;
    }

    public HashMap<Long, Passtime> getPasstimeMap() {
        return this.passtimeMap;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}
