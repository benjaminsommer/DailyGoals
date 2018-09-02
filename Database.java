package com.benjaminsommer.dailygoals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.benjaminsommer.dailygoals.entities.CombinedDataSet;
import com.benjaminsommer.dailygoals.entities.DataSet;
import com.benjaminsommer.dailygoals.entities.DaySummary;
import com.benjaminsommer.dailygoals.entities.Goal;
import com.benjaminsommer.dailygoals.entities.Money;
import com.benjaminsommer.dailygoals.entities.MoneyExtended;
import com.benjaminsommer.dailygoals.entities.Reward;
import com.benjaminsommer.dailygoals.entities.SummarizedDataSet;

import java.util.ArrayList;

/**
 * Created by DEU209213 on 23.01.2016.
 */



public class Database extends SQLiteOpenHelper {

    private static final String TAG = Database.class.getSimpleName();

    // Deklaration der Datenbank-Paramter
    private static final String DATABASE_NAME = "dailygoals.db";
    private static final int DATABASE_VERSION = 8;

    // Deklaration der DailyGoals-Database
    private static final String TABLE_NAME_GOALS = "goals";
    private static final String _ID = "id";
    private static final String DATE = "datum";
    private static final String GOAL_ID = "goal_id";
    private static final String GOAL_VALUE = "goal_value";
    private static final String NOTICE = "goal_notice";
    private static final String MONEY_GOAL = "money_goal";

    // Deklaration der GoalsDefinition-Database
    private static final String TABLE_GOAL_DEFINITION = "goals_definition";
    private static final String ID_GOAL_DEFINITION = "id_goals";
    private static final String GOAL_POS = "goal_pos";
    private static final String GOAL_NAME = "goal_name";
    private static final String GOAL_CATEGORY = "goal_category";
    private static final String GOAL_DESCRIPTION = "goal_description";
    private static final String GOAL_COLOR = "goal_color";
    private static final String GOAL_FREQUENCY = "goal_frequency";
    private static final String GOAL_FREQUENCY_CODE = "goal_frequency_code";
    private static final String GOAL_REWARD_TYPE = "goal_reward_type";
    private static final String GOAL_REWARD = "goal_reward";
    private static final String GOAL_ACTIVATED = "goal_activated";

    // Deklaration der Money-Database
    private static final String TABLE_MONEY = "table_money";
    private static final String ID_MONEY = "id_money";
    private static final String DATE_MONEY = "date_money";
    private static final String VALUE_MONEY = "value_money";
    private static final String TYPE_MONEY = "type_money";
    private static final String SETTING_VALUE_MONEY = "setting_value_money";
    private static final String SETTING_TYPE_MONEY = "setting_type_money";

    // Declaration of To Do database
    private static final String TABLE_TODO = "table_todo";
    private static final String ID_TODO = "id_todo";
    private static final String NAME_TODO = "name_todo";
    private static final String STATE_TODO = "state_todo";
    private static final String BOOL_REMINDER_TODO = "bool_reminder_todo";
    private static final String REMINDER_TIME_TODO = "reminder_time_todo";
    private static final String BOOL_SNOOZE_TODO = "bool_snooze_todo";
    private static final String BOOL_REWARD_TODO = "bool_reward_todo";
    private static final String REWARD_AMOUNT_TODO = "reward_amount_todo";
    private static final String MONEY_TODO = "money_todo";

    // Deklaration der Goal-Werte
    private static final int NO_SELECT = 1;
    private static final int NO = 10;
    private static final int YES = 100;


    // Table create statements
    // Table DailyGoals-Database
    private static final String TABLE_DAILYGOALS_CREATE = "CREATE TABLE " +
            TABLE_NAME_GOALS + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DATE + " TEXT, " +
            GOAL_ID + " INTEGER, " +
            GOAL_VALUE + " INTEGER, " +
            NOTICE + " TEXT, " +
            MONEY_GOAL + " REAL);" ;

    // Table GoalsDefinition-Database
    private static final String TABLE_GOAL_DEFINITION_CREATE = "CREATE TABLE " +
            TABLE_GOAL_DEFINITION + " (" +
            ID_GOAL_DEFINITION + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            GOAL_POS + " INTEGER, " +
            GOAL_NAME + " TEXT, " +
            GOAL_CATEGORY + " INTEGER, " +
            GOAL_DESCRIPTION + " TEXT, " +
            GOAL_COLOR + " INTEGER, " +
            GOAL_FREQUENCY + " INTEGER, " +
            GOAL_FREQUENCY_CODE + " INTEGER, " +
            GOAL_REWARD_TYPE + " INTEGER, " +
            GOAL_REWARD + " REAL, " +
            GOAL_ACTIVATED + " INTEGER);";

    // Table Money-Database
    private static final String TABLE_MONEY_CREATE = "CREATE TABLE " +
            TABLE_MONEY + " (" +
            ID_MONEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DATE_MONEY + " TEXT, " +
            VALUE_MONEY + " REAL, " +
            TYPE_MONEY + " INTEGER, " +
            SETTING_VALUE_MONEY + " REAL, " +
            SETTING_TYPE_MONEY + " INTEGER);";

