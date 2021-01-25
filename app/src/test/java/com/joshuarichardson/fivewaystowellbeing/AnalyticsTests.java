package com.joshuarichardson.fivewaystowellbeing;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.joshuarichardson.fivewaystowellbeing.analytics.LogAnalyticEventHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AnalyticsTests {

    FirebaseAnalytics mockFirebaseAnalytics;

    @Captor
    ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);

    @Before
    public void setUp() {
        mockFirebaseAnalytics = mock(FirebaseAnalytics.class);
    }

    @Test
    public void whenCreatePassTimeEventCalled_ThenFirebaseAnalyticsLogEventsShouldBeCalled() {
        LogAnalyticEventHelper analyticsHelper = new LogAnalyticEventHelper(this.mockFirebaseAnalytics);
        analyticsHelper.logCreatePasstimeEvent(this);

        // How to capture variables https://howtodoinjava.com/mockito/verify-multiple-method-arguments/
        verify(mockFirebaseAnalytics, times(1)).logEvent(stringCaptor.capture(), any(Bundle.class));

        List<String> strings = stringCaptor.getAllValues();
        assertThat(strings.get(0)).isEqualTo("create_passtime");
    }

    @Test
    public void whenCreateSurveyEventCalled_ThenFirebaseAnalyticsLogEventsShouldBeCalled() {
        LogAnalyticEventHelper analyticsHelper = new LogAnalyticEventHelper(this.mockFirebaseAnalytics);
        analyticsHelper.logCreateSurveyEvent(this);

        // How to capture variables https://howtodoinjava.com/mockito/verify-multiple-method-arguments/
        verify(mockFirebaseAnalytics, times(1)).logEvent(stringCaptor.capture(), any(Bundle.class));

        List<String> strings = stringCaptor.getAllValues();
        assertThat(strings.get(0)).isEqualTo("create_survey");
    }

    @Test
    public void whenWayToWellbeingEventCalled_ThenFirebaseAnalyticsLogEventsShouldBeCalled() {
        LogAnalyticEventHelper analyticsHelper = new LogAnalyticEventHelper(this.mockFirebaseAnalytics);
        analyticsHelper.logWayToWellbeingEvent(this, WaysToWellbeing.CONNECT);

        // How to capture variables https://howtodoinjava.com/mockito/verify-multiple-method-arguments/
        verify(mockFirebaseAnalytics, times(2)).logEvent(stringCaptor.capture(), any(Bundle.class));

        List<String> strings = stringCaptor.getAllValues();
        assertThat(strings.get(0)).isEqualTo("connect");
        assertThat(strings.get(1)).isEqualTo("achieved_way_to_wellbeing");
    }
}
