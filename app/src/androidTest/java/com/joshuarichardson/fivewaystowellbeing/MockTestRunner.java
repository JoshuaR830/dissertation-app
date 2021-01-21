package com.joshuarichardson.fivewaystowellbeing;

import android.app.Application;
import android.content.Context;

import androidx.test.runner.AndroidJUnitRunner;
import dagger.hilt.android.testing.HiltTestApplication;

// https://developer.android.com/training/dependency-injection/hilt-testing
public class MockTestRunner extends AndroidJUnitRunner {
    @Override
    public Application newApplication(ClassLoader cl, String className, Context context) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        return super.newApplication(cl, HiltTestApplication.class.getName(), context);
    }
}