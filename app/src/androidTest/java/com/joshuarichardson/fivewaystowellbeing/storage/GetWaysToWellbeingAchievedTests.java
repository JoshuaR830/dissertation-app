package com.joshuarichardson.fivewaystowellbeing.storage;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.utilities.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

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
        // ToDo - need  in memory database stuff in here
        // ToDo - Set up a whole thing to do a whole bunch of stuff
        // Reference: https://developer.android.com/training/data-storage/room/testing-db
        Context context = ApplicationProvider.getApplicationContext();
        this.wellbeingDatabase = Room.inMemoryDatabaseBuilder(context, WellbeingDatabase.class).build();

        this.wellbeingDatabase.surveyResponseDao().insert(new SurveyResponse(2345678, WaysToWellbeing.UNASSIGNED, "", "Description", 5, 10, 25, 80, 110));
    }

    @Test
    public void updatingWaysToWellbeing_ShouldUpdateSurveyResponse() {
        long surveyId = this.wellbeingDatabase.surveyResponseDao().insert(new SurveyResponse(1234567, WaysToWellbeing.UNASSIGNED, "", "Description", 45, 50, 85, 30, 40));
        SurveyResponse originalResponse = this.wellbeingDatabase.surveyResponseDao().getSurveyResponseById(surveyId);

        assertThat(originalResponse.getConnectValue()).isEqualTo(45);
        assertThat(originalResponse.getBeActiveValue()).isEqualTo(50);
        assertThat(originalResponse.getKeepLearningValue()).isEqualTo(85);
        assertThat(originalResponse.getTakeNoticeValue()).isEqualTo(30);
        assertThat(originalResponse.getGiveValue()).isEqualTo(40);

        this.wellbeingDatabase.surveyResponseDao().updateWaysToWellbeing(surveyId, 10, 20, 50, 80, 100);

        // ToDo - by checking before and after update should give possibility to cause some celebration when you achieve

        SurveyResponse newResponse = this.wellbeingDatabase.surveyResponseDao().getSurveyResponseById(surveyId);
        assertThat(newResponse.getConnectValue()).isEqualTo(10);
        assertThat(newResponse.getBeActiveValue()).isEqualTo(20);
        assertThat(newResponse.getKeepLearningValue()).isEqualTo(50);
        assertThat(newResponse.getTakeNoticeValue()).isEqualTo(80);
        assertThat(newResponse.getGiveValue()).isEqualTo(100);
    }

    @Test
    public void whenGettingByTimes_ShouldReturnAListOfTheCorrectWaysToWellbeingInOrder() throws TimeoutException, InterruptedException {
        this.wellbeingDatabase.surveyResponseDao().insert(new SurveyResponse(4389598, WaysToWellbeing.UNASSIGNED, "", "Description", 35, 40, 50, 20, 10));
        this.wellbeingDatabase.surveyResponseDao().insert(new SurveyResponse(1234567, WaysToWellbeing.UNASSIGNED, "", "Description", 20, 25, 80, 40, 20));
        this.wellbeingDatabase.surveyResponseDao().insert(new SurveyResponse(2437654, WaysToWellbeing.UNASSIGNED, "", "Description", 10, 10, 70, 60, 30));

        List<SurveyResponse> values = LiveDataTestUtil.getOrAwaitValue(this.wellbeingDatabase.surveyResponseDao().getSurveyResponsesByTimestampRange(1234567, 4389598));

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
        List<SurveyResponse> input = Arrays.asList(
            new SurveyResponse(1389598, WaysToWellbeing.UNASSIGNED, "", "Description", 100, 100, 100, 100, 100),
            new SurveyResponse(2389598, WaysToWellbeing.UNASSIGNED, "", "Description", 35, 100, 50, 100, 100),
            new SurveyResponse(3389598, WaysToWellbeing.UNASSIGNED, "", "Description", 35, 100, 50, 100, 10),
            new SurveyResponse(4389598, WaysToWellbeing.UNASSIGNED, "", "Description", 35, 100, 50, 20, 100),
            new SurveyResponse(5389598, WaysToWellbeing.UNASSIGNED, "", "Description", 100, 100, 50, 20, 100)
        );

        WellbeingValues values = WellbeingAverageValueHelper.getAverageConnectValue(input);
        assertThat(values.getAverageConnectValue()).isEqualTo(61);
        assertThat(values.getAverageBeActiveValue()).isEqualTo(100);
        assertThat(values.getAverageKeepLearningValue()).isEqualTo(60);
        assertThat(values.getAverageTakeNoticeValue()).isEqualTo(68);
        assertThat(values.getAverageGiveValue()).isEqualTo(82);
    }

    @Test
    public void whenGettingNumberOfWaysToWellbeingAchieved_ShouldOnlyCountDaysWhereUnique() {
        List<SurveyResponse> input = Arrays.asList(
            new SurveyResponse(1389598, WaysToWellbeing.UNASSIGNED, "", "Description", 100, 100, 100, 100, 100),
            new SurveyResponse(2389598, WaysToWellbeing.UNASSIGNED, "", "Description", 35, 100, 50, 100, 100),
            new SurveyResponse(3389598, WaysToWellbeing.UNASSIGNED, "", "Description", 35, 100, 50, 100, 10),
            new SurveyResponse(4389598, WaysToWellbeing.UNASSIGNED, "", "Description", 35, 100, 50, 20, 100),
            new SurveyResponse(5389598, WaysToWellbeing.UNASSIGNED, "", "Description", 100, 100, 50, 20, 100)
        );

        WellbeingValues values = WellbeingAverageValueHelper.getAverageConnectValue(input);

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
