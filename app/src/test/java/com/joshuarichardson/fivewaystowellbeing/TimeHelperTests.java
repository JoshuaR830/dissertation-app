package com.joshuarichardson.fivewaystowellbeing;

import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.google.common.truth.Truth.assertThat;

public class TimeHelperTests {
    @Test
    public void whenPartWayThroughDay_ShouldGetEndOfThatDay() {
        GregorianCalendar originalTime = new GregorianCalendar(2021, 1, 12, 8, 23, 33);
        originalTime.set(Calendar.MILLISECOND, 123);
        long endOfDay = TimeHelper.getEndOfDay(originalTime.getTimeInMillis());

        Calendar expectedTime = new GregorianCalendar(2021, 1, 12, 23, 59, 59);
        expectedTime.set(Calendar.MILLISECOND, 999);
        assertThat(endOfDay).isEqualTo(expectedTime.getTimeInMillis());
    }

    @Test
    public void whenStartOfDay_ShouldGetEndOfThatDay() {
        GregorianCalendar originalTime = new GregorianCalendar(2022, 2, 12, 0, 0, 0);
        originalTime.set(Calendar.MILLISECOND, 0);
        long endOfDay = TimeHelper.getEndOfDay(originalTime.getTimeInMillis());

        Calendar expectedTime = new GregorianCalendar(2022, 2, 12, 23, 59, 59);
        expectedTime.set(Calendar.MILLISECOND, 999);
        assertThat(endOfDay).isEqualTo(expectedTime.getTimeInMillis());
    }

    @Test
    public void whenEndOfDay_ShouldGetEndOfThatDay() {
        GregorianCalendar originalTime = new GregorianCalendar(2023, 3, 10, 23, 59, 59);
        originalTime.set(Calendar.MILLISECOND, 999);
        long endOfDay = TimeHelper.getEndOfDay(originalTime.getTimeInMillis());

        Calendar expectedTime = new GregorianCalendar(2023, 3, 10, 23, 59, 59);
        expectedTime.set(Calendar.MILLISECOND, 999);
        assertThat(endOfDay).isEqualTo(expectedTime.getTimeInMillis());
    }

    @Test
    public void whenPartWayThroughDay_ShouldGetStartOfThatDay() {
        GregorianCalendar originalTime = new GregorianCalendar(2021, 1, 12, 8, 23, 33);
        originalTime.set(Calendar.MILLISECOND, 123);
        long startOfDay = TimeHelper.getStartOfDay(originalTime.getTimeInMillis());

        Calendar expectedTime = new GregorianCalendar(2021, 1, 12, 0, 0, 0);
        expectedTime.set(Calendar.MILLISECOND, 0);
        assertThat(startOfDay).isEqualTo(expectedTime.getTimeInMillis());
    }

    @Test
    public void whenStartOfDay_ShouldGetStartOfThatDay() {
        GregorianCalendar originalTime = new GregorianCalendar(2022, 2, 12, 0, 0, 0);
        originalTime.set(Calendar.MILLISECOND, 0);
        long startOfDay = TimeHelper.getStartOfDay(originalTime.getTimeInMillis());

        Calendar expectedTime = new GregorianCalendar(2022, 2, 12, 0, 0, 0);
        expectedTime.set(Calendar.MILLISECOND, 0);
        assertThat(startOfDay).isEqualTo(expectedTime.getTimeInMillis());
    }

    @Test
    public void whenEndOfDay_ShouldGetStartOfThatDay() {
        GregorianCalendar originalTime = new GregorianCalendar(2023, 3, 10, 23, 59, 59);
        originalTime.set(Calendar.MILLISECOND, 999);
        long startOfDay = TimeHelper.getStartOfDay(originalTime.getTimeInMillis());

        Calendar expectedTime = new GregorianCalendar(2023, 3, 10, 0, 0, 0);
        expectedTime.set(Calendar.MILLISECOND, 0);
        assertThat(startOfDay).isEqualTo(expectedTime.getTimeInMillis());
    }
}
