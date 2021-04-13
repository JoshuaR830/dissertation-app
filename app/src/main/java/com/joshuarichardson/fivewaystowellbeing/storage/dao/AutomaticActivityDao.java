package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.AutomaticActivity;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface AutomaticActivityDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(AutomaticActivity automaticActivity);

    @Query("UPDATE automatic_activity SET start_time = :startTime WHERE activity_type = :activityType")
    void updateStartTime(String activityType, long startTime);

    @Query("UPDATE automatic_activity SET end_time = :endTime WHERE activity_type = :activityType")
    void updateEndTime(String activityType, long endTime);

    @Query("SELECT * FROM automatic_activity WHERE activity_type = :activityType AND activity_id > 0")
    AutomaticActivity getPhysicalActivityByTypeWithAssociatedActivity(String activityType);

    @Query("SELECT * FROM automatic_activity WHERE activity_id > 0 AND name IS NOT NULL ORDER BY name ASC")
    List<AutomaticActivity> getAllPhysicalActivitiesWithNamesAndAssociatedActivities();

    @Query("SELECT * FROM automatic_activity WHERE name IS NOT NULL ORDER BY name ASC")
    List<AutomaticActivity> getAllPhysicalActivitiesWithNames();

    @Query("UPDATE automatic_activity SET activity_id = :activityId WHERE activity_type = :activityType")
    void updateActivityId(String activityType, long activityId);

    @Query("UPDATE automatic_activity SET is_pending = :isPending WHERE activity_type = :activityType")
    void updateIsPendingStatus(String activityType, boolean isPending);

    @Query("SELECT * FROM automatic_activity WHERE is_pending = 1 ORDER BY start_time")
    List<AutomaticActivity> getPending();

    @Query("UPDATE automatic_activity SET is_notification_confirmed = :isConfirmed WHERE activity_type = :activityType")
    void updateIsNotificationConfirmedStatus(String activityType, boolean isConfirmed);
}