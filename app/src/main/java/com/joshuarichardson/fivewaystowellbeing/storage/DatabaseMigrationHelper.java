package com.joshuarichardson.fivewaystowellbeing.storage;

import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Contains all of the database migrations
 */
public class DatabaseMigrationHelper {
    // Add the new wellbeing column to the activity_records table
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE activity_records ADD COLUMN way_to_wellbeing TEXT DEFAULT " + WaysToWellbeing.UNASSIGNED.toString());
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            // Create the new questions table
            database.execSQL("CREATE TABLE wellbeing_questions (" +
                "wellbeing_question_id INTEGER NOT NULL PRIMARY KEY, " +
                "question TEXT NOT NULL, " +
                "positive_message TEXT NOT NULL, " +
                "negative_message TEXT NOT NULL, " +
                "way_to_wellbeing TEXT NOT NULL, " +
                "weighting INTEGER NOT NULL, " +
                "activity_type TEXT NOT NULL, " +
                "input_type TEXT NOT NULL " +
                ")"
            );

            // Create the new wellbeing records table
            database.execSQL("CREATE TABLE wellbeing_records (" +
                "wellbeing_record_id INTEGER NOT NULL PRIMARY KEY, " +
                "time INTEGER NOT NULL, " +
                "user_input INTEGER NOT NULL default 0, " +
                "survey_response_activity_record_id INTEGER NOT NULL, " +
                "sequence_number INTEGER NOT NULL default 0, " +
                "question_id INTEGER NOT NULL, "  +
                "FOREIGN KEY(question_id) REFERENCES wellbeing_questions(wellbeing_question_id) ON DELETE CASCADE, " +
                "FOREIGN KEY(survey_response_activity_record_id) REFERENCES survey_activity(survey_activity_id) ON DELETE CASCADE" +
                ")"
            );

            // Create a new table that will replace the survey_activity table - this is required because old table has the wrong primary key
            // Reference: medium.com/@pekwerike/handling-roomdb-migration-create-a-new-primary-key-column-in-an-existing-entity-f15d10932f5b
            database.execSQL("CREATE TABLE new_survey_activity (" +
                "survey_activity_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "survey_response_id INTEGER NOT NULL, " +
                "activity_record_id INTEGER NOT NULL, " +
                "sequence_number INTEGER NOT NULL, " +
                "note TEXT, " +
                "start_time INTEGER NOT NULL, " +
                "end_time INTEGER NOT NULL, " +
                "FOREIGN KEY(activity_record_id) REFERENCES activity_records(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(survey_response_id) REFERENCES survey_response(id) ON DELETE CASCADE" +
                ");"
            );

            // Insert the values from the old table and new default values into the new table
            database.execSQL("INSERT INTO new_survey_activity(" +
                "survey_response_id, " +
                "activity_record_id, " +
                "sequence_number, " +
                "note, " +
                "start_time, " +
                "end_time " +
                ")" +
                "SELECT survey_response_id, activity_record_id, 0, null, -1, -1 FROM survey_activity"
            );

            // Delete the old table
            database.execSQL("DROP TABLE survey_activity");

