package com.joshuarichardson.fivewaystowellbeing.storage;

import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

/**
 * Lists of values for each way to wellbeing achieved in time.
 * Ideal for plotting a graph of the five ways to wellbeing.
 * Provides functions for processing the data.
 */
public class WellbeingValues {
    private final long startDay;
    private final long endDay;

    List<Integer> connectValues = new ArrayList<>();
    List<Integer> beActiveValues = new ArrayList<>();
    List<Integer> keepLearningValues = new ArrayList<>();
    List<Integer> takeNoticeValues = new ArrayList<>();
    List<Integer> giveValues = new ArrayList<>();

    public WellbeingValues(List<WellbeingResult> surveys, long startDay, long endDay) {
        this.startDay = startDay;
        this.endDay = endDay;
        this.processSurveys(surveys);
    }

    /**
     * The list must take into account days that were not logged to avoid data being unrepresentative.
     * The timestamp for each survey can be used to find missed days.
     *
     * @param wellbeingResults A list of all the wellbeing results to process
     */
    private void processSurveys(List<WellbeingResult> wellbeingResults) {
        long previousTimestamp = this.startDay;
        for (WellbeingResult result : wellbeingResults) {

            // Add blanks for any surveys that don't exist
            while(result.getTimestamp() > previousTimestamp) {
                connectValues.add(0);
                beActiveValues.add(0);
                keepLearningValues.add(0);
                takeNoticeValues.add(0);
                giveValues.add(0);

                // A day is added
                previousTimestamp += 86400000;
            }

            // Add real values
            connectValues.add(result.getConnectValue());
            beActiveValues.add(result.getBeActiveValue());
            keepLearningValues.add(result.getKeepLearningValue());
            takeNoticeValues.add(result.getTakeNoticeValue());
            giveValues.add(result.getGiveValue());

            // Increment it so that it is ahead by 1 for the next check
            previousTimestamp = result.getTimestamp() + 86400000;
        }
    }

    /**
     * Calculate the number of connect values that meet the daily requirement
     *
     * @return The number of times connect was achieved
     */
    public int getAchievedConnectNumber() {
        // Reference: https://zetcode.com/java/filterlist/
        Predicate<Integer> connectFilter = value -> value >= 100;
        return (int) connectValues.stream().filter(connectFilter).count();
    }

    /**
     * Calculate the number of be active values that meet the daily requirement
     *
     * @return The number of times be active was achieved
     */
    public int getAchievedBeActiveNumber() {
        // Reference https://zetcode.com/java/filterlist/
        Predicate<Integer> beActiveFilter = value -> value >= 100;
        return (int) beActiveValues.stream().filter(beActiveFilter).count();
    }

    /**
     * Calculate the number of keep learning values that meet the daily requirement
     *
     * @return The number of times keep learning was achieved
     */
    public int getAchievedKeepLearningNumber() {
        // Reference https://zetcode.com/java/filterlist/
        Predicate<Integer> keepLearningFilter = value -> value >= 100;
        return (int) keepLearningValues.stream().filter(keepLearningFilter).count();
    }

    /**
     * Calculate the number of take notice values that meet the daily requirement
     *
     * @return The number of times take notice was achieved
     */
    public int getAchievedTakeNoticeNumber() {
        // Reference https://zetcode.com/java/filterlist/
        Predicate<Integer> takeNoticeFilter = value -> value >= 100;
        return (int) takeNoticeValues.stream().filter(takeNoticeFilter).count();
    }

    /**
     * Calculate the number of give values that meet the daily requirement
     *
     * @return The number of times give was achieved
     */
    public int getAchievedGiveNumber() {
        // Reference https://zetcode.com/java/filterlist/
        Predicate<Integer> giveFilter = value -> value >= 100;
        return (int) giveValues.stream().filter(giveFilter).count();
    }

    /**
     * Calculate the average daily percentage of connect achieved
     *
     * @return The average connect percentage achieved
     */
    public int getAverageConnectValue() {
        // Reference https://stackoverflow.com/a/17846520/13496270
        int total = connectValues.stream().mapToInt(Integer::intValue).sum();
        return total/connectValues.size();
    }

    /**
     * Calculate the average daily percentage of be active achieved
     *
     * @return The average be active percentage achieved
     */
    public int getAverageBeActiveValue() {
        // Reference https://stackoverflow.com/a/17846520/13496270
        int total = beActiveValues.stream().mapToInt(Integer::intValue).sum();
        return total/beActiveValues.size();
    }

    /**
     * Calculate the average daily percentage of keep learning achieved
     *
     * @return The average keep learning percentage achieved
     */
    public int getAverageKeepLearningValue() {
        // Reference https://stackoverflow.com/a/17846520/13496270
        int total = keepLearningValues.stream().mapToInt(Integer::intValue).sum();
        return total/keepLearningValues.size();

    }

