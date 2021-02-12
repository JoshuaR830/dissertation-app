package com.joshuarichardson.fivewaystowellbeing.storage;

import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;

import java.util.List;

public class WellbeingGraphValueHelper {

    private int connectValue;
    private int beActiveValue;
    private int keepLearningValue;
    private int takeNoticeValue;
    private int giveValue;

    public WellbeingGraphValueHelper(int connect, int beActive, int keepLearning, int takeNotice, int give) {
        this.connectValue = connect;
        this.beActiveValue = beActive;
        this.keepLearningValue = keepLearning;
        this.takeNoticeValue = takeNotice;
        this.giveValue = give;
    }

    public int getBeActiveValue() {
        return this.beActiveValue;
    }

    public int getConnectValue() {
        return this.connectValue;
    }

    public int getGiveValue() {
        return this.giveValue;
    }

    public int getKeepLearningValue() {
        return this.keepLearningValue;
    }

    public int getTakeNoticeValue() {
        return this.takeNoticeValue;
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
}
