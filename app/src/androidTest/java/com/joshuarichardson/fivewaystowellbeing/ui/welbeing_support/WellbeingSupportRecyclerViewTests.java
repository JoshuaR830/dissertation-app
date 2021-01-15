package com.joshuarichardson.fivewaystowellbeing.ui.welbeing_support;

import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.ui.wellbeing_support.WellbeingSupportActivity;

import org.junit.Rule;
import org.junit.Test;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.joshuarichardson.fivewaystowellbeing.utilities.RecyclerViewTestUtil.atRecyclerPosition;

public class WellbeingSupportRecyclerViewTests {
    @Rule
    public ActivityScenarioRule<WellbeingSupportActivity> wellbeingSupportActivity = new ActivityScenarioRule<>(WellbeingSupportActivity.class);

    // How to test recycler views using espresso https://developer.android.com/training/testing/espresso/lists#recycler-view-list-items
    @Test
    public void theCardForNHSShouldBeShown() {
        onView(withId(R.id.wellbeing_support_recycler_view))
            .perform(scrollToPosition(0))
            .check(matches(atRecyclerPosition(0, hasDescendant(withText("NHS")))))
            .check(matches(atRecyclerPosition(0, hasDescendant(withText("Information about NHS mental health services.")))));
    }

    @Test void theCardForMindShouldBeShown() {
        onView(withId(R.id.wellbeing_support_recycler_view))
            .perform(scrollToPosition(1))
            .check(matches(atRecyclerPosition(1, hasDescendant(withText("Mind")))))
            .check(matches(atRecyclerPosition(1, hasDescendant(withText("Tips and guides for managing with mental health problems.")))));
    }

    @Test void theCardForSelfHelpResourcesShouldBeShown() {
        onView(withId(R.id.wellbeing_support_recycler_view))
            .perform(scrollToPosition(1))
            .check(matches(atRecyclerPosition(2, hasDescendant(withText("Self-help resources")))))
            .check(matches(atRecyclerPosition(2, hasDescendant(withText("A selection of self-help resources.")))));
    }
}