    // Table To-Do-Database
    private static final String TABLE_TODO_CREATE = "CREATE TABLE " +
            TABLE_TODO + " (" +
            ID_TODO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME_TODO + " TEXT, " +
            STATE_TODO + " INTEGER, " +
            BOOL_REMINDER_TODO + " INTEGER, " +
            REMINDER_TIME_TODO + " INTEGER, " +
            BOOL_SNOOZE_TODO + " INTEGER, " +
            BOOL_REWARD_TODO + " INTEGER, " +
            REWARD_AMOUNT_TODO + " REAL, " +
            MONEY_TODO + " REAL);";


    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Erstellung der benötigten Tabellen
        db.execSQL(TABLE_DAILYGOALS_CREATE);
        db.execSQL(TABLE_GOAL_DEFINITION_CREATE);
        db.execSQL(TABLE_MONEY_CREATE);
        db.execSQL(TABLE_TODO_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(Database.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        //Datenbank droppen
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_GOALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GOAL_DEFINITION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MONEY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
                //neue Datenbank erstellen
        onCreate(db);

    }

    /** All CRUD (Create, Read, Update, Delete) Operations */

    //// CRUD for DATASET
    // Adding new date row
    void addDataSet(DataSet dataSet) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DATE, dataSet.getDataSetDate());
        values.put(GOAL_ID, dataSet.getDataSetRemoteId());
        values.put(GOAL_VALUE, dataSet.getDataSetValue());
        values.put(NOTICE, dataSet.getDataSetNotice());
        values.put(MONEY_GOAL, dataSet.getDataSetMoney());
        db.insert(TABLE_NAME_GOALS, null, values);
        db.close();
    }

    // Getting single date goal-values
    DataSet getDataSet(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_GOALS,
                new String[] {_ID, DATE, GOAL_ID, GOAL_VALUE, NOTICE, MONEY_GOAL},
                _ID + "=?",
                new String[] {String.valueOf(id) },
                null,
                null,
                null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        DataSet dataSet = new DataSet();
        dataSet.setDataSetId(Integer.parseInt(cursor.getString(0)));
        dataSet.setDataSetDate(cursor.getString(1));
        dataSet.setDataSetRemoteId(cursor.getString(2));
        dataSet.setDataSetValue(Integer.parseInt(cursor.getString(3)));
        dataSet.setDataSetNotice(cursor.getString(4));
        dataSet.setDataSetMoney(Float.parseFloat(cursor.getString(5)));
        return dataSet;
    }

    // Getting complete table
    public ArrayList<DataSet> getCompleteTable() {
        ArrayList<DataSet> datesList = new ArrayList<DataSet>();
        //Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME_GOALS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DataSet dataSet = new DataSet();
                dataSet.setDataSetId(Integer.parseInt(cursor.getString(0)));
                dataSet.setDataSetDate(cursor.getString(1));
                dataSet.setDataSetRemoteId(cursor.getString(2));
                dataSet.setDataSetValue(Integer.parseInt(cursor.getString(3)));
                dataSet.setDataSetNotice(cursor.getString(4));
                dataSet.setDataSetMoney(Float.parseFloat(cursor.getString(5)));
                // Adding datasets to list
                datesList.add(dataSet);
            } while (cursor.moveToNext());
        }
        // return Datumsliste
        return datesList;
    }

    public ArrayList<DataSet> getDataSetsOfSpecificDay(String date) {
        ArrayList<DataSet> datesList = new ArrayList<>();
        // Select Query
        String selectQuery = "SELECT * FROM " + TABLE_GOAL_DEFINITION + " WHERE " + DATE + " =?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {date});
        // looping through all rows and adding them to the list
        if (cursor.moveToFirst()) {
            do {
                DataSet dataSet = new DataSet();
                dataSet.setDataSetId(Integer.parseInt(cursor.getString(0)));
                dataSet.setDataSetDate(cursor.getString(1));
                dataSet.setDataSetRemoteId(cursor.getString(2));
                dataSet.setDataSetValue(Integer.parseInt(cursor.getString(3)));
                dataSet.setDataSetNotice(cursor.getString(4));
                dataSet.setDataSetMoney(Float.parseFloat(cursor.getString(5)));
                datesList.add(dataSet);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return datesList;
    }

    public SummarizedDataSet getSummarizedDataSetOfSpecificDay(String date) {
        SummarizedDataSet dataSet = new SummarizedDataSet();
        // Select Query
        String selectQuery = "SELECT " + DATE + ", " +
                "SUM(CASE WHEN " + GOAL_VALUE + "=" + YES + " THEN 1 ELSE 0 END), " +
                "SUM(CASE WHEN " + GOAL_VALUE + "=" + NO + " THEN 1 ELSE 0 END), " +
                "SUM(CASE WHEN " + GOAL_VALUE + "=" + NO_SELECT + " THEN 1 ELSE 0 END) " +
                "FROM " + TABLE_NAME_GOALS + " WHERE " + DATE + " = '" + date + "' GROUP BY " + DATE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding them to the list
        if (cursor.moveToFirst()) {
            dataSet.setDate(cursor.getString(0));
            dataSet.setValueYes(Integer.parseInt(cursor.getString(1)));
            dataSet.setValueNo(Integer.parseInt(cursor.getString(2)));
            dataSet.setValueOpen(Integer.parseInt(cursor.getString(3)));
        }
        cursor.close();
        db.close();
        return dataSet;
    }

    // Updating single date goal-values
    public int updateDataSet(DataSet dataSet) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DATE, dataSet.getDataSetDate());
        values.put(GOAL_ID, dataSet.getDataSetRemoteId());
        values.put(GOAL_VALUE, dataSet.getDataSetValue());
        values.put(NOTICE, dataSet.getDataSetNotice());
        values.put(MONEY_GOAL, dataSet.getDataSetMoney());
        // updating row
        return db.update(TABLE_NAME_GOALS, values, _ID + " = ?",
                new String[] { String.valueOf(dataSet.getDataSetId()) });
    }

    // Deleting single date goal-values
    public void deleteDataSet(DataSet dataSet) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_GOALS, _ID + " = ?", new String[]{String.valueOf(dataSet.getDataSetId())});
        db.close();
    }

    // Deleting all date goal values
    public void deleteAllDataSets() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME_GOALS);
        db.close();
    }

    // Getting dates count
    public int getDatesCount() {
        String countQuery = "SELECT * FROM " + TABLE_NAME_GOALS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        // return count
        return cursor.getCount();
    }

    public boolean isDataSetTableEmpty() {
        boolean flag;
        //String quString = "SELECT EXISTS(SELECT 1 FROM " + TABLE_NAME_GOALS  + ");";
        String quString = "SELECT count(*) FROM " + TABLE_NAME_GOALS;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(quString, null);

//        int count = cursor.getCount();
//        if (count == 0) {
//            flag = false;
//        } else {
//            flag = true;
//        }

        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if (count > 1) {
            flag =  false;
        } else {
            flag = true;
        }
        cursor.close();
        db.close();
        return flag;
    }

    public boolean doesSpecificDataSetInputExist(String date, int goalID) {
        boolean check = false;
        SQLiteDatabase db = getReadableDatabase();
        String count = "SELECT count(*) FROM " + TABLE_NAME_GOALS + " WHERE " + DATE + "='" + date + "' AND " + GOAL_ID + "=" + goalID;
        Cursor cursor = db.rawQuery(count, null);
        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if (icount > 0) {
            check = true;
        } else {
            check = false;
        }
        cursor.close();
        db.close();
        return check;
    }

    public DataSet getSpecificDataSet(String date, int goalID) {
        DataSet dataSet = new DataSet();
        SQLiteDatabase db = getReadableDatabase();
        String count = "SELECT * FROM " + TABLE_NAME_GOALS + " WHERE " + DATE + "='" + date + "' AND " + GOAL_ID + "=" + goalID;
        Cursor cursor = db.rawQuery(count, null);
        // looping through all rows and adding them to the list
        if (cursor.moveToFirst()) {
            dataSet.setDataSetId(Integer.parseInt(cursor.getString(0)));
            dataSet.setDataSetDate(cursor.getString(1));
            dataSet.setDataSetRemoteId(cursor.getString(2));
            dataSet.setDataSetValue(Integer.parseInt(cursor.getString(3)));
            dataSet.setDataSetNotice(cursor.getString(4));
            dataSet.setDataSetMoney(Float.parseFloat(cursor.getString(5)));
        }
        cursor.close();
        db.close();
        return dataSet;
    }

    public DataSet getLatestDataSet() {
        String selectQuery = "SELECT * FROM " + TABLE_NAME_GOALS + " ORDER BY " + DATE + " DESC LIMIT 1;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        DataSet dataSet = new DataSet();
        dataSet.setDataSetId(Integer.parseInt(cursor.getString(0)));
        dataSet.setDataSetDate(cursor.getString(1));
        dataSet.setDataSetRemoteId(cursor.getString(2));
        dataSet.setDataSetValue(Integer.parseInt(cursor.getString(3)));
        dataSet.setDataSetNotice(cursor.getString(4));
        dataSet.setDataSetMoney(Float.parseFloat(cursor.getString(5)));

        cursor.close();
        db.close();
        // return dataSet
        return dataSet;
    }

    public ArrayList<SummarizedDataSet> getResultsPerGoal() {
        String selectQuery = "SELECT " + GOAL_ID +
                ", SUM(CASE WHEN " + GOAL_VALUE + "=" + YES + " THEN 1 ELSE 0 END)" +
                ", SUM(CASE WHEN " + GOAL_VALUE + "=" + NO + " THEN 1 ELSE 0 END)" +
                ", SUM(CASE WHEN " + GOAL_VALUE + "=" + NO_SELECT + " THEN 1 ELSE 0 END)" +
                " FROM " + TABLE_NAME_GOALS +
                " GROUP BY " + GOAL_ID +
                " ORDER BY " + GOAL_ID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<SummarizedDataSet> arrayList = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();
        }
        do {
            SummarizedDataSet dataSet = new SummarizedDataSet();
            dataSet.setGoalID(Integer.valueOf(cursor.getString(0)));
            dataSet.setValueYes(Integer.valueOf(cursor.getString(1)));
            dataSet.setValueNo(Integer.valueOf(cursor.getString(2)));
            dataSet.setValueOpen(Integer.valueOf(cursor.getString(3)));
            arrayList.add(dataSet);
        } while (cursor.moveToNext());
        cursor.close();
        db.close();
        return arrayList;
    }

    public SummarizedDataSet getOverallResults() {
        SummarizedDataSet dataSet = new SummarizedDataSet();
        String selectQuery = "SELECT SUM(CASE WHEN " + GOAL_VALUE + "=" + YES + " THEN 1 ELSE 0 END)" +
                ", SUM(CASE WHEN " + GOAL_VALUE + "=" + NO + " THEN 1 ELSE 0 END)" +
                ", SUM(CASE WHEN " + GOAL_VALUE + "=" + NO_SELECT + " THEN 1 ELSE 0 END)" +
                " FROM " + TABLE_NAME_GOALS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
            dataSet.setValueYes(Integer.valueOf(cursor.getString(0)));
            dataSet.setValueNo(Integer.valueOf(cursor.getString(1)));
            dataSet.setValueOpen(Integer.valueOf(cursor.getString(2)));
        }
        cursor.close();
        db.close();
        return dataSet;
    }

    public ArrayList<String> getAllDatesFromDataSetDB() {
        String selectQuery = "SELECT DISTINCT " + DATE + " FROM " + TABLE_NAME_GOALS + " ORDER BY " + DATE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<String> arrayList = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();
        }
        do {
            String date = new String();
            date = cursor.getString(0);
            arrayList.add(date);
        } while (cursor.moveToNext());
        cursor.close();
        db.close();
        return arrayList;
    }

    public ArrayList<DaySummary> getAllSummarizedGoalValuesPerDate() {
        ArrayList<DaySummary> result = new ArrayList<>();
        String s2 = "CASE ";
        String strfQuery = "CASE WHEN (STRFTIME('%W', " + TABLE_NAME_GOALS + "." + DATE +
                ") + (1 - STRFTIME('%W', STRFTIME('%Y', " + TABLE_NAME_GOALS + "." + DATE + ") || '-01-04')) = 0) " +
                "THEN ((STRFTIME('%W', " + TABLE_NAME_GOALS + "." + DATE + ", 'start of year', '-1 days') + (1 - STRFTIME('%W', STRFTIME('%Y', " + TABLE_NAME_GOALS + "." + DATE + ", 'start of year', '-1 days') || '-01-04'))) || ' / ' || STRFTIME('%Y', " + TABLE_NAME_GOALS + "." + DATE + ", 'start of year', '-1 days')) " +
                "ELSE ((STRFTIME('%W', " + TABLE_NAME_GOALS + "." + DATE + ") + (1 - STRFTIME('%W', STRFTIME('%Y', " + TABLE_NAME_GOALS + "." + DATE + ") || '-01-04'))) || ' / ' || STRFTIME('%Y', " + TABLE_NAME_GOALS + "." + DATE + ")) END";
        String selectQuery = "SELECT (" + strfQuery + "), " +
                DATE + ", " +
                "SUM(CASE WHEN " + TABLE_NAME_GOALS + "." + GOAL_VALUE + "=" + YES + " THEN 1 ELSE 0 END), " +
                "SUM(CASE WHEN " + TABLE_NAME_GOALS + "." + GOAL_VALUE + "=" + NO + " THEN 1 ELSE 0 END), " +
                "SUM(CASE WHEN " + TABLE_NAME_GOALS + "." + GOAL_VALUE + "=" + NO_SELECT + " THEN 1 ELSE 0 END), " +
                "SUM(" + MONEY_GOAL + " )" +
                " FROM " + TABLE_NAME_GOALS +
                " GROUP BY " + DATE + " ORDER BY " + DATE + " DESC";

//        String selectQuery = "SELECT (" + strfQuery + "), " +
//                DATE + ", " +
//                "SUM(CASE WHEN " + TABLE_NAME_GOALS + "." + GOAL_VALUE + "=" + YES + " THEN 1 ELSE 0 END), " +
//                "SUM(CASE WHEN " + TABLE_NAME_GOALS + "." + GOAL_VALUE + "=" + NO + " THEN 1 ELSE 0 END), " +
//                "SUM(CASE WHEN " + TABLE_NAME_GOALS + "." + GOAL_VALUE + "=" + NO_SELECT + " THEN 1 ELSE 0 END), " +
//                TABLE_MONEY + "." + VALUE_MONEY +
//                " FROM " + TABLE_NAME_GOALS + " INNER JOIN " + TABLE_MONEY  + " ON " + TABLE_NAME_GOALS + "." + DATE + "=" + TABLE_MONEY + "." + DATE_MONEY +
//                " GROUP BY " + DATE + " ORDER BY " + DATE + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        do {
            DaySummary daySummary = new DaySummary();
            daySummary.setWeek(cursor.getString(0));
            daySummary.setDate(cursor.getString(1));
            daySummary.setValueYes(Integer.valueOf(cursor.getString(2)));
            daySummary.setValueNo(Integer.valueOf(cursor.getString(3)));
            daySummary.setValueOpen(Integer.valueOf(cursor.getString(4)));
            daySummary.setMoney(Double.valueOf(cursor.getString(5)));

            result.add(daySummary);
        } while (cursor.moveToNext());
        cursor.close();
        db.close();
        return result;
    }

    public ArrayList<SummarizedDataSet> getSummarizedDataSetByTimeframe(int goalID, int timeframe) {
        // int timeframe: 0 = by day, 1 = by week, 2 = by month, 3 = by year, 4 = by weekday

        ArrayList<SummarizedDataSet> result = new ArrayList<>();
        String selectQuery;
        switch (timeframe) {
            case 0:
                if (goalID == 0) {
                    selectQuery = "SELECT " + _ID +
                            ", strftime('%d.%m', " + DATE +
                            "), " + GOAL_ID +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + YES + " THEN 1 ELSE 0 END)" +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + NO + " THEN 1 ELSE 0 END)" +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + NO_SELECT + " THEN 1 ELSE 0 END" +
                            ") FROM " + TABLE_NAME_GOALS +
                            " GROUP BY " + DATE +
                            " ORDER BY " + DATE + " DESC";
                    break;
                } else {
                    selectQuery = "SELECT " + _ID +
                            ", strftime('%d.%m', " + DATE +
                            "), " + GOAL_ID +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + YES + " THEN 1 ELSE 0 END)" +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + NO + " THEN 1 ELSE 0 END)" +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + NO_SELECT + " THEN 1 ELSE 0 END" +
                            ") FROM " + TABLE_NAME_GOALS +
                            " WHERE " + GOAL_ID +
                            " = " + goalID +
                            " GROUP BY " + DATE +
                            " ORDER BY " + DATE + " DESC";
                    break;
                }

            case 1:
                String strfQuery = "CASE WHEN (STRFTIME('%W', " + DATE +
                        ") + (1 - STRFTIME('%W', STRFTIME('%Y', " + DATE + ") || '-01-04')) = 0) " +
                        "THEN (STRFTIME('%Y', " + DATE + ", 'start of year', '-1 days') || '-' || (STRFTIME('%W', " + DATE + ", 'start of year', '-1 days') + (1 - STRFTIME('%W', STRFTIME('%Y', " + DATE + ", 'start of year', '-1 days') || '-01-04')))) " +
                        "ELSE (STRFTIME('%Y', " + DATE + ") || '-' || (STRFTIME('%W', " + DATE + ") + (1 - STRFTIME('%W', STRFTIME('%Y', " + DATE + ") || '-01-04')))) END";
                if (goalID == 0) {
                    selectQuery = "SELECT " + _ID +
                            ",( " + strfQuery + ")" +
                            ", " + GOAL_ID +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + YES + " THEN 1 ELSE 0 END)" +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + NO + " THEN 1 ELSE 0 END)" +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + NO_SELECT + " THEN 1 ELSE 0 END" +
                            ") FROM " + TABLE_NAME_GOALS +
                            " GROUP BY (" + strfQuery +
                            ") ORDER BY (" + strfQuery + ") DESC";
                    break;
                } else {
                    selectQuery = "SELECT " + _ID +
                            ",( " + strfQuery + ")" +
                            ", " + GOAL_ID +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + YES + " THEN 1 ELSE 0 END)" +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + NO + " THEN 1 ELSE 0 END)" +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + NO_SELECT + " THEN 1 ELSE 0 END" +
                            ") FROM " + TABLE_NAME_GOALS +
                            " WHERE " + GOAL_ID +
                            " = " + goalID +
                            " GROUP BY (" + strfQuery +
                            ") ORDER BY (" + strfQuery + ") DESC";
                    break;
                }

            case 2:
                if (goalID == 0) {
                    selectQuery = "SELECT " + _ID +
                            ", strftime('%Y-%m', " + DATE +
                            "), " + GOAL_ID +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + YES + " THEN 1 ELSE 0 END)" +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + NO + " THEN 1 ELSE 0 END)" +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + NO_SELECT + " THEN 1 ELSE 0 END" +
                            ") FROM " + TABLE_NAME_GOALS +
                            " GROUP BY strftime('%Y-%m', " + DATE +
                            ") ORDER BY strftime('%Y-%m', " + DATE + ") DESC";
                    break;
                } else {
                    selectQuery = "SELECT " + _ID +
                            ", strftime('%Y-%m', " + DATE +
                            "), " + GOAL_ID +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + YES + " THEN 1 ELSE 0 END)" +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + NO + " THEN 1 ELSE 0 END)" +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + NO_SELECT + " THEN 1 ELSE 0 END" +
                            ") FROM " + TABLE_NAME_GOALS +
                            " WHERE " + GOAL_ID +
                            " = " + goalID +
                            " GROUP BY strftime('%Y-%m', " + DATE +
                            ") ORDER BY strftime('%Y-%m', " + DATE + ") DESC";
                    break;
                }

            case 3:
                if (goalID == 0) {
                    selectQuery = "SELECT " + _ID +
                            ", strftime('%Y', " + DATE +
                            "), " + GOAL_ID +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + YES + " THEN 1 ELSE 0 END)" +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + NO + " THEN 1 ELSE 0 END)" +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + NO_SELECT + " THEN 1 ELSE 0 END" +
                            ") FROM " + TABLE_NAME_GOALS +
                            " GROUP BY strftime('%Y', " + DATE +
                            ") ORDER BY strftime('%Y', " + DATE + ") DESC";
                    break;
                } else {
                    selectQuery = "SELECT " + _ID +
                            ", strftime('%Y', " + DATE +
                            "), " + GOAL_ID +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + YES + " THEN 1 ELSE 0 END)" +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + NO + " THEN 1 ELSE 0 END)" +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + NO_SELECT + " THEN 1 ELSE 0 END" +
                            ") FROM " + TABLE_NAME_GOALS +
                            " WHERE " + GOAL_ID +
                            " = " + goalID +
                            " GROUP BY strftime('%Y', " + DATE +
                            ") ORDER BY strftime('%Y', " + DATE + ") DESC";
                    break;
                }

            case 4:
                if (goalID == 0) {
                    selectQuery = "SELECT " + _ID +
                            ", strftime('%w', " + DATE +
                            "), " + GOAL_ID +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + YES + " THEN 1 ELSE 0 END)" +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + NO + " THEN 1 ELSE 0 END)" +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + NO_SELECT + " THEN 1 ELSE 0 END" +
                            ") FROM " + TABLE_NAME_GOALS +
                            " GROUP BY strftime('%w', " + DATE +
                            ") ORDER BY strftime('%w', " + DATE + ")";
                    break;
                } else {
                    selectQuery = "SELECT " + _ID +
                            ", strftime('%w', " + DATE +
                            "), " + GOAL_ID +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + YES + " THEN 1 ELSE 0 END)" +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + NO + " THEN 1 ELSE 0 END)" +
                            ", SUM(CASE WHEN " + GOAL_VALUE + "=" + NO_SELECT + " THEN 1 ELSE 0 END" +
                            ") FROM " + TABLE_NAME_GOALS +
                            " WHERE " + GOAL_ID +
                            " = " + goalID +
                            " GROUP BY strftime('%w', " + DATE +
                            ") ORDER BY strftime('%w', " + DATE + ")";
                    break;
                }
            default:
                selectQuery = "SELECT strftime('%W', " + DATE + ") AS " + DATE + " FROM " + TABLE_NAME_GOALS;
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        do {
            SummarizedDataSet dataSet = new SummarizedDataSet();
            dataSet.setGoalID(Integer.valueOf(cursor.getString(0)));
            dataSet.setDate(cursor.getString(1));
            dataSet.setGoalID(Integer.valueOf(cursor.getString(2)));
            dataSet.setValueYes(Integer.valueOf(cursor.getString(3)));
            dataSet.setValueNo(Integer.valueOf(cursor.getString(4)));
            dataSet.setValueOpen(Integer.valueOf(cursor.getString(5)));
            result.add(dataSet);
        } while (cursor.moveToNext());
        cursor.close();
        db.close();
        return result;
    }

    public ArrayList<Goal> getAllGoalsInDataSetTable() {
        ArrayList<Goal> result = new ArrayList<>();
        String selectQuery = "SELECT + " + TABLE_NAME_GOALS + "." + GOAL_ID +
                ", " + TABLE_GOAL_DEFINITION + "." + GOAL_NAME +
                " FROM " + TABLE_NAME_GOALS + " INNER JOIN " +
                TABLE_GOAL_DEFINITION + " ON " + TABLE_NAME_GOALS + "." + GOAL_ID + "=" +
                TABLE_GOAL_DEFINITION + "." + ID_GOAL_DEFINITION +
                " GROUP BY " + TABLE_NAME_GOALS + "." + GOAL_ID +
                " ORDER BY " + TABLE_NAME_GOALS + "." + GOAL_ID ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        do {
            Goal goal = new Goal();
            goal.setGoalId(Integer.valueOf(cursor.getString(0)));
            goal.setGoalName(cursor.getString(1));
            result.add(goal);
        } while (cursor.moveToNext());
        cursor.close();
        db.close();
        return result;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //// CRUD for GOAL
    // add single goal description
    public void addGoal(Goal goal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GOAL_NAME, goal.getGoalName());
        values.put(GOAL_POS, goal.getGoalPos());
        values.put(GOAL_CATEGORY, goal.getGoalCategory());
        values.put(GOAL_DESCRIPTION, goal.getGoalDescription());
        values.put(GOAL_COLOR, goal.getGoalColor());
        //// GOAL FREQUENCY IS MISSING!
        values.put(GOAL_REWARD_TYPE, convertBooleanToInt(goal.getGoalRewardType()));
        values.put(GOAL_REWARD, goal.getGoalReward());
        values.put(GOAL_ACTIVATED, goal.getActivated());
        long goalPosID = db.insert(TABLE_GOAL_DEFINITION, null, values);
        values.put(GOAL_POS, goalPosID);
        db.update(TABLE_GOAL_DEFINITION, values, ID_GOAL_DEFINITION + "=?",
                new String[] { String.valueOf(goalPosID) });

        db.close();
    }

    // get single goal description by ID
    public Goal getGoalByID(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GOAL_DEFINITION,
                new String[]{ID_GOAL_DEFINITION, GOAL_POS, GOAL_NAME, GOAL_CATEGORY, GOAL_DESCRIPTION, GOAL_COLOR, GOAL_FREQUENCY, GOAL_FREQUENCY_CODE, GOAL_REWARD_TYPE, GOAL_REWARD, GOAL_ACTIVATED},
                ID_GOAL_DEFINITION + "=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        Goal goal = new Goal();
        goal.setGoalId(Integer.parseInt(cursor.getString(0)));
        goal.setGoalPos(Integer.parseInt(cursor.getString(1)));
        goal.setGoalName(cursor.getString(2));
        goal.setGoalCategory(Integer.parseInt(cursor.getString(3)));
        goal.setGoalDescription(cursor.getString(4));
        goal.setGoalColor(Integer.parseInt(cursor.getString(5)));
        //// GOAL FREQUENCY IS MISSING!
        goal.setGoalRewardType(convertIntToBoolean(Integer.parseInt(cursor.getString(8))));
        goal.setGoalReward(Double.parseDouble(cursor.getString(9)));
        goal.setActivated(Integer.parseInt(cursor.getString(10)));
        // return date goal-values
        cursor.close();
        db.close();
        return goal;
    }

    public boolean doesGoalPosExist(int goalPos) {
        String selectQuery = "SELECT 1 FROM " + TABLE_GOAL_DEFINITION + " WHERE " + GOAL_POS + "=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {String.valueOf(goalPos)});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    // get single goal description by goal position
    public Goal getGoalByGoalPos(int goalPos) {

        // define variable
        Goal goal = new Goal();

        //Select All Query
        if (isGoalTableEmpty() == false) {
            String selectQuery = "SELECT * FROM " + TABLE_GOAL_DEFINITION + " WHERE " + GOAL_POS + " = ?";
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, new String[] {String.valueOf(goalPos)});

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                goal.setGoalId(Integer.parseInt(cursor.getString(0)));
                goal.setGoalPos(Integer.parseInt(cursor.getString(1)));
                goal.setGoalName(cursor.getString(2));
                goal.setGoalCategory(Integer.parseInt(cursor.getString(3)));
                goal.setGoalDescription(cursor.getString(4));
                goal.setGoalColor(Integer.parseInt(cursor.getString(5)));
                //// GOAL FREQUENCY IS MISSING!
                goal.setGoalRewardType(convertIntToBoolean(Integer.parseInt(cursor.getString(8))));
                goal.setGoalReward(Double.parseDouble(cursor.getString(9)));
                goal.setActivated(Integer.parseInt(cursor.getString(10)));
            } else {
                goal.setGoalPos(goalPos);
            }
            // return Goal
            return goal;
        } else {
            goal.setGoalPos(goalPos);
            return goal;
        }


    }

    // Getting complete Goal-table
    public ArrayList<Goal> getCompleteGoalsTable(boolean onlyActive) {
        ArrayList<Goal> goalsList = new ArrayList<Goal>();
        //Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_GOAL_DEFINITION + " ORDER BY " + GOAL_POS;
        if (onlyActive) {
            selectQuery = "SELECT * FROM " + TABLE_GOAL_DEFINITION + " WHERE " + GOAL_ACTIVATED + "=" + 1 + " ORDER BY " + GOAL_POS;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Goal goal = new Goal();
                goal.setGoalId(Integer.parseInt(cursor.getString(0)));
                goal.setGoalPos(Integer.parseInt(cursor.getString(1)));
                goal.setGoalName(cursor.getString(2));
                goal.setGoalCategory(Integer.parseInt(cursor.getString(3)));
                goal.setGoalDescription(cursor.getString(4));
                goal.setGoalColor(Integer.parseInt(cursor.getString(5)));
                //// GOAL FREQUENCY IS MISSING!
                goal.setGoalRewardType(convertIntToBoolean(Integer.parseInt(cursor.getString(8))));
                goal.setGoalReward(Double.parseDouble(cursor.getString(9)));
                goal.setActivated(Integer.parseInt(cursor.getString(10)));
                // Adding goals to list
                goalsList.add(goal);
            } while (cursor.moveToNext());
        }
        // return Datumsliste
        cursor.close();
        db.close();
        return goalsList;
    }

    public int updateGoal(Goal goal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GOAL_POS, goal.getGoalPos());
        values.put(GOAL_NAME, goal.getGoalName());
        values.put(GOAL_CATEGORY, goal.getGoalCategory());
        values.put(GOAL_DESCRIPTION, goal.getGoalDescription());
        values.put(GOAL_COLOR, goal.getGoalColor());
        //// GOAL FREQUENCY IS MISSING!
        values.put(GOAL_REWARD_TYPE, convertBooleanToInt(goal.getGoalRewardType()));
        values.put(GOAL_REWARD, goal.getGoalReward());
        values.put(GOAL_ACTIVATED, goal.getActivated());
        //updating row
        return db.update(TABLE_GOAL_DEFINITION, values, ID_GOAL_DEFINITION + "=?",
                new String[] { String.valueOf(goal.getGoalId())});
    }

    // Deleting single date goal-values
    public void deleteGoal(Goal goal) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GOAL_DEFINITION, ID_GOAL_DEFINITION + "=?", new String[]{String.valueOf(goal.getGoalId())});
        db.close();
    }

    // Deleting all goals
    public void deleteAllGoals() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_GOAL_DEFINITION);
        db.close();
    }

    public boolean isGoalTableEmpty() {
        boolean check = true;
        SQLiteDatabase db = getReadableDatabase();
        String count = "SELECT count(*) FROM " + TABLE_GOAL_DEFINITION;
        Cursor cursor = db.rawQuery(count, null);
        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if (icount > 0) {
            check = false;
        } else {
            check = true;
        }
        cursor.close();
        db.close();
        return check;
    }

    public boolean doesActiveGoalExist() {
        boolean check = false;
        SQLiteDatabase db = getReadableDatabase();
        String count = "SELECT count(*) FROM " + TABLE_GOAL_DEFINITION + " WHERE " + GOAL_ACTIVATED + "=" + 1;
        Cursor cursor = db.rawQuery(count, null);
        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if (icount > 0) {
            check = true;
        } else {
            check = false;
        }
        cursor.close();
        db.close();
        return check;
    }

    ///////////////////////////////////////////
    // CRUD for CombinedDataSet

    public ArrayList<CombinedDataSet> getCombinedTableByDate(String date) {
        ArrayList<CombinedDataSet> datesList = new ArrayList<>();
        // Select Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME_GOALS + " INNER JOIN " +
                TABLE_GOAL_DEFINITION + " ON " + TABLE_NAME_GOALS + "." + GOAL_ID + "=" +
                TABLE_GOAL_DEFINITION + "." + ID_GOAL_DEFINITION + " WHERE " + DATE + "=?" + " ORDER BY " + GOAL_POS + ", " + GOAL_ID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{date});
        // looping through all rows and adding them to the list
        if (cursor.moveToFirst()) {
            do {
//                CombinedDataSet combinedDataSet = new CombinedDataSet();
//                combinedDataSet.setID(Integer.parseInt(cursor.getString(0)));
//                combinedDataSet.setDate(cursor.getString(1));
//                combinedDataSet.setGoalID(Integer.parseInt(cursor.getString(2)));
//                combinedDataSet.setGoalValue(Integer.parseInt(cursor.getString(3)));
//                combinedDataSet.setNotice(cursor.getString(4));
//                combinedDataSet.setGoalPos(Integer.parseInt(cursor.getString(7)));
//                combinedDataSet.setGoalName(cursor.getString(8));
//                combinedDataSet.setGoalCategory(Integer.parseInt(cursor.getString(9)));
//                combinedDataSet.setGoalDescription(cursor.getString(10));
//                combinedDataSet.setGoalColor(Integer.parseInt(cursor.getString(11)));
//                combinedDataSet.setGoalFrequency(Integer.parseInt(cursor.getString(12)));
//                combinedDataSet.setGoalFrequencyCode(Long.parseLong(cursor.getString(13)));
//                combinedDataSet.setGoalRewardType(convertIntToBoolean(Integer.parseInt(cursor.getString(14))));
//                combinedDataSet.setGoalReward(Double.parseDouble(cursor.getString(15)));
//                datesList.add(combinedDataSet);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return datesList;
    }

    ///////////////////////////////////////////
    // CRUD for Money Database

    // add money entry
    public void addMoneyEntry(Money money) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DATE_MONEY, money.getDate());
        values.put(VALUE_MONEY, money.getValue());
        values.put(TYPE_MONEY, money.getType());
        values.put(SETTING_VALUE_MONEY, money.getSettingValue());
        values.put(SETTING_TYPE_MONEY, money.getSettingType());
        db.insert(TABLE_MONEY, null, values);
        db.close();
    }

    // update money entry
    public void updateMoneyEntry(Money money) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DATE_MONEY, money.getDate());
        values.put(VALUE_MONEY, money.getValue());
        values.put(TYPE_MONEY, money.getType());
        values.put(SETTING_VALUE_MONEY, money.getSettingValue());
        values.put(SETTING_TYPE_MONEY, money.getSettingType());
        //updating row
        db.update(TABLE_MONEY, values, ID_MONEY + "=?",
                new String[]{String.valueOf(money.getID())});
        db.close();
    }

    public Money getMoneyEntryByDate(String date) {
        Money money = new Money();
        SQLiteDatabase db = getReadableDatabase();
        String count = "SELECT * FROM " + TABLE_MONEY + " WHERE " + DATE_MONEY + "='" + date + "'";
        Cursor cursor = db.rawQuery(count, null);
        // looping through all rows and adding them to the list
        if (cursor.moveToFirst()) {
            money.setID(Integer.parseInt(cursor.getString(0)));
            money.setDate(cursor.getString(1));
            money.setValue(Float.parseFloat(cursor.getString(2)));
            money.setType(Integer.parseInt(cursor.getString(3)));
            money.setSettingValue(Float.parseFloat(cursor.getString(4)));
            money.setSettingType(Integer.parseInt(cursor.getString(5)));
        }
        cursor.close();
        db.close();
        return money;

    }

    // get all money entries
    public ArrayList<Money> getAllMoneyEntries() {
        ArrayList<Money> moneyList = new ArrayList<Money>();
        //Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_MONEY;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Money money = new Money();
                money.setID(Integer.parseInt(cursor.getString(0)));
                money.setDate(cursor.getString(1));
                money.setValue(Float.parseFloat(cursor.getString(2)));
                money.setType(Integer.parseInt(cursor.getString(3)));
                money.setSettingValue(Float.parseFloat(cursor.getString(4)));
                money.setSettingType(Integer.parseInt(cursor.getString(5)));
                // Adding money to list
                moneyList.add(money);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return moneyList;
    }

    // get all money entries with DataSet values attached
    public ArrayList<MoneyExtended> getAllMoneyPlusDataSetEntries() {
        ArrayList<MoneyExtended> moneyList = new ArrayList<>();
        // Select query
        String selectQuery = "SELECT " + ID_MONEY + ", " +
                DATE_MONEY + ", " +
                VALUE_MONEY + ", " +
                TYPE_MONEY + ", " +
                SETTING_VALUE_MONEY + ", " +
                SETTING_TYPE_MONEY + ", " +
                "SUM(CASE WHEN " + GOAL_VALUE + "=" + YES + " THEN 1 ELSE 0 END), " +
                "SUM(CASE WHEN " + GOAL_VALUE + "=" + NO + " THEN 1 ELSE 0 END), " +
                "SUM(CASE WHEN " + GOAL_VALUE + "=" + NO_SELECT + " THEN 1 ELSE 0 END) " +
                "FROM " + TABLE_MONEY + " INNER JOIN " + TABLE_NAME_GOALS  + " ON " + TABLE_MONEY + "." + DATE_MONEY + "=" + TABLE_NAME_GOALS + "." + DATE +
                " GROUP BY " + DATE_MONEY +
                " ORDER BY " + ID_MONEY;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MoneyExtended money = new MoneyExtended();
                money.setID(Integer.parseInt(cursor.getString(0)));
                money.setDate(cursor.getString(1));
                money.setValue(Float.parseFloat(cursor.getString(2)));
                money.setType(Integer.parseInt(cursor.getString(3)));
                money.setSettingValue(Float.parseFloat(cursor.getString(4)));
                money.setSettingType(Integer.parseInt(cursor.getString(5)));
                money.setValueYes(Integer.parseInt(cursor.getString(6)));
                money.setValueNo(Integer.parseInt(cursor.getString(7)));
                money.setValueOpen(Integer.parseInt(cursor.getString(8)));
                // Adding money to list
                moneyList.add(money);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return moneyList;
    }

    public void deleteAllMoneyEntries() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_MONEY);
        db.close();
    }

    ///////////////////////////////////////////
    // CRUD for Reward
    public Reward getRewardPerDate(String date, int type) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "";
        if (type == 0) {

        } else if (type == 1) {
            query = "SELECT SUM(" + MONEY_GOAL + ") FROM " + TABLE_NAME_GOALS + " WHERE " + DATE + " =?";
        } else if (type == 2) {
            query = "SELECT SUM(" + MONEY_TODO + ") FROM " + TABLE_TODO + " WHERE " + "DATE((" + REMINDER_TIME_TODO + "/1000), 'unixepoch')=?";
        }

        Reward reward = new Reward();
        reward.setDate(date);
        reward.setType(type);

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, new String[] {date});
            if (cursor != null) {
                try {
                    if (cursor.moveToNext()) {
                        reward.setRewardValue(Float.parseFloat(cursor.getString(0)));
                    } else {
                        reward.setRewardValue(0.0f);
                    }
                } finally {
                    cursor.close();
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }



        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            reward.setRewardValue(0.0f);
        }

        return reward;

    }

    public Reward getTotalReward(int type)  {
        // 0 = all, 1 = DailyGoals, 2 = ToDos, 3 = Output Reward
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "";
        if (type == 0) {

        } else if (type == 1) {
            query = "SELECT SUM(" + MONEY_GOAL + ") FROM " + TABLE_NAME_GOALS;
        } else if (type == 2) {
            query = "SELECT SUM(" + MONEY_TODO + ") FROM " + TABLE_TODO;
        }

        Reward reward = new Reward();
        reward.setDate("");
        reward.setType(type);

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        reward.setRewardValue(Float.parseFloat(cursor.getString(0)));
                    } else {
                        reward.setRewardValue(0.0f);
                    }
                } finally {
                    cursor.close();
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return reward;

    }


    ///////////////////////////////////////////
    // CRUD for To Do Database

