package com.joshuarichardson.fivewaystowellbeing.ui.activities;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.joshuarichardson.fivewaystowellbeing.CreatePassTimeActivity;
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

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_CLASS_TEXT;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withInputType;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.joshuarichardson.fivewaystowellbeing.utilities.MaterialComponentTestUtil.withMaterialHint;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

@HiltAndroidTest
public class PassTimeQuestionsShouldMatchQuestionTypes {

    @Rule
    public HiltAndroidRule hiltTest = new HiltAndroidRule(this);

    @Rule
    public ActivityScenarioRule<CreatePassTimeActivity> answerSurveyActivity = new ActivityScenarioRule<>(CreatePassTimeActivity.class);

    @Before
    public void setUp() throws InterruptedException {
        hiltTest.inject();
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void passTimeQuestions_ShouldMatchQuestionType() {
        onView(withId(R.id.pass_time_name_input_container)).check(matches(withMaterialHint("Activity name")));
        onView(withId(R.id.pass_time_duration_input_container)).check(matches(withMaterialHint("Activity duration")));
        onView(withId(R.id.pass_time_type_input_container)).check(matches(withMaterialHint("Activity type")));
        onView(withId(R.id.way_to_wellbeing_input_container)).check(matches(withMaterialHint("Select way to wellbeing")));

        onView(withId(R.id.pass_time_name_input)).check(matches(withClassName(equalTo(TextInputEditText.class.getName()))));
        onView(withId(R.id.pass_time_duration_input)).check(matches(withClassName(equalTo(TextInputEditText.class.getName()))));
        onView(withId(R.id.pass_time_type_input)).check(matches(withClassName(equalTo(MaterialAutoCompleteTextView.class.getName()))));
        onView(withId(R.id.way_to_wellbeing_input)).check(matches(withClassName(equalTo(MaterialAutoCompleteTextView.class.getName()))));

        onView(withId(R.id.pass_time_name_input)).check(matches(withText("")));
        onView(withId(R.id.pass_time_duration_input)).check(matches(withText("0")));
        onView(withId(R.id.pass_time_type_input)).check(matches(withText("")));
        onView(withId(R.id.way_to_wellbeing_input)).check(matches(withText("")));

        onView(withId(R.id.pass_time_name_input)).check(matches(withInputType(TYPE_CLASS_TEXT)));
        onView(withId(R.id.pass_time_duration_input)).check(matches(withInputType(TYPE_CLASS_NUMBER)));
    }

    @Test
    public void passTimeDropDown_ShouldContainAllItemsFromList() {
        onView(withId(R.id.pass_time_type_input)).perform(click());

        DataInteraction popup = onData(instanceOf(String.class))
                .inRoot(RootMatchers.isPlatformPopup());

        popup.atPosition(0).check(matches(withText("App")));
        popup.atPosition(1).check(matches(withText("Sport")));
        popup.atPosition(2).check(matches(withText("Hobby")));
        popup.atPosition(3).check(matches(withText("Pet")));
        popup.atPosition(4).check(matches(withText("Work")));
        popup.atPosition(5).check(matches(withText("Learning")));
    }

    @Test
    public void waysToWellbeingDropDown_ShouldContainAllWaysToWellbeing() {
        onView(withId(R.id.way_to_wellbeing_input)).perform(click());

        DataInteraction popup = onData(instanceOf(String.class))
            .inRoot(RootMatchers.isPlatformPopup());

        popup.atPosition(0).check(matches(withText("Connect")));
        popup.atPosition(1).check(matches(withText("Be active")));
        popup.atPosition(2).check(matches(withText("Keep learning")));
        popup.atPosition(3).check(matches(withText("Take notice")));
        popup.atPosition(4).check(matches(withText("Give")));
        popup.atPosition(5).check(matches(withText("None")));
    }

    @Test
    public void selectingActivityType_ShouldSetADefaultWayToWellbeing() {
        onView(withId(R.id.way_to_wellbeing_input)).check(matches(withText("")));
        onView(withId(R.id.pass_time_type_input)).perform(click());

        DataInteraction popup = onData(instanceOf(String.class))
            .inRoot(RootMatchers.isPlatformPopup());

        popup.atPosition(1).perform(click());

        onView(withId(R.id.way_to_wellbeing_input)).check(matches(withText("Be active")));
    }
}
