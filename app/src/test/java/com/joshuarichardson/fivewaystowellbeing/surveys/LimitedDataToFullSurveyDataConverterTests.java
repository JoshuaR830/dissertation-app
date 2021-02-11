package com.joshuarichardson.fivewaystowellbeing.surveys;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.storage.LimitedRawSurveyData;
import com.joshuarichardson.fivewaystowellbeing.storage.RawSurveyData;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class LimitedDataToFullSurveyDataConverterTests {

    @Test
    public void whenMultipleItemsProvided_ShouldReturnListOfMultipleItems() {
        List<RawSurveyData> data = LimitedRawSurveyData.convertToRawSurveyDataList(Arrays.asList(
            new LimitedRawSurveyData(12345, "Note 1", "Activity 1", ActivityType.HOBBY.toString()),
            new LimitedRawSurveyData(12346, "Note 2", "Activity 2", ActivityType.HOBBY.toString()),
            new LimitedRawSurveyData(12347, "Note 3", "Activity 3", ActivityType.HOBBY.toString())
        ));

        assertThat(data).isNotNull();
        assertThat(data.size()).isEqualTo(3);
    }

    @Test
    public void whenOnly1ItemProvided_ShouldReturnListOf1() {
        List<RawSurveyData> data = LimitedRawSurveyData.convertToRawSurveyDataList(Collections.singletonList(new LimitedRawSurveyData(12345, "Note 1", "Activity 1", ActivityType.HOBBY.toString())));

        assertThat(data).isNotNull();
        assertThat(data.size()).isEqualTo(1);
    }

    @Test
    public void whenNoItemsProvided_ShouldReturnEmptyList() {
        List<RawSurveyData> data = LimitedRawSurveyData.convertToRawSurveyDataList(new ArrayList<>());

        assertThat(data).isNotNull();
        assertThat(data.size()).isEqualTo(0);
    }
}
