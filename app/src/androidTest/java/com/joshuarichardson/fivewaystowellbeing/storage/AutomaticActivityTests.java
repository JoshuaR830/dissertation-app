package com.joshuarichardson.fivewaystowellbeing.storage;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.storage.dao.AutomaticActivityDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.AutomaticActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import static com.google.common.truth.Truth.assertThat;

public class AutomaticActivityTests {
    private WellbeingDatabase wellbeingDb;
    private AutomaticActivityDao automaticActivityDao;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        this.wellbeingDb = Room.inMemoryDatabaseBuilder(context, WellbeingDatabase.class).build();
        this.automaticActivityDao = this.wellbeingDb.physicalActivityDao();

        this.automaticActivityDao.insert(new AutomaticActivity("CYCLE", null, 543789, 6543212, 595448, false, false));
    }

    @Test
    public void whenActivityIsInserted_ItShouldBeRetrievableByType() {
        this.automaticActivityDao.insert(new AutomaticActivity("RUN", null, 321543, 4374857, 3784283, false, false));
        AutomaticActivity originalAutomaticActivity = this.automaticActivityDao.getPhysicalActivityByTypeWithAssociatedActivity("RUN");
        assertThat(originalAutomaticActivity.getActivityType()).isEqualTo("RUN");
        assertThat(originalAutomaticActivity.getStartTime()).isEqualTo(321543);
        assertThat(originalAutomaticActivity.getEndTime()).isEqualTo(4374857);
        assertThat(originalAutomaticActivity.getActivityId()).isEqualTo(3784283);
        assertThat(originalAutomaticActivity.isPending()).isEqualTo(false);
        assertThat(originalAutomaticActivity.isNotificationConfirmed()).isEqualTo(false);
    }

    @Test
    public void whenGettingPhysicalActivityById_TheCorrectPhysicalActivityShouldBeReturned() {
        AutomaticActivity originalAutomaticActivity = this.automaticActivityDao.getPhysicalActivityByTypeWithAssociatedActivity("CYCLE");
        assertThat(originalAutomaticActivity.getActivityType()).isEqualTo("CYCLE");
        assertThat(originalAutomaticActivity.getStartTime()).isEqualTo(543789);
        assertThat(originalAutomaticActivity.getActivityId()).isEqualTo(595448);
        assertThat(originalAutomaticActivity.isPending()).isEqualTo(false);
        assertThat(originalAutomaticActivity.isNotificationConfirmed()).isEqualTo(false);
    }

    @Test
    public void whenStartTimeUpdated_ThePhysicalActivityShouldReturnTheNewTime() {
        AutomaticActivity originalAutomaticActivity = this.automaticActivityDao.getPhysicalActivityByTypeWithAssociatedActivity("CYCLE");
        assertThat(originalAutomaticActivity.getStartTime()).isEqualTo(543789);

        this.automaticActivityDao.updateStartTime("CYCLE", 12345);
        AutomaticActivity newAutomaticActivity = this.automaticActivityDao.getPhysicalActivityByTypeWithAssociatedActivity("CYCLE");
        assertThat(newAutomaticActivity.getStartTime()).isEqualTo(12345);
    }

    @Test
    public void whenEndTimeUpdated_ThePhysicalActivityShouldReturnTheNewTime() {
        AutomaticActivity originalAutomaticActivity = this.automaticActivityDao.getPhysicalActivityByTypeWithAssociatedActivity("CYCLE");
        assertThat(originalAutomaticActivity.getEndTime()).isEqualTo(6543212);

        this.automaticActivityDao.updateEndTime("CYCLE", 12345);
        AutomaticActivity newAutomaticActivity = this.automaticActivityDao.getPhysicalActivityByTypeWithAssociatedActivity("CYCLE");
        assertThat(newAutomaticActivity.getEndTime()).isEqualTo(12345);
    }

    @Test
    public void whenActivityUpdated_ThePhysicalActivityShouldReturnTheNewActivityId() {
        AutomaticActivity originalAutomaticActivity = this.automaticActivityDao.getPhysicalActivityByTypeWithAssociatedActivity("CYCLE");
        assertThat(originalAutomaticActivity.getActivityId()).isEqualTo(595448);

        this.automaticActivityDao.updateActivityId("CYCLE", 784568);
        AutomaticActivity newAutomaticActivity = this.automaticActivityDao.getPhysicalActivityByTypeWithAssociatedActivity("CYCLE");
        assertThat(newAutomaticActivity.getActivityId()).isEqualTo(784568);
    }

    @Test
    public void whenIsPendingUpdated_ThePhysicalActivityShouldReturnDifferentPendingStatus() {
        AutomaticActivity originalAutomaticActivity = this.automaticActivityDao.getPhysicalActivityByTypeWithAssociatedActivity("CYCLE");
        assertThat(originalAutomaticActivity.isPending()).isEqualTo(false);

        this.automaticActivityDao.updateIsPendingStatus("CYCLE", true);
        AutomaticActivity newAutomaticActivity = this.automaticActivityDao.getPhysicalActivityByTypeWithAssociatedActivity("CYCLE");
        assertThat(newAutomaticActivity.isPending()).isEqualTo(true);
    }

    @Test
    public void whenIsNotificationConfirmedUpdated_ThePhysicalActivityShouldReturnDifferentConfirmedStatus() {
        AutomaticActivity originalAutomaticActivity = this.automaticActivityDao.getPhysicalActivityByTypeWithAssociatedActivity("CYCLE");
        assertThat(originalAutomaticActivity.isNotificationConfirmed()).isEqualTo(false);

        this.automaticActivityDao.updateIsNotificationConfirmedStatus("CYCLE", true);
        AutomaticActivity newAutomaticActivity = this.automaticActivityDao.getPhysicalActivityByTypeWithAssociatedActivity("CYCLE");
        assertThat(newAutomaticActivity.isNotificationConfirmed()).isEqualTo(true);
    }

    @Test
    public void whenGetPending_CorrectNumberOfItemsShouldBeReturned() {
        // This is a duplicate so only count as 1
        this.automaticActivityDao.insert(new AutomaticActivity("RUN", null, 321543, 4374857, 3784283, true, false));
        this.automaticActivityDao.insert(new AutomaticActivity("RUN", null, 321543, 4374857, 3784283, true, false));

        this.automaticActivityDao.insert(new AutomaticActivity("WALK", null, 321543, 4374857, 3784283, true, false));

        List<AutomaticActivity> activitiesList = this.automaticActivityDao.getPending();
        assertThat(activitiesList.size()).isEqualTo(2);
    }

    @Test
    public void whenDifferenceIsLessThan10Minutes_ShouldReturnFalse() {
        AutomaticActivity originalAutomaticActivity = this.automaticActivityDao.getPhysicalActivityByTypeWithAssociatedActivity("CYCLE");
        assertThat(originalAutomaticActivity.getTimeStatus(1143788)).isEqualTo(false);
    }

    @Test
    public void whenDifferenceIsExactly10Minutes_ShouldReturnTrue() {
        AutomaticActivity originalAutomaticActivity = this.automaticActivityDao.getPhysicalActivityByTypeWithAssociatedActivity("CYCLE");
        assertThat(originalAutomaticActivity.getTimeStatus(1143789)).isEqualTo(true);
    }

    @Test
    public void whenDifferenceIsMoreThan10Minutes_ShouldReturnTrue() {
        AutomaticActivity originalAutomaticActivity = this.automaticActivityDao.getPhysicalActivityByTypeWithAssociatedActivity("CYCLE");
        assertThat(originalAutomaticActivity.getTimeStatus(1143790)).isEqualTo(true);
    }

    @Test
    public void whenRequestingAllAutomatedActivitiesWithInAppActivities_ShouldReturnNonNullNames() {
        this.automaticActivityDao.insert(new AutomaticActivity("com.even_less_fun", "C", 321543, 4374857, 453787, true, false));
        this.automaticActivityDao.insert(new AutomaticActivity("com.less_fun", "A", 321543, 4374857, 0, true, false));
        this.automaticActivityDao.insert(new AutomaticActivity("com.fun", "B", 321543, 4374857, 3784283, true, false));

        List<AutomaticActivity> activities = this.automaticActivityDao.getAllPhysicalActivitiesWithNames();
        assertThat(activities.size()).isEqualTo(3);
        assertThat(activities.get(0).getName()).isEqualTo("A");
        assertThat(activities.get(1).getName()).isEqualTo("B");
        assertThat(activities.get(2).getName()).isEqualTo("C");
    }
}