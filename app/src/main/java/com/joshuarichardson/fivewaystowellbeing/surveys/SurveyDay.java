package com.joshuarichardson.fivewaystowellbeing.surveys;

import com.joshuarichardson.fivewaystowellbeing.TimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A representation of a survey with a note, title, timestamp and list of activities
 */
public class SurveyDay {

    private final String title;
    private final String note;
    private final ArrayList<Long> activitySurveyKeys;
    private final HashMap<Long, ActivityInstance> activityMap;
    private final long timestamp;

    public SurveyDay(long time, String surveyNote, ArrayList<Long> activityRecordIds, HashMap<Long, ActivityInstance> list) {
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

    public HashMap<Long, ActivityInstance> getActivityMap() {
        return this.activityMap;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}
