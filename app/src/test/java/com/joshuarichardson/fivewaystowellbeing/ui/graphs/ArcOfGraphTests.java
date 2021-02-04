package com.joshuarichardson.fivewaystowellbeing.ui.graphs;

import android.graphics.Color;

import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class ArcOfGraphTests {

    @Before
    public void setup() {
    }

    @Test
    public void whenThereAre0SegmentsAndThisIsTheFirstSegment() {
        WaysToWellbeingGraph wellbeingGraph = new WaysToWellbeingGraph(10, Color.BLACK, 0, 0);

        // This is because it should default to 0
        assertThat(wellbeingGraph.getStartAngle()).isEqualTo(0);
        assertThat(wellbeingGraph.getArcLength()).isEqualTo(0);
    }

    @Test
    public void whenThereAreFewerSegmentsThenTheCurrentSegment() {
        WaysToWellbeingGraph wellbeingGraph = new WaysToWellbeingGraph(10, Color.BLACK, 3, 4);

        // This is because it should default to 0
        assertThat(wellbeingGraph.getStartAngle()).isEqualTo(0);
        assertThat(wellbeingGraph.getArcLength()).isEqualTo(0);
    }

    @Test
    public void whenTheSegmentSelectedIsLessThan0() {
        WaysToWellbeingGraph wellbeingGraph = new WaysToWellbeingGraph(10, Color.BLACK, 10, -1);

        // This is because it should default to 0
        assertThat(wellbeingGraph.getStartAngle()).isEqualTo(0);
        assertThat(wellbeingGraph.getArcLength()).isEqualTo(0);
    }

    @Test
    public void whenThereIs1Segment() {
        WaysToWellbeingGraph wellbeingGraph = new WaysToWellbeingGraph(10, Color.BLACK, 1, 0);

        assertThat(wellbeingGraph.getStartAngle()).isEqualTo(90);
        assertThat(wellbeingGraph.getArcLength()).isEqualTo(360);
    }

    @Test
    public void whenThereAre5SegmentsAndThe1stIsChosen() {
        WaysToWellbeingGraph wellbeingGraph = new WaysToWellbeingGraph(10, Color.BLACK, 5, 0);

        assertThat(wellbeingGraph.getStartAngle()).isEqualTo(234);
        assertThat(wellbeingGraph.getArcLength()).isEqualTo(72);
    }

    @Test
    public void whenThereAre360Segments() {
        WaysToWellbeingGraph wellbeingGraph = new WaysToWellbeingGraph(10, Color.BLACK, 5, 0);

        assertThat(wellbeingGraph.getStartAngle()).isEqualTo(265.5);
        assertThat(wellbeingGraph.getArcLength()).isEqualTo(1);
    }

    @Test
    public void whenThereAre5SegmentsAndThe3rdIsChosen() {
        WaysToWellbeingGraph wellbeingGraph = new WaysToWellbeingGraph(10, Color.BLACK, 5, 2);

        assertThat(wellbeingGraph.getStartAngle()).isEqualTo(18);
        assertThat(wellbeingGraph.getArcLength()).isEqualTo(72);
    }

    @Test
    public void whenThereAre5SegmentsAndThe5thIsChosen() {
        WaysToWellbeingGraph wellbeingGraph = new WaysToWellbeingGraph(10, Color.BLACK, 5, 4);

        assertThat(wellbeingGraph.getStartAngle()).isEqualTo(162);
        assertThat(wellbeingGraph.getArcLength()).isEqualTo(72);
    }

}
