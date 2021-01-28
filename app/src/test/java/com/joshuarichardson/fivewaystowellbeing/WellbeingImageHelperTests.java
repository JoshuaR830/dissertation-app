package com.joshuarichardson.fivewaystowellbeing;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class WellbeingImageHelperTests {

    @Test
    public void WhenWayToWellbeingIsConnect_ThenConnectImageShouldBeReturned() {
        int imageId = WellbeingHelper.getImage(WaysToWellbeing.CONNECT);
        assertThat(imageId).isEqualTo(R.drawable.icon_way_to_wellbeing_connect);
    }

    @Test
    public void WhenWayToWellbeingIsBEActive_ThenBEActiveImageShouldBeReturned() {
        int imageId = WellbeingHelper.getImage(WaysToWellbeing.BE_ACTIVE);
        assertThat(imageId).isEqualTo(R.drawable.icon_way_to_wellbeing_be_active);
    }

    @Test
    public void WhenWayToWellbeingIsKeepLearning_ThenKeepLearningImageShouldBeReturned() {
        int imageId = WellbeingHelper.getImage(WaysToWellbeing.KEEP_LEARNING);
        assertThat(imageId).isEqualTo(R.drawable.icon_way_to_wellbeing_keep_learning);
    }

    @Test
    public void WhenWayToWellbeingIsTakeNotice_ThenTakeNoticeImageShouldBeReturned() {
        int imageId = WellbeingHelper.getImage(WaysToWellbeing.TAKE_NOTICE);
        assertThat(imageId).isEqualTo(R.drawable.icon_way_to_wellbeing_take_notice);
    }

    @Test
    public void WhenWayToWellbeingIsGive_ThenGiveImageShouldBeReturned() {
        int imageId = WellbeingHelper.getImage(WaysToWellbeing.GIVE);
        assertThat(imageId).isEqualTo(R.drawable.icon_way_to_wellbeing_give);
    }
}
