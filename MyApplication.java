package com.benjaminsommer.dailygoals;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.benjaminsommer.dailygoals.di.AppComponent;
import com.benjaminsommer.dailygoals.di.AppInjector;
import com.benjaminsommer.dailygoals.di.DaggerAppComponent;
import com.facebook.FacebookSdk;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasServiceInjector;

/**
 * Created by DEU209213 on 22.01.2017.
 */
public class MyApplication extends Application implements HasActivityInjector, HasServiceInjector {

    private static AppComponent mAppComponent;
    private static Context context;

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Inject
    DispatchingAndroidInjector<Service> dispatchingServiceInjector;

    @Override
    public void onCreate() {
        super.onCreate();

        FacebookSdk.sdkInitialize(getApplicationContext());
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.benjaminsommer.dailygoals",  // replace with your unique package name
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        // initialize AppInjector by Dagger 2
        mAppComponent = DaggerAppComponent.builder().application(this).build();
        mAppComponent.inject(this);
        AppInjector.init(this);

        MyApplication.context = getApplicationContext();

    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return  dispatchingAndroidInjector;
    }

    @Override
    public DispatchingAndroidInjector<Service> serviceInjector() {
        return dispatchingServiceInjector;
    }

    public static Context getContext() {
        return MyApplication.context;
    }

    public static AppComponent getAppComponent() {
        return mAppComponent;
    }

}
