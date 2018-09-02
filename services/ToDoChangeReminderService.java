package com.benjaminsommer.dailygoals.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.entities.ToDo;
import com.benjaminsommer.dailygoals.repository.ToDoRepository;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * Created by sommer on 05.06.2017.
 */

public class ToDoChangeReminderService extends IntentService {

    public final static String ACTION_SNOOZE_INTERVAL_1 = "action_snooze_interval_1";
    public final static String ACTION_SNOOZE_INTERVAL_2 = "action_snooze_interval_2";
    public final static String ACTION_SNOOZE_INTERVAL_3 = "action_snooze_interval_3";

    @Inject
    ToDoRepository toDoRepository;

    public ToDoChangeReminderService() {
        super(ToDoChangeReminderService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String action = intent.getAction();
        int id = intent.getIntExtra(ToDoReminderService.ACTION_ID, -1);
        if (ToDoReminderService.ACTION_DONE.equals(action)) {
            changeToDoState(id, true);
        } else if (ToDoReminderService.ACTION_CANCEL.equals(action)) {
            changeToDoState(id, false);
        } else if (ToDoReminderService.ACTION_SNOOZE.equals(action)) {
            snoozeToDo(id);
        } else if (ACTION_SNOOZE_INTERVAL_1.equals(action)) {
            finalizeSnooze(id, getSnoozeTimeframe("key_pref_todo_reminder1"));
        } else if (ACTION_SNOOZE_INTERVAL_2.equals(action)) {
            finalizeSnooze(id, getSnoozeTimeframe("key_pref_todo_reminder2"));
        } else if (ACTION_SNOOZE_INTERVAL_3.equals(action)) {
            finalizeSnooze(id, getSnoozeTimeframe("key_pref_todo_reminder3"));
        }

    }

    private void changeToDoState(int id, boolean isDone) {
        if (id != -1) {
            ToDo toDo = toDoRepository.getSpecificToDo(id);
            long now = System.currentTimeMillis();
            if (isDone) {
                toDo.setToDoState(100);
            } else {
                toDo.setToDoState(10);
            }
            toDo.setToDoReminderTime(now);
            toDo.setToDoTimeFinished(now);
            toDoRepository.updateToDo(toDo);
            NotificationManagerCompat.from(this).cancel(id);

        }
    }

    private void snoozeToDo(int id) {
        if (id != -1) {

            ToDo toDo = toDoRepository.getSpecificToDo(id);

            NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

            DateTime dateTime = new DateTime(toDo.getToDoReminderTime());
            String txtDate = dateTime.toString("dd.MM.yyyy");
            String txtHour = dateTime.toString("HH:mm");

            // PendingIntents when To Do is set to DONE
            Intent intentInterval1 = new Intent(this, ToDoChangeReminderService.class);
            intentInterval1.setAction(ACTION_SNOOZE_INTERVAL_1);
            intentInterval1.putExtra(ToDoReminderService.ACTION_ID, id);
            PendingIntent pIntentInterval1 = PendingIntent.getService(this, id, intentInterval1, PendingIntent.FLAG_UPDATE_CURRENT);

            // PendingIntents when To Do is set to CANCEL
            Intent intentInterval2 = new Intent(this, ToDoChangeReminderService.class);
            intentInterval2.setAction(ACTION_SNOOZE_INTERVAL_2);
            intentInterval2.putExtra(ToDoReminderService.ACTION_ID, id);
            PendingIntent pIntentInterval2 = PendingIntent.getService(this, id, intentInterval2, PendingIntent.FLAG_UPDATE_CURRENT);

            // PendingIntents when To Do is snoozed
            Intent intentInterval3 = new Intent(this, ToDoChangeReminderService.class);
            intentInterval3.setAction(ACTION_SNOOZE_INTERVAL_3);
            intentInterval3.putExtra(ToDoReminderService.ACTION_ID, id);
            PendingIntent pIntentInterval3 = PendingIntent.getService(this, id, intentInterval3, PendingIntent.FLAG_UPDATE_CURRENT);

            ArrayList<NotificationCompat.Action> actionList = new ArrayList<>();
            actionList.add(new NotificationCompat.Action(R.drawable.ic_alarm_24px, getSnoozeLabel("key_pref_todo_reminder1"), pIntentInterval1));
            actionList.add(new NotificationCompat.Action(R.drawable.ic_alarm_24px, getSnoozeLabel("key_pref_todo_reminder2"), pIntentInterval2));
            actionList.add(new NotificationCompat.Action(R.drawable.ic_alarm_24px, getSnoozeLabel("key_pref_todo_reminder3"), pIntentInterval3));

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.icon_to_do_24px)
                    .setContentTitle(toDo.getToDoName())
                    .setContentText(txtDate + ", " + txtHour)
                    .setWhen(toDo.getToDoReminderTime())
                    .setAutoCancel(false)
                    .setOngoing(true);

            for (int i = 0; i < actionList.size(); i++) {
                builder.addAction(actionList.get(i));
            }

            notificationManager.notify(id, builder.build());

//            // send local broadcast to activity to refresh
//            Intent broadcastIntent = new Intent();
//            broadcastIntent.setAction(ToDoFragment.TODO_NOTIFY_ACTIVITY_ACTION);
//            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

        }
    }

    private void finalizeSnooze(int id, long timeframe) {

        if (id != -1) {
            ToDo toDo = toDoRepository.getSpecificToDo(id);
            long newReminderTime = System.currentTimeMillis() + timeframe;
            toDo.setToDoReminderTime(newReminderTime);
            toDoRepository.updateToDo(toDo);
            NotificationManagerCompat.from(this).cancel(id);

            // set new reminder time
            ToDoReminderReceiver toDoReminderReceiver = new ToDoReminderReceiver();
            toDoReminderReceiver.setAlarm(getApplicationContext(), id, newReminderTime);

        }

    }

    private long getSnoozeTimeframe (String preferenceKey) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String prefValue = prefs.getString(preferenceKey, "0;1");
        String[] strParts = prefValue.split(";");
        int type = Integer.valueOf(strParts[0]);
        long value = Long.valueOf(strParts[1]);
        switch (type) {
            case 0:
                return value * 1000 * 60;
            case 1:
                return value * 60 * 1000 * 60;
            case 2:
                return value * 24 * 60 * 1000 * 60;
            case 3:
                DateTime now = new DateTime(DateTime.now());
                DateTime tmrw = new DateTime().withTimeAtStartOfDay().plusDays(1).withTimeAtStartOfDay();
                return tmrw.getMillis() - now.getMillis();
            default:
                return value * 1000 * 60;
        }

    }

    private String getSnoozeLabel (String preferenceKey) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String prefValue = prefs.getString(preferenceKey, "0;1");
        String[] strParts = prefValue.split(";");
        int type = Integer.valueOf(strParts[0]);
        long value = Long.valueOf(strParts[1]);
        switch (type) {
            case 0:
                return strParts[1] + " " + "min";
            case 1:
                return strParts[1] + " " + "h";
            case 2:
                return strParts[1] + " " + "d";
            case 3:
                LocalTime time = new LocalTime(value);
                return time.toString("HH:mm") + " " + "h";
            default:
                return strParts[1] + " " + "min";
        }

    }

}
