package com.joshuarichardson.fivewaystowellbeing.storage;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.storage.dao.PhysicalActivityDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.PhysicalActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import static com.google.common.truth.Truth.assertThat;

public class PhysicalActivityTests {
    private WellbeingDatabase wellbeingDb;
    private PhysicalActivityDao physicalActivityDao;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        this.wellbeingDb = Room.inMemoryDatabaseBuilder(context, WellbeingDatabase.class).build();
        this.physicalActivityDao = this.wellbeingDb.physicalActivityDao();

        this.physicalActivityDao.insert(new PhysicalActivity("CYCLE", 543789, 6543212, 595448, false, false));
    }

    @Test
    public void whenActivityIsInserted_ItShouldBeRetrievableByType() {
        this.physicalActivityDao.insert(new PhysicalActivity("RUN", 321543, 4374857, 3784283, false, false));
        PhysicalActivity originalPhysicalActivity = this.physicalActivityDao.getPhysicalActivityByType("RUN");
        assertThat(originalPhysicalActivity.getActivityType()).isEqualTo("RUN");
        assertThat(originalPhysicalActivity.getStartTime()).isEqualTo(321543);
        assertThat(originalPhysicalActivity.getEndTime()).isEqualTo(4374857);
        assertThat(originalPhysicalActivity.getActivityId()).isEqualTo(3784283);
        assertThat(originalPhysicalActivity.isPending()).isEqualTo(false);
        assertThat(originalPhysicalActivity.isNotificationConfirmed()).isEqualTo(false);
    }

    @Test
    public void whenGettingPhysicalActivityById_TheCorrectPhysicalActivityShouldBeReturned() {
        PhysicalActivity originalPhysicalActivity = this.physicalActivityDao.getPhysicalActivityByType("CYCLE");
        assertThat(originalPhysicalActivity.getActivityType()).isEqualTo("CYCLE");
        assertThat(originalPhysicalActivity.getStartTime()).isEqualTo(543789);
        assertThat(originalPhysicalActivity.getActivityId()).isEqualTo(595448);
        assertThat(originalPhysicalActivity.isPending()).isEqualTo(false);
        assertThat(originalPhysicalActivity.isNotificationConfirmed()).isEqualTo(false);
    }

    @Test
    public void whenStartTimeUpdated_ThePhysicalActivityShouldReturnTheNewTime() {
        PhysicalActivity originalPhysicalActivity = this.physicalActivityDao.getPhysicalActivityByType("CYCLE");
        assertThat(originalPhysicalActivity.getStartTime()).isEqualTo(543789);

        this.physicalActivityDao.updateStartTime("CYCLE", 12345);
        PhysicalActivity newPhysicalActivity = this.physicalActivityDao.getPhysicalActivityByType("CYCLE");
        assertThat(newPhysicalActivity.getStartTime()).isEqualTo(12345);
    }

    @Test
    public void whenEndTimeUpdated_ThePhysicalActivityShouldReturnTheNewTime() {
        PhysicalActivity originalPhysicalActivity = this.physicalActivityDao.getPhysicalActivityByType("CYCLE");
        assertThat(originalPhysicalActivity.getEndTime()).isEqualTo(6543212);

        this.physicalActivityDao.updateEndTime("CYCLE", 12345);
        PhysicalActivity newPhysicalActivity = this.physicalActivityDao.getPhysicalActivityByType("CYCLE");
        assertThat(newPhysicalActivity.getEndTime()).isEqualTo(12345);
    }

    @Test
    public void whenActivityUpdated_ThePhysicalActivityShouldReturnTheNewActivityId() {
        PhysicalActivity originalPhysicalActivity = this.physicalActivityDao.getPhysicalActivityByType("CYCLE");
        assertThat(originalPhysicalActivity.getActivityId()).isEqualTo(595448);

        this.physicalActivityDao.updateActivityId("CYCLE", 784568);
        PhysicalActivity newPhysicalActivity = this.physicalActivityDao.getPhysicalActivityByType("CYCLE");
        assertThat(newPhysicalActivity.getActivityId()).isEqualTo(784568);
    }

    @Test
    public void whenIsPendingUpdated_ThePhysicalActivityShouldReturnDifferentPendingStatus() {
        PhysicalActivity originalPhysicalActivity = this.physicalActivityDao.getPhysicalActivityByType("CYCLE");
        assertThat(originalPhysicalActivity.isPending()).isEqualTo(false);

        this.physicalActivityDao.updateIsPendingStatus("CYCLE", true);
        PhysicalActivity newPhysicalActivity = this.physicalActivityDao.getPhysicalActivityByType("CYCLE");
        assertThat(newPhysicalActivity.isPending()).isEqualTo(true);
    }

    @Test
    public void whenIsNotificationConfirmedUpdated_ThePhysicalActivityShouldReturnDifferentConfirmedStatus() {
        PhysicalActivity originalPhysicalActivity = this.physicalActivityDao.getPhysicalActivityByType("CYCLE");
        assertThat(originalPhysicalActivity.isNotificationConfirmed()).isEqualTo(false);

        this.physicalActivityDao.updateIsNotificationConfirmedStatus("CYCLE", true);
        PhysicalActivity newPhysicalActivity = this.physicalActivityDao.getPhysicalActivityByType("CYCLE");
        assertThat(newPhysicalActivity.isNotificationConfirmed()).isEqualTo(true);
    }

    @Test
    public void whenGetPending_CorrectNumberOfItemsShouldBeReturned() {
        // This is a duplicate so only count as 1
        this.physicalActivityDao.insert(new PhysicalActivity("RUN", 321543, 4374857, 3784283, true, false));
        this.physicalActivityDao.insert(new PhysicalActivity("RUN", 321543, 4374857, 3784283, true, false));

        this.physicalActivityDao.insert(new PhysicalActivity("WALK", 321543, 4374857, 3784283, true, false));

        List<PhysicalActivity> activitiesList = this.physicalActivityDao.getPending();
        assertThat(activitiesList.size()).isEqualTo(2);
    }

    @Test
    public void whenDifferenceIsLessThan10Minutes_ShouldReturnFalse() {
        PhysicalActivity originalPhysicalActivity = this.physicalActivityDao.getPhysicalActivityByType("CYCLE");
        assertThat(originalPhysicalActivity.getTimeStatus(1143788)).isEqualTo(false);
    }

    @Test
    public void whenDifferenceIsExactly10Minutes_ShouldReturnTrue() {
        PhysicalActivity originalPhysicalActivity = this.physicalActivityDao.getPhysicalActivityByType("CYCLE");
        assertThat(originalPhysicalActivity.getTimeStatus(1143789)).isEqualTo(true);
    }

    @Test
    public void whenDifferenceIsMOreThan10Minutes_ShouldReturnTrue() {
        PhysicalActivity originalPhysicalActivity = this.physicalActivityDao.getPhysicalActivityByType("CYCLE");
        assertThat(originalPhysicalActivity.getTimeStatus(1143790)).isEqualTo(true);
    }
}