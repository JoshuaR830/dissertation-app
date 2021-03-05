package com.joshuarichardson.fivewaystowellbeing.storage;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.DatabaseInsertionHelper;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseActivityRecord;
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

        SurveyResponse actualResponse = this.surveyResponseDao.getSurveyResponseById(surveyId);

        assertWithMessage("There should be at least 1 item in the list")
                .that(actualResponse)
                .isNotNull();

        // Need to confirm that the properties are correct
        assertThat(actualResponse.getSurveyResponseId())
                .isEqualTo(surveyId);

        assertThat(actualResponse.getSurveyResponseTimestamp())
                .isEqualTo(1607960240);

        assertThat(actualResponse.getSurveyResponseWayToWellbeing())
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
    public void insertingMultipleSurveys_ThenGettingAllNonLiveSurveys_ShouldReturnAllAddedSurveyResponses() throws TimeoutException, InterruptedException {
        long surveyId1 = this.surveyResponseDao.insert(new SurveyResponse(922720200, WaysToWellbeing.KEEP_LEARNING, "title", "description"));
        long surveyId2 = this.surveyResponseDao.insert(new SurveyResponse(922720201, WaysToWellbeing.BE_ACTIVE, "title", "description"));
        long surveyId3 = this.surveyResponseDao.insert(new SurveyResponse(922720202, WaysToWellbeing.CONNECT, "title", "description"));
        this.surveyResponseDao.insert(new SurveyResponse(922720203, WaysToWellbeing.GIVE, "title", "description"));

        long activityRecord = this.wellbeingDb.activityRecordDao().insert(new ActivityRecord("Name", 325768, 54389798, ActivityType.CHORES, WaysToWellbeing.GIVE, false));

        this.wellbeingDb.surveyResponseActivityRecordDao().insert(new SurveyResponseActivityRecord(surveyId1, activityRecord, 1, "note", -1, -1, 1, false));
        this.wellbeingDb.surveyResponseActivityRecordDao().insert(new SurveyResponseActivityRecord(surveyId2, activityRecord, 1, "note", -1, -1, 1, false));
        this.wellbeingDb.surveyResponseActivityRecordDao().insert(new SurveyResponseActivityRecord(surveyId3, activityRecord, 1, "note", -1, -1, 1, false));

        List<SurveyResponse> surveyResponses = LiveDataTestUtil.getOrAwaitValue(this.surveyResponseDao.getNonEmptyHistoryPageData());

        assertThat(surveyResponses).isNotNull();
        assertThat(surveyResponses.size()).isEqualTo(3);
    }

    @Test
    public void insertingMultipleSurveys_ThenGettingAllEmptySurveys_ShouldReturnAllEmptySurveys() throws TimeoutException, InterruptedException {
        long surveyId1 = this.surveyResponseDao.insert(new SurveyResponse(922720200, WaysToWellbeing.KEEP_LEARNING, "title", "description"));
        long surveyId2 = this.surveyResponseDao.insert(new SurveyResponse(922720201, WaysToWellbeing.BE_ACTIVE, "title", "description"));
        long surveyId3 = this.surveyResponseDao.insert(new SurveyResponse(922720202, WaysToWellbeing.CONNECT, "title", "description"));
        long surveyId4 = this.surveyResponseDao.insert(new SurveyResponse(922720203, WaysToWellbeing.GIVE, "title", "description"));

        long activityRecord = this.wellbeingDb.activityRecordDao().insert(new ActivityRecord("Name", 325768, 54389798, ActivityType.CHORES, WaysToWellbeing.GIVE, false));

        this.wellbeingDb.surveyResponseActivityRecordDao().insert(new SurveyResponseActivityRecord(surveyId1, activityRecord, 1, "note", -1, -1, 1, false));
        this.wellbeingDb.surveyResponseActivityRecordDao().insert(new SurveyResponseActivityRecord(surveyId2, activityRecord, 1, "note", -1, -1, 1, false));
        this.wellbeingDb.surveyResponseActivityRecordDao().insert(new SurveyResponseActivityRecord(surveyId3, activityRecord, 1, "note", -1, -1, 1, false));

        List<SurveyResponse> surveyResponses = LiveDataTestUtil.getOrAwaitValue(this.surveyResponseDao.getEmptyHistoryPageData());

        assertThat(surveyResponses).isNotNull();
        assertThat(surveyResponses.size()).isEqualTo(1);
        assertThat(surveyResponses.get(0).getSurveyResponseId()).isEqualTo(surveyId4);
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
