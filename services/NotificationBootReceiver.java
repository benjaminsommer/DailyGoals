package com.benjaminsommer.dailygoals.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by DEU209213 on 21.01.2017.
 */
public class NotificationBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        DatabaseUpdateReceiver databaseUpdateReceiver = new DatabaseUpdateReceiver();
        NotificationAlarmReceiver notificationAlarmReceiver = new NotificationAlarmReceiver();
        ToDoReminderReceiver toDoReminderReceiver = new ToDoReminderReceiver();

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            databaseUpdateReceiver.setAlarm(context);
            notificationAlarmReceiver.setAlarm(context);
            toDoReminderReceiver.setBootAlarm(context);

        }
    }
}
