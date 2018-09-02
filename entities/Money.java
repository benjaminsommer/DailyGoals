package com.benjaminsommer.dailygoals.entities;

/**
 * Created by DEU209213 on 23.12.2016.
 */
public class Money {

//    private int _id;
//    private int _type; // 1 = Input Daily Goal, 2 = Input To Do; 3 = Output Reward

    private int _id;
    private String _date;
    private float _value;
    private int _type; // 1 = Input, 2 = Output
    private float _setting_value;
    private int _setting_type; // 1 = count per goal, 2 = count for all goals

    public Money() {
        _value = 0.0f;
        _type = 1;
        _setting_value = 0.0f;
        _setting_type = 1;
    }

    public int getID() {
        return _id;
    }

    public void setID(int id) {
        this._id = id;
    }

    public String getDate() {
        return _date;
    }

    public void setDate(String date) {
        this._date = date;
    }

    public float getValue() {
        return _value;
    }

    public void setValue(float value) {
        this._value = value;
    }

    public int getType() {
        return _type;
    }

    public void setType(int type) {
        this._type = type;
    }

    public float getSettingValue() {
        return _setting_value;
    }

    public void setSettingValue(float settingValue) {
        this._setting_value = settingValue;
    }

    public int getSettingType() {
        return _setting_type;
    }

    public void setSettingType(int settingType) {
        this._setting_type = settingType;
    }

}
