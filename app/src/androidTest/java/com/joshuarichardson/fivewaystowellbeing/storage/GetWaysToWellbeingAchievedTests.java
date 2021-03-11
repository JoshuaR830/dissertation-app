package com.joshuarichardson.fivewaystowellbeing.storage;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingResult;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import static com.google.common.truth.Truth.assertThat;

public class GetWaysToWellbeingAchievedTests {
    private WellbeingDatabase wellbeingDatabase;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        // Reference: https://developer.android.com/training/data-storage/room/testing-db
        Context context = ApplicationProvider.getApplicationContext();
        this.wellbeingDatabase = Room.inMemoryDatabaseBuilder(context, WellbeingDatabase.class).build();

        this.wellbeingDatabase.wellbeingResultsDao().insert(new WellbeingResult(1, 2345678, 5, 10, 25, 80, 110));
    }

    @Test
    public void updatingWaysToWellbeing_ShouldUpdateSurveyResponse() {
        long surveyId = this.wellbeingDatabase.wellbeingResultsDao().insert(new WellbeingResult(2, 1234567, 45, 50, 85, 30, 40));
        WellbeingResult originalResponse = this.wellbeingDatabase.wellbeingResultsDao().getResultsBySurveyId(surveyId);

        assertThat(originalResponse.getConnectValue()).isEqualTo(45);
        assertThat(originalResponse.getBeActiveValue()).isEqualTo(50);
        assertThat(originalResponse.getKeepLearningValue()).isEqualTo(85);
        assertThat(originalResponse.getTakeNoticeValue()).isEqualTo(30);
        assertThat(originalResponse.getGiveValue()).isEqualTo(40);

        this.wellbeingDatabase.wellbeingResultsDao().updateWaysToWellbeing(surveyId, 10, 20, 50, 80, 100);

        WellbeingResult newResponse = this.wellbeingDatabase.wellbeingResultsDao().getResultsBySurveyId(surveyId);
        assertThat(newResponse.getConnectValue()).isEqualTo(10);
        assertThat(newResponse.getBeActiveValue()).isEqualTo(20);
        assertThat(newResponse.getKeepLearningValue()).isEqualTo(50);
        assertThat(newResponse.getTakeNoticeValue()).isEqualTo(80);
        assertThat(newResponse.getGiveValue()).isEqualTo(100);
    }

    @Test
    public void whenGettingByTimes_ShouldReturnAListOfTheCorrectWaysToWellbeingInOrder() {
        this.wellbeingDatabase.wellbeingResultsDao().insert(new WellbeingResult(2, 4389598, 35, 40, 50, 20, 10));
        this.wellbeingDatabase.wellbeingResultsDao().insert(new WellbeingResult(3, 1234567, 20, 25, 80, 40, 20));
        this.wellbeingDatabase.wellbeingResultsDao().insert(new WellbeingResult(4, 2437654, 10, 10, 70, 60, 30));

        List<WellbeingResult> values = this.wellbeingDatabase.wellbeingResultsDao().getResultsByTimestampRange(1234567, 4389598);

        assertThat(values.get(0).getConnectValue()).isEqualTo(20);
        assertThat(values.get(0).getBeActiveValue()).isEqualTo(25);
        assertThat(values.get(0).getKeepLearningValue()).isEqualTo(80);
        assertThat(values.get(0).getTakeNoticeValue()).isEqualTo(40);
        assertThat(values.get(0).getGiveValue()).isEqualTo(20);

        assertThat(values.get(1).getConnectValue()).isEqualTo(5);
        assertThat(values.get(1).getBeActiveValue()).isEqualTo(10);
        assertThat(values.get(1).getKeepLearningValue()).isEqualTo(25);
        assertThat(values.get(1).getTakeNoticeValue()).isEqualTo(80);
        assertThat(values.get(1).getGiveValue()).isEqualTo(110);

        assertThat(values.get(2).getConnectValue()).isEqualTo(10);
        assertThat(values.get(2).getBeActiveValue()).isEqualTo(10);
        assertThat(values.get(2).getKeepLearningValue()).isEqualTo(70);
        assertThat(values.get(2).getTakeNoticeValue()).isEqualTo(60);
        assertThat(values.get(2).getGiveValue()).isEqualTo(30);

        assertThat(values.get(3).getConnectValue()).isEqualTo(35);
        assertThat(values.get(3).getBeActiveValue()).isEqualTo(40);
        assertThat(values.get(3).getKeepLearningValue()).isEqualTo(50);
        assertThat(values.get(3).getTakeNoticeValue()).isEqualTo(20);
        assertThat(values.get(3).getGiveValue()).isEqualTo(10);
    }

    @Test
    public void whenGettingAverageValue_ShouldReturnCorrectValues() {
        List<WellbeingResult> input = Arrays.asList(
            new WellbeingResult(2, 2389598, 35, 100, 50, 100, 100),
            new WellbeingResult(3, 3389598, 35, 100, 50, 100, 10),
            new WellbeingResult(4, 4389598, 35, 100, 50, 20, 100),
            new WellbeingResult(5, 5389598, 100, 100, 50, 20, 100),
            new WellbeingResult(6, 1389598, 100, 100, 100, 100, 100)
        );

        WellbeingValues values = new WellbeingValues(input);
        assertThat(values.getAverageConnectValue()).isEqualTo(61);
        assertThat(values.getAverageBeActiveValue()).isEqualTo(100);
        assertThat(values.getAverageKeepLearningValue()).isEqualTo(60);
        assertThat(values.getAverageTakeNoticeValue()).isEqualTo(68);
        assertThat(values.getAverageGiveValue()).isEqualTo(82);
    }

    @Test
    public void whenGettingNumberOfWaysToWellbeingAchieved_ShouldOnlyCountDaysWhereUnique() {
        List<WellbeingResult> input = Arrays.asList(
            new WellbeingResult(2, 2389598, 35, 100, 50, 100, 100),
            new WellbeingResult(3, 3389598, 35, 100, 50, 100, 10),
            new WellbeingResult(4, 4389598, 35, 100, 50, 20, 100),
            new WellbeingResult(5, 5389598, 100, 100, 50, 20, 100),
            new WellbeingResult(6, 1389598, 100, 100, 100, 100, 100)
        );

        WellbeingValues values = new WellbeingValues(input);

        int connectNumber = values.getAchievedConnectNumber();
        int beActiveNumber = values.getAchievedBeActiveNumber();
        int keepLearningNumber = values.getAchievedKeepLearningNumber();
        int takeNoticeNumber = values.getAchievedTakeNoticeNumber();
        int giveNumber = values.getAchievedGiveNumber();

        assertThat(connectNumber).isEqualTo(2);
        assertThat(beActiveNumber).isEqualTo(5);
        assertThat(keepLearningNumber).isEqualTo(1);
        assertThat(takeNoticeNumber).isEqualTo(3);
        assertThat(giveNumber).isEqualTo(4);
    }
}
