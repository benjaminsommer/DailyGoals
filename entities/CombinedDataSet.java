package com.benjaminsommer.dailygoals.entities;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.TypeConverters;
import android.util.Log;

import com.benjaminsommer.dailygoals.database.GoalTypeConverters;

import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by DEU209213 on 04.11.2016.
 */
public class CombinedDataSet {

    @Embedded
    private DataSet dataSet;
    @Embedded
    private Goal goal;

/*    // private variables
    private int dataSetId;
    private String dataSetRemoteId;
    private String datasetDate;
    private String goalKey;
    private int datasetValue;
    private String datasetNotice;
    private float datasetMoney;
    private boolean expanded;

    private int goalPos;
    private String goalName;
    private int goalCategory;
    private String goalDescription;
    private int goalColor;
    private List<Integer> goalFrequency;
    private double goalReward;*/

    public CombinedDataSet() {
        this.dataSet = new DataSet();
        this.goal = new Goal();
    }

    @Ignore
    public CombinedDataSet(DataSet dataSet, Goal goal) {
        this.dataSet = dataSet;
        this.goal = goal;
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }
}
