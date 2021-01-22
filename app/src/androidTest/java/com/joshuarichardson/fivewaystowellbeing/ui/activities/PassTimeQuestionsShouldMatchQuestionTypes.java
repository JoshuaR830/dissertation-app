package com.joshuarichardson.fivewaystowellbeing.ui.activities;

import android.widget.AutoCompleteTextView;

import com.google.android.material.textfield.TextInputEditText;
import com.joshuarichardson.fivewaystowellbeing.CreatePassTimeActivity;
import com.joshuarichardson.fivewaystowellbeing.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

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
    public void setUp() {
        hiltTest.inject();
    }

    @Test
    public void passTimeQuestions_ShouldMatchQuestionType() {
        onView(withId(R.id.passtimeQuestion1)).check(matches(withMaterialHint("Activity name")));
        onView(withId(R.id.passtimeQuestion2)).check(matches(withMaterialHint("Activity duration")));
        onView(withId(R.id.passtimeQuestion3)).check(matches(withMaterialHint("Activity type")));

        onView(withId(R.id.pass_time_name_input)).check(matches(withClassName(equalTo(TextInputEditText.class.getName()))));
        onView(withId(R.id.pass_time_duration_input)).check(matches(withClassName(equalTo(TextInputEditText.class.getName()))));
        onView(withId(R.id.pass_time_type_input)).check(matches(withClassName(equalTo(AutoCompleteTextView.class.getName()))));

        onView(withId(R.id.pass_time_name_input)).check(matches(withText("")));
        onView(withId(R.id.pass_time_duration_input)).check(matches(withText("")));
        onView(withId(R.id.pass_time_type_input)).check(matches(withText("")));

        onView(withId(R.id.pass_time_name_input)).check(matches(withInputType(TYPE_CLASS_TEXT)));
        onView(withId(R.id.pass_time_duration_input)).check(matches(withInputType(TYPE_CLASS_NUMBER)));
        onView(withId(R.id.pass_time_type_input)).check(matches(withInputType(TYPE_CLASS_TEXT)));
    }

    @Test
    public void passTimeDropDown_ShouldContainAllItemsFromList() {
        onView(withId(R.id.pass_time_type_input)).perform(click());

        DataInteraction popup = onData(instanceOf(String.class))
                .inRoot(RootMatchers.isPlatformPopup());

        popup.atPosition(0).check(matches(withText("Sport")));
        popup.atPosition(1).check(matches(withText("App")));
    }
}
