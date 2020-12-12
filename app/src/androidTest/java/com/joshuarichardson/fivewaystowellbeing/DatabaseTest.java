package com.joshuarichardson.fivewaystowellbeing;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    // So I want to unit test the room implementation
    // Need to get the data access object in a before - so that I have access to room
    // Need to test inserting a value
    // Need to retrieve a value from it

    // The implementation can have testmode stuff allowing for in memory testing - no actual data
    //     inMemoryDatabaseBuilder
    // This allows you to test every query

    @Test
    public void TestDatabase() {

    }
}
