package com.benjaminsommer.dailygoals.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.benjaminsommer.dailygoals.database.TimestampConverters;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * Created by DEU209213 on 09.05.2017.
 */

@Entity (tableName = "todo")
@IgnoreExtraProperties
public class ToDo {

    // private variables
    @PrimaryKey (autoGenerate = true)
    @Exclude private int toDoId;
    private String toDoRemoteId;
    private String toDoName;
    private int toDoState; // 1 = No select, 10 = No, 100 = Yes
    private boolean toDoHasReminder;
    @TypeConverters({TimestampConverters.class})
    private long toDoReminderTime;
    private boolean toDoHasSnooze;
    private boolean toDoHasReward;
    private double toDoRewardAmount;
    private float toDoMoney;
    private long toDoTimeCreated;
    private long toDoTimeFinished;

    // empty constructor
    public ToDo() {
        toDoRemoteId = "";
        toDoState = 1;
        toDoHasReminder = true;
        toDoHasSnooze = true;
        toDoHasReward = true;
        toDoReminderTime = 0;
        toDoMoney = 0.0f;
    }

    public int getToDoId() {
        return toDoId;
    }

    public void setToDoId(int toDoId) {
        this.toDoId = toDoId;
    }

    public String getToDoRemoteId() {
        return toDoRemoteId;
    }

    public void setToDoRemoteId(String toDoRemoteId) {
        this.toDoRemoteId = toDoRemoteId;
    }

    public String getToDoName() {
        return toDoName;
    }

    public void setToDoName(String toDoName) {
        this.toDoName = toDoName;
    }

    public int getToDoState() {
        return toDoState;
    }

    public void setToDoState(int toDoState) {
        this.toDoState = toDoState;
    }

    public boolean isToDoHasReminder() {
        return toDoHasReminder;
    }

    public void setToDoHasReminder(boolean toDoHasReminder) {
        this.toDoHasReminder = toDoHasReminder;
    }

    public long getToDoReminderTime() {
        return toDoReminderTime;
    }

    public void setToDoReminderTime(long toDoReminderTime) {
        this.toDoReminderTime = toDoReminderTime;
    }

    public boolean isToDoHasSnooze() {
        return toDoHasSnooze;
    }

    public void setToDoHasSnooze(boolean toDoHasSnooze) {
        this.toDoHasSnooze = toDoHasSnooze;
    }

    public boolean isToDoHasReward() {
        return toDoHasReward;
    }

    public void setToDoHasReward(boolean toDoHasReward) {
        this.toDoHasReward = toDoHasReward;
    }

    public double getToDoRewardAmount() {
        return toDoRewardAmount;
    }

    public void setToDoRewardAmount(double toDoRewardAmount) {
        this.toDoRewardAmount = toDoRewardAmount;
    }

    public float getToDoMoney() {
        return toDoMoney;
    }

    public void setToDoMoney(float toDoMoney) {
        this.toDoMoney = toDoMoney;
    }

    public long getToDoTimeCreated() {
        return toDoTimeCreated;
    }

    public void setToDoTimeCreated(long toDoTimeCreated) {
        this.toDoTimeCreated = toDoTimeCreated;
    }

    public long getToDoTimeFinished() {
        return toDoTimeFinished;
    }

    public void setToDoTimeFinished(long toDoTimeFinished) {
        this.toDoTimeFinished = toDoTimeFinished;
    }

    @Override
    public String toString() {
        return "ToDo{" +
                "toDoId=" + toDoId +
                ", toDoRemoteId='" + toDoRemoteId + '\'' +
                ", toDoName='" + toDoName + '\'' +
                ", toDoState=" + toDoState +
                ", toDoHasReminder=" + toDoHasReminder +
                ", toDoReminderTime=" + toDoReminderTime +
                ", toDoHasSnooze=" + toDoHasSnooze +
                ", toDoHasReward=" + toDoHasReward +
                ", toDoRewardAmount=" + toDoRewardAmount +
                ", toDoMoney=" + toDoMoney +
                ", toDoTimeCreated=" + toDoTimeCreated +
                ", toDoTimeFinished=" + toDoTimeFinished +
                '}';
    }
}
