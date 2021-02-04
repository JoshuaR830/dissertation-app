package com.joshuarichardson.fivewaystowellbeing.ui.graphs;

import android.graphics.Color;
import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.N})
public class ArcOfGraphTests {
    @Test
    public void whenThereAre0SegmentsAndThisIsTheFirstSegment() {
        WaysToWellbeingGraphValues wellbeingGraph = new WaysToWellbeingGraphValues(10, Color.BLACK, 0, 0);

        // This is because it should default to 0
        assertThat(wellbeingGraph.getStartAngle()).isEqualTo(0);
        assertThat(wellbeingGraph.getArcLength()).isEqualTo(0);
    }

    @Test
    public void whenThereAreFewerSegmentsThenTheCurrentSegment() {
        WaysToWellbeingGraphValues wellbeingGraph = new WaysToWellbeingGraphValues(10, Color.BLACK, 3, 4);

        // This is because it should default to 0
        assertThat(wellbeingGraph.getStartAngle()).isEqualTo(0);
        assertThat(wellbeingGraph.getArcLength()).isEqualTo(0);
    }

    @Test
    public void whenTheSegmentSelectedIsLessThan0() {
        WaysToWellbeingGraphValues wellbeingGraph = new WaysToWellbeingGraphValues(10, Color.BLACK, 10, -1);

        // This is because it should default to 0
        assertThat(wellbeingGraph.getStartAngle()).isEqualTo(0);
        assertThat(wellbeingGraph.getArcLength()).isEqualTo(0);
    }

    @Test
    public void whenThereIs1Segment() {
        WaysToWellbeingGraphValues wellbeingGraph = new WaysToWellbeingGraphValues(10, Color.BLACK, 1, 0);

        assertThat(wellbeingGraph.getStartAngle()).isEqualTo(90);
        assertThat(wellbeingGraph.getArcLength()).isEqualTo(360);
    }

    @Test
    public void whenThereAre5SegmentsAndThe1stIsChosen() {
        WaysToWellbeingGraphValues wellbeingGraph = new WaysToWellbeingGraphValues(10, Color.BLACK, 5, 0);

        assertThat(wellbeingGraph.getStartAngle()).isEqualTo(234);
        assertThat(wellbeingGraph.getArcLength()).isEqualTo(72);
    }

    @Test
    public void whenThereAre360Segments() {
        WaysToWellbeingGraphValues wellbeingGraph = new WaysToWellbeingGraphValues(10, Color.BLACK, 360, 1);

        assertThat(wellbeingGraph.getStartAngle()).isEqualTo(271);
        assertThat(wellbeingGraph.getArcLength()).isEqualTo(1);
    }

    @Test
    public void whenThereAre5SegmentsAndThe3rdIsChosen() {
        WaysToWellbeingGraphValues wellbeingGraph = new WaysToWellbeingGraphValues(10, Color.BLACK, 5, 2);

        assertThat(wellbeingGraph.getStartAngle()).isEqualTo(18);
        assertThat(wellbeingGraph.getArcLength()).isEqualTo(72);
    }

    @Test
    public void whenThereAre5SegmentsAndThe5thIsChosen() {
        WaysToWellbeingGraphValues wellbeingGraph = new WaysToWellbeingGraphValues(10, Color.BLACK, 5, 4);

        assertThat(wellbeingGraph.getStartAngle()).isEqualTo(162);
        assertThat(wellbeingGraph.getArcLength()).isEqualTo(72);
    }

}
