package com.joshuarichardson.fivewaystowellbeing.ui.graphs;

import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphValueHelper;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class SetActivityValuesForWayToWellbeingValuesTests {
    @Test
    public void whenActivityValueIsUpdated_ShouldNotAffectTheQuestionValues() {
        WellbeingGraphValueHelper helper = new WellbeingGraphValueHelper(10, 20, 30, 70, 50);
        helper.updateActivityValuesForWayToWellbeing(WaysToWellbeing.GIVE);
        assertThat(helper.getGiveValue()).isEqualTo(100);
        helper.resetActivityValues();
        assertThat(helper.getGiveValue()).isEqualTo(50);
    }

    @Test
    public void whenValueWouldBeGreaterThan100_ValueShouldBeCappedAt100() {
        WellbeingGraphValueHelper helper = new WellbeingGraphValueHelper(110, 20, 30, 70, 50);
        helper.updateActivityValuesForWayToWellbeing(WaysToWellbeing.TAKE_NOTICE);
        assertThat(helper.getTakeNoticeValue()).isEqualTo(100);
        assertThat(helper.getConnectValue()).isEqualTo(100);
    }
}
