package com.benjaminsommer.dailygoals.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.entities.ToDo;
import com.benjaminsommer.dailygoals.repository.ToDoRepository;
import com.benjaminsommer.dailygoals.ui.todo.ToDoActivity;

import org.joda.time.DateTime;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * Created by sommer on 05.06.2017.
 */

public class ToDoReminderService extends IntentService {

    private static final String TAG = ToDoReminderService.class.getSimpleName();

    public final static String ACTION_DONE = "action_done";
    public final static String ACTION_CANCEL = "action_cancel";
    public final static String ACTION_SNOOZE = "action_snooze";
    public final static String ACTION_ID = "action_id";

    @Inject
    ToDoRepository toDoRepository;

    public ToDoReminderService() {
        super(ToDoReminderService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {


        // get intent extra
        int id;
        if (intent.hasExtra(ToDoReminderReceiver.NOTIF_ID)) {
            id = intent.getIntExtra(ToDoReminderReceiver.NOTIF_ID, -1);
        } else {
            id = -1;
        }
        sendNotification(id);


    }

    private void sendNotification (int id) {

        if (id != -1) {
            ToDo toDo = toDoRepository.getSpecificToDo(id);

            // PendingIntent when To Do is set to DONE
            Intent intentDone = new Intent(this, ToDoChangeReminderService.class);
            intentDone.setAction(ACTION_DONE);
            intentDone.putExtra(ACTION_ID, id);
            PendingIntent pIntentDone = PendingIntent.getService(this, id, intentDone, PendingIntent.FLAG_UPDATE_CURRENT);

            // PendingIntent when To Do is set to CANCEL
            Intent intentCancel = new Intent(this, ToDoChangeReminderService.class);
            intentCancel.setAction(ACTION_CANCEL);
            intentCancel.putExtra(ACTION_ID, id);
            PendingIntent pIntentCancel = PendingIntent.getService(this, id, intentCancel, PendingIntent.FLAG_UPDATE_CURRENT);

            // PendingIntent when To Do is snoozed
            Intent intentSnooze = new Intent(this, ToDoChangeReminderService.class);
            intentSnooze.setAction(ACTION_SNOOZE);
            intentSnooze.putExtra(ACTION_ID, id);
            PendingIntent pIntentSnooze = PendingIntent.getService(this, id, intentSnooze, PendingIntent.FLAG_UPDATE_CURRENT);

            // PendingIntent when To Do is pressed
            Intent intentPressed = new Intent(this, ToDoActivity.class);
            intentPressed.putExtra(ToDoActivity.NOTIFICATION_IDENTIFIER, id);
            PendingIntent pIntentPressed = PendingIntent.getActivity(this, id, intentPressed, PendingIntent.FLAG_UPDATE_CURRENT);
            Log.d(TAG, toDo.toString());

            ArrayList<NotificationCompat.Action> actionList = new ArrayList<>();
            actionList.add(new NotificationCompat.Action(R.drawable.ic_done_black_24px, "Done", pIntentDone));
            actionList.add(new NotificationCompat.Action(R.drawable.icon_cancel_24dp, "Cancel", pIntentCancel));
            if (toDo.isToDoHasSnooze()) {
                actionList.add(new NotificationCompat.Action(R.drawable.ic_alarm_24px, "Snooze", pIntentSnooze));
            }

            NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

            DateTime dateTime = new DateTime(toDo.getToDoReminderTime());
            String txtDate = dateTime.toString("dd.MM.yyyy");
            String txtHour = dateTime.toString("HH:mm");

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.icon_to_do_24px)
                    .setContentTitle(toDo.getToDoName())
                    .setContentText(txtDate + ", " + txtHour)
                    .setWhen(toDo.getToDoReminderTime())
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setContentIntent(pIntentPressed);

            for (int i = 0; i < actionList.size(); i++) {
                builder.addAction(actionList.get(i));
            }

            notificationManager.notify(id, builder.build());
        }
    }

}
