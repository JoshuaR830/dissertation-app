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
}
