package com.joshuarichardson.fivewaystowellbeing.hilt.modules;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.QuestionsToAsk;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyQuestionSet;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;

import static com.joshuarichardson.fivewaystowellbeing.storage.DatabaseMigrationHelper.MIGRATION_10_11;
import static com.joshuarichardson.fivewaystowellbeing.storage.DatabaseMigrationHelper.MIGRATION_1_2;
import static com.joshuarichardson.fivewaystowellbeing.storage.DatabaseMigrationHelper.MIGRATION_2_3;
import static com.joshuarichardson.fivewaystowellbeing.storage.DatabaseMigrationHelper.MIGRATION_3_4;
import static com.joshuarichardson.fivewaystowellbeing.storage.DatabaseMigrationHelper.MIGRATION_4_5;
import static com.joshuarichardson.fivewaystowellbeing.storage.DatabaseMigrationHelper.MIGRATION_5_6;
import static com.joshuarichardson.fivewaystowellbeing.storage.DatabaseMigrationHelper.MIGRATION_6_7;
import static com.joshuarichardson.fivewaystowellbeing.storage.DatabaseMigrationHelper.MIGRATION_7_8;
import static com.joshuarichardson.fivewaystowellbeing.storage.DatabaseMigrationHelper.MIGRATION_8_9;
import static com.joshuarichardson.fivewaystowellbeing.storage.DatabaseMigrationHelper.MIGRATION_9_10;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.WELLBEING_DATABASE_NAME;
import static com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes.BASIC_SURVEY;

/**
 * Set up the database so that it can be injected into other classes
 */
@Module
@InstallIn(ApplicationComponent.class)
public class WellbeingDatabaseModule {
    public static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(4);

    @Provides
    @Singleton
    public static WellbeingDatabase getWellbeingDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, WellbeingDatabase.class, WELLBEING_DATABASE_NAME)
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11)
            .addCallback(new RoomDatabase.Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);
                    databaseExecutor.execute(() -> {
                        long now = Calendar.getInstance().getTimeInMillis();
                        long setId = getWellbeingDatabase(context).surveyQuestionSetDao().insert(new SurveyQuestionSet(now, 0));
                        getWellbeingDatabase(context).questionsToAskDao().insert(new QuestionsToAsk("", "", setId, BASIC_SURVEY.toString(), 0, null));
                    });
                }

                @Override
                public void onDestructiveMigration(@NonNull SupportSQLiteDatabase db) {
                    super.onDestructiveMigration(db);
                    databaseExecutor.execute(() -> {
                        long now = Calendar.getInstance().getTimeInMillis();
                        long setId = getWellbeingDatabase(context).surveyQuestionSetDao().insert(new SurveyQuestionSet(now, 0));
                        getWellbeingDatabase(context).questionsToAskDao().insert(new QuestionsToAsk("", "", setId, BASIC_SURVEY.toString(), 0, null));
                    });
                }
            })
            .build();
        }
}
