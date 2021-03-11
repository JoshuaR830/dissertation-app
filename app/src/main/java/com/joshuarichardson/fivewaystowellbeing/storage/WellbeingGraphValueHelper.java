package com.joshuarichardson.fivewaystowellbeing.storage;

import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;

import java.util.List;

import static com.joshuarichardson.fivewaystowellbeing.storage.DatabaseQuestionHelper.ACTIVITY_OF_TYPE_VALUE;

public class WellbeingGraphValueHelper {

    private int connectValue;
    private int beActiveValue;
    private int keepLearningValue;
    private int takeNoticeValue;
    private int giveValue;

    private int connectActivityValues = 0;
    private int beActiveActivityValues = 0;
    private int keepLearningActivityValues = 0;
    private int takeNoticeActivityValues = 0;
    private int giveActivityValues = 0;

    public WellbeingGraphValueHelper(int connect, int beActive, int keepLearning, int takeNotice, int give) {
        this.connectValue = connect;
        this.beActiveValue = beActive;
        this.keepLearningValue = keepLearning;
        this.takeNoticeValue = takeNotice;
        this.giveValue = give;
    }

    public int getBeActiveValue() {
        return Math.min((this.beActiveValue + this.beActiveActivityValues), 100);
    }

    public int getConnectValue() {
        return Math.min((this.connectValue + this.connectActivityValues), 100);
    }

    public int getGiveValue() {
        return Math.min((this.giveValue + this.giveActivityValues), 100);
    }

    public int getKeepLearningValue() {
        return Math.min((this.keepLearningValue + this.keepLearningActivityValues), 100);
    }

    public int getTakeNoticeValue() {
        return Math.min((this.takeNoticeValue + this.takeNoticeActivityValues), 100);
    }

    public static WellbeingGraphValueHelper getWellbeingGraphValues(List<WellbeingGraphItem> wayToWellBeingGraphValue) {
        int connect = 0;
        int beActive = 0;
        int keepLearning = 0;
        int takeNotice = 0;
        int give = 0;

        for(WellbeingGraphItem value : wayToWellBeingGraphValue) {

            WaysToWellbeing wayToWellbeing = WaysToWellbeing.valueOf(value.getWayToWellbeing());

            switch (wayToWellbeing) {
                case CONNECT:
                    connect += value.getValue();
                    break;
                case BE_ACTIVE:
                    beActive += value.getValue();
                    break;
                case KEEP_LEARNING:
                    keepLearning += value.getValue();
                    break;
                case TAKE_NOTICE:
                    takeNotice += value.getValue();
                    break;
                case GIVE:
                    give += value.getValue();
                    break;
            }
        }

        return new WellbeingGraphValueHelper(connect, beActive, keepLearning, takeNotice, give);
    }

    public void updateActivityValuesForWayToWellbeing(WaysToWellbeing wayToWellbeing) {
        switch(wayToWellbeing) {
            case CONNECT:
                this.connectActivityValues += ACTIVITY_OF_TYPE_VALUE;
                break;
            case BE_ACTIVE:
                this.beActiveActivityValues += ACTIVITY_OF_TYPE_VALUE;
                break;
            case KEEP_LEARNING:
                this.keepLearningActivityValues += ACTIVITY_OF_TYPE_VALUE;
                break;
            case TAKE_NOTICE:
                this.takeNoticeActivityValues += ACTIVITY_OF_TYPE_VALUE;
                break;
            case GIVE:
                this.giveActivityValues += ACTIVITY_OF_TYPE_VALUE;
                break;
            default:
                break;
        }
    }

    public void resetActivityValues() {
        this.connectActivityValues = 0;
        this.beActiveActivityValues = 0;
        this.keepLearningActivityValues = 0;
        this.takeNoticeActivityValues = 0;
        this.giveActivityValues = 0;
    }
}
