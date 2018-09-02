package com.benjaminsommer.dailygoals.entities;

/**
 * Created by DEU209213 on 08.01.2017.
 */
public class MoneyExtended {

    private int _id;
    private String _date;
    private float _value;
    private int _type; // 1 = Input, 2 = Output
    private float _setting_value;
    private int _setting_type; // 1 = count per goal, 2 = count for all goals
    private int _value_yes;
    private int _value_no;
    private int _value_open;

    public MoneyExtended() {
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
