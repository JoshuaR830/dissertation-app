package com.joshuarichardson.fivewaystowellbeing;

import org.junit.Test;

import java.util.GregorianCalendar;

import static com.google.common.truth.Truth.assertThat;

public class TimeFormatterTests {
    @Test
    public void timeStampToDayMonthYearString() {
        String time = TimeFormatter.formatTimeAsDayMonthYearString(new GregorianCalendar(1999, 2, 29, 15, 10).getTimeInMillis());
        assertThat(time).isEqualTo("29 Mar 1999");
    }

    @Test
    public void beforeEpochBegan() {
        String time = TimeFormatter.formatTimeAsDayMonthYearString(-1);
        assertThat(time).isEqualTo(null);
    }

    @Test
    public void after32BitEpoch() {
        String time = TimeFormatter.formatTimeAsDayMonthYearString(new GregorianCalendar(3100, 0, 19, 3, 15).getTimeInMillis());
        assertThat(time).isEqualTo("19 Jan 3100");
    }

    @Test
    public void timeStampToDayMonthString() {
        String time = TimeFormatter.formatTimeAsDayMonthString(new GregorianCalendar(1999, 2, 29, 15, 10).getTimeInMillis());
        assertThat(time).isEqualTo("29 Mar");
    }

    @Test
    public void timeStampToDayMonthBeforeEpochBegan() {
        String time = TimeFormatter.formatTimeAsDayMonthString(-1);
        assertThat(time).isEqualTo(null);
    }

    @Test
    public void timeStampToDayMonthAfter32BitEpoch() {
        String time = TimeFormatter.formatTimeAsDayMonthString(new GregorianCalendar(3100, 0, 19, 3, 15).getTimeInMillis());
        assertThat(time).isEqualTo("19 Jan");
    }

    @Test
    public void timeStampToHourMinuteString() {
        String time = TimeFormatter.formatTimeAsHourMinuteString(36660000);
        assertThat(time).isEqualTo("10:11");
    }

    @Test
    public void checkCorrectPadding() {
        String time = TimeFormatter.formatTimeAsHourMinuteString(36060000);
        assertThat(time).isEqualTo("10:01");
    }

    @Test
    public void lessThanZero() {
        String time = TimeFormatter.formatTimeAsHourMinuteString(-1);
        assertThat(time).isEqualTo(null);
    }

    @Test
    public void exactlyOneDay() {
        String time = TimeFormatter.formatTimeAsHourMinuteString(86400000);
        assertThat(time).isEqualTo("24:00");
    }

    @Test
    public void moreThanToday() {
        String time = TimeFormatter.formatTimeAsHourMinuteString(86400001);
        assertThat(time).isEqualTo(null);
    }
}
