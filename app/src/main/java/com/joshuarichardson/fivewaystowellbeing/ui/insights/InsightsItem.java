package com.joshuarichardson.fivewaystowellbeing.ui.insights;

import android.view.View;

import androidx.annotation.Nullable;

public class InsightsItem {
    private String title;
    private String info;
    private View specialView;
    private int columnWidth;

    public InsightsItem(String title, String info) {
        this.title = title;
        this.info = info;
        this.specialView = null;
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

    public String getInfo() {
        return this.info;
    }

    public View getSpecialView() {
        return this.specialView;
    }

    public int getColumnWidth() {
        return this.columnWidth;
    }
}
