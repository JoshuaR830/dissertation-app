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
    ActivityRecord getActivityRecordById(long activityRecordId);

    @Query("SELECT * FROM activity_records WHERE timestamp >= :startTime AND timestamp <= :endTime")
    LiveData<List<ActivityRecord>> getActivitiesInTimeRange(long startTime, long endTime);

    @Query("SELECT * FROM activity_records")
    LiveData<List<ActivityRecord>> getAllActivities();

    @Query("SELECT * FROM activity_records")
    List<ActivityRecord> getAllActivitiesNotLive();

    // ToDo - consider deleting this query
    @Query("SELECT * FROM activity_records WHERE name LIKE :searchTerm || '%' ")
    List<ActivityRecord> getActivitiesMatchingSearch(String searchTerm);

    // ToDo Delete may be necessary later
}
