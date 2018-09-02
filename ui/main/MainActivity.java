package com.benjaminsommer.dailygoals.ui.main;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.benjaminsommer.dailygoals.BuildConfig;
import com.benjaminsommer.dailygoals.firebase.FirebaseAuthLiveData;
import com.benjaminsommer.dailygoals.services.DatabaseUpdateService;
import com.benjaminsommer.dailygoals.ui.introduction.IntroductionActivity;
import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.entities.Goal;
import com.benjaminsommer.dailygoals.ui.goals.GoalsActivity;
import com.benjaminsommer.dailygoals.util.SharedPrefHelperClass;
import com.benjaminsommer.dailygoals.workmanager.DailyDataSetReminderWorker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import dagger.android.AndroidInjection;

/*
* Story for MainActivity
* 1. Start-up screen with animation
* 2. Check if it is the first run
*       if yes: initialize Preference variables (reminder times, daily reminders etc.), start IntroductionActivity
*/


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    // variables
    private boolean isFirebaseUserActive;
    private TextView textDaily, textGoals, textLoading;
    private ProgressBar progressBar;

    @Inject
    SharedPreferences sharedPrefs;

    @Inject
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // observe FirebaseAuth
        FirebaseAuthLiveData.get().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean != isFirebaseUserActive) {
                    isFirebaseUserActive = aBoolean;
                }
            }
        });

        // view declaration
        ImageView logoArrow = (ImageView) findViewById(R.id.main_logo_arrow);
        ImageView logoBlanco = (ImageView) findViewById(R.id.main_logo_blanco);
        ImageView logoDate = (ImageView) findViewById(R.id.main_logo_date);
        textDaily = (TextView) findViewById(R.id.main_textPartOne);
        textGoals = (TextView) findViewById(R.id.main_textPartTwo);
        textLoading = (TextView) findViewById(R.id.main_textView_loading);
        progressBar = (ProgressBar) findViewById(R.id.main_progressBar);

        // animation section
        // at the end of animation: check if it is the first run of the app, if yes start preference initialization and start IntroductionActivity
        startAnimation(logoArrow, logoDate, logoBlanco);

    }

    private void startAnimation(ImageView logoArrow, ImageView logoDate, ImageView logoBlanco) {
        logoBlanco.setAlpha(0.0f);
        logoArrow.setAlpha(0.0f);
        textDaily.setAlpha(0.0f);
        textGoals.setAlpha(0.0f);

        ObjectAnimator dateTranslateAnimator = ObjectAnimator.ofFloat(logoDate, "translationX", 1000, 0);
        dateTranslateAnimator.setDuration(2000);

        ObjectAnimator blancoFadeAnimator = ObjectAnimator.ofFloat(logoBlanco, "alpha", 0.0f, 1.0f);
        blancoFadeAnimator.setDuration(2000);

        ObjectAnimator arrowTranslateAnimator = ObjectAnimator.ofFloat(logoArrow, "translationX", -1000, 0);
        arrowTranslateAnimator.setDuration(2000);

        ObjectAnimator arrowShowAnimator = ObjectAnimator.ofFloat(logoArrow, "alpha", 0.0f, 1.0f);
        arrowShowAnimator.setDuration(1);

        ObjectAnimator textDailyAnimator = ObjectAnimator.ofFloat(textDaily, "alpha", 0.0f, 1.0f);
        textDailyAnimator.setDuration(2000);

        ObjectAnimator textGoalsAnimator = ObjectAnimator.ofFloat(textGoals, "alpha", 0.0f, 1.0f);
        textGoalsAnimator.setDuration(2000);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(40.0f, 42.0f);
        valueAnimator.setDuration(200);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                textDaily.setTextSize(animatedValue);
                textGoals.setTextSize(animatedValue);
            }
        });
        valueAnimator.setStartDelay(1000);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.setRepeatCount(2);

        ValueAnimator progressBarHideAnimator = ValueAnimator.ofFloat(1.0f, 0.0f);
        progressBarHideAnimator.setDuration(100);
        progressBarHideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                textLoading.setAlpha(animatedValue);
                progressBar.setAlpha(animatedValue);
            }
        });


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(dateTranslateAnimator).with(textDailyAnimator);
        animatorSet.play(blancoFadeAnimator).with(arrowTranslateAnimator).with(textGoalsAnimator).with(arrowShowAnimator);
        animatorSet.play(dateTranslateAnimator).before(blancoFadeAnimator);
        animatorSet.play(blancoFadeAnimator).before(valueAnimator);
        animatorSet.play(valueAnimator).with(progressBarHideAnimator);


        animatorSet.start();

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                // check if it is first run, upgrade or general start and set the corresponding intent
                checkFirstRun();

            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }


    public void checkDatabaseContent() {

        // get SharedPreferences information (reward value and currency)
        String strRewardValue = sharedPrefs.getString("pref_amount", "0.00");
        float rewardValue = Float.valueOf(strRewardValue);
        String strRewardType = sharedPrefs.getString("pref_goalsReward", "1");
        int rewardType = Integer.valueOf(strRewardType);
        boolean alarmDecision = sharedPrefs.getBoolean("preferenceFragment_notificationDecision", true);

        Intent updateService = new Intent(this, DatabaseUpdateService.class);
        this.startService(updateService);

//        //// ESTABLISH REQUIRED DATABASE INPUT
//        // Check if an "ACTIVE" goal is existing in the database - if not, add one
//        if (db.doesActiveGoalExist() == false) {
//            Goal newGoal = new Goal();
//            db.addGoal(newGoal);
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

    }



