package com.joshuarichardson.fivewaystowellbeing.storage;

import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;

import java.util.List;

import static com.joshuarichardson.fivewaystowellbeing.storage.DatabaseQuestionHelper.ACTIVITY_OF_TYPE_VALUE;

/**
 * Helper to transform the wellbeing data for visualisations
 */
public class WellbeingGraphValueHelper {

    private final int connectValue;
    private final int beActiveValue;
    private final int keepLearningValue;
    private final int takeNoticeValue;
    private final int giveValue;

    public WellbeingGraphValueHelper(int connect, int beActive, int keepLearning, int takeNotice, int give) {
        this.connectValue = connect;
        this.beActiveValue = beActive;
        this.keepLearningValue = keepLearning;
        this.takeNoticeValue = takeNotice;
        this.giveValue = give;
    }

    public int getBeActiveValue() {
        return Math.min((this.beActiveValue), 100);
    }

    public int getConnectValue() {
        return Math.min((this.connectValue), 100);
    }

    public int getGiveValue() {
        return Math.min((this.giveValue), 100);
    }

    public int getKeepLearningValue() {
        return Math.min((this.keepLearningValue), 100);
    }

    public int getTakeNoticeValue() {
        return Math.min((this.takeNoticeValue), 100);
    }

    /**
     * Collate the graph data for each way to wellbeing
     *
     * @param wayToWellBeingGraphValue A list of graph items with a way to wellbeing and value
     * @return Return the helper which contains all of the collated data
     */
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
