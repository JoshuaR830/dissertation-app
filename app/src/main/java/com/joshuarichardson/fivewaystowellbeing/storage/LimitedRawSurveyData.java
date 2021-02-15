package com.joshuarichardson.fivewaystowellbeing.storage;

import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;

import java.util.ArrayList;
import java.util.List;

public class LimitedRawSurveyData {

    private long date;
    private String surveyNote;
    private String activityName;
    private String activityType;

    @Deprecated
    // This allows old activities to continue to be shown after updating
    public LimitedRawSurveyData(long date, String surveyNote, String activityName, String activityType) {
        this.date = date;
        this.surveyNote = surveyNote;
        this.activityName = activityName;
        this.activityType = activityType;
    }

    public static List<RawSurveyData> convertToRawSurveyDataList(List<LimitedRawSurveyData> limitedList) {
        ArrayList<RawSurveyData> rawData = new ArrayList<>();

        for (LimitedRawSurveyData listItem : limitedList) {
            rawData.add(new RawSurveyData(listItem.getDate(), listItem.getSurveyNote(), listItem.getActivityNote(), listItem.getActivityName(), listItem.getSurveyActivityId(), listItem.getQuestion(), listItem.getWellbeingRecordId(), listItem.getUserInput(), listItem.getActivityType(), WaysToWellbeing.UNASSIGNED.toString()));
        }

        return rawData;
    }

    public long getSurveyActivityId() {
        return -1;
    }

    public Boolean getUserInput() {
        return false;
    }

    public String getQuestion() {
        return null;
    }

    public long getDate() {
        return this.date;
    }

    public String getActivityName() {
        return this.activityName;
    }

    public String getActivityNote() {
        return null;
    }

    public String getSurveyNote() {
        return this.surveyNote;
    }

    public long getWellbeingRecordId() {
        return -1;
    }

    public String getActivityType() {
        return this.activityType;
    }
}
