package com.joshuarichardson.fivewaystowellbeing.storage;

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
