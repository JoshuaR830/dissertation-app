package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ActivityRecordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(ActivityRecord activityRecord);

    @Query("SELECT * FROM activity_records WHERE id = :activityRecordId")
    LiveData<List<ActivityRecord>> getActivityRecordById(int activityRecordId);

    @Query("SELECT * FROM activity_records WHERE timestamp >= :startTime AND timestamp <= :endTime")
    LiveData<List<ActivityRecord>> getActivitiesInTimeRange(int startTime, int endTime);

    @Query("SELECT * FROM activity_records")
    LiveData<List<ActivityRecord>> getAllActivities();

    // Delete may be necessary later
}
