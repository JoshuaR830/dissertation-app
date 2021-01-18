package com.joshuarichardson.fivewaystowellbeing.ui.surveys;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.AnswerSurveyActivity;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.instanceOf;

public class SurveyViewBuiltCorrectly {

    private WellbeingDatabase wellbeingDb;
    private ActivityRecordDao activityDao;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Rule
    public ActivityScenarioRule<AnswerSurveyActivity> answerSurveyActivity = new ActivityScenarioRule<>(AnswerSurveyActivity.class);

    @Before
    public void setup() {
        // https://developer.android.com/training/data-storage/room/testing-db
        Context context = ApplicationProvider.getApplicationContext();
        this.wellbeingDb = Room.inMemoryDatabaseBuilder(context, WellbeingDatabase.class).build();
        this.activityDao = this.wellbeingDb.activityRecordDao();

        // Insert some items into the activities database
        this.activityDao.insert(new ActivityRecord("Running", 1200, 1607960240, ActivityType.SPORT));
        this.activityDao.insert(new ActivityRecord("Jumping", 1201, 1607960241, ActivityType.SPORT));
        this.activityDao.insert(new ActivityRecord("Fishing", 1202, 1607960242, ActivityType.SPORT));
    }

    @After
    public void teardown() {
        this.wellbeingDb.close();
    }

    @Test
    public void dropDownList_ShouldContainAllActivities () {
        onView(withId(R.id.drop_down_input))
            .perform(click());

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
}
