package com.joshuarichardson.fivewaystowellbeing.ui.welbeing_support;

import com.joshuarichardson.fivewaystowellbeing.MainActivity;
import com.joshuarichardson.fivewaystowellbeing.R;

import org.junit.Rule;
import org.junit.Test;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

public class EspressoTestPractice {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void somethingToPractice() {
        // With text is a matcher - isDisplayed is what happens
        // So this checks that a view with "Five Ways to Wellbeing" is displayed
        onView(withText("Five Ways to Wellbeing")).check(matches(isDisplayed()));

        // Can even have multiple actions with 1 .perform()
        onView(withText("Five Ways to Wellbeing")).perform(click());


        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.get_wellbeing_support)).perform(click());
    }

    // Think what users might do
    // Interaction with UI
    // onView & onData for interaction with a view - take a matcher that matches exactly 1 view
    // ViewMatchers - locate a view within the view hierarchy
    // ViewActions - List of objects to pass to ViewInteraction.perform()
    // ViewAssertions - ViewInteraction.check()


}
