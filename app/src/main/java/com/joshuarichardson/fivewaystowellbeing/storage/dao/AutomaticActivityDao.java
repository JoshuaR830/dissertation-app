package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.AutomaticActivity;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * Provides methods for accessing the automatic_activity table
 */
@Dao
public interface AutomaticActivityDao {
    /**
     * Insert a new activity to automatically suggest
     *
     * @param automaticActivity A new automatic activity to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(AutomaticActivity automaticActivity);

    /**
     * Update the start time of the automatic activity
     *
     * @param activityType The activity type
     * @param startTime The millisecond timestamp for when the activity started
     */
    @Query("UPDATE automatic_activity SET start_time = :startTime WHERE activity_type = :activityType")
    void updateStartTime(String activityType, long startTime);

    /**
     * Update the end time of the automatic activity
     *
     * @param activityType The activity type
     * @param endTime The millisecond timestamp for when the activity ended
     */
    @Query("UPDATE automatic_activity SET end_time = :endTime WHERE activity_type = :activityType")
    void updateEndTime(String activityType, long endTime);

    /**
     * Get the automatic activity matching a specific type
     *
     * @param activityType The type of activity to retrieve details for
     * @return The automatic activity matching the request
     */
    @Query("SELECT * FROM automatic_activity WHERE activity_type = :activityType AND activity_id > 0")
    AutomaticActivity getPhysicalActivityByTypeWithAssociatedActivity(String activityType);

    /**
     * Get a sorted list of all apps (only apps have names)
     *
     * @return A list of all automatic activities with names in alphabetical order
     */
    @Query("SELECT * FROM automatic_activity WHERE name IS NOT NULL ORDER BY name ASC")
    List<AutomaticActivity> getAllPhysicalActivitiesWithNames();

    /**
     * Assign an in app activity to a physical or digital activity
     *
     * @param activityType The type of the automatically tracked activity
     * @param activityId The id of the activity that should be automatically logged
     */
    @Query("UPDATE automatic_activity SET activity_id = :activityId WHERE activity_type = :activityType")
    void updateActivityId(String activityType, long activityId);

    /**
     * Update the automatic activity to be pending user confirmation.
     * If it is pending confirmation it will be displayed in the app until it no loonger is
     *
     * @param activityType The type of the automatically tracked activity
     * @param isPending The pending status of the activity
     */
    @Query("UPDATE automatic_activity SET is_pending = :isPending WHERE activity_type = :activityType")
    void updateIsPendingStatus(String activityType, boolean isPending);

    /**
     * Get all pending activities in order of which was started first
     *
     * @return A list of automatic activities
     */
    @Query("SELECT * FROM automatic_activity WHERE is_pending = 1 ORDER BY start_time")
    List<AutomaticActivity> getPending();

    /**
     * Set the notification confirmation status based on whether the user has confirmed the activity
     *
     * @param activityType The type of the automatically tracked activity
     * @param isConfirmed The confirmed status of the notification
     */
    @Query("UPDATE automatic_activity SET is_notification_confirmed = :isConfirmed WHERE activity_type = :activityType")
    void updateIsNotificationConfirmedStatus(String activityType, boolean isConfirmed);
}