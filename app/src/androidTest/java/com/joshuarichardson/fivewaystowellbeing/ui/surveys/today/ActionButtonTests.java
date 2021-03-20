package com.joshuarichardson.fivewaystowellbeing.ui.surveys.today;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.ProgressFragmentTestFixture;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.RawSurveyData;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;

import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.lifecycle.MutableLiveData;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(WellbeingDatabaseModule.class)
public class ActionButtonTests extends ProgressFragmentTestFixture {

    @Module
    @InstallIn(ApplicationComponent.class)
    public class TestWellbeingDatabaseModule {
        @Provides
        public WellbeingDatabase provideDatabaseService(@ApplicationContext Context context) {

            // Set up the default items to return
            defaultResponses();

            // Return the DAOs from the DB
            mockDatabaseResponses();

            return mockWellbeingDatabase;
        }
    }

    @Override
    protected void defaultResponses() {
        super.defaultResponses();
        long now = new Date().getTime();

        List<SurveyResponse> list = Arrays.asList(
            new SurveyResponse(now, WaysToWellbeing.CONNECT.name(), "title 1", "description 1"),
            new SurveyResponse(now, WaysToWellbeing.BE_ACTIVE.name(), "title 2", "description 2"));

        when(wellbeingDao.getDataBySurvey(anyLong())).thenReturn(Arrays.asList(
            new RawSurveyData(now, "Survey note", "", "Activity name 1", 1, "Question 1", 1, false, ActivityType.HOBBY.toString(), WaysToWellbeing.CONNECT.toString(), -1, -1, 0, false),
            new RawSurveyData(now, "Survey note", "Activity note 2", "Activity name 2", 2, "Question 1", 2, false, ActivityType.HOBBY.toString(), WaysToWellbeing.CONNECT.toString(), 10860000, -1, 0, false),
            new RawSurveyData(now, "Survey note", "", "Activity name 3", 3, "Question 1", 3, false, ActivityType.LEARNING.toString(), WaysToWellbeing.CONNECT.toString(), -1, 14460000, 0, false),
            new RawSurveyData(now, "Survey note", "", "Activity name 4", 4, "Question 1", 4, false, ActivityType.LEARNING.toString(), WaysToWellbeing.CONNECT.toString(), 10860000, 14460000, 0, false)
        ));

        when(surveyDao.getSurveyResponsesByTimestampRange(anyLong(), anyLong()))
            .thenReturn(new MutableLiveData<>(list));
    }

    @Test
    public void verifyCorrectBehaviourForNoteInputBox() throws InterruptedException {
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);
        onView(allOf(withId(R.id.pass_time_item), nthChildOf(withId(R.id.survey_item_container), 0)))
            .perform(scrollTo())
            .check(matches(isDisplayed()));

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
            .check(matches(withMaterialHelper("Press done to save changes")));

        // The done button now saves the note
        onView(allOf(withId(R.id.done_button), isDescendantOfA(allOf(withId(R.id.activity_content), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))))
            .perform(scrollTo(), click());

        verify(this.surveyResponseActivityDao, times(1)).updateNote(anyLong(), anyString());

        onView(allOf(withId(R.id.expand_options_button), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click());

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

        onView(allOf(withId(R.id.add_note_button), isDescendantOfA(allOf(withId(R.id.activity_content), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 1))))))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click());

        onView(allOf(withId(R.id.note_input), isDescendantOfA(allOf(withId(R.id.activity_content), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 1))))))
            .perform(scrollTo())
            .check(matches(withText("Activity note 2")));
    }

    @Test
    public void timeText() throws InterruptedException {
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);
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

        onView(allOf(withId(R.id.add_start_time), isDescendantOfA(allOf(withId(R.id.activity_content), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))))
            .perform(scrollTo(), click());

        onView(withText("OK"))
            .perform(click());

        onView(allOf(withId(R.id.activity_time_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo())
            .check(matches(withText("12:00")));

        verify(this.surveyResponseActivityDao, times(1)).updateStartTime(anyLong(), anyLong());
    }

    @Test
    public void onEndTimeTextClicked_ShouldUpdateDatabase() throws InterruptedException {
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);

        onView(allOf(withId(R.id.add_end_time), isDescendantOfA(allOf(withId(R.id.activity_content), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))))
            .perform(scrollTo(), click());

        onView(withText("OK"))
            .perform(click());

        onView(allOf(withId(R.id.activity_time_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo())
            .check(matches(withText("12:00")));

        verify(this.surveyResponseActivityDao, times(1)).updateEndTime(anyLong(), anyLong());
    }

    @Test
    public void onWorstEmotionClicked_ShouldUpdateDatabase() throws InterruptedException {
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);

        onView(allOf(withId(R.id.sentiment_worst), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo(), click());

        verify(this.surveyResponseActivityDao, times(1)).updateEmotion(anyLong(), eq(1));
    }

    @Test
    public void onBadEmotionClicked_ShouldUpdateDatabase() throws InterruptedException {
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);

        onView(allOf(withId(R.id.sentiment_bad), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo(), click());

        verify(this.surveyResponseActivityDao, times(1)).updateEmotion(anyLong(), eq(2));
    }

    @Test
    public void onNeutralEmotionClicked_ShouldUpdateDatabase() throws InterruptedException {
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);

        onView(allOf(withId(R.id.sentiment_neutral), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo(), click());

        verify(this.surveyResponseActivityDao, times(1)).updateEmotion(anyLong(), eq(3));
    }

    @Test
    public void onGoodEmotionClicked_ShouldUpdateDatabase() throws InterruptedException {
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);

        onView(allOf(withId(R.id.sentiment_good), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo(), click());

        verify(this.surveyResponseActivityDao, times(1)).updateEmotion(anyLong(), eq(4));
    }

    @Test
    public void onBestEmotionClicked_ShouldUpdateDatabase() throws InterruptedException {
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);

        onView(allOf(withId(R.id.sentiment_best), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo(), click());

        verify(this.surveyResponseActivityDao, times(1)).updateEmotion(anyLong(), eq(5));
    }

    @Test
    public void onIsDoneClicked_ShouldUpdateDatabase() throws InterruptedException {
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);

        onView(allOf(withId(R.id.done_button), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo(), click());

        verify(this.surveyResponseActivityDao, times(1)).updateIsDone(anyLong(), eq(true));
    }
}
