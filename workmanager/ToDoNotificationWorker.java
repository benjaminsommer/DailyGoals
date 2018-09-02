package com.benjaminsommer.dailygoals.workmanager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.benjaminsommer.dailygoals.MyApplication;
import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.di.AppComponent;
import com.benjaminsommer.dailygoals.entities.ToDo;
import com.benjaminsommer.dailygoals.repository.ToDoRepository;
import com.benjaminsommer.dailygoals.ui.todo.ToDoActivity;
import com.benjaminsommer.dailygoals.services.ToDoChangeReminderReceiver;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.util.ArrayList;

import javax.inject.Inject;

import androidx.work.Worker;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by SOMMER on 19.05.2018.
 */

public class ToDoNotificationWorker extends Worker {

    private static final String TAG = ToDoNotificationWorker.class.getSimpleName();

    public final static String ACTION_DONE = "action_done";
    public final static String ACTION_CANCEL = "action_cancel";
    public final static String ACTION_SNOOZE = "action_snooze";

    public final static String ACTION_SNOOZE_INTERVAL_1 = "action_snooze_interval_1";
    public final static String ACTION_SNOOZE_INTERVAL_2 = "action_snooze_interval_2";
    public final static String ACTION_SNOOZE_INTERVAL_3 = "action_snooze_interval_3";

    public final static String CHANNEL_ID = "to_do_channel_id";

    public final static String ACTION_ID = "action_id";
    public final static String KEY_ID = "id";
    public final static String KEY_NEW_OR_SNOOZE_TYPE = "is_new_or_snooze";

    @Inject
    ToDoRepository toDoRepository;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    Resources resources;

    @NonNull
    @Override
    public Worker.Result doWork() {

        // depedency injection
        Context context = MyApplication.getContext();
        if (context instanceof MyApplication) {
            AppComponent daggerAppComponent = MyApplication.getAppComponent();
            daggerAppComponent.inject(this);
        }

        boolean isNewToDo = getInputData().getBoolean(KEY_NEW_OR_SNOOZE_TYPE, false); // false = new, true = snooze)
        int id = getInputData().getInt(KEY_ID, -1);
        Log.d(TAG, "id: " + String.valueOf(id));
        Log.d(TAG, "isNewToDo: " + String.valueOf(isNewToDo));

        if (id != -1) {
            ToDo toDo = toDoRepository.getSpecificToDo(id);
            Log.d(TAG, toDo.toString());

            // PendingIntent when To Do is pressed
            Intent intentPressed = new Intent(context, ToDoActivity.class);
            intentPressed.putExtra(ToDoActivity.NOTIFICATION_IDENTIFIER, id);
            PendingIntent pIntentPressed = PendingIntent.getActivity(context, id, intentPressed, PendingIntent.FLAG_UPDATE_CURRENT);
            Log.d(TAG, toDo.toString());

            ArrayList<NotificationCompat.Action> actionList = new ArrayList<>();

            if (!isNewToDo) {

                // PendingIntent when To Do is set to DONE
                Intent intentDone = new Intent(context, ToDoChangeReminderReceiver.class);
                intentDone.setAction(ACTION_DONE);
                intentDone.putExtra(ACTION_ID, id);
                PendingIntent pIntentDone = PendingIntent.getBroadcast(context, id, intentDone, PendingIntent.FLAG_UPDATE_CURRENT);

                // PendingIntent when To Do is set to CANCEL
                Intent intentCancel = new Intent(context, ToDoChangeReminderReceiver.class);
                intentCancel.setAction(ACTION_CANCEL);
                intentCancel.putExtra(ACTION_ID, id);
                PendingIntent pIntentCancel = PendingIntent.getBroadcast(context, id, intentCancel, PendingIntent.FLAG_UPDATE_CURRENT);

                // PendingIntent when To Do is snoozed
                Intent intentSnooze = new Intent(context, ToDoChangeReminderReceiver.class);
                intentSnooze.setAction(ACTION_SNOOZE);
                intentSnooze.putExtra(ACTION_ID, id);
                PendingIntent pIntentSnooze = PendingIntent.getBroadcast(context, id, intentSnooze, PendingIntent.FLAG_UPDATE_CURRENT);

                actionList.add(new NotificationCompat.Action(R.drawable.ic_done_black_24px, "Done", pIntentDone));
                actionList.add(new NotificationCompat.Action(R.drawable.icon_cancel_24dp, "Cancel", pIntentCancel));
                if (toDo.isToDoHasSnooze()) {
                    actionList.add(new NotificationCompat.Action(R.drawable.ic_alarm_24px, "Snooze", pIntentSnooze));
                }

            } else {

                // PendingIntents when To Do is set to Reminder type 1
                Intent intentInterval1 = new Intent(context, ToDoChangeReminderReceiver.class);
                intentInterval1.setAction(ACTION_SNOOZE_INTERVAL_1);
                intentInterval1.putExtra(ACTION_ID, id);
                PendingIntent pIntentInterval1 = PendingIntent.getBroadcast(context, id, intentInterval1, PendingIntent.FLAG_UPDATE_CURRENT);

                // PendingIntents when To Do is set to Reminder type 2
                Intent intentInterval2 = new Intent(context, ToDoChangeReminderReceiver.class);
                intentInterval2.setAction(ACTION_SNOOZE_INTERVAL_2);
                intentInterval2.putExtra(ACTION_ID, id);
                PendingIntent pIntentInterval2 = PendingIntent.getBroadcast(context, id, intentInterval2, PendingIntent.FLAG_UPDATE_CURRENT);

                // PendingIntents when To Do is set to Reminder type 3
                Intent intentInterval3 = new Intent(context, ToDoChangeReminderReceiver.class);
                intentInterval3.setAction(ACTION_SNOOZE_INTERVAL_3);
                intentInterval3.putExtra(ACTION_ID, id);
                PendingIntent pIntentInterval3 = PendingIntent.getBroadcast(context, id, intentInterval3, PendingIntent.FLAG_UPDATE_CURRENT);

                actionList.add(new NotificationCompat.Action(R.drawable.ic_alarm_24px, getSnoozeLabel(resources.getString(R.string.PREF_KEY_TODO_REMINDER_1)), pIntentInterval1));
                actionList.add(new NotificationCompat.Action(R.drawable.ic_alarm_24px, getSnoozeLabel(resources.getString(R.string.PREF_KEY_TODO_REMINDER_2)), pIntentInterval2));
                actionList.add(new NotificationCompat.Action(R.drawable.ic_alarm_24px, getSnoozeLabel(resources.getString(R.string.PREF_KEY_TODO_REMINDER_3)), pIntentInterval3));

            }

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

            DateTime dateTime = new DateTime(toDo.getToDoReminderTime());
            String txtDate = dateTime.toString("dd.MM.yyyy");
            String txtHour = dateTime.toString("HH:mm");

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.icon_to_do_24px)
                    .setContentTitle(toDo.getToDoName())
                    .setContentText(txtDate + ", " + txtHour)
                    .setWhen(toDo.getToDoReminderTime())
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setCategory(NotificationCompat.CATEGORY_REMINDER)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pIntentPressed);

            for (int i = 0; i < actionList.size(); i++) {
                builder.addAction(actionList.get(i));
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "Reminder";
                String description = "To Do Reminder";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                notificationManager.createNotificationChannel(channel);
            }


            notificationManager.notify(id, builder.build());

            return Result.SUCCESS;

        } else {

            return Result.FAILURE;

        }

    }

    private String getSnoozeLabel (String preferenceKey) {

        String prefValue = sharedPreferences.getString(preferenceKey, "0;1");
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