//    public void setNotificationAlarm(Context context, SharedPreferences sharedPreferences) {
//
//        String strAlarmTime = sharedPreferences.getString("pref_notificationTime", "11:00");
//        String[] timeSections = strAlarmTime.split(":");
//        int hours = Integer.parseInt(timeSections[0]);
//        int minutes = Integer.parseInt(timeSections[1]);
//        boolean alarmDecision = sharedPreferences.getBoolean("preferenceFragment_notificationDecision", true);
//        boolean vibrationDecision = sharedPreferences.getBoolean("pref_vibration", true);
//
//        if (alarmDecision) {
//
//            // set notification every day
//            Calendar dailyAlarm = Calendar.getInstance();
//            dailyAlarm.set(Calendar.HOUR_OF_DAY, hours);
//            dailyAlarm.set(Calendar.MINUTE, minutes);
//            dailyAlarm.set(Calendar.SECOND, 0);
//
//            Intent intentAlarm = new Intent(context, NotificationReceiver.class);
//
//            PendingIntent pendingIntentAlarm = PendingIntent.getBroadcast(context, 100, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
//
//            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
//            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, dailyAlarm.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntentAlarm);
//            Log.d("setAlarm", "Day: " + dailyAlarm.get(Calendar.DATE) + ", Month: " + dailyAlarm.get(Calendar.MONTH) + ", Year: " + dailyAlarm.get(Calendar.YEAR) + ", Hour: " + dailyAlarm.get(Calendar.HOUR_OF_DAY) + ", Minute: " + dailyAlarm.get(Calendar.MINUTE) + ", Second: " + dailyAlarm.get(Calendar.SECOND));
//
//        } else {
//
//            Intent intentAlarm = new Intent(context.getApplicationContext(), NotificationReceiver.class);
//            PendingIntent pendingIntentAlarm = PendingIntent.getBroadcast(context, 100, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
//            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
//            alarmManager.cancel(pendingIntentAlarm);
//        }
//
//    }


    public String generateTodayAsString() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String today = format.format(date);
        return today;
    }

    public ArrayList<Goal> validateGoalsPerDay(ArrayList<Goal> completeGoalList, Calendar date) {
        ArrayList<Goal> finalGoalList = new ArrayList<>();
        //// date analysis
        // check for weekday of date
        int weekday = date.get(Calendar.DAY_OF_WEEK); // Sunday = 1 ... Saturday = 7
        // change it to MO-SO order; Monday = 1 ... Sunday = 7
        if (weekday == 1) {
            weekday = 7;
        } else {
            weekday = weekday - 1;
        }
        // check for month status
        boolean isDateFirstOfMonth = false;
        boolean isDateMidOfMonth = false;
        boolean isDateLastOfMonth = false;
        // calculate variables
        if (date.get(Calendar.DAY_OF_MONTH) == 1) {
            isDateFirstOfMonth = true;
        } else if (date.get(Calendar.DAY_OF_MONTH) == 15) {
            isDateMidOfMonth = true;
        } else if (date.get(Calendar.DAY_OF_MONTH) == date.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            isDateLastOfMonth = true;
        }

        for (int i = 0; i < completeGoalList.size(); i++) {
//            int goalFrequency = completeGoalList.get(i).getGoalFrequency().get(0);
//            //LinkedList<Integer> freqCode = completeGoalList.get(i).getgoalfre();
//            // check for weekly goals ( == 1), monthly goals ( == 2) and daily goals ( else)
//            if (goalFrequency == 1) {
//                // check if day is checked for goal
//                int isWeekdaySelected = freqCode.get(weekday - 1);
//                if (isWeekdaySelected == 2) {
//                    finalGoalList.add(completeGoalList.get(i));
//                }
//            } else if (goalFrequency == 2) {
//                if ((isDateFirstOfMonth && freqCode.get(7) == 2) || (isDateMidOfMonth && freqCode.get(8) == 2) || (isDateLastOfMonth && freqCode.get(9) == 2)) {
//                    finalGoalList.add(completeGoalList.get(i));
//                }
//            } else if (goalFrequency == 0) {
//                finalGoalList.add(completeGoalList.get(i));
//            }
        }
        return finalGoalList;
    }

    public boolean validateGoalPerDay(Goal goal, Calendar date) {
        boolean blnAdding = false;
        //// date analysis
        // check for weekday of date
        int weekday = date.get(Calendar.DAY_OF_WEEK); // Sunday = 1 ... Saturday = 7
        // change it to MO-SO order; Monday = 1 ... Sunday = 7
        if (weekday == 1) {
            weekday = 7;
        } else {
            weekday = weekday - 1;
        }
        // check for month status
        boolean isDateFirstOfMonth = false;
        boolean isDateMidOfMonth = false;
        boolean isDateLastOfMonth = false;
        // calculate variables
        if (date.get(Calendar.DAY_OF_MONTH) == 1) {
            isDateFirstOfMonth = true;
        } else if (date.get(Calendar.DAY_OF_MONTH) == 15) {
            isDateMidOfMonth = true;
        } else if (date.get(Calendar.DAY_OF_MONTH) == date.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            isDateLastOfMonth = true;
        }

        // calculation if to add or not
        int goalFrequency = goal.getGoalFrequency().get(0);
        List<Integer> freqCode = goal.getGoalFrequency();
        // check for weekly goals ( == 1), monthly goals ( == 2) and daily goals ( else)
        if (goalFrequency == 1) {
            // check if day is checked for goal
            if (freqCode.get(weekday) == 2) {
                blnAdding = true;
            }
        } else if (goalFrequency == 2) {
            if ((isDateFirstOfMonth && freqCode.get(8) == 2) || (isDateMidOfMonth && freqCode.get(9) == 2) || (isDateLastOfMonth && freqCode.get(10) == 2)) {
                blnAdding = true;
            }
        } else if (goalFrequency == 0) {
            blnAdding = true;
        }

        return blnAdding;
    }

    private void checkFirstRun() {

        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;
        Log.d("Current Version Code", String.valueOf(BuildConfig.VERSION_CODE));

        // Get saved version code
        int savedVersionCode = sharedPrefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            /*
            * This is just a normal run
            */

            // THIS NEEDS TO BE DataSetActivity.class instead of GoalsActivity.class
            Intent intent = new Intent(getApplicationContext(), GoalsActivity.class);
            //intent.putExtra("dataSetPosition", -1);
            startActivity(intent);

        } else if (savedVersionCode == DOESNT_EXIST) {

            /*
            * This is a new install (or the user cleared the shared preferences)
            */

            // set Shared Preferences for the first app start with standard values
            sharedPrefs.edit()
                    .putString(resources.getString(R.string.PREF_KEY_AMOUNT_MONEY), "0.5")
                    .putString(resources.getString(R.string.PREF_KEY_NOTIFICATION_TIME), "10:00")
                    .putString(resources.getString(R.string.PREF_KEY_TODO_REMINDER_1), "0;15")
                    .putString(resources.getString(R.string.PREF_KEY_TODO_REMINDER_2), "1;3")
                    .putString(resources.getString(R.string.PREF_KEY_TODO_REMINDER_3), "2;1")
                    .apply();

            // implement work manager for daily dataset reminder
            SharedPrefHelperClass sphc = new SharedPrefHelperClass(sharedPrefs, resources);
            sphc.startInititalDataSetReminder();

            // start Introduction Activity
            Intent newStart = new Intent(getApplicationContext(), IntroductionActivity.class);
            startActivity(newStart);


        } else if (currentVersionCode > savedVersionCode) {

            /*
            * This is an upgrade
            */

            Intent intent = new Intent(getApplicationContext(), GoalsActivity.class);
            //intent.putExtra("dataSetPosition", -1);
            startActivity(intent);
        }

        // Update the shared preferences with the current version code
        sharedPrefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }

}
