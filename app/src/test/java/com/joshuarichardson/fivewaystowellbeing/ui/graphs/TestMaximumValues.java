package com.joshuarichardson.fivewaystowellbeing.ui.graphs;

import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphValueHelper;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class TestMaximumValues {
    @Test
    public void whenValueWouldBeGreaterThan100_ValueShouldBeCappedAt100() {
        WellbeingGraphValueHelper helper = new WellbeingGraphValueHelper(110, 110, 110, 110, 110);
        assertThat(helper.getConnectValue()).isEqualTo(100);
        assertThat(helper.getBeActiveValue()).isEqualTo(100);
        assertThat(helper.getKeepLearningValue()).isEqualTo(100);
        assertThat(helper.getTakeNoticeValue()).isEqualTo(100);
        assertThat(helper.getGiveValue()).isEqualTo(100);
    }

    @Test
    public void whenValueWouldBe100_ValueShouldBe100() {
        WellbeingGraphValueHelper helper = new WellbeingGraphValueHelper(100, 100, 100, 100, 100);
        assertThat(helper.getConnectValue()).isEqualTo(100);
        assertThat(helper.getBeActiveValue()).isEqualTo(100);
        assertThat(helper.getKeepLearningValue()).isEqualTo(100);
        assertThat(helper.getTakeNoticeValue()).isEqualTo(100);
        assertThat(helper.getGiveValue()).isEqualTo(100);
    }

    @Test
    public void whenValueWouldBeGreaterUnder100_ValueShouldBeLessThan100() {
        WellbeingGraphValueHelper helper = new WellbeingGraphValueHelper(99, 99, 99, 99, 99);
        assertThat(helper.getConnectValue()).isEqualTo(99);
        assertThat(helper.getBeActiveValue()).isEqualTo(99);
        assertThat(helper.getKeepLearningValue()).isEqualTo(99);
        assertThat(helper.getTakeNoticeValue()).isEqualTo(99);
        assertThat(helper.getGiveValue()).isEqualTo(99);
    }
}
