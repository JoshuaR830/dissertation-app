package com.joshuarichardson.fivewaystowellbeing.storage;

import android.util.Log;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class WellbeingValues {
    List<Integer> connectValues = new ArrayList<>();
    List<Integer> beActiveValues = new ArrayList<>();
    List<Integer> keepLearningValues = new ArrayList<>();
    List<Integer> takeNoticeValues = new ArrayList<>();
    List<Integer> giveValues = new ArrayList<>();

    public WellbeingValues(List<WellbeingResult> surveys) {
        this.processSurveys(surveys);
    }

    private void processSurveys(List<WellbeingResult> wellbeingResults) {
        for (WellbeingResult result : wellbeingResults) {
            connectValues.add(result.getConnectValue());
            beActiveValues.add(result.getBeActiveValue());
            keepLearningValues.add(result.getKeepLearningValue());
            takeNoticeValues.add(result.getTakeNoticeValue());
            giveValues.add(result.getGiveValue());
            Log.d("Be active", String.valueOf(result.getBeActiveValue()));
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
}
