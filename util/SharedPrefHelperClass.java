package com.benjaminsommer.dailygoals.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.benjaminsommer.dailygoals.MyApplication;
import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.ui.dataset.DataSetActivity;
import com.benjaminsommer.dailygoals.workmanager.DailyDataSetReminderWorker;
import com.benjaminsommer.dailygoals.workmanager.InitialOneTimeForDailyReminderWorker;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class SharedPrefHelperClass {

    public final static String TAG = SharedPrefHelperClass.class.getSimpleName();

    public final static String TAG_WORKER_INITIAL_START_FOR_DAILY_REMINDER = "tag_worker_daily_reminder_initial";
    public final static String TAG_WORKER_PERIODIC_FOR_DAILY_REMINDER = "tag_worker_daily_reminder_periodic";
    public final static String STRING_DATASET_CHANNEL_ID = "dataset_channel_id";
    public final static int INT_DAILY_DATASET_REMINDER_ID = 999999;
    private static Context context;
    private SharedPreferences sharedPreferences;
    private Resources resources;

//    public final static String PREF_CAT_REWARD = context.getResources().getString(R.string.PREF_CAT_REWARD);
//    public final static String PREF_KEY_AMOUNT_MONEY = context.getResources().getString(R.string.PREF_KEY_AMOUNT_MONEY);
//    public final static String PREF_KEY_CURRENCY = context.getResources().getString(R.string.PREF_KEY_CURRENCY);
//    public final static String PREF_CAT_NOTIFICATION = context.getResources().getString(R.string.PREF_CAT_NOTIFICATION);
//    public final static String PREF_KEY_NOTIFICATION_DECISION = context.getResources().getString(R.string.PREF_KEY_NOTIFICATION_DECISION);
//    public final static String PREF_KEY_NOTIFICATION_TIME = context.getResources().getString(R.string.PREF_KEY_NOTIFICATION_TIME);
//    public final static String PREF_KEY_NOTIFICATION_VIBRATE = context.getResources().getString(R.string.PREF_KEY_NOTIFICATION_VIBRATE);
//    public final static String PREF_CAT_TODO = context.getResources().getString(R.string.PREF_CAT_TODO);
//    public final static String PREF_KEY_TODO_REMINDER = context.getResources().getString(R.string.PREF_KEY_TODO_REMINDER);
//    public final static String PREF_KEY_TODO_REWARD = context.getResources().getString(R.string.PREF_KEY_TODO_REWARD);
//    public final static String PREF_KEY_TODO_REMINDER_1 = context.getResources().getString(R.string.PREF_KEY_TODO_REMINDER_1);
//    public final static String PREF_KEY_TODO_REMINDER_2 = context.getResources().getString(R.string.PREF_KEY_TODO_REMINDER_2);
//    public final static String PREF_KEY_TODO_REMINDER_3 = context.getResources().getString(R.string.PREF_KEY_TODO_REMINDER_3);
//    public final static String PREF_CAT_OTHERS = context.getResources().getString(R.string.PREF_CAT_OTHERS);
//    public final static String PREF_KEY_LANGUAGE = context.getResources().getString(R.string.PREF_KEY_LANGUAGE);
//    public final static String PREF_KEY_DELETE_DB = context.getResources().getString(R.string.PREF_KEY_DELETE_DB);
//    public final static String PREF_KEY_START_INTRODUCTION = context.getResources().getString(R.string.PREF_KEY_START_INTRODUCTION);

//    public final static String PREF_CAT_REWARD = "pref_cat_reward";
//    public final static String PREF_KEY_AMOUNT_MONEY = "pref_reward_amount";
//    public final static String PREF_KEY_CURRENCY = "pref_reward_currency";
//    public final static String PREF_CAT_NOTIFICATION = "pref_cat_notifications";
//    public final static String PREF_KEY_NOTIFICATION_DECISION = "pref_notification_decision";
//    public final static String PREF_KEY_NOTIFICATION_TIME = "pref_notification_time";
//    public final static String PREF_KEY_NOTIFICATION_VIBRATE = "pref_notification_vibrate";
//    public final static String PREF_CAT_TODO = "pref_cat_todo";
//    public final static String PREF_KEY_TODO_REMINDER = "pref_todo_reminder";
//    public final static String PREF_KEY_TODO_REWARD = "pref_todo_reward";
//    public final static String PREF_KEY_TODO_REMINDER_1 = "pref_todo_reminder1";
//    public final static String PREF_KEY_TODO_REMINDER_2 = "pref_todo_reminder2";
//    public final static String PREF_KEY_TODO_REMINDER_3 = "pref_todo_reminder3";
//    public final static String PREF_CAT_OTHERS = "pref_cat_others";
//    public final static String PREF_KEY_LANGUAGE = "pref_language";
//    public final static String PREF_KEY_DELETE_DB = "pref_delete_db";
//    public final static String PREF_KEY_START_INTRODUCTION = "pref_start_introduction";

    public SharedPrefHelperClass(SharedPreferences sharedPreferences, Resources resources) {
        this.context = MyApplication.getContext();
        this.sharedPreferences = sharedPreferences;
        this.resources = resources;
    }

    // start a ONE TIME Work Request to start the periodic Work Request at a specific time
    public void startInititalDataSetReminder() {

        Log.i(TAG, "startInitialDataSetReminder started");

        // cancel existing reminder if there is one
        cancelDailyDataSetReminder();

        // establish new ONE TIME Work Request
        String reminderTime = sharedPreferences.getString(resources.getString(R.string.PREF_KEY_NOTIFICATION_TIME), "11:00");
        String[] split = reminderTime.split(":");
        int hours = Integer.valueOf(split[0]);
        int minutes = Integer.valueOf(split[1]);
        long dateTimeInMillis = DateTime.now().withTime(hours, minutes,0, 0).getMillis();
        long nowInMillis = DateTime.now().getMillis();

        OneTimeWorkRequest initialRequestForPeriodicWorker = new OneTimeWorkRequest.Builder(InitialOneTimeForDailyReminderWorker.class)
                .setInitialDelay((dateTimeInMillis - nowInMillis), TimeUnit.MILLISECONDS)
                .addTag(TAG_WORKER_INITIAL_START_FOR_DAILY_REMINDER)
                .build();
        WorkManager.getInstance().enqueue(initialRequestForPeriodicWorker);

    }

    // start a PERIODIC Work Request for the daily reminder
    public void startPeriodicDataSetReminder() {

        Log.i(TAG, "startPeriodicDataSetReminder started");

        PeriodicWorkRequest dailyDataSetReminder = new PeriodicWorkRequest.Builder(DailyDataSetReminderWorker.class, 24, TimeUnit.HOURS)
                .addTag(TAG_WORKER_PERIODIC_FOR_DAILY_REMINDER)
                .build();
        WorkManager.getInstance().enqueue(dailyDataSetReminder);

    }

    // cancel the ONE TIME and PERIOIDIC Work Request
    public void cancelDailyDataSetReminder() {

        WorkManager.getInstance().cancelAllWorkByTag(TAG_WORKER_INITIAL_START_FOR_DAILY_REMINDER);
        WorkManager.getInstance().cancelAllWorkByTag(TAG_WORKER_PERIODIC_FOR_DAILY_REMINDER);

    }

    public void sendNotification(int openDataSets, String today) {

        // define string for context text
        String contextText = "";
        if (openDataSets == 1) {
            contextText = "Du hast heute noch 1 offenes Ziel";
        } else {
            contextText = String.format("Du hast heute noch %s offene Ziele", String.valueOf(openDataSets));        }

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        Intent intent = new Intent(context, DataSetActivity.class);
        intent.putExtra(DataSetActivity.DAY_STRING, today);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, STRING_DATASET_CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_calendar_24dp)
                .setContentTitle("DailyGoals")
                .setContentText(contextText)
                .setAutoCancel(false)
                .setOngoing(false)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reminder";
            String description = "DataSet Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(STRING_DATASET_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(INT_DAILY_DATASET_REMINDER_ID, builder.build());

    }

    @NonNull
    public String generateReminderTimeframe(String prefValue) {
        String[] strParts = prefValue.split(";");
        int type = Integer.valueOf(strParts[0]);
        long value = Long.valueOf(strParts[1]);
        String strUnit = "";
        String strValue = "";
        switch (type) {
            case 0:
                strUnit = context.getResources().getString(R.string.minutes);
                strValue = strParts[1];
                break;
            case 1:
                strUnit = context.getResources().getString(R.string.hours);
                strValue = strParts[1];
                break;
            case 2:
                strUnit = context.getResources().getString(R.string.days);
                strValue = strParts[1];
                break;
            case 3:
                strUnit = context.getResources().getString(R.string.time) + " " + context.getResources().getString(R.string.nextDay);
                LocalTime localTime = new LocalTime(value);
                strValue = localTime.toString("HH:mm");
                break;
        }
        return strValue + " " +  strUnit;
    }

    public long getSnoozeTimeframe (String preferenceKey) {

        String prefValue = sharedPreferences.getString(preferenceKey, "0;1");
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

    public String generateTodayAsString() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);
        return format.format(date);
    }

}
