package com.joshuarichardson.fivewaystowellbeing.ui.surveys;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.AnswerSurveyActivity;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.QuestionsToAskDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyQuestionSetDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.QuestionsToAsk;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyQuestionSet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withInputType;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes.BASIC_SURVEY;
import static com.joshuarichardson.fivewaystowellbeing.utilities.MaterialComponentTestUtil.withMaterialHint;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(WellbeingDatabaseModule.class)
public class BasicSurveyViewBuiltCorrectly {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Rule
    public ActivityScenarioRule<AnswerSurveyActivity> answerSurveyActivity = new ActivityScenarioRule<>(AnswerSurveyActivity.class);

    @Rule
    public HiltAndroidRule hiltTest = new HiltAndroidRule(this);

    @Module
    @InstallIn(ApplicationComponent.class)
    public static class TestWellbeingDatabaseModule {
        @Provides
        public WellbeingDatabase provideDatabaseService(@ApplicationContext Context context) {
            WellbeingDatabase mockWellbeingDatabase = mock(WellbeingDatabase.class);
            ActivityRecordDao activityDao = mock(ActivityRecordDao.class);
            QuestionsToAskDao questionsToAskDao = mock(QuestionsToAskDao.class);
            SurveyQuestionSetDao surveyQuestionsDao = mock(SurveyQuestionSetDao.class);

            // Set the data for the questions to ask
            List<QuestionsToAsk> questionsToAsk = Arrays.asList(new QuestionsToAsk("N/A", "N/A", 1, BASIC_SURVEY.name(), 0, null));
            when(questionsToAskDao.getQuestionsBySetId(anyLong())).thenReturn(questionsToAsk);

            // Set the data to return for unanswered surveys
            SurveyQuestionSet[] surveyQuestionList = new SurveyQuestionSet[] {new SurveyQuestionSet(485798345, 0)};
            List<SurveyQuestionSet> surveyQuestionSets = Arrays.asList(surveyQuestionList);
            when(surveyQuestionsDao.getUnansweredSurveyQuestionSets()).thenReturn(surveyQuestionSets);

            when(mockWellbeingDatabase.questionsToAskDao()).thenReturn(questionsToAskDao);
            when(mockWellbeingDatabase.surveyQuestionSetDao()).thenReturn(surveyQuestionsDao);

            // Set up the activities for the list
            ArrayList<ActivityRecord> activityList = new ArrayList<>();
            activityList.add(new ActivityRecord("Activity", 2000, 736284628, ActivityType.APP, WaysToWellbeing.UNASSIGNED));
            when(activityDao.getAllActivitiesNotLive()).thenReturn(activityList);

            MutableLiveData<List<ActivityRecord>> data = new MutableLiveData<>();

            ActivityRecord activity1 = new ActivityRecord("Running", 1200, 1607960240, ActivityType.SPORT, WaysToWellbeing.UNASSIGNED);
            ActivityRecord activity2 = new ActivityRecord("Jumping", 1201, 1607960241, ActivityType.SPORT, WaysToWellbeing.UNASSIGNED);
            ActivityRecord activity3 = new ActivityRecord("Fishing", 1202, 1607960242, ActivityType.SPORT, WaysToWellbeing.UNASSIGNED);

            activity1.setActivityRecordId(0);
            activity2.setActivityRecordId(1);
            activity3.setActivityRecordId(2);

            ArrayList<ActivityRecord> array = new ArrayList<>();
            array.add(activity1);
            array.add(activity2);
            array.add(activity3);

            data.setValue(array);

            when(activityDao.getAllActivities()).thenReturn(data);
            when(activityDao.getAllActivitiesNotLive()).thenReturn(array);

            when(mockWellbeingDatabase.activityRecordDao()).thenReturn(activityDao);

            return mockWellbeingDatabase;
        }
    }

    @Before
    public void setUp() throws InterruptedException {
        hiltTest.inject();
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void surveyTitle_ShouldAllowTextEntry() {
        onView(withId(R.id.survey_title_input_container))
            .check(matches(withMaterialHint("Enter a title")));

        onView(withId(R.id.survey_title_input))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(matches(withText("")))
            .check(matches(withInputType(TYPE_CLASS_TEXT)))
            .perform(typeText("Title"))
            .check(matches(withText("Title")));
    }

    @Test
    public void surveyDescription_ShouldAllowTextEntry() {
        onView(withId(R.id.survey_description_input_container))
            .check(matches(withMaterialHint("Enter a description")));

        onView(withId(R.id.survey_description_input))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(matches(withText("")))
            .check(matches(withInputType(TYPE_CLASS_TEXT)))
            .perform(typeText("Description"))
            .check(matches(withText("Description")));
    }

    @Test
    public void activityDropDownList_ShouldContainAllActivities() {
        onView(withId(R.id.survey_activity_input_container))
                .check(matches(withMaterialHint("Select an activity")));

        onView(withId(R.id.survey_activity_input))
            .perform(scrollTo(), click());

        // Trying to get the drop down list https://stackoverflow.com/a/45368345/13496270
        // Get the adapter of Strings
        // Search the popup
        DataInteraction popup = onData(instanceOf(String.class))
                .inRoot(RootMatchers.isPlatformPopup());

        // Check the text matches
        popup.atPosition(0)
            .check(matches(withText("Running")));

        popup.atPosition(1)
            .check(matches(withText("Jumping")));

        popup.atPosition(2)
            .check(matches(withText("Fishing")));
    }

    @Test
    public void waysToWellbeingDropDownList_ShouldContainAllWaysToWellbeing() {
        onView(withId(R.id.way_to_wellbeing_input_container))
                .check(matches(withMaterialHint("Select way to wellbeing")));

        onView(withId(R.id.way_to_wellbeing_input))
            .perform(scrollTo(), click());

        // Trying to get the drop down list https://stackoverflow.com/a/45368345/13496270
        // Get the adapter of Strings
        // Search the popup
        DataInteraction popup = onData(instanceOf(String.class))
                .inRoot(RootMatchers.isPlatformPopup());

        // Check the text matches on all items in the drop down list
        popup.atPosition(0)
            .check(matches(withText("Connect")));

        popup.atPosition(1)
            .check(matches(withText("Be active")));

        popup.atPosition(2)
            .check(matches(withText("Keep learning")));

        popup.atPosition(3)
            .check(matches(withText("Take notice")));

        popup.atPosition(4)
            .check(matches(withText("Give")));

        popup.atPosition(5)
            .check(matches(withText("None")));
    }
}
