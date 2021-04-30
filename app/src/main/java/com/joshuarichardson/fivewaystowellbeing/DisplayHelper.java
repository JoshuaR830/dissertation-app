package com.joshuarichardson.fivewaystowellbeing;

import android.content.Context;

/**
 * A helper for getting display sizes
 */
public class DisplayHelper {
    /**
     * Convert display points to pixels
     *
     * @param context The applciation context
     * @param dp The number of display points
     * @return The number of pixels
     *
     * Reference: http://www.androidtutorialshub.com/android-convert-dp-px-px-dp/
     */
    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }

    /**
     * Get the width of the display in pixels
     *
     * @param context The applicatin context
     * @return The number of pixels wide
     */
    public static int getDisplayWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * get the height of the display
     *
     * @param context The application context
     * @return The number of picels wide
     */
    public static int getDisplayHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * Get the smallest dimension between width and height
     *
     * @param context The application context
     * @return The minimum size
     */
    public static int getSmallestMaxDimension(Context context) {
        int height = getDisplayHeight(context);
        int width = getDisplayWidth(context);

        return Math.min(height, width);
    }
}
