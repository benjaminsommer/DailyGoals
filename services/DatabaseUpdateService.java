package com.benjaminsommer.dailygoals.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.benjaminsommer.dailygoals.database.DataSetDao;
import com.benjaminsommer.dailygoals.database.GoalsDao;
import com.benjaminsommer.dailygoals.entities.DataSet;
import com.benjaminsommer.dailygoals.entities.Goal;
import com.benjaminsommer.dailygoals.firebase.FirebaseService;
import com.benjaminsommer.dailygoals.util.GoalDataSetCalcHelperClass;
import com.google.firebase.auth.FirebaseAuth;

import org.joda.time.LocalDate;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * Created by DEU209213 on 12.03.2017.
 */
public class DatabaseUpdateService extends IntentService {

    private static final String TAG = DatabaseUpdateService.class.getSimpleName();
    public static final String BOOL_DELETE = "IsDeleteAllGoalsSelected";

    // declaration
    private FirebaseAuth mAuth;
    private GoalDataSetCalcHelperClass calcHelperClass;
    
    @Inject
    GoalsDao goalsDao;
    @Inject
    DataSetDao dataSetDao;
    @Inject
    FirebaseService firebaseService;
    @Inject
    SharedPreferences sharedPrefs;
    @Inject
    Executor executor;

