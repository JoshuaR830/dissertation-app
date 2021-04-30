package com.joshuarichardson.fivewaystowellbeing.ui.wellbeing_support;

/**
 * This is used for details about wellbeing support
 */
public class WellbeingSupportItem {

    private final String title;
    private final String description;
    private final int imageResourceId;
    private final String websiteUrl;

    // This is used for details about wellbeing support
    public  WellbeingSupportItem(String title, String description, int imageResourceId, String websiteUrl) {
        this.title = title;
        this.description = description;
        this.imageResourceId = imageResourceId;
        this.websiteUrl = websiteUrl;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public int getImageResourceId() {
        return this.imageResourceId;
    }

    public String getWebsiteUrl() {
        return this.websiteUrl;
    }
}
