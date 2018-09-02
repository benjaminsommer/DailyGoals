package com.benjaminsommer.dailygoals.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.benjaminsommer.dailygoals.entities.CombinedDataSet;
import com.benjaminsommer.dailygoals.entities.DataSet;
import com.benjaminsommer.dailygoals.entities.StatResult;

import java.util.List;

/**
 * Created by SOMMER on 27.01.2018.
 */

@Dao
public interface DataSetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDataSets(List<DataSet> dataSets);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertDataSet(DataSet dataSet);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateDataSet(DataSet dataSet);

    @Delete
    void deleteAllDataSets(List<DataSet> dataSets);

    @Delete
    void deleteDataSet(DataSet dataSet);

    @Query("SELECT * FROM dataset")
    List<DataSet> getAllDataSets();

    @Query("SELECT * FROM dataset")
    LiveData<List<DataSet>> getAllDataSetsAsLiveData();

    @Query("SELECT * FROM dataset INNER JOIN goal ON dataset.goalKey = goal.goalRemoteId WHERE dataset.dataSetDate = :date ORDER BY goal.goalPos, goal.goalId")
    LiveData<List<CombinedDataSet>> getDataSetListByDate(String date);

    @Query("SELECT * FROM dataset WHERE dataSetDate = :date")
    List<DataSet> getDataSetsByDate(String date);

    @Query("SELECT MAX(dataSetDate) FROM dataset")
    LiveData<String> getMaxDate();

    @Query("SELECT MIN(dataSetDate) FROM dataset")
    LiveData<String> getMinDate();

    //// section for stat result queries
    @Query("SELECT null as date, COUNT(CASE dataSetValue WHEN 100 THEN 1 ELSE null END) as resultYes, COUNT(CASE dataSetValue WHEN 10 THEN 1 ELSE null END) as resultNo, COUNT(CASE dataSetValue WHEN 1 THEN 1 ELSE null END) as resultOpen, (CASE COUNT(*) WHEN 0 THEN 0 ELSE (COUNT(CASE dataSetValue WHEN 100 THEN 1 ELSE null END) / (COUNT(*) * 1.0)) END) as resultPercent, SUM(dataSetMoney) as resultMoney FROM dataset WHERE date(dataSetDate) = date('now')")
    LiveData<StatResult> getStatResultForToday();

    @Query("SELECT null as date, COUNT(CASE dataSetValue WHEN 100 THEN 1 ELSE null END) as resultYes, COUNT(CASE dataSetValue WHEN 10 THEN 1 ELSE null END) as resultNo, COUNT(CASE dataSetValue WHEN 1 THEN 1 ELSE null END) as resultOpen, (CASE COUNT(*) WHEN 0 THEN 0 ELSE (COUNT(CASE dataSetValue WHEN 100 THEN 1 ELSE null END) / (COUNT(*) * 1.0)) END) as resultPercent, SUM(dataSetMoney) as resultMoney FROM dataset WHERE STRFTIME('%Y-%W', dataSetDate) = STRFTIME('%Y-%W', 'now')")
    LiveData<StatResult> getStatResultForWeek();

    @Query("SELECT null as date, COUNT(CASE dataSetValue WHEN 100 THEN 1 ELSE null END) as resultYes, COUNT(CASE dataSetValue WHEN 10 THEN 1 ELSE null END) as resultNo, COUNT(CASE dataSetValue WHEN 1 THEN 1 ELSE null END) as resultOpen, (CASE COUNT(*) WHEN 0 THEN 0 ELSE (COUNT(CASE dataSetValue WHEN 100 THEN 1 ELSE null END) / (COUNT(*) * 1.0)) END) as resultPercent, SUM(dataSetMoney) as resultMoney FROM dataset")
    LiveData<StatResult> getStatResultForTotal();

    // section for stat result queries in DateListe overview
    @Query("SELECT dataSetDate as date, COUNT(CASE dataSetValue WHEN 100 THEN 1 ELSE null END) as resultYes, COUNT(CASE dataSetValue WHEN 10 THEN 1 ELSE null END) as resultNo, COUNT(CASE dataSetValue WHEN 1 THEN 1 ELSE null END) as resultOpen, (CASE COUNT(*) WHEN 0 THEN 0 ELSE (COUNT(CASE dataSetValue WHEN 100 THEN 1 ELSE null END) / (COUNT(*) * 1.0)) END) as resultPercent, SUM(dataSetMoney) as resultMoney FROM dataset GROUP BY dataSetDate ORDER BY dataSetDate DESC")
    LiveData<List<StatResult>> getStatResultOnDate();

    // query for daily dataset notification => get int of 'open' datasets
    @Query("SELECT COUNT(CASE dataSetValue WHEN 1 THEN 1 ELSE null END) FROM dataset WHERE dataSetDate =:date")
    int getOpenDataSetsPerDate(String date);

}
