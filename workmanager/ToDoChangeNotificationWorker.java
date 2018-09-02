package com.benjaminsommer.dailygoals.workmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;

import com.benjaminsommer.dailygoals.MyApplication;
import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.di.AppComponent;
import com.benjaminsommer.dailygoals.entities.ToDo;
import com.benjaminsommer.dailygoals.repository.ToDoRepository;
import com.benjaminsommer.dailygoals.services.ToDoChangeReminderReceiver;
import com.benjaminsommer.dailygoals.util.SharedPrefHelperClass;

import org.joda.time.DateTime;

import javax.inject.Inject;

import androidx.work.Worker;

/**
 * Created by SOMMER on 31.05.2018.
 */

public class ToDoChangeNotificationWorker extends Worker {

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

        int id = getInputData().getInt(ToDoChangeReminderReceiver.TAG_TODO_ID, -1);
        int snoozeKey = getInputData().getInt(ToDoChangeReminderReceiver.TAG_TODO_SNOOZE_KEY, 1);

        SharedPrefHelperClass sphc = new SharedPrefHelperClass(sharedPreferences, resources);
        long timeframe = sphc.getSnoozeTimeframe(resources.getString(R.string.PREF_KEY_TODO_REMINDER_FLEX, String.valueOf(snoozeKey)));

        if (id != -1) {
            NotificationManagerCompat.from(context).cancel(id);

            ToDo toDo = toDoRepository.getSpecificToDo(id);
            long newReminderTime = System.currentTimeMillis() + timeframe;
            toDo.setToDoReminderTime(newReminderTime);
            toDoRepository.updateToDo(toDo);

            return Result.SUCCESS;

        } else {

            return Result.FAILURE;

        }

    }

}
