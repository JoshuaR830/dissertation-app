package com.joshuarichardson.fivewaystowellbeing;

import android.content.Context;

public class DisplayHelper {
    //    Reference: http://www.androidtutorialshub.com/android-convert-dp-px-px-dp/
    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }

    public static int getDisplayWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getDisplayHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getSmallestMaxDimension(Context context) {
        int height = getDisplayHeight(context);
        int width = getDisplayWidth(context);

        return Math.min(height, width);
    }
}
