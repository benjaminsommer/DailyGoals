package com.benjaminsommer.dailygoals.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by DEU209213 on 21.01.2017.
 */
public class NotificationAlarmReceiver extends WakefulBroadcastReceiver {

    public static final String INTENT_EXTRA = "intent_extra";

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private SharedPreferences prefs;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getExtras().getBoolean(INTENT_EXTRA)) {
            Intent service = new Intent(context, NotificationSchedulingService.class);
            startWakefulService(context, service);
        }

    }

    public void setAlarm(Context context) {

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationAlarmReceiver.class);
        intent.putExtra(INTENT_EXTRA, true);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendarNow = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Log.d("Time1:", String.valueOf(calendar.getTimeInMillis()));

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String strAlarmTime = prefs.getString("pref_notificationTime", "11:00");
        String[] timeSections = strAlarmTime.split(":");
        int hours = Integer.parseInt(timeSections[0]);
        int minutes = Integer.parseInt(timeSections[1]);
        boolean vibrationDecision = prefs.getBoolean("pref_vibration", true);

        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);
        Log.d("Hour:", String.valueOf(hours));
        Log.d("Minute:", String.valueOf(minutes));
        Log.d("Time2:", String.valueOf(calendar.getTimeInMillis()));

        long alarmTime = 0;
        if (calendar.getTimeInMillis() <= calendarNow.getTimeInMillis()) {
            alarmTime = calendar.getTimeInMillis() + (AlarmManager.INTERVAL_DAY + 1);
        } else {
            alarmTime = calendar.getTimeInMillis();
        }

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarmTime, AlarmManager.INTERVAL_DAY, alarmIntent);

        // Enable to automatically restart the alarm when the device is rebooted
        ComponentName receiver = new ComponentName(context, NotificationBootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

    }

    public void cancelAlarm(Context context) {

        if (alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
            alarmIntent.cancel();
        }

        // Disable so that it doesn't automatically restart the alarm when the device is rebooted
        ComponentName receiver = new ComponentName(context, NotificationBootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

    }

}
