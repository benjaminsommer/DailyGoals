package com.benjaminsommer.dailygoals.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.benjaminsommer.dailygoals.MyApplication;
import com.benjaminsommer.dailygoals.database.ToDoDao;
import com.benjaminsommer.dailygoals.di.AppExecutors;
import com.benjaminsommer.dailygoals.entities.LocalRemoteId;
import com.benjaminsommer.dailygoals.entities.StatResult;
import com.benjaminsommer.dailygoals.entities.ToDo;
import com.benjaminsommer.dailygoals.firebase.ApiResponse;
import com.benjaminsommer.dailygoals.firebase.FirebaseService;
import com.benjaminsommer.dailygoals.objects.Resource;
import com.benjaminsommer.dailygoals.services.ToDoReminderReceiver;
import com.benjaminsommer.dailygoals.util.AbsentLiveData;
import com.benjaminsommer.dailygoals.workmanager.ToDoNotificationWorker;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

/**
 * Created by SOMMER on 31.03.2018.
 */

@Singleton
public class ToDoRepository {

    private final static String TAG = ToDoRepository.class.getSimpleName();

    public final static String TODO_WORKER_TAG = "dailygoals.todo.worker.tag";

    private final ToDoDao toDoDao;
    private final AppExecutors appExecutors;
    private final FirebaseService firebaseService;
    private final Executor executor;
    private FirebaseAuth mAuth;

    @Inject
    public ToDoRepository(ToDoDao toDoDao, AppExecutors appExecutors, FirebaseService firebaseService, Executor executor) {
        this.toDoDao = toDoDao;
        this.appExecutors = appExecutors;
        this.firebaseService = firebaseService;
        this.executor = executor;
        mAuth = FirebaseAuth.getInstance();
    }

