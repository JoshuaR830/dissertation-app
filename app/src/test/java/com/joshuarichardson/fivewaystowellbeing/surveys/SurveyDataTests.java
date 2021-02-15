package com.joshuarichardson.fivewaystowellbeing.surveys;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.storage.RawSurveyData;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class SurveyDataTests {
    private List<RawSurveyData> data;

    @Before
    public void setup() {
        this.data = Arrays.asList(
            new RawSurveyData(new GregorianCalendar(1999, 2, 29, 15, 10).getTime().getTime(), "Survey note 1", "Activity note 1", "Activity name 1", 1, "Question 1_1", 1, false, ActivityType.HOBBY.toString(), WaysToWellbeing.KEEP_LEARNING.toString()),
            new RawSurveyData(new GregorianCalendar(1999, 2, 29, 15, 10).getTime().getTime(), "Survey note 1", "Activity note 1", "Activity name 1", 1, "Question 1_2", 2, true, ActivityType.HOBBY.toString(), WaysToWellbeing.KEEP_LEARNING.toString()),
            new RawSurveyData(new GregorianCalendar(1999, 2, 29, 15, 10).getTime().getTime(), "Survey note 1", "Activity note 1", "Activity name 1", 1, "Question 1_3", 3, false, ActivityType.HOBBY.toString(), WaysToWellbeing.KEEP_LEARNING.toString()),
            new RawSurveyData(new GregorianCalendar(1999, 2, 29, 15, 10).getTime().getTime(), "Survey note 1", "Activity note 2", "Activity name 2", 2, "Question 2_1", 4, true, ActivityType.HOBBY.toString(), WaysToWellbeing.KEEP_LEARNING.toString()),
            new RawSurveyData(new GregorianCalendar(1999, 2, 29, 15, 10).getTime().getTime(), "Survey note 1", "Activity note 2", "Activity name 2", 2, "Question 2_2", 5, false, ActivityType.HOBBY.toString(), WaysToWellbeing.KEEP_LEARNING.toString())
        );
    }

    @Test
    public void ShouldReturnInCorrectFormat() {
        SurveyDay day = SurveyDataHelper.transform(this.data);

        // This checks that the correct title is returned
        assertThat(day.getTitle()).isEqualTo("29 Mar 1999");

        // check that the right number of keys are returned
        assertThat(day.getPasstimeSurveyKeys().size()).isEqualTo(2);

        assertThat(day.getPasstimeMap().size()).isEqualTo(2);
        Passtime passtime1 = day.getPasstimeMap().get(1L);

        assertThat(passtime1).isNotNull();

        assertThat(passtime1.getName()).isEqualTo("Activity name 1");
        assertThat(passtime1.getNote()).isEqualTo("Activity note 1");

        List<Question> questionList1 = passtime1.getQuestions();

        assertThat(questionList1).isNotNull();
        assertThat(questionList1.size()).isEqualTo(3);

        Question question1_1 = questionList1.get(0);
        assertThat(question1_1).isNotNull();
        assertThat(question1_1.getQuestion()).isEqualTo("Question 1_1");
        assertThat(question1_1.getWellbeingRecordId()).isEqualTo(1);
        assertThat(question1_1.getUserResponse()).isEqualTo(false);

        Question question1_2 = questionList1.get(1);
        assertThat(question1_2).isNotNull();
        assertThat(question1_2.getQuestion()).isEqualTo("Question 1_2");
        assertThat(question1_2.getWellbeingRecordId()).isEqualTo(2);
        assertThat(question1_2.getUserResponse()).isEqualTo(true);

        Question question1_3 = questionList1.get(2);
        assertThat(question1_3).isNotNull();
        assertThat(question1_3.getQuestion()).isEqualTo("Question 1_3");
        assertThat(question1_3.getWellbeingRecordId()).isEqualTo(3);
        assertThat(question1_3.getUserResponse()).isEqualTo(false);


        Passtime passtime2 = day.getPasstimeMap().get(2L);

        assertThat(passtime2).isNotNull();

        assertThat(passtime2.getName()).isEqualTo("Activity name 2");
        assertThat(passtime2.getNote()).isEqualTo("Activity note 2");

        List<Question> questionList2 = passtime2.getQuestions();
        assertThat(questionList2).isNotNull();
        assertThat(questionList2.size()).isEqualTo(2);
        Question question2_1 = questionList2.get(0);
        assertThat(question2_1.getQuestion()).isEqualTo("Question 2_1");
        assertThat(question2_1.getWellbeingRecordId()).isEqualTo(4);
        assertThat(question2_1.getUserResponse()).isEqualTo(true);

        Question question2_2 = questionList2.get(1);
        assertThat(question2_2.getQuestion()).isEqualTo("Question 2_2");
        assertThat(question2_2.getWellbeingRecordId()).isEqualTo(5);
        assertThat(question2_2.getUserResponse()).isEqualTo(false);
    }
}
