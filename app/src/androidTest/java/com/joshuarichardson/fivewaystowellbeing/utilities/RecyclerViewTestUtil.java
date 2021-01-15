package com.joshuarichardson.fivewaystowellbeing.utilities;

import android.view.View;

import com.joshuarichardson.fivewaystowellbeing.ui.wellbeing_support.WellbeingSupportAdapter;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.BoundedMatcher;

// Need to be able to test values at a position in the recycler view https://stackoverflow.com/a/34795431/13496270
public class RecyclerViewTestUtil {
    public static Matcher<View> atRecyclerPosition(int position, Matcher<View> matcher) {
        if(matcher == null) {
            return null;
        }
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position: " + position + ": ");
                matcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(RecyclerView recyclerView) {
                // Need to get the ViewHolder at the position
                WellbeingSupportAdapter.WellbeingSupportViewHolder viewHolder = (WellbeingSupportAdapter.WellbeingSupportViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
                return viewHolder != null && matcher.matches(viewHolder.itemView);
            }
        };


    }
}
