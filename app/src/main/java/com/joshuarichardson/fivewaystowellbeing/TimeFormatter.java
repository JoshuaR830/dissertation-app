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

    public static String formatTimeAsDayMonthString(long time) {
        if(time < 0) {
            return null;
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM", Locale.getDefault());
        return dateFormatter.format(time);
    }

    public static String formatTimeAsHourMinuteString(long time) {
        if(time < 0 || time > 86400000) {
            return null;
        }

        long myHours = time / 3600000;
        long myMinutes = (time - (myHours * 3600000)) / 60000;

        return String.format(Locale.getDefault(), "%02d:%02d", myHours, myMinutes);
    }
}
