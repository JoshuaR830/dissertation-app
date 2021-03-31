package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.PhysicalActivity;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface PhysicalActivityDao {
    @Insert
    void insert(PhysicalActivity physicalActivity);

    @Query("UPDATE physical_activity SET start_time = :startTime WHERE activity_type = :activityType")
    void updateTime(String activityType, long startTime);

    @Query("SELECT * FROM physical_activity WHERE activity_type = :activityType")
    PhysicalActivity getPhysicalActivityByType(String activityType);

    @Query("UPDATE physical_activity SET activity_id = :activityId WHERE activity_type = :activityType")
    void updateActivityId(String activityType, long activityId);
}