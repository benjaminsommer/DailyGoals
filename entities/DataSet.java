package com.benjaminsommer.dailygoals.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * Created by DEU209213 on 30.01.2016.
 */

@Entity(tableName = "dataset",
        foreignKeys = @ForeignKey(entity = Goal.class, parentColumns = "goalRemoteId", childColumns = "goalKey"),
        indices = {@Index("goalKey")})
@IgnoreExtraProperties
public class DataSet {

    //private variables
    @PrimaryKey (autoGenerate = true)
    @Exclude private int dataSetId;
    @Exclude private String dataSetRemoteId;
    private String dataSetDate;
    private String goalKey;
    private int dataSetValue;
    private String dataSetNotice;
    private float dataSetMoney;
    @Exclude private boolean isDataSetExpanded;
    @Exclude private int dataSetSelectedNode; // from 1-4

    // Empty constructor
    public DataSet() {
        dataSetRemoteId = "";
        dataSetValue = 1;
        dataSetNotice = "";
        dataSetMoney = 0.0f;
        dataSetSelectedNode = 1;
    }

    @Exclude
    public int getDataSetId() {
        return dataSetId;
    }

    @Exclude
    public void setDataSetId(int dataSetId) {
        this.dataSetId = dataSetId;
    }

    @Exclude
    public String getDataSetRemoteId() {
        return dataSetRemoteId;
    }

    @Exclude
    public void setDataSetRemoteId(String dataSetRemoteId) {
        this.dataSetRemoteId = dataSetRemoteId;
    }

    public String getDataSetDate() {
        return dataSetDate;
    }

    public void setDataSetDate(String dataSetDate) {
        this.dataSetDate = dataSetDate;
    }

    public String getGoalKey() {
        return goalKey;
    }

    public void setGoalKey(String goalKey) {
        this.goalKey = goalKey;
    }

    public int getDataSetValue() {
        return dataSetValue;
    }

    public void setDataSetValue(int dataSetValue) {
        this.dataSetValue = dataSetValue;
    }

    public String getDataSetNotice() {
        return dataSetNotice;
    }

    public void setDataSetNotice(String dataSetNotice) {
        this.dataSetNotice = dataSetNotice;
    }

    public float getDataSetMoney() {
        return dataSetMoney;
    }

    public void setDataSetMoney(float dataSetMoney) {
        this.dataSetMoney = dataSetMoney;
    }

    @Exclude
    public boolean isDataSetExpanded() {
        return isDataSetExpanded;
    }

    @Exclude
    public void setDataSetExpanded(boolean dataSetExpanded) {
        isDataSetExpanded = dataSetExpanded;
    }

    @Exclude
    public int getDataSetSelectedNode() {
        return dataSetSelectedNode;
    }

    @Exclude
    public void setDataSetSelectedNode(int dataSetSelectedNode) {
        this.dataSetSelectedNode = dataSetSelectedNode;
    }

    @Override
    public String toString() {
        return "DataSet{" +
                "dataSetId=" + dataSetId +
                ", dataSetRemoteId='" + dataSetRemoteId + '\'' +
                ", dataSetDate='" + dataSetDate + '\'' +
                ", goalKey='" + goalKey + '\'' +
                ", dataSetValue=" + dataSetValue +
                ", dataSetNotice='" + dataSetNotice + '\'' +
                ", dataSetMoney=" + dataSetMoney +
                ", isDataSetExpanded=" + isDataSetExpanded +
                ", dataSetSelectedNode=" + dataSetSelectedNode +
                '}';
    }
}
