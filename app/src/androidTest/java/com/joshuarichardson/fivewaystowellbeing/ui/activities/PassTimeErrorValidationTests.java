package com.joshuarichardson.fivewaystowellbeing.ui.activities;

import com.joshuarichardson.fivewaystowellbeing.ui.pass_times.edit.CreatePassTimeActivity;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.joshuarichardson.fivewaystowellbeing.utilities.MaterialComponentTestUtil.withMaterialError;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;

@HiltAndroidTest
public class PassTimeErrorValidationTests {

    @Rule
    public HiltAndroidRule hiltTest = new HiltAndroidRule(this);

    @Rule
    public ActivityScenarioRule<CreatePassTimeActivity> createPasstimeActivity = new ActivityScenarioRule<>(CreatePassTimeActivity.class);

    @Before
    public void setUp() throws InterruptedException {
        hiltTest.inject();
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void whenNameIsEnteredButTypeIsEmpty_OnlyNameShouldHaveErrorText() {
        onView(withId(R.id.pass_time_name_input)).perform(typeText("Activity name"), closeSoftKeyboard());
        onView(withId(R.id.passtime_submit_button)).perform(click());

        onView(withId(R.id.pass_time_name_input_container)).check(matches(not(withMaterialError("Please enter a name"))));
        onView(withId(R.id.pass_time_type_input_container)).check(matches(withMaterialError("Please choose a type")));
    }

    @Test
    public void whenTypeIsEnteredButNameIsEmpty_OnlyTypeShouldHaveErrorText() {
        onView(withId(R.id.pass_time_type_input)).perform(click());

        DataInteraction popup = onData(instanceOf(String.class))
            .inRoot(RootMatchers.isPlatformPopup());

        popup.atPosition(0)
            .perform(click());

        onView(withId(R.id.passtime_submit_button))
            .perform(click());

        onView(withId(R.id.pass_time_name_input_container))
            .check(matches(withMaterialError("Please enter a name")));

        onView(withId(R.id.pass_time_type_input_container))
            .check(matches(not(withMaterialError("Please choose a type"))));
    }

    @Test
    public void whenNeitherTypeOrNameAreEntered_BothShouldShowErrorText() {
        onView(withId(R.id.passtime_submit_button))
            .perform(click());

        onView(withId(R.id.pass_time_name_input_container))
            .check(matches(withMaterialError("Please enter a name")));

        onView(withId(R.id.pass_time_type_input_container))
            .check(matches(withMaterialError("Please choose a type")));
    }
}
