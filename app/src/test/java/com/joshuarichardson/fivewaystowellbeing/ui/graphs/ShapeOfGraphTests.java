package com.joshuarichardson.fivewaystowellbeing.ui.graphs;

import android.graphics.Color;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class ShapeOfGraphTests {
    @Test
    public void updateSizeTestsWhenMultiplierIs1() {
        WaysToWellbeingGraph wellbeingGraph = new WaysToWellbeingGraph(10, Color.BLACK, 0, 0);
        wellbeingGraph.updateSize(1, 10, 10);

        assertThat(wellbeingGraph.getShape().bottom).isEqualTo(20);
        assertThat(wellbeingGraph.getShape().top).isEqualTo(0);
        assertThat(wellbeingGraph.getShape().left).isEqualTo(0);
        assertThat(wellbeingGraph.getShape().right).isEqualTo(20);

    }

    @Test
    public void updateSizeTestsWhenMultiplierIs2() {
        WaysToWellbeingGraph wellbeingGraph = new WaysToWellbeingGraph(10, Color.BLACK, 0, 0);
        wellbeingGraph.updateSize(2, 10, 10);

        assertThat(wellbeingGraph.getShape().bottom).isEqualTo(30);
        assertThat(wellbeingGraph.getShape().top).isEqualTo(-10);
        assertThat(wellbeingGraph.getShape().left).isEqualTo(-10);
        assertThat(wellbeingGraph.getShape().right).isEqualTo(30);
    }

    @Test
    public void updateSizeTestsWhenSizeIs0() {
        WaysToWellbeingGraph wellbeingGraph = new WaysToWellbeingGraph(10, Color.BLACK, 0, 0);
        wellbeingGraph.updateSize(2, 0, 0);

        assertThat(wellbeingGraph.getShape().bottom).isEqualTo(20);
        assertThat(wellbeingGraph.getShape().top).isEqualTo(-20);
        assertThat(wellbeingGraph.getShape().left).isEqualTo(-20);
        assertThat(wellbeingGraph.getShape().right).isEqualTo(20);
    }
}
