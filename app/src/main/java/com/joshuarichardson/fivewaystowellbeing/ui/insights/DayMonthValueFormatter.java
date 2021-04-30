package com.joshuarichardson.fivewaystowellbeing.ui.insights;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.joshuarichardson.fivewaystowellbeing.TimeFormatter;

import java.util.List;

/**
 * Formatter to display the correct date on the line graph
 */
public class DayMonthValueFormatter extends ValueFormatter {

    private final List<Long> times;

    public DayMonthValueFormatter(List<Long> times) {
        this.times = times;
    }

    @Override
    // Reference https://weeklycoding.com/mpandroidchart-documentation/formatting-data-values/
    public String getAxisLabel(float value, AxisBase axis) {
        return TimeFormatter.formatTimeAsDayMonthString(times.get((int)value));
    }
}