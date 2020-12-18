package com.joshuarichardson.fivewaystowellbeing.utilities;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

/*
    This is taken from https://gist.github.com/JoseAlcerreca/1e9ee05dcdd6a6a6fa1cbfc125559bba as I needed to test LiveData
    Copyright 2019 Google LLC - Apache-2.0
*/


public class LiveDataTestUtil {
    // This is static so can't have state

    @SuppressWarnings("unchecked")
    public static <T> T getOrAwaitValue(final LiveData<T> liveData) throws TimeoutException, InterruptedException {

        // Create a new Object array
        // Uses array so that it is final
        Object[] data = new Object[1];

        // Create a new countdown latch - blocks until counted down to 0
        CountDownLatch latch = new CountDownLatch(1);

        // Need an observer that can observe changes to the live data provided
        Observer<T> liveDataObserver = new Observer<T>() {
            @Override
            // By observing an on change and counting down it means that it is possible to keep track of the number of changes and stop blocking after 1 - at this point the LiveData will have been updated
            public void onChanged(@Nullable T liveDataValue) {
                data[0] = liveDataValue;
                latch.countDown();
                liveData.removeObserver(this);
            }
        };

        liveData.observeForever(liveDataObserver);

        // If the latch hasn't counted down after a given time stop observing
        if (!latch.await(2000, TimeUnit.MILLISECONDS)) {
            throw new TimeoutException("Live data not set in acceptable time");
        }

        // Trying to cast earlier caused some issues
        return (T) data[0];
    }
}
