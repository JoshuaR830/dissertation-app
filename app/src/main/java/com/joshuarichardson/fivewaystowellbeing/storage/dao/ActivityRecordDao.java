package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * Provide methods for accessing activity_records table
 */
@Dao
public interface ActivityRecordDao {

    /**
     * Insert a new activity
     *
     * @param activityRecord The activity to insert
     * @return The id of the new activity
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(ActivityRecord activityRecord);

    /**
     * Get the activity record by its unique id
     *
     * @param activityRecordId The id of the record to retrieve
     * @return The activity specified
     */
    @Query("SELECT * FROM activity_records WHERE id = :activityRecordId")
    ActivityRecord getActivityRecordById(long activityRecordId);

    /**
     * Get all of the activities between the start and end time inclusive
     *
     * @param startTime The start time in milliseconds
     * @param endTime The end time in milliseconds
     * @return A list of activities matching within the specified time range
     */
    @Query("SELECT * FROM activity_records WHERE timestamp >= :startTime AND timestamp <= :endTime")
    List<ActivityRecord> getActivitiesInTimeRange(long startTime, long endTime);

    /**
     * Get all of the activities ever created that have not been hidden/deleted
     *
     * @return An observable list of all activities
     */
    @Query("SELECT * FROM activity_records WHERE is_hidden = 0")
    LiveData<List<ActivityRecord>> getAllActivities();

    /**
     * Get all activities that have not been hidden/deleted
     *
     * @return A list of all activities
     */
    @Query("SELECT * FROM activity_records WHERE is_hidden = 0")
    List<ActivityRecord> getAllVisibleActivitiesNotLive();

    /**
     * Update all of the activity info
     *
     * @param activityRecordId The id of the activity to update
     * @param name The new name of the activity
     * @param type The new activity type
     * @param wayToWellbeingString The new way to wellbeing
     * @param timestamp the new timestamp of the activity
     */
    @Query("UPDATE activity_records SET type = :type, name = :name, way_to_wellbeing = :wayToWellbeingString, timestamp = :timestamp WHERE id = :activityRecordId")
    void update(long activityRecordId, String name, String type, String wayToWellbeingString, long timestamp);

    /**
     * Hide the activity from any lists the user could interact with without deleting it from old wellbeing logs
     *
     * @param activityRecordId The id of the activity to hide
     * @param isHidden Choose to hide or show the activity
     */
    @Query("UPDATE activity_records SET is_hidden = :isHidden WHERE id = :activityRecordId")
    void flagHidden(long activityRecordId, boolean isHidden);
}
