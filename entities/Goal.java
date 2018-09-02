package com.benjaminsommer.dailygoals.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.util.Log;

import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.database.GoalTypeConverters;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by DEU209213 on 07.05.2016.
 */

@Entity(indices = {@Index(value = "goalRemoteId", unique = true)})
@IgnoreExtraProperties
public class Goal {

    // private variables
    @PrimaryKey (autoGenerate = true)
    @Exclude private int goalId;
    private String goalRemoteId;
    private int goalPos;
    private String goalName;
    private int goalCategory;
    private String goalDescription;
    private int goalColor;
    @TypeConverters(GoalTypeConverters.class) private List<Integer> goalFrequency;
    private boolean goalRewardType;
    private double goalReward;
    private int activated;
    @Exclude private boolean goalExpanded;

    // constructor
    public Goal() {
        goalRemoteId = "";
        goalPos = 0;
        goalName = "";
        goalCategory = 15;
        goalDescription = "";
        goalColor = R.color.cat_Black;
        /*
        * List goalFrequency has 11 items
        * goalFrequency[0] = selected type: 0=Daily, 1=Weekly, 2=Monthly
        * goalFrequency[1-10] = Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday; first of month, middle of month, last of month
        * goalFrequqncy[1-10]: 1 = not selected; 2 = selected
        * */
        goalFrequency = Arrays.asList(0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2);
        goalRewardType = false; // false = standard, true = individual
        activated = 1; // 0=No, 1=Yes
        goalExpanded = false;
    }

    // getter and setter
    @Exclude
    public int getGoalId() {
        return goalId;
    }

    @Exclude
    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }

    public String getGoalRemoteId() {
        return goalRemoteId;
    }

    public void setGoalRemoteId(String goalRemoteId) {
        this.goalRemoteId = goalRemoteId;
    }

    public int getGoalPos() {
        return goalPos;
    }

    public void setGoalPos(int goalPos) {
        this.goalPos = goalPos;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public int getGoalCategory() {
        return goalCategory;
    }

    public void setGoalCategory(int goalCategory) {
        this.goalCategory = goalCategory;
    }

    public String getGoalDescription() {
        return goalDescription;
    }

    public void setGoalDescription(String goalDescription) {
        this.goalDescription = goalDescription;
    }

    public int getGoalColor() {
        return goalColor;
    }

    public void setGoalColor(int goalColor) {
        this.goalColor = goalColor;
    }

    public List<Integer> getGoalFrequency() {
        return goalFrequency;
    }

    public void setGoalFrequency(List<Integer> goalFrequency) {
        this.goalFrequency = goalFrequency;
    }

    //    public LinkedList<Integer> getGoalFrequencyCodeAsList() {
//        LinkedList<Integer> stack = new LinkedList<>();
//        long number = goalFrequencyCode;
//        while (number > 0) {
//            long longTempMod = number % 10;
//            int intTempMod = (int) longTempMod;
//            stack.push(intTempMod);
//            number = number / 10;
//        }
//        return stack;
//    }
//
//    public void setGoalFrequencyCodeFromList(LinkedList<Integer> stack) {
//        double number = 0;
//        for (int j = 0; j < stack.size(); j++) {
//            Log.d(String.valueOf(j), String.valueOf(stack.get(j)));
//        }
//        if (stack.size() <= 1) {
//            goalFrequencyCode = 1111111111;
//        } else {
//            for (int i = 0; i < stack.size(); i++) {
//                Log.d(String.valueOf(stack.get(i)), String.valueOf(number));
//                number = number + (stack.get(i) * Math.pow(10, (stack.size() - 1 - i)));
//            }
//            Log.d("Sent from Goal - number", String.valueOf(number));
//            goalFrequencyCode = (long) number;
//            Log.d("Sent from Goal - long", String.valueOf(goalFrequencyCode));
//        }
//    }


    public boolean getGoalRewardType() {
        return goalRewardType;
    }

    public void setGoalRewardType(boolean goalRewardType) {
        this.goalRewardType = goalRewardType;
    }

    public double getGoalReward() {
        return goalReward;
    }

    public void setGoalReward(double goalReward) {
        this.goalReward = goalReward;
    }

    public int getActivated() {
        return activated;
    }

    public void setActivated(int activated) {
        this.activated = activated;
    }

    @Exclude
    public boolean isGoalExpanded() {
        return goalExpanded;
    }

    @Exclude
    public void setGoalExpanded(boolean goalExpanded) {
        this.goalExpanded = goalExpanded;
    }

    @Override
    public String toString() {
        return "Goal{" +
                "goalId=" + goalId +
                ", goalRemoteId='" + goalRemoteId + '\'' +
                ", goalPos=" + goalPos +
                ", goalName='" + goalName + '\'' +
                ", goalCategory=" + goalCategory +
                ", goalDescription='" + goalDescription + '\'' +
                ", goalColor=" + goalColor +
                ", goalFrequency=" + goalFrequency +
                ", goalRewardType=" + goalRewardType +
                ", goalReward=" + goalReward +
                ", activated=" + activated +
                ", expanded=" + goalExpanded +
                '}';
    }
}
