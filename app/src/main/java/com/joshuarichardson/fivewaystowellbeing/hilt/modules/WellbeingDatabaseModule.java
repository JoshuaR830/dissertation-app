package com.joshuarichardson.fivewaystowellbeing.hilt.modules;

import android.content.Context;
import android.util.Log;

import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.QuestionsToAsk;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyQuestionSet;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;

import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.WELLBEING_DATABASE_NAME;
import static com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes.BASIC_SURVEY;

@Module
@InstallIn(ApplicationComponent.class)
public class WellbeingDatabaseModule {
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    // Add the new wellbeing column to the activity_records table
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE activity_records ADD COLUMN way_to_wellbeing TEXT DEFAULT " + WaysToWellbeing.UNASSIGNED.toString());
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            // The questions should exist already - but should not get deleted - they need to persist as they don't get added to in normal operation of code
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

            // Requires a question so should technically be deleted when a question is deleted
            // foreign key goes on thing that can't exist without it - a record can't exist without the question - the whole point of a record is that it has info about a question
            database.execSQL("CREATE TABLE wellbeing_records (" +
                "wellbeing_record_id INTEGER NOT NULL PRIMARY KEY, " +
                "user_input BOOLEAN, " +
                "time INTEGER NOT NULL, " +
                "survey_response_activity_record_id INTEGER NOT NULL, " +
                "sequence_number INTEGER NOT NULL, " +
                "question_id INTEGER NOT NULL, "  +
                "FOREIGN KEY(question_id) REFERENCES wellbeing_questions(wellbeing_question_id) ON DELETE CASCADE" +
                ")"
            );

            database.execSQL("ALTER TABLE survey_activity ADD COLUMN sequence_number INTEGER DEFAULT 0");
            database.execSQL("ALTER TABLE survey_activity ADD COLUMN note TEXT");
            database.execSQL("ALTER TABLE survey_activity ADD COLUMN start_time INTEGER DEFAULT -1");
            database.execSQL("ALTER TABLE survey_activity ADD COLUMN end_time INTEGER DEFAULT -1");
        }
    };

    @Provides
    @Singleton
    public static WellbeingDatabase getWellbeingDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, WellbeingDatabase.class, WELLBEING_DATABASE_NAME)
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .addCallback(new RoomDatabase.Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);
                    databaseWriteExecutor.execute(() -> {
                        Date now = new Date();
                        long setId = getWellbeingDatabase(context).surveyQuestionSetDao().insert(new SurveyQuestionSet(now.getTime(), 0));
                        getWellbeingDatabase(context).questionsToAskDao().insert(new QuestionsToAsk("", "", setId, BASIC_SURVEY.toString(), 0, null));
                    });
                }

                @Override
                public void onDestructiveMigration(@NonNull SupportSQLiteDatabase db) {
                    super.onDestructiveMigration(db);
                    databaseWriteExecutor.execute(() -> {
                        Date now = new Date();
                        Log.d("Destructive migration", "Add the stuff");
                        long setId = getWellbeingDatabase(context).surveyQuestionSetDao().insert(new SurveyQuestionSet(now.getTime(), 0));
                        Log.d("Set Id", String.valueOf(setId));
                        getWellbeingDatabase(context).questionsToAskDao().insert(new QuestionsToAsk("", "", setId, BASIC_SURVEY.toString(), 0, null));
                    });
                }
            })
            .fallbackToDestructiveMigration()
            .build();
        }
}
