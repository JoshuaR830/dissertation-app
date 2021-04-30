package com.joshuarichardson.fivewaystowellbeing.hilt.modules;

import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * Make FirebaseAnalytics injectable into the tracking helper {@link com.joshuarichardson.fivewaystowellbeing.analytics.LogAnalyticEventHelper}
 */
@Module
@InstallIn(ApplicationComponent.class)
public class FirebaseAnalyticsModule {

    public static FirebaseAnalytics firebaseAnalytics;

    @Provides
    @Singleton
    public FirebaseAnalytics getFirebaseAnalytics(@ApplicationContext Context context) {
        return com.google.firebase.analytics.FirebaseAnalytics.getInstance(context);
    }
}
