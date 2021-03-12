package com.joshuarichardson.fivewaystowellbeing.ui.insights;

import android.view.View;

import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;

import androidx.annotation.Nullable;

public class InsightsItem {
    private String title;
    private WaysToWellbeing wayToWellbeing;
    private int currentValue;
    private int oldValue;
    private String info;
    private View specialView;
    private int columnWidth;

    public InsightsItem(String title, WaysToWellbeing wayToWellbeing, int currentValue, int oldValue) {
        this.title = title;
        this.wayToWellbeing = wayToWellbeing;
        this.currentValue = currentValue;
        this.specialView = null;
        this.oldValue = oldValue;
        this.columnWidth = 1;
    }

    public InsightsItem(String title, String info, int columnWidth, @Nullable View specialView) {
        this.title = title;
        this.info = info;
        this.specialView = specialView;
        this.columnWidth = columnWidth;
    }

    public String getTitle() {
        return this.title;
    }

    public int getCurrentValue() {
        return this.currentValue;
    }

    public String getInfo() {
        return this.info;
    }

    public int getValueDifference() {
        // If old value is bigger then this will be -ve, if current value is higher it will be +ve
        return this.currentValue - this.oldValue;
    }

    public WaysToWellbeing getWayToWellbeing() {
        return this.wayToWellbeing;
    }

    public View getSpecialView() {
        return this.specialView;
    }

    public int getColumnWidth() {
        return this.columnWidth;
    }
}
