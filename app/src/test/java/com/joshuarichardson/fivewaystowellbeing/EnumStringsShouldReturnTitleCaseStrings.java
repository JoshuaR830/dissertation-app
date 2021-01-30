package com.joshuarichardson.fivewaystowellbeing;

import org.junit.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class EnumStringsShouldReturnTitleCaseStrings {
    @Test
    public void EnumStrings_ShouldReturnTitleCaseStrings() {
        List<String> enumList = DropDownHelper.getEnumStrings(ActivityType.values());

        assertThat(enumList.size()).isEqualTo(6);

        assertThat(enumList.get(0)).isEqualTo("App");
        assertThat(enumList.get(1)).isEqualTo("Sport");
        assertThat(enumList.get(2)).isEqualTo("Hobby");
        assertThat(enumList.get(3)).isEqualTo("Pet");
        assertThat(enumList.get(4)).isEqualTo("Work");
        assertThat(enumList.get(5)).isEqualTo("Learning");
    }
}
