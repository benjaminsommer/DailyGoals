package com.benjaminsommer.dailygoals.workmanager;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Log;

import com.benjaminsommer.dailygoals.MyApplication;
import com.benjaminsommer.dailygoals.di.AppComponent;
import com.benjaminsommer.dailygoals.util.SharedPrefHelperClass;

import javax.inject.Inject;

import androidx.work.Worker;

public class InitialOneTimeForDailyReminderWorker extends Worker {

    @Inject
    Resources resources;

    public final static String TAG = InitialOneTimeForDailyReminderWorker.class.getSimpleName();

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

        // start periodic reminder
        SharedPrefHelperClass sphc = new SharedPrefHelperClass(null, resources);
        sphc.startPeriodicDataSetReminder();

        return Result.SUCCESS;

    }
}
