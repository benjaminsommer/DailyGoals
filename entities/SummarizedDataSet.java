package com.benjaminsommer.dailygoals.entities;

/**
 * Created by DEU209213 on 03.01.2017.
 */
public class SummarizedDataSet {

    //private variables
    private int _id;
    private String _date;
    private int _goal_id;
    private int _value_yes;
    private int _value_no;
    private int _value_open;


    // Empty constructor
    public SummarizedDataSet() {
        _goal_id = 1;
    }

    // constructor
    public SummarizedDataSet(int id, String date, int goal_id, int value_yes, int value_no, int value_open) {
        this._id = id;
        this._date = date;
        this._goal_id = goal_id;
        this._value_yes = value_yes;
        this._value_no = value_no;
        this._value_open = value_open;
    }

    // constructor
    public SummarizedDataSet(String date, int goal_id, int value_yes, int value_no, int value_open) {
        this._date = date;
        this._goal_id = goal_id;
        this._value_yes = value_yes;
        this._value_no = value_no;
        this._value_open = value_open;
    }

    // getting ID
    public int getID() {
        return this._id;
    }

    // settingID
    public void setID(int id) {
        this._id = id;
    }

    // getting date
    public String getDate() {
        return this._date;
    }

    // setting date
    public void setDate(String date) {
        this._date = date;
    }

    // getting goal_id
    public int getGoalID() {
        return this._goal_id;
    }

    // setting goal_id corresponding to right goal
    public void setGoalID(int goal_id) {
        this._goal_id = goal_id;
    }

    // getting value yes
    public int getValuesYes() {
        return this._value_yes;
    }

    // setting value yes
    public void setValueYes(int valueYes) {
        this._value_yes = valueYes;
    }

    // getting value no
    public int getValuesNo() {
        return this._value_no;
    }

    // setting value no
    public void setValueNo(int valueNo) {
        this._value_no = valueNo;
    }

    // getting value no
    public int getValuesOpen() {
        return this._value_open;
    }

    // setting value no
    public void setValueOpen(int valueOpen) {
        this._value_open = valueOpen;
    }

}
