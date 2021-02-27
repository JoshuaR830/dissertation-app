package com.joshuarichardson.fivewaystowellbeing.notifications;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.google.common.truth.Truth.assertThat;

public class GetTimeToScheduleTests {

    private Calendar calendar;

    @Before
    public void setup() {
        calendar = GregorianCalendar.getInstance();
    }

    @Test
    public void whenGreaterThanCurrentTime_ShouldBeScheduledForTomorrow() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int date = calendar.get(Calendar.DATE);
        calendar.add(Calendar.HOUR_OF_DAY, 1);


        long time = AlarmHelper.getTimeToSchedule(hour, 0);
        calendar.setTimeInMillis(time);

        assertThat(calendar.get(Calendar.DATE)).isEqualTo(date + 1);
    }

    @Test
    public void whenBeforeCurrentTime_shouldBeToday() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int date = calendar.get(Calendar.DATE);

        long time = AlarmHelper.getTimeToSchedule(hour + 1, 0);
        calendar.setTimeInMillis(time);

        if(hour + 1 > 23) {
            assertThat(calendar.get(Calendar.DATE)).isEqualTo(date + 1);
        } else {
            assertThat(calendar.get(Calendar.DATE)).isEqualTo(date);
        }
    }
}
