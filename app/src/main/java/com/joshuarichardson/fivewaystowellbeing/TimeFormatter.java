package com.joshuarichardson.fivewaystowellbeing;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * A helper to format times and dates
 */
public class TimeFormatter {
    /**
     * Convert a time to a string in the form dd MMM yyyy
     *
     * @param time A timestamp in milliseconds
     * @return A time string
     */
    public static String formatTimeAsDayMonthYearString(long time) {
        if(time < 0) {
            return null;
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return dateFormatter.format(time);
    }

    /**
     * Convert a time to a string in the form dd MMM
     *
     * @param time A timestamp in milliseconds
     * @return A time string
     */
    public static String formatTimeAsDayMonthString(long time) {
        if(time < 0) {
            return null;
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM", Locale.getDefault());
        return dateFormatter.format(time);
    }

    /**
     * Convert a time to a string in the form hh:mm
     *
     * @param time A timestamp in milliseconds
     * @return A time string
     */
    public static String formatTimeAsHourMinuteString(long time) {
        if(time < 0 || time > 86400000) {
            return null;
        }

        long myHours = time / 3600000;
        long myMinutes = (time - (myHours * 3600000)) / 60000;

        return String.format(Locale.getDefault(), "%02d:%02d", myHours, myMinutes);
    }
}