    public LiveData<Resource<List<ToDo>>> getOpenToDos() {
        return new NetworkBoundResource<List<ToDo>, List<ToDo>>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull List<ToDo> item) {
                List<LocalRemoteId> localList = toDoDao.loadOpenToDoIdTokens();
                Map<String, Integer> ids = new HashMap<>();
                for (LocalRemoteId id : localList) ids.put(id.getRemoteId(), id.getLocalId());
                List<ToDo> list = new ArrayList<>();
                for (int x = 0; x < item.size(); x++) {
                    list.add(item.get(x));
                    String remoteId = list.get(x).getToDoRemoteId();
                    if (ids.containsKey(remoteId)) {
                        list.get(x).setToDoId(ids.get(remoteId));
                    }
                }
                toDoDao.insertToDoList(list);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<ToDo> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<ToDo>> loadFromDb() {
                return toDoDao.loadOpenToDos();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ToDo>>> createCall() {
                return firebaseService.getOpenToDos();
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<ToDo>>> getFinishedToDos() {
        return new NetworkBoundResource<List<ToDo>, List<ToDo>>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull List<ToDo> item) {
                List<LocalRemoteId> localList = toDoDao.loadFinishedToDoIdTokens();
                Map<String, Integer> ids = new HashMap<>();
                for (LocalRemoteId id : localList) ids.put(id.getRemoteId(), id.getLocalId());
                List<ToDo> list = new ArrayList<>();
                for (int x = 0; x < item.size(); x++) {
                    list.add(item.get(x));
                    String remoteId = list.get(x).getToDoRemoteId();
                    if (ids.containsKey(remoteId)) {
                        list.get(x).setToDoId(ids.get(remoteId));
                    }
                }
                toDoDao.insertToDoList(list);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<ToDo> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<ToDo>> loadFromDb() {
                return toDoDao.loadFinishedToDos();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ToDo>>> createCall() {
                return firebaseService.getFinishedToDos();
            }
        }.asLiveData();
    }

    public ToDo getSpecificToDo (int id) {
        return toDoDao.getToDo(id);
    }

    public void addToDo(ToDo toDo) {
        new addToDoAsync(toDoDao, firebaseService, mAuth).execute(toDo);
    }

    public void updateToDo(ToDo toDo) {
        new updateToDoAsync(toDoDao, firebaseService).execute(toDo);
    }

    public void deleteToDo(ToDo toDo) {
        new deleteToDoAsync(toDoDao, firebaseService).execute(toDo);
    }

    public void cancelNotification(ToDo toDo) {
        deleteToDoReminder(toDo);
    }

    private static class addToDoAsync extends AsyncTask<ToDo, Void, Void> {

        ToDoDao toDoDao;
        FirebaseService firebaseService;
        FirebaseAuth mAuth;


        public addToDoAsync(ToDoDao toDoDao, FirebaseService firebaseService, FirebaseAuth firebaseAuth) {
            this.toDoDao = toDoDao;
            this.firebaseService = firebaseService;
            this.mAuth = firebaseAuth;
        }

        @Override
        protected Void doInBackground(ToDo... toDos) {
            ToDo toDo = toDos[0];
            long toDoId = toDoDao.addToDo(toDo);
            toDo.setToDoId((int) toDoId);
            if (mAuth.getCurrentUser() != null) {
                // insert in Firebase and update key as soon as added
                String toDoRemoteId = firebaseService.addToDo(toDo);
                // add remote id to goal
                toDo.setToDoRemoteId(toDoRemoteId);
            } else {
                toDo.setToDoRemoteId("LOCAL" + String.valueOf(toDoId));
            }
            toDoDao.updateToDo(toDo);

            if (toDo.isToDoHasReminder()) {

                addToDoReminder(toDo);

            }

            return null;
        }
    }

    private static class updateToDoAsync extends AsyncTask<ToDo, Void, Void> {

        ToDoDao toDoDao;
        FirebaseService firebaseService;


        public updateToDoAsync(ToDoDao toDoDao, FirebaseService firebaseService) {
            this.toDoDao = toDoDao;
            this.firebaseService = firebaseService;
        }

        @Override
        protected Void doInBackground(ToDo... toDos) {
            ToDo toDo = toDos[0];
            toDoDao.updateToDo(toDo);
            firebaseService.updateToDo(toDo);

            ToDo testToDo = toDoDao.getToDo(toDo.getToDoId());
            List<ToDo> list = toDoDao.getOpenToDos(true);
            Log.d(TAG, testToDo.toString());
            Log.d(TAG, "List Open To Dos: " + list.toString());


            if (toDo.getToDoState() != 1) {

                deleteToDoReminder(toDo);

            } else {
                if (toDo.isToDoHasReminder()) {

                    addToDoReminder(toDo);

                }
            }

            return null;
        }
    }

    private static class deleteToDoAsync extends AsyncTask<ToDo, Void, Void> {

        ToDoDao toDoDao;
        FirebaseService firebaseService;


        public deleteToDoAsync(ToDoDao toDoDao, FirebaseService firebaseService) {
            this.toDoDao = toDoDao;
            this.firebaseService = firebaseService;
        }

        @Override
        protected Void doInBackground(ToDo... toDos) {
            toDoDao.deleteToDo(toDos[0]);
            firebaseService.deleteToDo(toDos[0]);

            deleteToDoReminder(toDos[0]);

            return null;
        }
    }

    public Task<Void> deleteAllLocalToDos() {
        return Tasks.call(executor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                List<ToDo> list = toDoDao.getAllToDos();
                toDoDao.deleteAllToDos(list);
                return null;
            }
        });
    }

    public Task<List<ToDo>> getAllRemoteToDos() {
        return firebaseService.getAllToDos();
    }

    public Task<Void> addToDoList(final List<ToDo> list) {
        return Tasks.call(executor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                toDoDao.insertToDoList(list);
                return null;
            }
        });
    }

    //// START stat result section

    public LiveData<StatResult> getToDoStatResultsForToday(byte period) {
        LiveData<StatResult> liveData;
        switch (period) {
            case StatResult.TODAY:
                liveData = toDoDao.getStatResultForToday();
                break;
            case StatResult.WEEK:
                liveData = toDoDao.getStatResultForWeek();
                break;
            case StatResult.TOTAL:
                liveData = toDoDao.getStatResultForTotal();
                break;
            default:
                liveData = AbsentLiveData.create();
                break;
        }
        return liveData;
    }

    //// END stat result section

    public static void addToDoReminder(ToDo toDo) {

        //// START WORK MANAGER
        long delay = toDo.getToDoReminderTime() - System.currentTimeMillis();
        Data myData = new Data.Builder()
                .putInt(ToDoNotificationWorker.KEY_ID, toDo.getToDoId())
                .putBoolean(ToDoNotificationWorker.KEY_NEW_OR_SNOOZE_TYPE, false) // false == new To Do
                .build();

        OneTimeWorkRequest addToDoReminder = new OneTimeWorkRequest.Builder(ToDoNotificationWorker.class)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag(TODO_WORKER_TAG + toDo.getToDoId())
                .setInputData(myData)
                .build();
        WorkManager.getInstance().enqueue(addToDoReminder);
        //// END WORKM ANAGER

//        Context context = MyApplication.getContext();
//        ToDoReminderReceiver toDoReminderReceiver = new ToDoReminderReceiver();
//        toDoReminderReceiver.setAlarm(context, toDo.getToDoId(), toDo.getToDoReminderTime());

    }

    public static void deleteToDoReminder(ToDo toDo) {

        //// START WORK MANAGER
        WorkManager.getInstance().cancelAllWorkByTag(TODO_WORKER_TAG + toDo.getToDoId());
        //// END WORK MANAGER

//        NotificationManagerCompat.from(MyApplication.getContext()).cancel(toDo.getToDoId());


    }


}
