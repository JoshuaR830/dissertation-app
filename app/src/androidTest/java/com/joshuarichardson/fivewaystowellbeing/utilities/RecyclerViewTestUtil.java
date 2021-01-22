package com.joshuarichardson.fivewaystowellbeing.utilities;

import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.BoundedMatcher;

// Need to be able to test values at a position in the recycler view https://stackoverflow.com/a/34795431/13496270
public class RecyclerViewTestUtil {
    public static Matcher<View> atRecyclerPosition(int position, Matcher<View> matcher) {
        if (matcher == null) {
            return null;
        }

        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            // Create the message which can be used for reporting
            public void describeTo(Description description) {
                description.appendText("has item at position: " + position + ": ");
                matcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(RecyclerView recyclerView) {
                // Need to get the ViewHolder at the position
                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) recyclerView.findViewHolderForAdapterPosition(position);

                // Does the item match the expectation
                return viewHolder != null && matcher.matches(viewHolder.itemView);
            }
        };
    }
}
