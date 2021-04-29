package com.joshuarichardson.fivewaystowellbeing.storage;

/**
 * The values to plot on the progress graph for a specific way to wellbeing
 */
public class WellbeingGraphItem {
    private final String wayToWellbeing;
    private final int value;

    public WellbeingGraphItem(String wayToWellbeing, int value) {
        this.wayToWellbeing = wayToWellbeing;
        this.value = value;
    }

    public String getWayToWellbeing() {
        return this.wayToWellbeing;
    }

    public int getValue() {
        return this.value;
    }
}
