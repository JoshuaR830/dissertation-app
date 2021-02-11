package com.joshuarichardson.fivewaystowellbeing;

import android.content.Context;

public class DisplayHelper {
    //    Reference: http://www.androidtutorialshub.com/android-convert-dp-px-px-dp/
    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }
}
