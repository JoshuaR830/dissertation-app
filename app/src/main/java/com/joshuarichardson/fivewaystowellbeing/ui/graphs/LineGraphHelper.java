package com.joshuarichardson.fivewaystowellbeing.ui.graphs;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.chip.ChipGroup;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.ui.insights.DayMonthValueFormatter;
import com.joshuarichardson.fivewaystowellbeing.ui.insights.InsightsAdapter;
import com.joshuarichardson.fivewaystowellbeing.ui.insights.InsightsItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Create the line graph for the five ways to wellbeing
 */
public class LineGraphHelper {
    /**
     * Create the line graph for the insights page
     *
     * @param context The application context
     * @param graphCard The view that the graph will populate
     * @param insightsItem The insights data that will be displayed
     * @param callback The callback for when the chip is clicked
     */
    public static void drawGraph(Context context, View graphCard, InsightsItem insightsItem, InsightsAdapter.ChipInfoCallback callback) {
        LineChart dailyAchieved = graphCard.findViewById(R.id.daily_wellbeing_line_chart);

        XAxis x = dailyAchieved.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setGranularity(1);
        x.setDrawLabels(true);

        // Reference https://weeklycoding.com/mpandroidchart-documentation/xaxis/
        YAxis y = dailyAchieved.getAxisLeft();
        y.setAxisMinimum(0f);
        y.setGranularity(5);

        Description description = dailyAchieved.getDescription();
        description.setEnabled(false);

        // Reference: https://weeklycoding.com/mpandroidchart-documentation/interaction-with-the-chart/
        dailyAchieved.setDragEnabled(false);
        dailyAchieved.setScaleEnabled(false);
        dailyAchieved.setPinchZoom(false);
        dailyAchieved.setDoubleTapToZoomEnabled(false);

        Legend legend = dailyAchieved.getLegend();
        legend.setEnabled(false);

        YAxis right = dailyAchieved.getAxisRight();
        right.setEnabled(false);

        // Create the entries
        List<Entry> connectEntries = new ArrayList<>();
        List<Entry> beActiveEntries = new ArrayList<>();
        List<Entry> keepLearningEntries = new ArrayList<>();
        List<Entry> takeNoticeEntries = new ArrayList<>();
        List<Entry> giveEntries = new ArrayList<>();

        // Create a list of values
        List<Integer> connectValues = insightsItem.getCurrentValues().getConnectValues();
        List<Integer> beActiveValues = insightsItem.getCurrentValues().getBeActiveValues();
        List<Integer> keepLearningValues = insightsItem.getCurrentValues().getKeepLearningValues();
        List<Integer> takeNoticeValues = insightsItem.getCurrentValues().getTakeNoticeValues();
        List<Integer> giveValues = insightsItem.getCurrentValues().getGiveValues();
        long startTime = insightsItem.getCurrentValues().getStartDay();

        // Create entries for each day
        List<Long> dayMillisList = new ArrayList<>();
        for (int i = 0; i < connectValues.size(); i++) {
            connectEntries.add(new Entry(i, connectValues.get(i)));
            beActiveEntries.add(new Entry(i, beActiveValues.get(i)));
            keepLearningEntries.add(new Entry(i, keepLearningValues.get(i)));
            takeNoticeEntries.add(new Entry(i, takeNoticeValues.get(i)));
            giveEntries.add(new Entry(i, giveValues.get(i)));
            dayMillisList.add(startTime);
            startTime += 86400000;
        }

        x.setValueFormatter(new DayMonthValueFormatter(dayMillisList));
        x.setYOffset(8f);
        x.setTextSize(10f);
        x.setLabelRotationAngle(90);

        y.setXOffset(8f);
        y.setTextSize(10f);

        List<ILineDataSet> waysToWellbeingDataSets = new ArrayList<>();

        // Create the data sets
        LineDataSet connectDataSet = new LineDataSet(connectEntries, context.getString(R.string.wellbeing_connect));
        connectDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        connectDataSet.setColor(context.getColor(R.color.way_to_wellbeing_connect));
        waysToWellbeingDataSets.add(connectDataSet);

        LineDataSet beActiveDataSet = new LineDataSet(beActiveEntries, context.getString(R.string.wellbeing_be_active));
        beActiveDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        beActiveDataSet.setColor(context.getColor(R.color.way_to_wellbeing_be_active));
        waysToWellbeingDataSets.add(beActiveDataSet);

        LineDataSet keepLearningDataSet = new LineDataSet(keepLearningEntries, context.getString(R.string.wellbeing_keep_learning));
        keepLearningDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        keepLearningDataSet.setColor(context.getColor(R.color.way_to_wellbeing_keep_learning));
        waysToWellbeingDataSets.add(keepLearningDataSet);

        LineDataSet takeNoticeDataSet = new LineDataSet(takeNoticeEntries, context.getString(R.string.wellbeing_take_notice));
        takeNoticeDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        takeNoticeDataSet.setColor(context.getColor(R.color.way_to_wellbeing_take_notice));
        waysToWellbeingDataSets.add(takeNoticeDataSet);

        LineDataSet giveDataSet = new LineDataSet(giveEntries, context.getString(R.string.wellbeing_give));
        giveDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        giveDataSet.setColor(context.getColor(R.color.way_to_wellbeing_give));
        waysToWellbeingDataSets.add(giveDataSet);

        // Add the data sets for the lines
        LineData lineData = new LineData(waysToWellbeingDataSets);
        dailyAchieved.setData(lineData);

        // Reference detect mode https://stackoverflow.com/a/56036734/13496270
        if ((context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            x.setTextColor(context.getColor(android.R.color.white));
            y.setTextColor(context.getColor(android.R.color.white));
            connectDataSet.setValueTextColor(context.getColor(android.R.color.white));
            beActiveDataSet.setValueTextColor(context.getColor(android.R.color.white));
            keepLearningDataSet.setValueTextColor(context.getColor(android.R.color.white));
            takeNoticeDataSet.setValueTextColor(context.getColor(android.R.color.white));
            giveDataSet.setValueTextColor(context.getColor(android.R.color.white));

        } else {
            x.setTextColor(context.getColor(android.R.color.black));
            y.setTextColor(context.getColor(android.R.color.black));
            connectDataSet.setValueTextColor(context.getColor(android.R.color.black));
            beActiveDataSet.setValueTextColor(context.getColor(android.R.color.black));
            keepLearningDataSet.setValueTextColor(context.getColor(android.R.color.black));
            takeNoticeDataSet.setValueTextColor(context.getColor(android.R.color.black));
            giveDataSet.setValueTextColor(context.getColor(android.R.color.black));
        }

        dailyAchieved.animateY(400, Easing.Linear);
        graphCard.setVisibility(View.VISIBLE);

        ChipGroup group = graphCard.findViewById(R.id.wellbeing_chip_group);
        LinearLayout helpContainer = graphCard.findViewById(R.id.way_to_wellbeing_help_container);

        // Listen for the chip selection to filter the graph
        group.setOnCheckedChangeListener((groupId, checkedId) -> {
            helpContainer.removeAllViews();
            waysToWellbeingDataSets.clear();
            switch (checkedId) {
                case R.id.chip_connect:
                    LayoutInflater.from(context).inflate(R.layout.card_connect, helpContainer);
                    callback.displaySuggestionChip(graphCard, insightsItem.getCurrentValues().getStartDay(), insightsItem.getCurrentValues().getEndDay(), WaysToWellbeing.CONNECT);
                    helpContainer.setVisibility(View.VISIBLE);
                    waysToWellbeingDataSets.add(connectDataSet);
                    break;
                case R.id.chip_be_active:
                    LayoutInflater.from(context).inflate(R.layout.card_be_active, helpContainer);
                    callback.displaySuggestionChip(graphCard, insightsItem.getCurrentValues().getStartDay(), insightsItem.getCurrentValues().getEndDay(), WaysToWellbeing.BE_ACTIVE);
                    helpContainer.setVisibility(View.VISIBLE);
                    waysToWellbeingDataSets.add(beActiveDataSet);
                    break;
                case R.id.chip_keep_learning:
                    LayoutInflater.from(context).inflate(R.layout.card_keep_learning, helpContainer);
                    callback.displaySuggestionChip(graphCard, insightsItem.getCurrentValues().getStartDay(), insightsItem.getCurrentValues().getEndDay(), WaysToWellbeing.KEEP_LEARNING);
                    helpContainer.setVisibility(View.VISIBLE);
                    waysToWellbeingDataSets.add(keepLearningDataSet);
                    break;
                case R.id.chip_take_notice:
                    LayoutInflater.from(context).inflate(R.layout.card_take_notice, helpContainer);
                    callback.displaySuggestionChip(graphCard, insightsItem.getCurrentValues().getStartDay(), insightsItem.getCurrentValues().getEndDay(), WaysToWellbeing.TAKE_NOTICE);
                    helpContainer.setVisibility(View.VISIBLE);
                    waysToWellbeingDataSets.add(takeNoticeDataSet);
                    break;
                case R.id.chip_give:
                    LayoutInflater.from(context).inflate(R.layout.card_give, helpContainer);
                    callback.displaySuggestionChip(graphCard, insightsItem.getCurrentValues().getStartDay(), insightsItem.getCurrentValues().getEndDay(), WaysToWellbeing.GIVE);
                    helpContainer.setVisibility(View.VISIBLE);
                    waysToWellbeingDataSets.add(giveDataSet);
                    break;
                default:
                    helpContainer.removeAllViews();
                    waysToWellbeingDataSets.add(connectDataSet);
                    waysToWellbeingDataSets.add(beActiveDataSet);
                    waysToWellbeingDataSets.add(keepLearningDataSet);
                    waysToWellbeingDataSets.add(takeNoticeDataSet);
                    waysToWellbeingDataSets.add(giveDataSet);
            }
            dailyAchieved.invalidate();
        });
    }
}
