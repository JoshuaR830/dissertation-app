package com.joshuarichardson.fivewaystowellbeing.ui.insights;

import com.joshuarichardson.fivewaystowellbeing.storage.ActivityStats;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class ActivitySelectionTests {
    @Test
    public void whenNoActivitiesAndComparedToMax_OnlyZeroShouldBeReturned() {

        List<ActivityStats> activityStats = Collections.emptyList();

        long mostAchievedId = InsightActivitySelectionHelper.selectMostAchieved(activityStats);
        long leastAchievedId = InsightActivitySelectionHelper.selectLeastAchieved(activityStats, true);

        assertThat(mostAchievedId).isEqualTo(0L);
        assertThat(leastAchievedId).isEqualTo(0L);
    }

    @Test
    public void whenNoActivitiesAndNotComparedToMax_OnlyZeroShouldBeReturned() {

        List<ActivityStats> activityStats = Collections.emptyList();

        long mostAchievedId = InsightActivitySelectionHelper.selectMostAchieved(activityStats);
        long leastAchievedId = InsightActivitySelectionHelper.selectLeastAchieved(activityStats, false);

        assertThat(mostAchievedId).isEqualTo(0L);
        assertThat(leastAchievedId).isEqualTo(0L);
    }

    @Test
    public void whenOneActivityAndComparedToMax_OnlyBestShouldBeReturned() {

        List<ActivityStats> activityStats = Collections.singletonList(new ActivityStats(1, 1));

        long mostAchievedId = InsightActivitySelectionHelper.selectMostAchieved(activityStats);
        long leastAchievedId = InsightActivitySelectionHelper.selectLeastAchieved(activityStats, true);

        assertThat(mostAchievedId).isEqualTo(1L);
        assertThat(leastAchievedId).isEqualTo(0L);
    }

    @Test
    public void whenOneActivityAndNotCoparedToMax_OnlyBestShouldBeReturned() {

        List<ActivityStats> activityStats = Collections.singletonList(new ActivityStats(1, 1));

        long mostAchievedId = InsightActivitySelectionHelper.selectMostAchieved(activityStats);
        long leastAchievedId = InsightActivitySelectionHelper.selectLeastAchieved(activityStats, false);

        assertThat(mostAchievedId).isEqualTo(1L);
        assertThat(leastAchievedId).isEqualTo(1L);
    }

    @Test
    public void whenActivitiesHaveTheSameCountAndComparedToMax_CorrectActivityIdShouldBeReturned() {

        List<ActivityStats> activityStats = Arrays.asList(new ActivityStats(1, 1), new ActivityStats(2, 1));

        long mostAchievedId = InsightActivitySelectionHelper.selectMostAchieved(activityStats);
        long leastAchievedId = InsightActivitySelectionHelper.selectLeastAchieved(activityStats, true);

        assertThat(mostAchievedId).isAnyOf(1L, 2L);
        assertThat(leastAchievedId).isEqualTo(0L);
    }

    @Test
    public void whenActivitiesHaveTheSameCountAndNotComparedToMax_CorrectActivityIdShouldBeReturned() {

        List<ActivityStats> activityStats = Arrays.asList(new ActivityStats(1, 1), new ActivityStats(2, 1));

        long mostAchievedId = InsightActivitySelectionHelper.selectMostAchieved(activityStats);
        long leastAchievedId = InsightActivitySelectionHelper.selectLeastAchieved(activityStats, false);

        // When equal and not compared to max, either can be most or least as they are the same - allows for displaying suggestions where only 1 suggestion is shown
        // and required regardless of whether they are equal or not
        assertThat(mostAchievedId).isAnyOf(1L, 2L);
        assertThat(leastAchievedId).isAnyOf(1L, 2L);
    }

    @Test
    public void whenActivitiesHaveDifferentNumbersAndComparedToMax_ActivityIdsShouldBeReturnedForBoth() {
        List<ActivityStats> activityStats = Arrays.asList(new ActivityStats(1, 3), new ActivityStats(2, 3), new ActivityStats(3, 2), new ActivityStats(4, 1), new ActivityStats(5, 1));

        long mostAchievedId = InsightActivitySelectionHelper.selectMostAchieved(activityStats);
        long leastAchievedId = InsightActivitySelectionHelper.selectLeastAchieved(activityStats, true);

        assertThat(mostAchievedId).isAnyOf(1L, 2L);
        assertThat(leastAchievedId).isAnyOf(4L, 5L);
    }

    @Test
    public void whenActivitiesHaveDifferentNumbersAndNotComparedToMax_ActivityIdsShouldBeReturnedForBoth() {
        List<ActivityStats> activityStats = Arrays.asList(new ActivityStats(1, 3), new ActivityStats(2, 3), new ActivityStats(3, 2), new ActivityStats(4, 1), new ActivityStats(5, 1));

        long mostAchievedId = InsightActivitySelectionHelper.selectMostAchieved(activityStats);
        long leastAchievedId = InsightActivitySelectionHelper.selectLeastAchieved(activityStats, false);

        assertThat(mostAchievedId).isAnyOf(1L, 2L);
        assertThat(leastAchievedId).isAnyOf(4L, 5L);
    }
}
