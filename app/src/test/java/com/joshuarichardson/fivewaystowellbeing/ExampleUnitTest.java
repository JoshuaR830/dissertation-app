package com.joshuarichardson.fivewaystowellbeing;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    // So this works here but not in an instrumented test
    @Test
    public void somethingToTestTest() {
        SomethingToTest first = new SomethingToTest("Hello");
        SomethingToTest second = new SomethingToTest("Hello");

        assertThat(second)
                .usingRecursiveComparison()
                .isEqualTo(first);
    }
}

