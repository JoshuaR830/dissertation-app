package com.joshuarichardson.fivewaystowellbeing.ui.history;

import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphValueHelper;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;

/**
 * The data to show on the survey history page
 */
public class HistoryPageData {
    private long surveyResponseId;
    private long surveyResponseTimestamp;
    private String surveyResponseWayToWellbeing;
    private String title;
    private String description;
    private WellbeingGraphValueHelper wellbeingValues;

    public HistoryPageData(SurveyResponse surveyResponse, WellbeingGraphValueHelper wellbeingGraphValues) {
        this.surveyResponseId = surveyResponse.getSurveyResponseId();
        this.surveyResponseTimestamp = surveyResponse.getSurveyResponseTimestamp();
        this.surveyResponseWayToWellbeing = surveyResponse.getSurveyResponseWayToWellbeing();
        this.title = surveyResponse.getTitle();
        this.description = surveyResponse.getDescription();
        this.wellbeingValues = wellbeingGraphValues;
    }

    public long getSurveyResponseId() {
        return this.surveyResponseId;
    }

    public long getSurveyResponseTimestamp() {
        return this.surveyResponseTimestamp;
    }

    public String getSurveyResponseWayToWellbeing() {
        return this.surveyResponseWayToWellbeing;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public WellbeingGraphValueHelper getWellbeingValues() {
        return this.wellbeingValues;
    }

    public void setSurveyResponseWayToWellbeing(WaysToWellbeing wayToWellbeing) {
        this.surveyResponseWayToWellbeing = wayToWellbeing.toString();
    }
}
