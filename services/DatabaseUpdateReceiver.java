package com.benjaminsommer.dailygoals.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.util.Calendar;

/**
 * Created by DEU209213 on 12.03.2017.
 * purpose of class: activate or deactivate a daily reminder for DataSet
 */

public class DatabaseUpdateReceiver extends BroadcastReceiver {

    AlarmManager alarmMgr;
    PendingIntent DbUpdateIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent updateService = new Intent(context, DatabaseUpdateService.class);
        context.startService(updateService);
    }

    public void setAlarm(Context context) {

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, DatabaseUpdateReceiver.class);
        DbUpdateIntent = PendingIntent.getBroadcast(context, 1, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 1);

        alarmMgr.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, DbUpdateIntent);

        // Enable to automatically restart the alarm when the device is rebooted
        ComponentName receiver = new ComponentName(context, NotificationBootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

    }

    public void cancelAlarm(Context context) {

        if (alarmMgr != null) {
            alarmMgr.cancel(DbUpdateIntent);
            DbUpdateIntent.cancel();
        }

        // Disable so that it doesn't automatically restart the alarm when the device is rebooted
        ComponentName receiver = new ComponentName(context, NotificationBootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

    }

}
