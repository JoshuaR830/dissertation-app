package com.joshuarichardson.fivewaystowellbeing.surveys;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.storage.LimitedRawSurveyData;
import com.joshuarichardson.fivewaystowellbeing.storage.RawSurveyData;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class LimitedSurveyDataTests {
    private List<RawSurveyData> transformedData;

    @Before
    public void setup() {
        List<LimitedRawSurveyData> data = Arrays.asList(
            new LimitedRawSurveyData(new GregorianCalendar(1999, 2, 29, 15, 10).getTime().getTime(), "Survey note 1", "Activity name 1", ActivityType.SPORT.toString()),
            new LimitedRawSurveyData(new GregorianCalendar(1999, 2, 29, 15, 10).getTime().getTime(), "Survey note 1", "Activity name 2", ActivityType.SPORT.toString()),
            new LimitedRawSurveyData(new GregorianCalendar(1999, 2, 29, 15, 10).getTime().getTime(), "Survey note 1", "Activity name 3", ActivityType.SPORT.toString()),
            new LimitedRawSurveyData(new GregorianCalendar(1999, 2, 29, 15, 10).getTime().getTime(), "Survey note 1", "Activity name 4", ActivityType.SPORT.toString()),
            new LimitedRawSurveyData(new GregorianCalendar(1999, 2, 29, 15, 10).getTime().getTime(), "Survey note 1", "Activity name 5", ActivityType.SPORT.toString())
        );

        this.transformedData = LimitedRawSurveyData.convertToRawSurveyDataList(data);
    }

    @Test
    public void ShouldReturnInCorrectFormat() {
        SurveyDay day = SurveyDataHelper.transform(this.transformedData);

        // This checks that the correct title is returned
        assertThat(day.getTitle()).isEqualTo("29 Mar 1999");

        // check that the right number of keys are returned
        assertThat(day.getActivitySurveyKeys().size()).isEqualTo(5);

        assertThat(day.getActivityMap().size()).isEqualTo(5);
        ActivityInstance activityInstance1 = day.getActivityMap().get(-1L);
        assertThat(activityInstance1).isNotNull();
        assertThat(activityInstance1.getName()).isEqualTo("Activity name 1");

        List<Question> questionList1 = activityInstance1.getQuestions();
        assertThat(questionList1).isNotNull();
        assertThat(questionList1.size()).isEqualTo(0);

        ActivityInstance activityInstance2 = day.getActivityMap().get(-2L);
        assertThat(activityInstance2).isNotNull();
        assertThat(activityInstance2.getName()).isEqualTo("Activity name 2");

        List<Question> questionList2 = activityInstance1.getQuestions();
        assertThat(questionList2).isNotNull();
        assertThat(questionList2.size()).isEqualTo(0);

        ActivityInstance activityInstance3 = day.getActivityMap().get(-3L);
        assertThat(activityInstance3).isNotNull();
        assertThat(activityInstance3.getName()).isEqualTo("Activity name 3");

        List<Question> questionList3 = activityInstance1.getQuestions();
        assertThat(questionList3).isNotNull();
        assertThat(questionList3.size()).isEqualTo(0);

        ActivityInstance activityInstance4 = day.getActivityMap().get(-4L);
        assertThat(activityInstance4).isNotNull();
        assertThat(activityInstance4.getName()).isEqualTo("Activity name 4");

        List<Question> questionList4 = activityInstance1.getQuestions();
        assertThat(questionList4).isNotNull();
        assertThat(questionList4.size()).isEqualTo(0);

        ActivityInstance activityInstance5 = day.getActivityMap().get(-5L);
        assertThat(activityInstance5).isNotNull();
        assertThat(activityInstance5.getName()).isEqualTo("Activity name 5");

        List<Question> questionList5 = activityInstance1.getQuestions();
        assertThat(questionList5).isNotNull();
        assertThat(questionList5.size()).isEqualTo(0);
    }
}
