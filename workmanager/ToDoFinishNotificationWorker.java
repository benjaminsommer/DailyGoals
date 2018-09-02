package com.benjaminsommer.dailygoals.workmanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.benjaminsommer.dailygoals.MyApplication;
import com.benjaminsommer.dailygoals.di.AppComponent;
import com.benjaminsommer.dailygoals.entities.ToDo;
import com.benjaminsommer.dailygoals.repository.ToDoRepository;
import com.benjaminsommer.dailygoals.services.ToDoChangeReminderReceiver;

import javax.inject.Inject;

import androidx.work.Worker;

/**
 * Created by SOMMER on 31.05.2018.
 */

public class ToDoFinishNotificationWorker extends Worker {

    public static final String TAG = ToDoFinishNotificationWorker.class.getSimpleName();

    @Inject
    ToDoRepository toDoRepository;

    @NonNull
    @Override
    public Worker.Result doWork() {

        // depedency injection
        Context context = MyApplication.getContext().getApplicationContext();
        if (context instanceof MyApplication) {
            AppComponent daggerAppComponent = MyApplication.getAppComponent();
            daggerAppComponent.inject(this);
        }

        int id = getInputData().getInt(ToDoChangeReminderReceiver.TAG_TODO_ID, -1);
        boolean isDone = getInputData().getBoolean(ToDoChangeReminderReceiver.TODO_FINISH_STATUS, true);

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

            NotificationManagerCompat.from(MyApplication.getContext()).cancel(id);

            return Result.SUCCESS;

        } else {

            return Result.FAILURE;

        }

    }



}
