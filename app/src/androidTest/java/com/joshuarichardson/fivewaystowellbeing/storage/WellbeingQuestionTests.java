package com.joshuarichardson.fivewaystowellbeing.storage;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingQuestionDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingQuestion;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import static com.google.common.truth.Truth.assertThat;

public class WellbeingQuestionTests {
    private WellbeingDatabase wellbeingDb;
    private WellbeingQuestionDao wellbeingQuestionDao;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        this.wellbeingDb = Room.inMemoryDatabaseBuilder(context, WellbeingDatabase.class).build();
        this.wellbeingQuestionDao = wellbeingDb.wellbeingQuestionDao();

        this.wellbeingQuestionDao.insert(new WellbeingQuestion(234, "question 1", "positive 1", "negative 1", WaysToWellbeing.GIVE.toString(), 1, ActivityType.SPORT.toString(), SurveyItemTypes.CHECKBOX.toString()));
        this.wellbeingQuestionDao.insert(new WellbeingQuestion(345, "question 2", "positive 2", "negative 2", WaysToWellbeing.CONNECT.toString(), 2, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()));
        this.wellbeingQuestionDao.insert(new WellbeingQuestion(456, "question 3", "positive 3", "negative 3", WaysToWellbeing.BE_ACTIVE.toString(), 3, ActivityType.LEARNING.toString(), SurveyItemTypes.CHECKBOX.toString()));
        this.wellbeingQuestionDao.insert(new WellbeingQuestion(567, "question 4", "positive 4", "negative 4", WaysToWellbeing.BE_ACTIVE.toString(), 4, ActivityType.LEARNING.toString(), SurveyItemTypes.CHECKBOX.toString()));
        this.wellbeingQuestionDao.insert(new WellbeingQuestion(678, "question 5", "positive 5", "negative 5", WaysToWellbeing.BE_ACTIVE.toString(), 5, ActivityType.LEARNING.toString(), SurveyItemTypes.CHECKBOX.toString()));
    }

    @After
    public void teardown() {
        this.wellbeingDb.close();
    }

    @Test
    public void insertingWellbeingQuestions_ThenGettingWellbeingQuestionById_ShouldReturnTheCorrectWellbeingQuestion() {
        WellbeingQuestion question = this.wellbeingQuestionDao.getQuestionById(345);

        assertThat(question.getId())
            .isEqualTo(345);

        assertThat(question.getQuestion())
            .isEqualTo("question 2");

        assertThat(question.getPositiveMessage())
            .isEqualTo("positive 2");

        assertThat(question.getNegativeMessage())
            .isEqualTo("negative 2");

        assertThat(question.getWayToWellbeing())
            .isEqualTo("CONNECT");

        assertThat(question.getWeighting())
            .isEqualTo(2);

        assertThat(question.getActivityType())
            .isEqualTo("HOBBY");

        assertThat(question.getInputType())
            .isEqualTo("CHECKBOX");
    }

    @Test
    public void insertingWellbeingQuestions_ThenGettingWellbeingQuestionsByActivityType_ShouldReturnAllQuestionsForActivityType() {
        List<WellbeingQuestion> questions = this.wellbeingQuestionDao.getQuestionsByActivityType(ActivityType.LEARNING.toString());

        assertThat(questions.size())
            .isEqualTo(3);

        // Order of questions not guaranteed
        assertThat(questions.get(0).getId())
            .isAnyOf(456L, 567L, 678L);

        assertThat(questions.get(1).getId())
            .isAnyOf(456L, 567L, 678L);

        assertThat(questions.get(2).getId())
            .isAnyOf(456L, 567L, 678L);

        // Ensure that they are different to each other
        assertThat(questions.get(0).getId())
            .isNotEqualTo(questions.get(1).getId());
        assertThat(questions.get(1).getId())
            .isNotEqualTo(questions.get(2).getId());
        assertThat(questions.get(0).getId())
            .isNotEqualTo(questions.get(2).getId());
    }

    @Test
    public void insertingWellbeingQuestions_ThenGettingWellbeingResponseByNonExistentId_ShouldReturnNull() {
        WellbeingQuestion question = this.wellbeingQuestionDao.getQuestionById(123);
        assertThat(question)
            .isNull();
    }

    @Test
    public void onDelete_TheQuestionShouldBeDeleted() {
        this.wellbeingQuestionDao.delete(new WellbeingQuestion(567, "question 4", "positive 4", "negative 4", WaysToWellbeing.BE_ACTIVE.toString(), 4, ActivityType.LEARNING.toString(), SurveyItemTypes.CHECKBOX.toString()));
        WellbeingQuestion deletedItem = this.wellbeingQuestionDao.getQuestionById(567);

        assertThat(deletedItem).isNull();
    }
}
