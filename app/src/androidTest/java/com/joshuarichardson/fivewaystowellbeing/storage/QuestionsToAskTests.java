package com.joshuarichardson.fivewaystowellbeing.storage;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.storage.dao.QuestionsToAskDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.QuestionsToAsk;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes;
import com.joshuarichardson.fivewaystowellbeing.utilities.LiveDataTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.TimeoutException;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class QuestionsToAskTests {
    private WellbeingDatabase wellbeingDb;
    private QuestionsToAskDao questionsToAskDao;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        // https://developer.android.com/training/data-storage/room/testing-db
        Context context = ApplicationProvider.getApplicationContext();
        this.wellbeingDb = Room.inMemoryDatabaseBuilder(context, WellbeingDatabase.class).build();
        this.questionsToAskDao = this.wellbeingDb.questionsToAskDao();
    }

    @After
    public void teardown() {
        this.wellbeingDb.close();
    }

    @Test
    public void insertQuestions_ThenGetById_ShouldReturnTheCorrectQuestion() throws TimeoutException, InterruptedException {
        QuestionsToAsk question1 = new QuestionsToAsk("How was Facebook today?", "You spent longer using it than usual", 1, SurveyItemTypes.DROP_DOWN_LIST.name(), 0, null);
        QuestionsToAsk question2 = new QuestionsToAsk("How was Twitter today?", "You spent less time than usual using it", 1, SurveyItemTypes.TEXT.name(), 1, null);
        this.questionsToAskDao.insert(question2);
        long questionId = this.questionsToAskDao.insert(question1);

        List<QuestionsToAsk> retrievedQuestion = LiveDataTestUtil.getOrAwaitValue(this.questionsToAskDao.getQuestionsById(questionId));

        assertThat(retrievedQuestion.size()).isGreaterThan(0);

        QuestionsToAsk actualQuestions = retrievedQuestion.get(0);

        assertThat(actualQuestions.getId()).isEqualTo(questionId);
        assertThat(actualQuestions.getQuestion()).isEqualTo("How was Facebook today?");
        assertThat(actualQuestions.getReason()).isEqualTo("You spent longer using it than usual");
        assertThat(actualQuestions.getSequenceNumber()).isEqualTo(0);
        assertThat(actualQuestions.getSetId()).isEqualTo(1);
        assertThat(actualQuestions.getType()).isEqualTo("DROP_DOWN_LIST");
    }

    @Test
    public void insertQuestions_thenGetAllQuestionsForSpecificSet_ShouldReturnCorrectNumberOfQuestions() throws TimeoutException, InterruptedException {
        // ToDo - need to add list items insteasd of null
        QuestionsToAsk question1 = new QuestionsToAsk("How was Google Chrome today?", "reason 1", 2, SurveyItemTypes.TEXT.name(), 0, null);
        QuestionsToAsk question2 = new QuestionsToAsk("How was Reddit today?", "reason 2", 1, SurveyItemTypes.DROP_DOWN_LIST.name(), 0, null);
        QuestionsToAsk question3 = new QuestionsToAsk("How was Instagram today?", "reason 3", 1, SurveyItemTypes.DROP_DOWN_LIST.name(), 1, null);
        QuestionsToAsk question4 = new QuestionsToAsk("How was YouTube today?", "reason 4", 2, SurveyItemTypes.TEXT.name(), 1, null);

        this.questionsToAskDao.insert(question1);
        this.questionsToAskDao.insert(question2);
        this.questionsToAskDao.insert(question3);
        this.questionsToAskDao.insert(question4);

        List<QuestionsToAsk> retrievedQuestion = LiveDataTestUtil.getOrAwaitValue(this.questionsToAskDao.getQuestionsBySetId(1));

        assertThat(retrievedQuestion.size()).isEqualTo(2);
        assertThat(retrievedQuestion.get(0).getSetId()).isEqualTo(1);
        assertThat(retrievedQuestion.get(1).getSetId()).isEqualTo(1);
    }

    @Test
    public void insertQuestions_thenGetAllQuestionsForNonExistentSet_ShouldReturnNoQuestions() throws TimeoutException, InterruptedException {
        QuestionsToAsk question1 = new QuestionsToAsk("How was Google Chrome today?", "reason 1", 2, SurveyItemTypes.TEXT.name(), 0, null);
        QuestionsToAsk question2 = new QuestionsToAsk("How was Reddit today?", "reason 2", 1, SurveyItemTypes.DROP_DOWN_LIST.name(), 0, null);
        QuestionsToAsk question3 = new QuestionsToAsk("How was Instagram today?", "reason 3", 1, SurveyItemTypes.DROP_DOWN_LIST.name(), 1, null);
        QuestionsToAsk question4 = new QuestionsToAsk("How was YouTube today?", "reason 4", 2, SurveyItemTypes.TEXT.name(), 1, null);

        this.questionsToAskDao.insert(question1);
        this.questionsToAskDao.insert(question2);
        this.questionsToAskDao.insert(question3);
        this.questionsToAskDao.insert(question4);

        List<QuestionsToAsk> retrievedQuestion = LiveDataTestUtil.getOrAwaitValue(this.questionsToAskDao.getQuestionsBySetId(3));

        assertThat(retrievedQuestion).isNotNull();
        assertThat(retrievedQuestion).isEmpty();
    }
}