    @Inject
    public DatabaseUpdateService() {
        super("UpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // injection and instantiation
        AndroidInjection.inject(this);
        mAuth = FirebaseAuth.getInstance();
        calcHelperClass = new GoalDataSetCalcHelperClass();
        LocalDate today = LocalDate.now();

        // check bundle extras
        Bundle extras = intent.getExtras();
        boolean deletion = false;
        if (extras != null) {
            deletion = extras.getBoolean(BOOL_DELETE, false);
        }

        //// START DELETION PROCEDURE
        if (deletion) {
            List<DataSet> datasets = dataSetDao.getAllDataSets();
            dataSetDao.deleteAllDataSets(datasets);
            List<Goal> goals = goalsDao.loadActiveAndNonActiveGoals();
            goalsDao.deleteGoals(goals);
            if (mAuth.getCurrentUser() != null) {
                firebaseService.deleteAllFirestoreGoals(50, executor);
                // TODO: 29.01.2018 add deletion for datasets 
            }
        }
        //// END DELETION PROCEDURE

//        // get SharedPreferences information (reward value and currency)
//        prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        String strRewardValue = prefs.getString("pref_amount", "0.00");
//        float rewardValue = Float.valueOf(strRewardValue);
//        String strRewardType = prefs.getString("pref_goalsReward", "1");
//        int rewardType = Integer.valueOf(strRewardType);

        //// START NEW GOAL AND NEW DATABASE PROCEDURE
        boolean isDbEmpty = (goalsDao.countGoals() <= 0);
        if (isDbEmpty) {
            insertNewGoalAndDataSet(today);
        }
        //// END NEW GOAL AND NEW DATABASE PROCEDURE


//        // Check if an "ACTIVE" goal is existing in the database - if not, add one
//        if (!db.doesActiveGoalExist()) {
//            Goal newGoal = new Goal();
//            db.addGoal(newGoal);
//
//
//
////            //// FIRESTORE
////            colRefGoals.document()
////                    .set(newGoal)
////                    .addOnSuccessListener(new OnSuccessListener<Void>() {
////                        @Override
////                        public void onSuccess(Void aVoid) {
////                            Log.d(TAG, "DocumentSnapshot successfully written");
////                        }
////                    })
////                    .addOnFailureListener(new OnFailureListener() {
////                        @Override
////                        public void onFailure(@NonNull Exception e) {
////                            Log.w(TAG, "Error writing document", e);
////                        }
////                    });
////            //// FIRESTORE
//
//        }
//
//        // add the required days in the DailyGoals database
//        // definition of date format
//        Calendar myCal1 = Calendar.getInstance();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//
//        // update of DailyGoals database or add new input
//        // if table is empty
//        ArrayList<Goal> goalsList = db.getCompleteGoalsTable(true);
//
//        //// FIRESTORE
//        colRefGoals.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
//                        ArrayList<String> goalkeyList = new ArrayList<String>();
//                        ArrayList<Goal> goalArrayList = new ArrayList<Goal>();
//                        goalkeyList.add(documentSnapshot.getId());
//                        goalArrayList.add(documentSnapshot.toObject(Goal.class));
//                        Log.d(TAG, documentSnapshot.getId() + "=>" + documentSnapshot.getData());
//                    }
//                } else {
//                    Log.d(TAG, "Error getting documents: ", task.getException());
//                }
//            }
//        });
//        //// FIRESTORE
//
//        // check if Dataset table is empty - if yes, fill with valid goals for that day
//        if (db.isDataSetTableEmpty()) {
//            String date = sdf.format(myCal1.getTime());
//            //ArrayList<Goal> goalsListForToday = validateGoalsPerDay(goalsList, myCal1);
//            if (goalsList != null) {
//                for (int x = 0; x < goalsList.size(); x++) {
//                    // add to DataSet table
//                    boolean blnAdding = validateGoalPerDay(goalsList.get(x), myCal1);
//                    if (blnAdding == true) {
//                        DataSet newDataSet = new DataSet();
//                        newDataSet.setDate(date);
//                        newDataSet.setGoalID(goalsList.get(x).getId());
//                        db.addDataSet(newDataSet);
//                    }
//                    // add to Money table
//                    Money moneyToday = new Money();
//                    moneyToday.setDate(date);
//                    moneyToday.setSettingValue(rewardValue);
//                    moneyToday.setSettingType(rewardType);
//                    db.addMoneyEntry(moneyToday);
//                }
//            }
//        } else {
//            // Get date today
//            myCal1.add(Calendar.DATE, 0);
//            int year = myCal1.get(Calendar.YEAR);
//            int month = myCal1.get(Calendar.MONTH);
//            int day = myCal1.get(Calendar.DATE);
//            myCal1.set(year, month, day, 0, 0, 0);
//
//            DataSet latestDataSet = db.getLatestDataSet();
//            String lastDateInDB = latestDataSet.getDate();
//            String[] dateSplit = lastDateInDB.split("[./-]");
//            int intYear = Integer.parseInt(dateSplit[0]);
//            int intMonth = Integer.parseInt(dateSplit[1]);
//            int intDate = Integer.parseInt(dateSplit[2]);
//            Calendar myCal2 = Calendar.getInstance();
//            myCal2.set(intYear, intMonth - 1, intDate, 0, 0, 0);
//
//            // Calculation of date difference
//            long time = myCal1.getTime().getTime() - myCal2.getTime().getTime();
//            long days = Math.round((double) time / (24. * 60. * 60. * 1000.));
//
//            // Adding dates if necessary
//            if (days > 0) {
//                Calendar myCal3 = myCal2;
//                myCal3.add(Calendar.DATE, 1);
//                for(int x = 1; x <= days; x++) {
//                    String date = sdf.format(myCal3.getTime());
//                    // add  to DataSet table
//                    for (int y = 0; y < goalsList.size(); y++) {
//                        boolean blnAdding = validateGoalPerDay(goalsList.get(y), myCal3);
//                        if (blnAdding == true) {
//                            DataSet newDataSet = new DataSet();
//                            newDataSet.setDate(date);
//                            newDataSet.setGoalID(goalsList.get(y).getId());
//                            db.addDataSet(newDataSet);
//                        }
//                    }
//                    // add to Money table
//                    Money moneyCurrentDay = new Money();
//                    moneyCurrentDay.setDate(date);
//                    moneyCurrentDay.setSettingValue(rewardValue);
//                    moneyCurrentDay.setSettingType(rewardType);
//                    db.addMoneyEntry(moneyCurrentDay);
//
//                    // add one day to Cal3
//                    myCal3.add(Calendar.DATE, 1);
//                }
//            }
//
//        }
//
//
//    }
//
//    public boolean validateGoalPerDay(Goal goal, Calendar date) {
//        boolean blnAdding = false;
//        //// date analysis
//        // check for weekday of date
//        int weekday = date.get(Calendar.DAY_OF_WEEK); // Sunday = 1 ... Saturday = 7
//        // change it to MO-SO order; Monday = 1 ... Sunday = 7
//        if (weekday == 1) {
//            weekday = 7;
//        } else {
//            weekday = weekday - 1;
//        }
//        // check for month status
//        boolean isDateFirstOfMonth = false;
//        boolean isDateMidOfMonth = false;
//        boolean isDateLastOfMonth = false;
//        // calculate variables
//        if (date.get(Calendar.DAY_OF_MONTH) == 1) {
//            isDateFirstOfMonth = true;
//        } else if (date.get(Calendar.DAY_OF_MONTH) == 15) {
//            isDateMidOfMonth = true;
//        } else if (date.get(Calendar.DAY_OF_MONTH) == date.getActualMaximum(Calendar.DAY_OF_MONTH)) {
//            isDateLastOfMonth = true;
//        }
//
//        // calculation if to add or not
//        int goalFrequency = goal.getGoalFrequency();
//        LinkedList<Integer> freqCode = goal.getGoalFrequencyCodeAsList();
//        // check for weekly goals ( == 1), monthly goals ( == 2) and daily goals ( else)
//        if (goalFrequency == 1) {
//            // check if day is checked for goal
//            int isWeekdaySelected = freqCode.get(weekday - 1);
//            if (isWeekdaySelected == 2) {
//                blnAdding = true;
//            }
//        } else if (goalFrequency == 2) {
//            if ((isDateFirstOfMonth && freqCode.get(7) == 2) || (isDateMidOfMonth && freqCode.get(8) == 2) || (isDateLastOfMonth && freqCode.get(9) == 2)) {
//                blnAdding = true;
//            }
//        } else if (goalFrequency == 0) {
//            blnAdding = true;
//        }
//
//        return blnAdding;

    }

    private void insertNewGoalAndDataSet(LocalDate date) {
        Goal goal = new Goal();
        // insert in Room
        long goalId = goalsDao.addGoal(goal);
        goal.setGoalId((int) goalId);
        if (mAuth.getCurrentUser() != null) {
            // insert in Firebase and update key as soon as added
            String goalRemoteId = firebaseService.addGoal(goal);
            // add remote id to goal
            goal.setGoalRemoteId(goalRemoteId);
        } else {
            goal.setGoalRemoteId("LOCAL" + String.valueOf(goalId));
        }
        goalsDao.updateGoal(goal);

        DataSet dataSet = calcHelperClass.generateSingleDataSet(goal, date);
        if (dataSet != null) {
            long dataSetId = dataSetDao.insertDataSet(dataSet);
            dataSet.setDataSetId((int) dataSetId);
            if (mAuth.getCurrentUser() != null) {
                String dataSetRemoteId = firebaseService.addDataSet(dataSet);
                dataSet.setDataSetRemoteId(dataSetRemoteId);
                dataSetDao.updateDataSet(dataSet);
            } else {
                dataSet.setDataSetRemoteId("LOCAL" + String.valueOf(dataSetId));
            }
        }
    }

}
