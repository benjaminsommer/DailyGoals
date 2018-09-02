package com.benjaminsommer.dailygoals.util;

import android.arch.lifecycle.LiveData;

/**
 * Created by SOMMER on 28.01.2018.
 */

/**
 * A LiveData class that has {@code null} value.
 */
public class AbsentLiveData extends LiveData {
    private AbsentLiveData() {
        postValue(null);
    }
    public static <T> LiveData<T> create() {
        //noinspection unchecked
        return new AbsentLiveData();
    }
}
