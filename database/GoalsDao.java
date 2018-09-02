package com.benjaminsommer.dailygoals.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.benjaminsommer.dailygoals.entities.Goal;
import com.benjaminsommer.dailygoals.entities.LocalRemoteId;

import java.util.List;

import javax.inject.Singleton;

/**
 * Created by SOMMER on 11.11.2017.
 */

@Dao
public interface GoalsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertGoals(List<Goal> goals);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(Goal goal);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addGoal(Goal goal);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAllGoals(List<Goal> goalsList);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateGoal(Goal goal);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void setExpandedToFalse(List<Goal> finalList);

    @Delete
    void deleteGoals(List<Goal> goals);

    @Query("SELECT COUNT(*) FROM goal")
    int countGoals();

    @Query("SELECT COUNT(*) FROM goal")
    LiveData<Integer> countGoalsAsLiveData();

    @Query("SELECT * FROM goal WHERE goalId = :localId")
    Goal loadAsGoal(int localId);

    @Query("SELECT * FROM goal WHERE activated = 1")
    LiveData<List<Goal>> loadAllGoals();

    @Query("SELECT * FROM goal WHERE activated = 1")
    List<Goal> loadAllActiveGoals();

    @Query("SELECT * FROM goal")
    List<Goal> loadActiveAndNonActiveGoals();

    @Query("SELECT goalRemoteId AS remoteId, goalId AS localId FROM goal WHERE goalRemoteId IN (:remote_ids)")
    public List<LocalRemoteId> receiveLocalIdFromRemoteId(List<String> remote_ids);

//    @Query("SELECT _id FROM goal WHERE _remote_id = :remote_id LIMIT 1")
//    int receiveLocalIdFromRemoteId(String remote_id);

}
