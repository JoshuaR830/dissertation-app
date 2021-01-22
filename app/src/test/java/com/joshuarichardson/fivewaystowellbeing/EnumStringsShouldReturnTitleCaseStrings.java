package com.joshuarichardson.fivewaystowellbeing;

import org.junit.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class EnumStringsShouldReturnTitleCaseStrings {
    @Test
    public void EnumStrings_ShouldReturnTitleCaseStrings() {
        List<String> enumList = DropDownHelper.getEnumStrings(ActivityType.values());

        assertThat(enumList.size()).isEqualTo(2);

        assertThat(enumList.get(0)).isEqualTo("App");
        assertThat(enumList.get(1)).isEqualTo("Sport");
    }
}
