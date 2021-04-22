package com.joshuarichardson.fivewaystowellbeing;

import java.util.Calendar;

// Found out how to get today midnight from https://stackoverflow.com/a/6850919/13496270
public class TimeHelper {
    public static long getStartOfDay(long time) {
        Calendar startOfDay = Calendar.getInstance();
        startOfDay.setTimeInMillis(time);
        startOfDay.set(Calendar.HOUR_OF_DAY, 0);
        startOfDay.set(Calendar.MINUTE, 0);
        startOfDay.set(Calendar.SECOND, 0);
        startOfDay.set(Calendar.MILLISECOND, 0);
        return startOfDay.getTimeInMillis();
    }

    public static long getEndOfDay(long time) {
        Calendar endOfDay = Calendar.getInstance();
        endOfDay.setTimeInMillis(time);
        endOfDay.set(Calendar.HOUR_OF_DAY, 23);
        endOfDay.set(Calendar.MINUTE, 59);
        endOfDay.set(Calendar.SECOND, 59);
        endOfDay.set(Calendar.MILLISECOND, 999);

        return endOfDay.getTimeInMillis();
    }
}
