package com.benjaminsommer.dailygoals.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by DEU209213 on 28.05.2017.
 */

public class ToDoReminderReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = ToDoReminderReceiver.class.getSimpleName();
    private static final String INTENT_ID = "intent_id";
    public static final String NOTIF_ID = "notification_id";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getExtras().getBoolean(INTENT_ID)) {
            int id = intent.getIntExtra(NOTIF_ID, -1);
            Intent service = new Intent(context, ToDoReminderService.class);
            service.putExtra(NOTIF_ID, id);
            Log.d(TAG, "NOTIF_ID: " + String.valueOf(id));
            startWakefulService(context, service);

        }

    }

    public void setAlarm(Context context, int id, long reminderTime) {

        Log.d(TAG, "id: " + String.valueOf(id));
        Log.d(TAG, "reminderTime: " + String.valueOf(reminderTime));


        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent reminderIntent = new Intent(context, ToDoReminderReceiver.class);
        reminderIntent.putExtra(INTENT_ID, true);
        reminderIntent.putExtra(NOTIF_ID, id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, reminderIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);



        // Enable to automatically restart the alarm when the device is rebooted
        ComponentName receiver = new ComponentName(context, NotificationBootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

    }

    public void cancelAlarm(Context context, int id) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent reminderIntent = new Intent(context, ToDoReminderReceiver.class);
        reminderIntent.putExtra(NOTIF_ID, id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, reminderIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);

    }

    public void setBootAlarm(Context context) {
        Intent service = new Intent(context, ToDoBootService.class);
        startWakefulService(context, service);
    }

}
