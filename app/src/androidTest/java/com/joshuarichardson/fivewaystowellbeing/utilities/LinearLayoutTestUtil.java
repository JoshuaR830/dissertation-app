package com.joshuarichardson.fivewaystowellbeing.utilities;

import android.view.View;
import android.widget.LinearLayout;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

// Reference: https://stackoverflow.com/a/38902318/13496270
public class LinearLayoutTestUtil {
    public static Matcher<View> nthChildOf(final Matcher<View> linearLayout, final int layoutPosition) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {

                // If it isn't an instance of LinearLayout then it's wrong
                if(!(item.getParent() instanceof LinearLayout)) {
                    return false;
                }

                LinearLayout layout = (LinearLayout) item.getParent();

                if(layout.getChildAt(layoutPosition) == null) {
                    return false;
                }

                return linearLayout.matches(item.getParent()) && layout.getChildAt(layoutPosition).equals(item);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("has child at position " + layoutPosition + ":");
                linearLayout.describeTo(description);
            }
        };
    }
}
