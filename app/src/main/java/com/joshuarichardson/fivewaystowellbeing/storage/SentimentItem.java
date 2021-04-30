package com.joshuarichardson.fivewaystowellbeing.storage;

/**
 * Instance of an emotion's colour and image.
 */
public class SentimentItem {
    private final int colorResource;
    private final int imageResource;

    public SentimentItem(int imageResource, int colorResource) {
        this.imageResource = imageResource;
        this.colorResource = colorResource;
    }

    public int getColorResource() {
        return this.colorResource;
    }

    public int getImageResource() {
        return this.imageResource;
    }
}
