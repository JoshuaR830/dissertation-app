package com.joshuarichardson.fivewaystowellbeing.storage;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class WellbeingValues {
    private long startDay;
    private long endDay;

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

                previousTimestamp += 86400000;
            }

            // Add real values
            connectValues.add(result.getConnectValue());
            beActiveValues.add(result.getBeActiveValue());
            keepLearningValues.add(result.getKeepLearningValue());
            takeNoticeValues.add(result.getTakeNoticeValue());
            giveValues.add(result.getGiveValue());

            // Increment it so that it is ahead by 1
            previousTimestamp = result.getTimestamp() + 86400000;
        }
    }

    public int getAchievedConnectNumber() {
        // Reference: https://zetcode.com/java/filterlist/
        Predicate<Integer> connectFilter = value -> value >= 100;
        return (int) connectValues.stream().filter(connectFilter).count();
    }

    public int getAchievedBeActiveNumber() {
        // Reference https://zetcode.com/java/filterlist/
        Predicate<Integer> beActiveFilter = value -> value >= 100;
        return (int) beActiveValues.stream().filter(beActiveFilter).count();
    }

    public int getAchievedKeepLearningNumber() {
        // Reference https://zetcode.com/java/filterlist/
        Predicate<Integer> keepLearningFilter = value -> value >= 100;
        return (int) keepLearningValues.stream().filter(keepLearningFilter).count();
    }

    public int getAchievedTakeNoticeNumber() {
        // Reference https://zetcode.com/java/filterlist/
        Predicate<Integer> takeNoticeFilter = value -> value >= 100;
        return (int) takeNoticeValues.stream().filter(takeNoticeFilter).count();
    }

    public int getAchievedGiveNumber() {
        // Reference https://zetcode.com/java/filterlist/
        Predicate<Integer> giveFilter = value -> value >= 100;
        return (int) giveValues.stream().filter(giveFilter).count();
    }

    public int getAverageConnectValue() {
        // Reference https://stackoverflow.com/a/17846520/13496270
        int total = connectValues.stream().mapToInt(Integer::intValue).sum();
        return total/connectValues.size();
    }

    public int getAverageBeActiveValue() {
        // Reference https://stackoverflow.com/a/17846520/13496270
        int total = beActiveValues.stream().mapToInt(Integer::intValue).sum();
        return total/beActiveValues.size();
    }

    public int getAverageKeepLearningValue() {
        // Reference https://stackoverflow.com/a/17846520/13496270
        int total = keepLearningValues.stream().mapToInt(Integer::intValue).sum();
        return total/keepLearningValues.size();

    }

    public int getAverageTakeNoticeValue() {
        // Reference https://stackoverflow.com/a/17846520/13496270
        int total = takeNoticeValues.stream().mapToInt(Integer::intValue).sum();
        return total/takeNoticeValues.size();
    }

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
}
