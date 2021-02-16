package com.joshuarichardson.fivewaystowellbeing.utilities;

import android.view.View;

import com.google.android.material.textfield.TextInputLayout;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class MaterialComponentTestUtil {

    // Reference https://stackoverflow.com/a/38874162/13496270
    public static Matcher<View> withMaterialHint(String hintText) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                // Check it is the expected layout type
                if (!(view instanceof TextInputLayout)) {
                    return false;
                }

                // Get the hint text
                CharSequence hint = ((TextInputLayout) view).getHint();

                // Ensure that the hint has a value
                if (hint == null) {
                    return false;
                }

                // Return true if it matches
                return hintText.equals(hint.toString());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with material hint: " + hintText);
            }
        };
    }

    // Reference https://stackoverflow.com/a/38874162/13496270
    public static Matcher<View> withMaterialError(String errorText) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                // Check it is the expected layout type
                if(!(view instanceof TextInputLayout)) {
                    return false;
                }

                // Get the hint text
                CharSequence error = ((TextInputLayout) view).getError();

                // Ensure that the hint has a value
                if (error == null) {
                    return false;
                }

                // Return true if it matches
                return errorText.equals(error.toString());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with material error: " + errorText);
            }
        };
    }

    // Reference https://stackoverflow.com/a/38874162/13496270
    public static Matcher<View> withMaterialHelper(String helperText) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                // Check it is the expected layout type
                if(!(view instanceof TextInputLayout)) {
                    return false;
                }

                // Get the hint text
                CharSequence helper = ((TextInputLayout) view).getHelperText();

                // Ensure that the hint has a value
                if (helper == null) {
                    return false;
                }

                // Return true if it matches
                return helperText.equals(helper.toString());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with material helper: " + helperText);
            }
        };
    }
}
