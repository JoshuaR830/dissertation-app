package com.joshuarichardson.fivewaystowellbeing.storage;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.storage.dao.PhysicalActivityDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.PhysicalActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

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

        this.physicalActivityDao.insert(new PhysicalActivity("CYCLE", 543789, 595448));
    }

    @Test
    public void whenActivityIsInserted_ItShouldBeRetrievableByType() {
        this.physicalActivityDao.insert(new PhysicalActivity("RUN", 321543, 3784283));
        PhysicalActivity originalPhysicalActivity = this.physicalActivityDao.getPhysicalActivityByType("RUN");
        assertThat(originalPhysicalActivity.getActivityType()).isEqualTo("RUN");
        assertThat(originalPhysicalActivity.getStartTime()).isEqualTo(321543);
        assertThat(originalPhysicalActivity.getActivityId()).isEqualTo(3784283);
    }

    @Test
    public void whenGettingPhysicalActivityById_TheCorrectPhysicalActivityShouldBeReturned() {
        PhysicalActivity originalPhysicalActivity = this.physicalActivityDao.getPhysicalActivityByType("CYCLE");
        assertThat(originalPhysicalActivity.getActivityType()).isEqualTo("CYCLE");
        assertThat(originalPhysicalActivity.getStartTime()).isEqualTo(543789);
        assertThat(originalPhysicalActivity.getActivityId()).isEqualTo(595448);
    }

    @Test
    public void whenTimeUpdated_ThePhysicalActivityShouldReturnTheNewTime() {
        PhysicalActivity originalPhysicalActivity = this.physicalActivityDao.getPhysicalActivityByType("CYCLE");
        assertThat(originalPhysicalActivity.getStartTime()).isEqualTo(543789);

        this.physicalActivityDao.updateTime("CYCLE", 12345);
        PhysicalActivity newPhysicalActivity = this.physicalActivityDao.getPhysicalActivityByType("CYCLE");
        assertThat(newPhysicalActivity.getStartTime()).isEqualTo(12345);
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