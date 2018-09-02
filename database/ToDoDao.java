package com.benjaminsommer.dailygoals.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.benjaminsommer.dailygoals.entities.LocalRemoteId;
import com.benjaminsommer.dailygoals.entities.StatResult;
import com.benjaminsommer.dailygoals.entities.ToDo;

import java.util.List;

import javax.inject.Singleton;

/**
 * Created by SOMMER on 31.03.2018.
 */

@Dao
public interface ToDoDao {

    static final String TODAY = "2018-04-15";

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    long addToDo(ToDo toDo);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertToDoList(List<ToDo> toDoList);

    @Update (onConflict = OnConflictStrategy.REPLACE)
    void updateToDo(ToDo toDo);

    @Delete
    void deleteToDo(ToDo toDo);

    @Delete
    void deleteAllToDos(List<ToDo> toDoList);

    @Query("SELECT * FROM todo")
    List<ToDo> getAllToDos();

    @Query("SELECT * FROM todo WHERE toDoId = :id")
    ToDo getToDo(int id);

    @Query("SELECT * FROM todo WHERE toDoState = 1 ORDER BY toDoHasReminder DESC, toDoReminderTime ASC")
    LiveData<List<ToDo>> loadOpenToDos();

    @Query("SELECT * FROM todo WHERE toDoState != 1 ORDER BY toDoState DESC, toDoTimeFinished DESC")
    LiveData<List<ToDo>> loadFinishedToDos();

    @Query("SELECT * FROM todo WHERE toDoState = 1 AND toDoHasReminder = :hasReminder ORDER BY toDoHasReminder DESC, toDoReminderTime ASC")
    List<ToDo> getOpenToDos(boolean hasReminder);

    @Query("SELECT toDoId AS localId, toDoRemoteId AS remoteId FROM todo WHERE toDoState = 1")
    List<LocalRemoteId> loadOpenToDoIdTokens();

    @Query("SELECT toDoId AS localId, toDoRemoteId AS remoteId FROM todo WHERE toDoState != 1")
    List<LocalRemoteId> loadFinishedToDoIdTokens();

    //// section for stat result queries
    @Query("SELECT null as date, COUNT(CASE toDoState WHEN 100 THEN 1 ELSE null END) as resultYes, COUNT(CASE toDoState WHEN 10 THEN 1 ELSE null END) as resultNo, COUNT(CASE toDoState WHEN 1 THEN 1 ELSE null END) as resultOpen, (CASE COUNT(*) WHEN 0 THEN 0 ELSE (COUNT(CASE toDoState WHEN 100 THEN 1 ELSE null END) / (COUNT(*) * 1.0)) END) as resultPercent, SUM(toDoMoney) as resultMoney FROM todo WHERE date(toDoReminderTime / 1000, 'unixepoch') = date('now')")
    LiveData<StatResult> getStatResultForToday();

    @Query("SELECT null as date, COUNT(CASE toDoState WHEN 100 THEN 1 ELSE null END) as resultYes, COUNT(CASE toDoState WHEN 10 THEN 1 ELSE null END) as resultNo, COUNT(CASE toDoState WHEN 1 THEN 1 ELSE null END) as resultOpen, (CASE COUNT(*) WHEN 0 THEN 0 ELSE (COUNT(CASE toDoState WHEN 100 THEN 1 ELSE null END) / (COUNT(*) * 1.0)) END) as resultPercent, SUM(toDoMoney) as resultMoney FROM todo WHERE STRFTIME('%Y-%W', toDoReminderTime / 1000, 'unixepoch') = STRFTIME('%Y-%W', 'now')")
    LiveData<StatResult> getStatResultForWeek();

    @Query("SELECT null as date, COUNT(CASE toDoState WHEN 100 THEN 1 ELSE null END) as resultYes, COUNT(CASE toDoState WHEN 10 THEN 1 ELSE null END) as resultNo, COUNT(CASE toDoState WHEN 1 THEN 1 ELSE null END) as resultOpen, (CASE COUNT(*) WHEN 0 THEN 0 ELSE (COUNT(CASE toDoState WHEN 100 THEN 1 ELSE null END) / (COUNT(*) * 1.0)) END) as resultPercent, SUM(toDoMoney) as resultMoney FROM todo")
    LiveData<StatResult> getStatResultForTotal();

}
