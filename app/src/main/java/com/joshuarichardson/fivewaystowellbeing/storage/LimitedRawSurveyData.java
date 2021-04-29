package com.joshuarichardson.fivewaystowellbeing.storage;

import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;

import java.util.ArrayList;
import java.util.List;

/**
 * Facilitates display of old style activities from the first version of the app
 */
public class LimitedRawSurveyData {

    private final long date;
    private final String surveyNote;
    private final String activityName;
    private final String activityType;

    @Deprecated
    // This allows old activities to continue to be shown after updating
    public LimitedRawSurveyData(long date, String surveyNote, String activityName, String activityType) {
        this.date = date;
        this.surveyNote = surveyNote;
        this.activityName = activityName;
        this.activityType = activityType;
    }

    /**
     * Create a list of items that are compatible with the current version of activities
     *
     * @param limitedList A list of data sourced from old activities
     * @return A compatible list of activities
     */
    public static List<RawSurveyData> convertToRawSurveyDataList(List<LimitedRawSurveyData> limitedList) {
        ArrayList<RawSurveyData> rawData = new ArrayList<>();

        for (LimitedRawSurveyData listItem : limitedList) {
            rawData.add(new RawSurveyData(listItem.getDate(), listItem.getSurveyNote(), listItem.getActivityNote(), listItem.getActivityName(), listItem.getSurveyActivityId(), listItem.getQuestion(), listItem.getWellbeingRecordId(), listItem.getUserInput(), listItem.getActivityType(), listItem.getWayToWellbeing(), listItem.getStartTime(), listItem.getEndTime(), listItem.getEmotion(), listItem.getIsDone()));
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

    public String getWayToWellbeing() {
        return WaysToWellbeing.UNASSIGNED.toString();
    }

    public long getStartTime() {
        return -1;
    }

    public long getEndTime() {
        return -1;
    }

    public int getEmotion() {
        return 0;
    }

    public boolean getIsDone() {
        return false;
    }
}