            // Rename the new table
            database.execSQL("ALTER TABLE new_survey_activity RENAME TO survey_activity");
        }
    };

    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE survey_activity ADD COLUMN emotion INTEGER DEFAULT 0 NOT NULL");
            database.execSQL("ALTER TABLE survey_activity ADD COLUMN is_done INTEGER DEFAULT 0 NOT NULL");
        }
    };

    public static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE activity_records ADD COLUMN is_hidden INTEGER DEFAULT 0 NOT NULL");
        }
    };

    public static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Create the new wellbeing result table
            database.execSQL("CREATE TABLE wellbeing_result (" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "timestamp INTEGER NOT NULL, " +
                "connect INTEGER NOT NULL, " +
                "be_active INTEGER NOT NULL, " +
                "keep_learning INTEGER NOT NULL, " +
                "take_notice INTEGER NOT NULL, " +
                "give INTEGER NOT NULL " +
                ")"
            );
        }
    };

    public static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Create the new physical activity result table
            database.execSQL("CREATE TABLE physical_activity (" +
                "activity_type TEXT NOT NULL PRIMARY KEY, " +
                "start_time INTEGER NOT NULL, " +
                "end_time INTEGER NOT NULL, " +
                "activity_id INTEGER NOT NULL, " +
                "is_pending INTEGER NOT NULL " +
                ")"
            );
        }
    };

    public static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE physical_activity ADD COLUMN is_notification_confirmed INTEGER DEFAULT 0 NOT NULL");
        }
    };

    public static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Create the new app_usage_table result table
            database.execSQL("CREATE TABLE app_usage_table (" +
                "id INTEGER NOT NULL PRIMARY KEY, " +
                "package_id TEXT NOT NULL, " +
                "start_time INTEGER NOT NULL, " +
                "end_time INTEGER NOT NULL, " +
                "previous_usage INTEGER NOT NULL, " +
                "is_pending INTEGER NOT NULL, " +
                "current_usage INTEGER NOT NULL " +
                ")"
            );

            database.execSQL("ALTER TABLE physical_activity ADD COLUMN name TEXT");
        }
    };

    public static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {


            database.execSQL("ALTER TABLE physical_activity RENAME TO automatic_activity");

            // Create a new table that will replace the survey_activity table - this is required because old table has the wrong primary key
            // Reference: medium.com/@pekwerike/handling-roomdb-migration-create-a-new-primary-key-column-in-an-existing-entity-f15d10932f5b
            database.execSQL("CREATE TABLE temp_app_usage_table (" +
                "id INTEGER NOT NULL PRIMARY KEY, " +
                "package_id TEXT NOT NULL, " +
                "start_time INTEGER NOT NULL, " +
                "end_time INTEGER NOT NULL, " +
                "is_pending INTEGER NOT NULL, " +
                "most_recent_resume_time INTEGER NOT NULL " +
                ");"
            );

            // Insert the values from the old table
            database.execSQL("INSERT INTO temp_app_usage_table(" +
                "id, " +
                "package_id, " +
                "start_time, " +
                "end_time, " +
                "is_pending, " +
                "most_recent_resume_time " +
                ")" +
                "SELECT id, package_id, start_time, end_time, is_pending, current_usage FROM app_usage_table"
            );

            // Delete the old table
            database.execSQL("DROP TABLE app_usage_table");
            database.execSQL("ALTER TABLE temp_app_usage_table RENAME TO app_usage_table");
        }
    };

    public static final Migration MIGRATION_10_11 = new Migration(10, 11) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            // Create a new table that will replace the survey_activity table - this is required because old table has the wrong primary key
            // Reference: medium.com/@pekwerike/handling-roomdb-migration-create-a-new-primary-key-column-in-an-existing-entity-f15d10932f5b
            database.execSQL("CREATE TABLE temp_wellbeing_result (" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "timestamp INTEGER NOT NULL, " +
                "connect INTEGER NOT NULL, " +
                "be_active INTEGER NOT NULL, " +
                "keep_learning INTEGER NOT NULL, " +
                "take_notice INTEGER NOT NULL, " +
                "give INTEGER NOT NULL, " +
                "FOREIGN KEY(id) REFERENCES survey_response(id) ON DELETE CASCADE " +
                ");"
            );

            // Insert the values from the old table
            database.execSQL("INSERT INTO temp_wellbeing_result(" +
                "id, " +
                "timestamp, " +
                "connect, " +
                "be_active, " +
                "keep_learning, " +
                "take_notice, " +
                "give " +
                ")" +
                "SELECT id, timestamp, connect, be_active, keep_learning, take_notice, give FROM wellbeing_result"
            );

            // Delete the old table
            database.execSQL("DROP TABLE wellbeing_result");
            database.execSQL("ALTER TABLE temp_wellbeing_result RENAME TO wellbeing_result");
        }
    };

    public static final Migration MIGRATION_11_12 = new Migration(11, 12) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE survey_question_set");
            database.execSQL("DROP TABLE questions_to_ask");
            database.execSQL("DROP TABLE survey_response_element");
        }
    };
}
