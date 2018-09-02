package com.benjaminsommer.dailygoals.di;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.benjaminsommer.dailygoals.database.DataSetDao;
import com.benjaminsommer.dailygoals.database.GoalsDao;
import com.benjaminsommer.dailygoals.database.GoalsDatabase;
import com.benjaminsommer.dailygoals.database.ToDoDao;
import com.benjaminsommer.dailygoals.firebase.FirebaseService;
import com.benjaminsommer.dailygoals.repository.ToDoRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by SOMMER on 18.11.2017.
 */

@Module(includes = ViewModelModule.class)
class AppModule {

    @Singleton @Provides
    Context provideContext(Application app) {
        return app.getApplicationContext();
    }

    @Provides
    Resources provideResources(Context context) {
        return context.getResources();
    }

    @Singleton @Provides
    FirebaseService provideFirebaseService() {
        return new FirebaseService();
    }

    @Singleton @Provides
    GoalsDatabase provideDb(Application app) {
        return Room.databaseBuilder(app, GoalsDatabase.class, "goal.db").build();
    }

    @Singleton @Provides
    GoalsDao provideGoalsDao(GoalsDatabase db) {
        return db.goalsDao();
    }

    @Singleton @Provides
    DataSetDao provideDataSetDao(GoalsDatabase db) {
        return db.dataSetDao();
    }

    @Singleton @Provides
    ToDoDao provideToDoDao(GoalsDatabase db) {
        return db.toDoDao();
    }

    @Provides
    Executor provideExecutor() {
        return Executors.newCachedThreadPool();
    }

    @Singleton @Provides
    SharedPreferences provideSharedPreferences(Context context) {
        //return app.getSharedPreferences("dailygoals_prefs", Context.MODE_PRIVATE);
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

}