    /**
     * Calculate the average daily percentage of take notice achieved
     *
     * @return The average take notice percentage achieved
     */
    public int getAverageTakeNoticeValue() {
        // Reference https://stackoverflow.com/a/17846520/13496270
        int total = takeNoticeValues.stream().mapToInt(Integer::intValue).sum();
        return total/takeNoticeValues.size();
    }

    /**
     * Calculate the average daily percentage of give achieved
     *
     * @return The average give percentage achieved
     */
    public int getAverageGiveValue() {
        // Reference https://stackoverflow.com/a/17846520/13496270
        int total = giveValues.stream().mapToInt(Integer::intValue).sum();
        return total/giveValues.size();
    }

    public List<Integer> getConnectValues() {
        return this.connectValues;
    }

    public List<Integer> getBeActiveValues() {
        return this.beActiveValues;
    }

    public List<Integer> getKeepLearningValues() {
        return this.keepLearningValues;
    }

    public List<Integer> getTakeNoticeValues() {
        return this.takeNoticeValues;
    }

    public List<Integer> getGiveValues() {
        return this.giveValues;
    }

    public long getEndDay() {
        return this.endDay;
    }

    public long getStartDay() {
        return this.startDay;
    }

    /**
     * Create a map of wellbeing type to number of items achieved
     *
     * @return The wellbeing type to number achieved map
     */
    private HashMap<WaysToWellbeing, Integer> getWellbeingAchievedMap() {
        HashMap<WaysToWellbeing, Integer> wellbeingAchievedMap = new HashMap<>();

        wellbeingAchievedMap.put(WaysToWellbeing.CONNECT, getAchievedConnectNumber());
        wellbeingAchievedMap.put(WaysToWellbeing.BE_ACTIVE, getAchievedBeActiveNumber());
        wellbeingAchievedMap.put(WaysToWellbeing.KEEP_LEARNING, getAchievedKeepLearningNumber());
        wellbeingAchievedMap.put(WaysToWellbeing.TAKE_NOTICE, getAchievedTakeNoticeNumber());
        wellbeingAchievedMap.put(WaysToWellbeing.GIVE, getAchievedGiveNumber());

        return wellbeingAchievedMap;
    }

    /**
     * Identify a random way to wellbeing type that exactly matches the condition
     *
     * @param conditionValue The condition that must be met e.g. (min/max number)
     * @param wellbeingAchievedMap Map from ways to wellbeing to number of times achieved
     * @return A way to wellbeing that matches the condition
     */
    private WaysToWellbeing getWayToWellbeingMatchingCondition(int conditionValue, HashMap<WaysToWellbeing, Integer> wellbeingAchievedMap) {
        List<WaysToWellbeing> wellbeingList = new ArrayList<>();
        wellbeingAchievedMap.forEach((waysToWellbeing, value) -> {
            // If the value matches a condition (min or max) then add the item to the list
            if(value == conditionValue) {
                wellbeingList.add(waysToWellbeing);
            }
        });

        Collections.shuffle(wellbeingList);

        if(wellbeingList.size() == 0) {
            return WaysToWellbeing.UNASSIGNED;
        }

        return wellbeingList.get(0);
    }

    /**
     * Get the least achieved way to wellbeing if it is not the same number as the most achieved way to wellbeing.
     * If there are multiple with the same low number, then a random one should be selected.
     *
     * @return One of the least achieved ways to wellbeing
     */
    public WaysToWellbeing getLeastAchieved() {
        HashMap<WaysToWellbeing, Integer> wellbeingAchievedMap = getWellbeingAchievedMap();
        int min = Collections.min(wellbeingAchievedMap.values());
        int max = Collections.max(wellbeingAchievedMap.values());

        // To overcome a problem where if all are the same you will get suggested to do better at one that is as good as one you did badly at
        if(min == max && min != 0) {
            return WaysToWellbeing.UNASSIGNED;
        }

        return getWayToWellbeingMatchingCondition(min, wellbeingAchievedMap);
    }

    /**
     * Get the most achieved way to wellbeing.
     * If there are multiple with that have been achieved the same number of times, a random one should be chosen.
     *
     * @return One of the top ways to wellbeing
     */
    public WaysToWellbeing getMostAchieved() {
        HashMap<WaysToWellbeing, Integer> wellbeingAchievedMap = getWellbeingAchievedMap();
        int max = Collections.max(wellbeingAchievedMap.values());

        return getWayToWellbeingMatchingCondition(max, wellbeingAchievedMap);
    }
}
