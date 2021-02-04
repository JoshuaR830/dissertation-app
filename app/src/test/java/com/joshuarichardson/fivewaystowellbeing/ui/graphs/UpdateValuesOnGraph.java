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
public class UpdateValuesOnGraph {
    @Test
    public void whenThereIs1Segment() {
        WaysToWellbeingGraphValues wellbeingGraph = new WaysToWellbeingGraphValues(10, Color.BLACK, 1, 0);

        wellbeingGraph.updateSize(1, 10, 10);

        assertThat(wellbeingGraph.getShape().bottom).isEqualTo(20);
        assertThat(wellbeingGraph.getShape().top).isEqualTo(0);
        assertThat(wellbeingGraph.getShape().left).isEqualTo(0);
        assertThat(wellbeingGraph.getShape().right).isEqualTo(20);

        wellbeingGraph.updateValue(20, 1, 10, 10);

        assertThat(wellbeingGraph.getShape().bottom).isEqualTo(30);
        assertThat(wellbeingGraph.getShape().top).isEqualTo(-10);
        assertThat(wellbeingGraph.getShape().left).isEqualTo(-10);
        assertThat(wellbeingGraph.getShape().right).isEqualTo(30);
    }
}
