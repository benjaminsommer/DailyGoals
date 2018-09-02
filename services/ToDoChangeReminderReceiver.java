package com.benjaminsommer.dailygoals.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.benjaminsommer.dailygoals.repository.ToDoRepository;
import com.benjaminsommer.dailygoals.workmanager.ToDoChangeNotificationWorker;
import com.benjaminsommer.dailygoals.workmanager.ToDoFinishNotificationWorker;
import com.benjaminsommer.dailygoals.workmanager.ToDoNotificationWorker;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

/**
 * Created by SOMMER on 19.05.2018.
 */

public class ToDoChangeReminderReceiver extends BroadcastReceiver {

    public final static String ACTION_DONE = "action_done";
    public final static String ACTION_CANCEL = "action_cancel";
    public final static String ACTION_SNOOZE = "action_snooze";

    public final static String TODO_FINISH_STATUS = "todo_finish_status";
    public final static String TAG_TODO_ID = "todo_tag_id";
    public final static String TAG_TODO_SNOOZE_KEY = "todo_snooze_key";
    public final static String TAG_TODO_FINISH_WORKER = "tag_todo_finish_worker";
    public final static String TAG_TODO_CHANGE_SNOOZE_WORKER = "tag_todo_change_snooze_worker";

    public final static String ACTION_SNOOZE_INTERVAL_1 = "action_snooze_interval_1";
    public final static String ACTION_SNOOZE_INTERVAL_2 = "action_snooze_interval_2";
    public final static String ACTION_SNOOZE_INTERVAL_3 = "action_snooze_interval_3";

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        int id = intent.getIntExtra(ToDoNotificationWorker.ACTION_ID, -1);
        if (ACTION_DONE.equals(action)) {
            finishToDo(true, id);
        } else if (ACTION_CANCEL.equals(action)) {
            finishToDo(false, id);
        } else if (ACTION_SNOOZE.equals(action)) {
            snoozeToDo(id);
        } else if (ACTION_SNOOZE_INTERVAL_1.equals(action)) {
            finalizeSnooze(id, 1);
        } else if (ACTION_SNOOZE_INTERVAL_2.equals(action)) {
            finalizeSnooze(id, 2);
        } else if (ACTION_SNOOZE_INTERVAL_3.equals(action)) {
            finalizeSnooze(id, 3);
        }

    }

    private void finishToDo(boolean isDone, int id) {

        Data myData = new Data.Builder()
                .putBoolean(TODO_FINISH_STATUS, isDone)
                .putInt(TAG_TODO_ID, id)
                .build();

        OneTimeWorkRequest addToDoReminder = new OneTimeWorkRequest.Builder(ToDoFinishNotificationWorker.class)
                .addTag(TAG_TODO_FINISH_WORKER + id)
                .setInputData(myData)
                .build();
        WorkManager.getInstance().enqueue(addToDoReminder);

    }

    private void snoozeToDo(int id) {

        Data myData = new Data.Builder()
                .putBoolean(ToDoNotificationWorker.KEY_NEW_OR_SNOOZE_TYPE, true)
                .putInt(ToDoNotificationWorker.KEY_ID, id)
                .build();

        OneTimeWorkRequest addToDoSnooze = new OneTimeWorkRequest.Builder(ToDoNotificationWorker.class)
                .addTag(ToDoRepository.TODO_WORKER_TAG + id)
                .setInputData(myData)
                .build();
        WorkManager.getInstance().enqueue(addToDoSnooze);

    }

    private void finalizeSnooze(int id, int reminderKey) {

        Data myData = new Data.Builder()
                .putInt(TAG_TODO_ID, id)
                .putInt(TAG_TODO_SNOOZE_KEY, reminderKey)
                .build();

        OneTimeWorkRequest finalizeToDoSnooze = new OneTimeWorkRequest.Builder(ToDoChangeNotificationWorker.class)
                .addTag(TAG_TODO_CHANGE_SNOOZE_WORKER + id)
                .setInputData(myData)
                .build();
        WorkManager.getInstance().enqueue(finalizeToDoSnooze);

    }

}
