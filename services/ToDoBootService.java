package com.benjaminsommer.dailygoals.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.benjaminsommer.dailygoals.database.ToDoDao;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * Created by SOMMER on 10.05.2018.
 */

public class ToDoBootService extends IntentService {

    @Inject
    ToDoDao toDoDao;

    public ToDoBootService() {
        super(ToDoBootService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

//        List<ToDo> liveList = toDoDao.getOpenToDos(true);
//        for (int x = 0; x < liveList.size(); x++) {
//            if (liveList.get(x).isToDoHasReminder()) {
//                int id = liveList.get(x).getToDoId();
//                long reminderTime = liveList.get(x).getToDoReminderTime();
//                ToDoReminderReceiver toDoReminderReceiver = new ToDoReminderReceiver();
//                toDoReminderReceiver.setAlarm(MyApplication.getContext(), id, reminderTime);
//            }
//        }

    }
}
