package com.joshuarichardson.fivewaystowellbeing.ui.surveys.today;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.MainActivity;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.RawSurveyData;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphItem;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingQuestionDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.joshuarichardson.fivewaystowellbeing.utilities.LinearLayoutTestUtil.nthChildOf;
import static com.joshuarichardson.fivewaystowellbeing.utilities.MaterialComponentTestUtil.withMaterialHelper;
import static com.joshuarichardson.fivewaystowellbeing.utilities.MaterialComponentTestUtil.withMaterialHint;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(WellbeingDatabaseModule.class)
public class ActionButtonTests {
    private SurveyResponseActivityRecordDao surveyActivity;

    @Rule(order = 0)
    public HiltAndroidRule hiltTest = new HiltAndroidRule(this);

    @Rule(order = 1)
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Rule(order = 2)
    public ActivityScenarioRule<MainActivity> mainActivity = new ActivityScenarioRule<>(MainActivity.class);

    @Module
    @InstallIn(ApplicationComponent.class)
    public class TestWellbeingDatabaseModule {
        @Provides
        public WellbeingDatabase provideDatabaseService(@ApplicationContext Context context) {
            ActionButtonTests.this.surveyActivity = mock(SurveyResponseActivityRecordDao.class);
            WellbeingDatabase mockWellbeingDatabase = mock(WellbeingDatabase.class);
            WellbeingQuestionDao questionDao = mock(WellbeingQuestionDao.class);
            WellbeingRecordDao wellbeingDao = mock(WellbeingRecordDao.class);

            long now = new Date().getTime();

            List<SurveyResponse> list = Arrays.asList(
                    new SurveyResponse(now, WaysToWellbeing.CONNECT.name(), "title 1", "description 1"),
                    new SurveyResponse(now, WaysToWellbeing.BE_ACTIVE.name(), "title 2", "description 2"));

            LiveData<List<WellbeingGraphItem>> graphData = new MutableLiveData<>(Arrays.asList());
            when(questionDao.getWaysToWellbeingBetweenTimes(anyLong(), anyLong())).thenReturn(graphData);

            SurveyResponseDao surveyDao = mock(SurveyResponseDao.class);

            LiveData<Integer> wayToWellbeing = new MutableLiveData<>();
            when(surveyDao.getLiveInsights(anyString()))
                .thenReturn(wayToWellbeing);


            when(wellbeingDao.getDataBySurvey(anyLong())).thenReturn(Arrays.asList(
                new RawSurveyData(now, "Survey note", "", "Activity name 1", 1, "Question 1", 1, false, ActivityType.HOBBY.toString(), WaysToWellbeing.CONNECT.toString(), -1, -1),
                new RawSurveyData(now, "Survey note", "Activity note 2", "Activity name 2", 2, "Question 1", 2, false, ActivityType.HOBBY.toString(), WaysToWellbeing.CONNECT.toString(), 10860000, -1),
                new RawSurveyData(now, "Survey note", "", "Activity name 3", 3, "Question 1", 3, false, ActivityType.LEARNING.toString(), WaysToWellbeing.CONNECT.toString(), -1, 14460000),
                new RawSurveyData(now, "Survey note", "", "Activity name 4", 4, "Question 1", 4, false, ActivityType.LEARNING.toString(), WaysToWellbeing.CONNECT.toString(), 10860000, 14460000)
            ));


            when(surveyDao.getSurveyResponsesByTimestampRange(anyLong(), anyLong()))
                    .thenReturn(new MutableLiveData<>(list));
            when(mockWellbeingDatabase.surveyResponseActivityRecordDao()).thenReturn(ActionButtonTests.this.surveyActivity);
            when(mockWellbeingDatabase.wellbeingRecordDao()).thenReturn(wellbeingDao);
            when(mockWellbeingDatabase.surveyResponseDao()).thenReturn(surveyDao);
            when(mockWellbeingDatabase.wellbeingQuestionDao()).thenReturn(questionDao);

            return mockWellbeingDatabase;
        }
    }

    @Before
    public void setup() {
        hiltTest.inject();
    }

