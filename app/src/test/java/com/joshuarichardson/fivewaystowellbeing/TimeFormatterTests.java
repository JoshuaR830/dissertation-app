package com.joshuarichardson.fivewaystowellbeing;

import org.junit.Test;

import java.util.GregorianCalendar;

import static com.google.common.truth.Truth.assertThat;

public class TimeFormatterTests {
    @Test
    public void timeStampToDayMonthYearString() {
        String time = TimeFormatter.formatTimeAsDayMonthYearString(new GregorianCalendar(1999, 2, 29, 15, 10).getTime().getTime());
        assertThat(time).isEqualTo("29 Mar 1999");
    }

    @Test
    public void beforeEpoch() {
        String time = TimeFormatter.formatTimeAsDayMonthYearString(new GregorianCalendar(1970, 11, 31, 24, 59).getTime().getTime());
        assertThat(time).isEqualTo(null);
    }

    @Test
    public void afterEpoch() {
        String time = TimeFormatter.formatTimeAsDayMonthYearString(new GregorianCalendar(3100, 0, 19, 3, 15).getTime().getTime());
        assertThat(time).isEqualTo("19 Jan 3100");
    }
}
