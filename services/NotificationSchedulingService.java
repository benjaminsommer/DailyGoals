package com.benjaminsommer.dailygoals.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.benjaminsommer.dailygoals.Database;
import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.entities.SummarizedDataSet;
import com.benjaminsommer.dailygoals.ui.dataset.DataSetActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by DEU209213 on 21.01.2017.
 */
public class NotificationSchedulingService extends IntentService {

    public NotificationSchedulingService() {
        super("ScheduleService");
    }

    public static final String TAG = "Schedule Notification";
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;
    private Database db = new Database(this);
    private SharedPreferences prefs;

    protected void onHandleIntent(Intent intent) {

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notificationDecision = prefs.getBoolean("preferenceFragment_notificationDecision", false);
        String today = generateTodayAsString();
        SummarizedDataSet dataSet = db.getSummarizedDataSetOfSpecificDay(today);
        int openGoals = dataSet.getValuesOpen();
        if (openGoals > 1 && notificationDecision) {
            sendNotification(openGoals, today);
        }

        NotificationAlarmReceiver.completeWakefulIntent(intent);

    }

    private void sendNotification(int openGoals, String today) {

        // define string for context text
        String contextText = "";
        if (openGoals == 1) {
            contextText = "Du hast heute noch 1 offenes Ziel";
        } else {
            contextText = "Du hast heute noch " + String.valueOf(openGoals) + " offene Ziele";
        }

        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, DataSetActivity.class);
        intent.putExtra(DataSetActivity.DAY_STRING, today);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_calendar_24dp)
                .setContentTitle("DailyGoals")
                .setContentText(contextText)
                .setVibrate(new long[]{1000, 300, 1000, 300})
                .setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public String generateTodayAsString() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String today = format.format(date);
        return today;
    }

}
