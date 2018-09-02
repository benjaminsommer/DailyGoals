package com.benjaminsommer.dailygoals.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.benjaminsommer.dailygoals.entities.DataSet;
import com.benjaminsommer.dailygoals.entities.Goal;
import com.benjaminsommer.dailygoals.entities.ToDo;

import javax.inject.Singleton;

/**
 * Created by SOMMER on 11.11.2017.
 */

@Database(entities = {Goal.class, DataSet.class, ToDo.class}, version = 1)
@TypeConverters(GoalTypeConverters.class)
public abstract class GoalsDatabase extends RoomDatabase {

    abstract public GoalsDao goalsDao();

    abstract public DataSetDao dataSetDao();

    abstract public ToDoDao toDoDao();

}
