package com.joshuarichardson.fivewaystowellbeing;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TimeFormatter {
    public static String formatTimeAsDayMonthYearString(long time) {
        if(time < 0) {
            return null;
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return dateFormatter.format(time);
    }
}
