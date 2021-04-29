package com.joshuarichardson.fivewaystowellbeing;

import android.content.Context;
import android.content.SharedPreferences;

import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.AutomaticActivityDao;
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
import static org.mockito.Mockito.doReturn;
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
    public AutomaticActivityDao automaticActivityDao = mock(AutomaticActivityDao.class);

    // What the mock database should return
    protected void mockDatabaseResponses() {
        doReturn(resultsDao)
            .when(mockWellbeingDatabase)
            .wellbeingResultsDao();

        doReturn(surveyDao)
            .when(mockWellbeingDatabase)
            .surveyResponseDao();

        doReturn(wellbeingDao)
            .when(mockWellbeingDatabase)
            .wellbeingRecordDao();

        doReturn(questionDao)
            .when(mockWellbeingDatabase)
            .wellbeingQuestionDao();

        doReturn(surveyResponseActivityDao)
            .when(mockWellbeingDatabase)
            .surveyResponseActivityRecordDao();

        doReturn(activityRecordDao)
            .when(mockWellbeingDatabase)
            .activityRecordDao();

        doReturn(automaticActivityDao)
            .when(mockWellbeingDatabase)
            .physicalActivityDao();
    }

    // The default responses provided by various common methods used on the progress fragment page
    protected void defaultResponses() {
        doReturn(new MutableLiveData<>(Collections.emptyList()))
            .when(surveyDao)
            .getSurveyResponsesByTimestampRange(anyLong(), anyLong());

        doReturn(new MutableLiveData<>(Collections.emptyList()))
            .when(questionDao)
            .getWaysToWellbeingBetweenTimes(anyLong(), anyLong());

        doReturn(Collections.emptyList())
            .when(surveyDao)
            .getSurveyResponsesByTimestampRangeNotLive(anyLong(), anyLong());

        doReturn(Collections.emptyList())
            .when(wellbeingDao)
            .getDataBySurvey(anyLong());

        doReturn(Collections.emptyList())
            .when(automaticActivityDao)
            .getPending();

        doReturn(null)
            .when(automaticActivityDao)
            .getPhysicalActivityByTypeWithAssociatedActivity(anyString());

        doReturn(new MutableLiveData<>())
            .when(surveyResponseActivityDao)
            .getEmotions(anyLong());

        doReturn(new MutableLiveData<>())
            .when(activityRecordDao)
            .getAllActivities();

        doReturn(Collections.emptyList())
            .when(questionDao)
            .getQuestionsByActivityType(any());

        doReturn(new MutableLiveData<>())
            .when(wellbeingDao)
            .getTrueWellbeingRecordsByTimestampRangeAndWayToWellbeingType(anyLong(), anyLong(), anyString());

        doReturn(new MutableLiveData<>())
            .when(wellbeingDao)
            .getFalseWellbeingRecordsByTimestampRangeAndWayToWellbeingType(anyLong(), anyLong(), anyString());
    }

    @Before
    public void setUp()  throws InterruptedException {
        // Reference set preferences https://medium.com/@SimonKaz/android-testing-setting-sharedprefs-before-launching-an-activity-558730506b7c
        hiltTest.inject();
        WellbeingDatabaseModule.databaseExecutor.awaitTermination(7000, TimeUnit.MILLISECONDS);
        Context context = getInstrumentation().getTargetContext();
        SharedPreferences.Editor preferenceEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        preferenceEditor.putInt("app_version", 5);
        try {
            onView(withText("Next")).perform(click());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            onView(withText("Allow")).perform(click());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
