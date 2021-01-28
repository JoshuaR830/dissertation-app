package com.joshuarichardson.fivewaystowellbeing.storage;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.DatabaseInsertionHelper;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.utilities.LiveDataTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeoutException;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

public class SurveyResponseTests {

    private WellbeingDatabase wellbeingDb;
    private SurveyResponseDao surveyResponseDao;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        this.wellbeingDb = Room.inMemoryDatabaseBuilder(context, WellbeingDatabase.class).build();
        this.surveyResponseDao = wellbeingDb.surveyResponseDao();
    }

    @After
    public void teardown() {
        this.wellbeingDb.close();
    }

    @Test
    public void InsertingASurvey_ThenGettingTheSurveyById_ShouldReturnTheCorrectSurveyResponse() throws TimeoutException, InterruptedException {

        SurveyResponse surveyResponse = new SurveyResponse(1607960240, WaysToWellbeing.BE_ACTIVE, "title", "description");

        long surveyId = this.surveyResponseDao.insert(surveyResponse);

        List<SurveyResponse> surveyResponses = LiveDataTestUtil.getOrAwaitValue(this.surveyResponseDao.getSurveyResponseById(surveyId));

        assertWithMessage("There should be at least 1 item in the list")
                .that(surveyResponses.size())
                .isGreaterThan(0);

        SurveyResponse actualSurveyResponse = surveyResponses.get(0);

        // Need to confirm that the properties are correct
        assertThat(actualSurveyResponse.getSurveyResponseId())
                .isEqualTo(surveyId);

        assertThat(actualSurveyResponse.getSurveyResponseTimestamp())
                .isEqualTo(1607960240);

        assertThat(actualSurveyResponse.getSurveyResponseWayToWellbeing())
                .isEqualTo(WaysToWellbeing.BE_ACTIVE.name());
    }

    @Test
    public void insertingMultipleSurveys_ThenGettingAllSurveys_ShouldReturnAllAddedSurveyResponses() throws TimeoutException, InterruptedException {
        DatabaseInsertionHelper.insert(new SurveyResponse[] {
                new SurveyResponse(0, WaysToWellbeing.KEEP_LEARNING, "title", "description"),
                new SurveyResponse(922720201, WaysToWellbeing.BE_ACTIVE, "title", "description"),
                new SurveyResponse(922720202, WaysToWellbeing.CONNECT, "title", "description"),
                new SurveyResponse(922720203, WaysToWellbeing.GIVE, "title", "description"),
                new SurveyResponse(2147483647, WaysToWellbeing.TAKE_NOTICE, "title", "description")
        }, this.surveyResponseDao);

        List<SurveyResponse> surveyResponses = LiveDataTestUtil.getOrAwaitValue(this.surveyResponseDao.getAllSurveyResponses());

        assertThat(surveyResponses).isNotNull();
        assertThat(surveyResponses.size()).isEqualTo(5);

    }

    @Test
    public void gettingSurveyResponsesBetweenTimes_ShouldReturnTheCorrectSurveyResponses() throws TimeoutException, InterruptedException {
        DatabaseInsertionHelper.insert(new SurveyResponse[] {
                new SurveyResponse(1608076799, WaysToWellbeing.BE_ACTIVE, "title", "description"),
                new SurveyResponse(1608076800, WaysToWellbeing.CONNECT, "title", "description"),
                new SurveyResponse(1608076801, WaysToWellbeing.BE_ACTIVE, "title", "description"),
                new SurveyResponse(1608163100, WaysToWellbeing.GIVE, "title", "description"),
                new SurveyResponse(1608163199, WaysToWellbeing.GIVE, "title", "description"),
                new SurveyResponse(1608163200, WaysToWellbeing.TAKE_NOTICE, "title", "description"),
                new SurveyResponse(1608163201, WaysToWellbeing.KEEP_LEARNING, "title", "description"),
                new SurveyResponse(0, WaysToWellbeing.TAKE_NOTICE, "title", "description"),
                new SurveyResponse(2147483647, WaysToWellbeing.KEEP_LEARNING, "title", "description")
        }, this.surveyResponseDao);

        List<SurveyResponse> surveyResponses = LiveDataTestUtil.getOrAwaitValue(this.surveyResponseDao.getSurveyResponsesByTimestampRange(1608076800, 1608163201));
        assertThat(surveyResponses).isNotNull();
        assertThat(surveyResponses.size()).isEqualTo(6);
    }
}
