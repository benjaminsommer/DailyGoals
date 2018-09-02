package com.benjaminsommer.dailygoals.util;

import com.benjaminsommer.dailygoals.entities.DataSet;
import com.benjaminsommer.dailygoals.entities.Goal;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


/**
 * Created by SOMMER on 16.02.2018.
 */

public class GoalDataSetCalcHelperClass {



    public GoalDataSetCalcHelperClass() {
    }

    public DataSet generateSingleDataSet(Goal goal, LocalDate date) {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
        DataSet dataSet = new DataSet();
        dataSet.setDataSetDate(dtf.print(date));
        dataSet.setGoalKey(goal.getGoalRemoteId());

        // check for status of today
        int goalFreq = goal.getGoalFrequency().get(0);
        int dayOfWeek = date.getDayOfWeek(); // Monday = 1; Sunday = 7;
        boolean startOfMonth = date.getDayOfMonth() == date.dayOfMonth().getMinimumValue();
        boolean midOfMonth = date.getDayOfMonth() == 15;
        boolean endOfMonth = date.getDayOfMonth() == date.dayOfMonth().getMaximumValue();

        if (goalFreq == 0) {
            return dataSet;
        } else if (goalFreq == 1 && goal.getGoalFrequency().get(dayOfWeek) == 2) {
            return dataSet;
        } else if (goalFreq == 2 && ((startOfMonth && goal.getGoalFrequency().get(8) == 2) || (midOfMonth && goal.getGoalFrequency().get(9) == 2) || (endOfMonth && goal.getGoalFrequency().get(10) == 2))) {
            return dataSet;
        } else {
            return null;
        }
    }



}
