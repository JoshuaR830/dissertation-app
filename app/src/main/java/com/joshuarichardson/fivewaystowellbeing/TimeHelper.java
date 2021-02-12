package com.joshuarichardson.fivewaystowellbeing;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeHelper {
    public static long getStartOfDay(long time) {
        Date date = new Date(time);
        Calendar startOfDay = new GregorianCalendar();
        startOfDay.setTime(date);
        startOfDay.set(Calendar.HOUR_OF_DAY, 0);
        startOfDay.set(Calendar.MINUTE, 0);
        startOfDay.set(Calendar.SECOND, 0);
        startOfDay.set(Calendar.MILLISECOND, 0);
        return startOfDay.getTimeInMillis();
    }

    public static long getEndOfDay(long time) {
        Date date = new Date(time);
        Calendar endOfDay = new GregorianCalendar();
        endOfDay.setTime(date);
        endOfDay.set(Calendar.HOUR_OF_DAY, 23);
        endOfDay.set(Calendar.MINUTE, 59);
        endOfDay.set(Calendar.SECOND, 59);
        endOfDay.set(Calendar.MILLISECOND, 999);

        return endOfDay.getTimeInMillis();
    }
}