//    public int addToDo(ToDo toDo) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(NAME_TODO, toDo.getName());
//        values.put(STATE_TODO, toDo.getState());
//        values.put(BOOL_REMINDER_TODO, convertBooleanToInt(toDo.isReminder()));
//        values.put(REMINDER_TIME_TODO, toDo.getReminderTime());
//        values.put(BOOL_SNOOZE_TODO, convertBooleanToInt(toDo.isSnooze()));
//        values.put(BOOL_REWARD_TODO, convertBooleanToInt(toDo.isReward()));
//        values.put(REWARD_AMOUNT_TODO, toDo.getRewardAmount());
//        values.put(MONEY_TODO, toDo.getMoneyToDo());
//        long id = db.insert(TABLE_TODO, null, values);
//        db.close();
//        return (int) id;
//    }
//
//    public void updateToDo (ToDo toDo) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(NAME_TODO, toDo.getName());
//        values.put(STATE_TODO, toDo.getState());
//        values.put(BOOL_REMINDER_TODO, convertBooleanToInt(toDo.isReminder()));
//        values.put(REMINDER_TIME_TODO, toDo.getReminderTime());
//        values.put(BOOL_SNOOZE_TODO, convertBooleanToInt(toDo.isSnooze()));
//        values.put(BOOL_REWARD_TODO, convertBooleanToInt(toDo.isReward()));
//        values.put(REWARD_AMOUNT_TODO, toDo.getRewardAmount());
//        values.put(MONEY_TODO, toDo.getMoneyToDo());
//        db.update(TABLE_TODO, values, ID_TODO + "=?",
//                new String[]{String.valueOf(toDo.getId())});
//        db.close();
//    }
//
//    public ToDo getSingleToDo (int id) {
//        ToDo toDo = new ToDo();
//        SQLiteDatabase db = getReadableDatabase();
//        String query = "SELECT * FROM " + TABLE_TODO + " WHERE " + ID_TODO + "='" + id + "'";
//        Cursor cursor = db.rawQuery(query, null);
//        // looping through all rows and adding them to the list
//        if (cursor.moveToFirst()) {
//            toDo.setId(Integer.parseInt(cursor.getString(0)));
//            toDo.setName(cursor.getString(1));
//            toDo.setState(Integer.parseInt(cursor.getString(2)));
//            int reminder = Integer.parseInt(cursor.getString(3));
//            toDo.setReminder(convertIntToBoolean(reminder));
//            toDo.setReminderTime(Long.parseLong(cursor.getString(4)));
//            int snooze = Integer.parseInt(cursor.getString(5));
//            toDo.setSnooze(convertIntToBoolean(snooze));
//            int reward = Integer.parseInt(cursor.getString(6));
//            toDo.setReward(convertIntToBoolean(reward));
//            toDo.setRewardAmount(Double.parseDouble(cursor.getString(7)));
//            toDo.setMoneyToDo(Float.parseFloat(cursor.getString(8)));
//        }
//        cursor.close();
//        db.close();
//        return toDo;
//    }
//
//    public ToDo getLastToDo () {
//        ToDo toDo = new ToDo();
//        SQLiteDatabase db = getReadableDatabase();
//        String query = "SELECT * FROM " + TABLE_TODO + " ORDER BY " + ID_TODO + " DESC LIMIT 1;";
//        Cursor cursor = db.rawQuery(query, null);
//        if (cursor != null) {
//            cursor.moveToFirst();
//        }
//        toDo.setId(Integer.parseInt(cursor.getString(0)));
//        toDo.setName(cursor.getString(1));
//        toDo.setState(Integer.parseInt(cursor.getString(2)));
//        int reminder = Integer.parseInt(cursor.getString(3));
//        toDo.setReminder(convertIntToBoolean(reminder));
//        toDo.setReminderTime(Long.parseLong(cursor.getString(4)));
//        int snooze = Integer.parseInt(cursor.getString(5));
//        toDo.setSnooze(convertIntToBoolean(snooze));
//        int reward = Integer.parseInt(cursor.getString(6));
//        toDo.setReward(convertIntToBoolean(reward));
//        toDo.setRewardAmount(Double.parseDouble(cursor.getString(7)));
//        toDo.setMoneyToDo(Float.parseFloat(cursor.getString(8)));
//
//        cursor.close();
//        db.close();
//        return toDo;
//    }
//
//    public ArrayList<ToDo> getAllToDos (boolean isOpen) {
//        ArrayList<ToDo> toDoList = new ArrayList<ToDo>();
//        SQLiteDatabase db = this.getWritableDatabase();
//        String selectQuery = "";
//        if (isOpen) {
//            selectQuery = "SELECT * FROM " + TABLE_TODO + " WHERE " + STATE_TODO + "=" + NO_SELECT + " ORDER BY " +  REMINDER_TIME_TODO + " ASC";
//        } else {
//            selectQuery = "SELECT * FROM " + TABLE_TODO + " WHERE " + STATE_TODO + "!=" + NO_SELECT + " ORDER BY " + REMINDER_TIME_TODO + " DESC";
//        }
//        Cursor cursor = db.rawQuery(selectQuery, null);
//        if (cursor.moveToFirst()) {
//            do {
//                ToDo toDo = new ToDo();
//                toDo.setId(Integer.parseInt(cursor.getString(0)));
//                toDo.setName(cursor.getString(1));
//                toDo.setState(Integer.parseInt(cursor.getString(2)));
//                int reminder = Integer.parseInt(cursor.getString(3));
//                toDo.setReminder(convertIntToBoolean(reminder));
//                toDo.setReminderTime(Long.parseLong(cursor.getString(4)));
//                int snooze = Integer.parseInt(cursor.getString(5));
//                toDo.setSnooze(convertIntToBoolean(snooze));
//                int reward = Integer.parseInt(cursor.getString(6));
//                toDo.setReward(convertIntToBoolean(reward));
//                toDo.setRewardAmount(Double.parseDouble(cursor.getString(7)));
//                toDo.setMoneyToDo(Float.parseFloat(cursor.getString(8)));
//                // Adding To Do to list
//                toDoList.add(toDo);
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        db.close();
//        return toDoList;
//    }

//    public void deleteSingleToDo (ToDo toDo) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_TODO, ID_TODO + " = ?", new String[]{String.valueOf(toDo.getId())});
//        db.close();
//    }

    public void deleteAllToDos () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_TODO);
        db.close();
    }

    private int convertBooleanToInt(boolean bool) {
        if (bool) {
            return 1;
        } else {
            return 0;
        }
    }

    private boolean convertIntToBoolean(int integer) {
        if (integer == 1) {
            return true;
        } else {
            return false;
        }
    }


}