    @Test
    public void verifyCorrectBehaviourForNoteInputBox() {
        onView(allOf(withId(R.id.pass_time_item), nthChildOf(withId(R.id.survey_item_container), 0)))
            .perform(scrollTo())
            .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.expand_options_button), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click());

        onView(allOf(withId(R.id.activity_content), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo())
            .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.note_input_container), isDescendantOfA(allOf(withId(R.id.activity_content), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))))
            .check(matches(not(isDisplayed())));

        onView(allOf(withId(R.id.add_note_button), isDescendantOfA(allOf(withId(R.id.activity_content), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click());

        onView(allOf(withId(R.id.add_start_time), isDescendantOfA(allOf(withId(R.id.activity_content), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))))
            .perform(scrollTo())
            .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.add_end_time), isDescendantOfA(allOf(withId(R.id.activity_content), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))))
            .perform(scrollTo())
            .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.note_input), isDescendantOfA(allOf(withId(R.id.activity_content), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))))
            .perform(scrollTo(), typeText("Text"), closeSoftKeyboard());

        onView(allOf(withId(R.id.note_input_container), isDescendantOfA(allOf(withId(R.id.activity_content), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))))
            .perform(scrollTo())
            .check(matches(allOf(isDisplayed(), withMaterialHint("Enter a note"))))
            .check(matches(withMaterialHelper("Unsaved changes")));

        onView(allOf(withId(R.id.save_note_button), isDescendantOfA(allOf(withId(R.id.activity_content), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))))
            .perform(scrollTo(), click());

        onView(allOf(withId(R.id.note_input_container), isDescendantOfA(allOf(withId(R.id.activity_content), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))))
            .perform(scrollTo())
            .check(matches(withMaterialHelper("Saved")));

        verify(this.surveyActivity, times(1)).updateNote(anyLong(), anyString());

        onView(allOf(withId(R.id.activity_note_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo())
            .check(matches(withText("Text")));

        onView(allOf(withId(R.id.add_note_button), isDescendantOfA(allOf(withId(R.id.activity_content), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click());

        // Button 1 for confirm
        onView(withId(android.R.id.button1))
            .perform(click());

        onView(allOf(withId(R.id.activity_note_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .check(matches(not(isDisplayed())));

        onView(allOf(withId(R.id.note_input_container), isDescendantOfA(allOf(withId(R.id.activity_content), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))))
            .check(matches(not(isDisplayed())));

        onView(allOf(withId(R.id.expand_options_button), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 1))))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click());

        onView(allOf(withId(R.id.add_note_button), isDescendantOfA(allOf(withId(R.id.activity_content), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 1))))))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click());

        onView(allOf(withId(R.id.note_input), isDescendantOfA(allOf(withId(R.id.activity_content), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 1))))))
            .perform(scrollTo())
            .check(matches(withText("Activity note 2")));
    }

    @Test
    public void timeText() {
        onView(allOf(withId(R.id.activity_time_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo())
            .check(matches(withText("")));

        onView(allOf(withId(R.id.activity_time_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 1))))
            .perform(scrollTo())
            .check(matches(withText("03:01")));

        onView(allOf(withId(R.id.activity_time_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 2))))
            .perform(scrollTo())
            .check(matches(withText("04:01")));

        onView(allOf(withId(R.id.activity_time_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 3))))
            .perform(scrollTo())
            .check(matches(withText("03:01 - 04:01")));
    }

    @Test
    public void onStartTimeTextClicked_ShouldUpdateDatabase() throws InterruptedException {
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);

        onView(allOf(withId(R.id.expand_options_button), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click());

        onView(allOf(withId(R.id.add_start_time), isDescendantOfA(allOf(withId(R.id.activity_content), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))))
            .perform(scrollTo(), click());

        onView(withText("OK"))
            .perform(click());

        onView(allOf(withId(R.id.activity_time_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo())
            .check(matches(withText("12:00")));

        verify(this.surveyActivity, times(1)).updateStartTime(anyLong(), anyLong());
    }

    @Test
    public void onEndTimeTextClicked_ShouldUpdateDatabase() {
        onView(allOf(withId(R.id.expand_options_button), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click());

        onView(allOf(withId(R.id.add_end_time), isDescendantOfA(allOf(withId(R.id.activity_content), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))))
            .perform(scrollTo(), click());

        onView(withText("OK"))
            .perform(click());

        onView(allOf(withId(R.id.activity_time_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo())
            .check(matches(withText("12:00")));

        verify(this.surveyActivity, times(1)).updateEndTime(anyLong(), anyLong());
    }
}
