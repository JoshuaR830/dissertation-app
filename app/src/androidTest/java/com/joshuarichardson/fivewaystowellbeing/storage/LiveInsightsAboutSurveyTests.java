package com.joshuarichardson.fivewaystowellbeing.storage;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseElementDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.utilities.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeoutException;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import static com.google.common.truth.Truth.assertThat;

public class LiveInsightsAboutSurveyTests {

    private WellbeingDatabase wellbeingDb;
    private SurveyResponseDao surveyResponseDao;
    private SurveyResponseElementDao surveyResponseElementDao;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        this.wellbeingDb = Room.inMemoryDatabaseBuilder(context, WellbeingDatabase.class).build();
        this.surveyResponseElementDao = this.wellbeingDb.surveyResponseElementDao();
        this.surveyResponseDao = this.wellbeingDb.surveyResponseDao();

        this.surveyResponseDao.insert(new SurveyResponse(534789473, WaysToWellbeing.CONNECT, "Title 1", "Description"));
        this.surveyResponseDao.insert(new SurveyResponse(534789473, WaysToWellbeing.BE_ACTIVE, "Title 2", "Description"));
        this.surveyResponseDao.insert(new SurveyResponse(534789473, WaysToWellbeing.BE_ACTIVE, "Title 3", "Description"));
        this.surveyResponseDao.insert(new SurveyResponse(534789473, WaysToWellbeing.TAKE_NOTICE, "Title 4", "Description"));
        this.surveyResponseDao.insert(new SurveyResponse(534789473, WaysToWellbeing.TAKE_NOTICE, "Title 5", "Description"));
        this.surveyResponseDao.insert(new SurveyResponse(534789473, WaysToWellbeing.TAKE_NOTICE, "Title 6", "Description"));
        this.surveyResponseDao.insert(new SurveyResponse(534789473, WaysToWellbeing.KEEP_LEARNING, "Title 7", "Description"));
        this.surveyResponseDao.insert(new SurveyResponse(534789473, WaysToWellbeing.KEEP_LEARNING, "Title 8", "Description"));
        this.surveyResponseDao.insert(new SurveyResponse(534789473, WaysToWellbeing.KEEP_LEARNING, "Title 9", "Description"));
        this.surveyResponseDao.insert(new SurveyResponse(534789473, WaysToWellbeing.KEEP_LEARNING, "Title 10", "Description"));
        this.surveyResponseDao.insert(new SurveyResponse(534789473, WaysToWellbeing.GIVE, "Title 11", "Description"));
        this.surveyResponseDao.insert(new SurveyResponse(534789473, WaysToWellbeing.GIVE, "Title 12", "Description"));
        this.surveyResponseDao.insert(new SurveyResponse(534789473, WaysToWellbeing.GIVE, "Title 13", "Description"));
        this.surveyResponseDao.insert(new SurveyResponse(534789473, WaysToWellbeing.GIVE, "Title 14", "Description"));
        this.surveyResponseDao.insert(new SurveyResponse(534789473, WaysToWellbeing.GIVE, "Title 15", "Description"));
    }


    @Test
    public void whenRequestingConnect_ThenTheCountShouldBeCorrect() throws TimeoutException, InterruptedException {
        LiveData<Integer> connectNum = this.surveyResponseDao.getLiveInsights(WaysToWellbeing.CONNECT.toString());
        Integer value = LiveDataTestUtil.getOrAwaitValue(connectNum);

        assertThat(value).isEqualTo(1);
    }

    @Test
    public void whenRequestingBeActive_ThenTheCountShouldBeCorrect() throws TimeoutException, InterruptedException {
        LiveData<Integer> beActiveNum = this.surveyResponseDao.getLiveInsights(WaysToWellbeing.BE_ACTIVE.toString());
        Integer value = LiveDataTestUtil.getOrAwaitValue(beActiveNum);

        assertThat(value).isEqualTo(2);
    }

    @Test
    public void whenRequestingTakeNotice_ThenTheCountShouldBeCorrect() throws TimeoutException, InterruptedException {
        LiveData<Integer> takeNoticeNum = this.surveyResponseDao.getLiveInsights(WaysToWellbeing.TAKE_NOTICE.toString());
        Integer value = LiveDataTestUtil.getOrAwaitValue(takeNoticeNum);

        assertThat(value).isEqualTo(3);
    }

    @Test
    public void whenRequestingKeepLearning_ThenTheCountShouldBeCorrect() throws TimeoutException, InterruptedException {
        LiveData<Integer> keepLearningNum = this.surveyResponseDao.getLiveInsights(WaysToWellbeing.KEEP_LEARNING.toString());
        Integer value = LiveDataTestUtil.getOrAwaitValue(keepLearningNum);

        assertThat(value).isEqualTo(4);
    }

    @Test
    public void whenRequestingGive_ThenTheCountShouldBeCorrect() throws TimeoutException, InterruptedException {
        LiveData<Integer> giveNum = this.surveyResponseDao.getLiveInsights(WaysToWellbeing.GIVE.toString());
        Integer value = LiveDataTestUtil.getOrAwaitValue(giveNum);

        assertThat(value).isEqualTo(5);
    }
}
