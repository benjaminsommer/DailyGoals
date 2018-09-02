package com.benjaminsommer.dailygoals.workmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Log;

import com.benjaminsommer.dailygoals.MyApplication;
import com.benjaminsommer.dailygoals.di.AppComponent;
import com.benjaminsommer.dailygoals.repository.DataSetRepository;
import com.benjaminsommer.dailygoals.util.SharedPrefHelperClass;

import javax.inject.Inject;

import androidx.work.Worker;

public class DailyDataSetReminderWorker extends Worker {

    public final static String TAG = DailyDataSetReminderWorker.class.getSimpleName();

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    Resources resources;

    @Inject
    DataSetRepository dataSetRepository;

    @NonNull
    @Override
    public Result doWork() {

        // register dagger + injection
        Context context = MyApplication.getContext();
        if (context instanceof MyApplication) {
            AppComponent daggerAppComponent = MyApplication.getAppComponent();
            daggerAppComponent.inject(this);
        }

        Log.d(TAG, TAG + " started");

        // get open goals of today
        SharedPrefHelperClass sphc = new SharedPrefHelperClass(sharedPreferences, resources);
        String today = sphc.generateTodayAsString();

        int openGoalCount = dataSetRepository.getOpenDataSetCount(today);
        if (openGoalCount > 0) {
            sphc.sendNotification(openGoalCount, today);
        }
        return Result.SUCCESS;

    }

}
