package com.benjaminsommer.dailygoals.entities;

/**
 * Created by DEU209213 on 30.11.2016.
 */
public class DaySummary {

    private String week;
    private String date;
    private int valueYes;
    private int valueNo;
    private int valueOpen;
    private double money;

    public DaySummary() {
    }

    public DaySummary(String week, String date, int value_yes, int value_no, int value_open, double money) {
        this.week = week;
        this.date = date;
        this.valueYes = value_yes;
        this.valueNo = value_no;
        this.valueOpen = value_open;
        this.money = money;
    }

    // getters and setters
    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // getting value yes
    public int getValuesYes() {
        return this.valueYes;
    }

    // setting value yes
    public void setValueYes(int valueYes) {
        this.valueYes = valueYes;
    }

    // getting value no
    public int getValuesNo() {
        return this.valueNo;
    }

    // setting value no
    public void setValueNo(int valueNo) {
        this.valueNo = valueNo;
    }

    // getting value no
    public int getValuesOpen() {
        return this.valueOpen;
    }

    // setting value no
    public void setValueOpen(int valueOpen) {
        this.valueOpen = valueOpen;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

}
