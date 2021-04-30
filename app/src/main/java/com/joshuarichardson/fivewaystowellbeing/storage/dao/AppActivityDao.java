package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.AppActivity;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

/**
 * Provide methods for accessing the app_usage_table
 */
@Dao
public interface AppActivityDao {

    /**
     * Insert a new app activity into the app_usage_table
     *
     * @param appActivity The app activity to insert
     */
    @Insert
    void insert(AppActivity appActivity);

    /**
     * Select the information for a given app activity
     *
     * @param packageName The package name of the app to get the information about
     * @return The app activity requested
     */
    @Query("SELECT * FROM app_usage_table WHERE package_id = :packageName")
    AppActivity getPhysicalActivityByType(String packageName);

    /**
     * Update the pending status of the an app to indicate whether the app has been used for
     * long enough to notify a user
     *
     * @param packageName The unique identifier for an app
     * @param pendingStatus The pending status to set
     */
    @Query("UPDATE app_usage_table SET is_pending = :pendingStatus WHERE package_id = :packageName AND is_pending = 0")
    void setPending(String packageName, boolean pendingStatus);

    /**
     * Update when an app was started and its time running
     *
     * @param packageName The unique identifier for an app
     * @param startTime The first time the app was resumed
     * @param lastResumedTime The last time the app was resumed
     */
    @Query("UPDATE app_usage_table SET start_time = :startTime, most_recent_resume_time = :lastResumedTime WHERE package_id = :packageName AND is_pending = 0")
    void updateStartAndLastResumedTime(String packageName, long startTime, long lastResumedTime);

    /**
     * Update the most recent potential end time
     *
     * @param packageName The unique identifier for an app
     * @param endTime The milliseconds timestamp of the pause event
     */
    @Query("UPDATE app_usage_table SET end_time = :endTime WHERE package_id = :packageName AND is_pending = 0")
    void updateEndTime(String packageName, long endTime);

    /**
     * Update the last time the app was resumed
     *
     * @param packageName The unique identifier for an app
     * @param lastResume The millisecond timestamp of the most recent resume
     */
    @Query("UPDATE app_usage_table SET most_recent_resume_time = :lastResume WHERE package_id = :packageName AND is_pending = 0")
    void updateUsageTime(String packageName, long lastResume);

    /**
     * Get all activities considered finished.
     * An app is considered finished if it has an end time that is greater than the last resume time
     * And when the duration difference is greater than the end time, indicating that a whole loop has passed
     *
     * @param durationDifference Allow an offset for if the resume didn't make it before checking
     * @return A list of apps that have finished
     */
    @Query("SELECT * FROM app_usage_table WHERE end_time > most_recent_resume_time AND end_time < :durationDifference")
    List<AppActivity> getFinishedApplications(long durationDifference);

    /**
     * Delete specific application from the table
     *
     * @param packageId The unique identifier for an app
     */
    @Query("DELETE FROM app_usage_table WHERE package_id = :packageId")
    void deleteWithPackageId(String packageId);

    /**
     * Delete all of the applications being tracked from the table
     */
    @Query("DELETE FROM app_usage_table")
    void deleteAll();
}