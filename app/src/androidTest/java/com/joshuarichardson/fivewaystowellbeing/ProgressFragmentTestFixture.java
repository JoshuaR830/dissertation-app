package com.joshuarichardson.fivewaystowellbeing;

import android.content.Context;
import android.content.SharedPreferences;

import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.PhysicalActivityDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingQuestionDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingResultsDao;

import org.junit.Before;
import org.junit.Rule;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import dagger.hilt.android.testing.HiltAndroidRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class ProgressFragmentTestFixture {

    // Rule declaration
    @Rule(order = 0)
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Rule(order = 1)
    public HiltAndroidRule hiltTest = new HiltAndroidRule(this);

    @Rule(order = 2)
    public ActivityScenarioRule<MainActivity> mainActivity = new ActivityScenarioRule<>(MainActivity.class);

    // Variable declaration
    public WellbeingDatabase mockWellbeingDatabase = mock(WellbeingDatabase.class);
    public WellbeingRecordDao wellbeingDao = mock(WellbeingRecordDao.class);
    public SurveyResponseDao surveyDao = mock(SurveyResponseDao.class);
    public WellbeingQuestionDao questionDao = mock(WellbeingQuestionDao.class);
    public WellbeingResultsDao resultsDao = mock(WellbeingResultsDao.class);
    public SurveyResponseActivityRecordDao surveyResponseActivityDao = mock (SurveyResponseActivityRecordDao.class);
    public ActivityRecordDao activityRecordDao = mock(ActivityRecordDao.class);
    public PhysicalActivityDao physicalActivityDao = mock(PhysicalActivityDao.class);

    // What the mock database should return
    protected void mockDatabaseResponses() {
        when(mockWellbeingDatabase.wellbeingResultsDao())
            .thenReturn(resultsDao);

        when(mockWellbeingDatabase.surveyResponseDao())
            .thenReturn(surveyDao);

        when(mockWellbeingDatabase.wellbeingRecordDao())
            .thenReturn(wellbeingDao);

        when(mockWellbeingDatabase.wellbeingQuestionDao())
            .thenReturn(questionDao);

        when(mockWellbeingDatabase.wellbeingQuestionDao())
            .thenReturn(questionDao);

        when(mockWellbeingDatabase.surveyResponseActivityRecordDao())
            .thenReturn(surveyResponseActivityDao);

        when(mockWellbeingDatabase.activityRecordDao())
            .thenReturn(activityRecordDao);

        when(mockWellbeingDatabase.physicalActivityDao())
            .thenReturn(physicalActivityDao);
    }

    // The default responses provided by various common methods used on the progress fragment page
    protected void defaultResponses() {
        when(surveyDao.getSurveyResponsesByTimestampRange(anyLong(), anyLong()))
            .thenReturn(new MutableLiveData<>(Collections.emptyList()));

        when(questionDao.getWaysToWellbeingBetweenTimes(anyLong(), anyLong()))
            .thenReturn(new MutableLiveData<>(Collections.emptyList()));

        when(surveyDao.getLiveInsights(anyString()))
            .thenReturn(new MutableLiveData<>());

        when(surveyDao.getSurveyResponsesByTimestampRangeNotLive(anyLong(), anyLong()))
            .thenReturn(Collections.emptyList());

        when(wellbeingDao.getDataBySurvey(anyLong()))
            .thenReturn(Collections.emptyList());

        when(physicalActivityDao.getPending())
            .thenReturn(Collections.emptyList());

        when(physicalActivityDao.getPhysicalActivityByType(anyString()))
            .thenReturn(null);

        when(surveyResponseActivityDao.getEmotions(anyLong()))
            .thenReturn(new MutableLiveData<>());

        when(activityRecordDao.getAllActivities())
            .thenReturn(new MutableLiveData<>());

        when(questionDao.getQuestionsByActivityType(any()))
            .thenReturn(Collections.emptyList());

        when(wellbeingDao.getTrueWellbeingRecordsByTimestampRange(anyLong(), anyLong(), anyString()))
            .thenReturn(new MutableLiveData<>());

        when(wellbeingDao.getFalseWellbeingRecordsByTimestampRange(anyLong(), anyLong(), anyString()))
            .thenReturn(new MutableLiveData<>());
    }

    @Before
    public void setUp()  throws InterruptedException {
        // Reference set preferences https://medium.com/@SimonKaz/android-testing-setting-sharedprefs-before-launching-an-activity-558730506b7c
        hiltTest.inject();
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(7000, TimeUnit.MILLISECONDS);

        try {
            onView(withText("Next")).perform(click());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
