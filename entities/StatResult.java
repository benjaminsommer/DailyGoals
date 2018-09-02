package com.benjaminsommer.dailygoals.entities;

/**
 * Created by SOMMER on 15.04.2018.
 */

public class StatResult {

    public static final byte TODAY = 0;
    public static final byte WEEK = 1;
    public static final byte TOTAL = 2;

    private String date;
    private int resultYes;
    private int resultNo;
    private int resultOpen;
    private float resultPercent;
    private float resultMoney;

    public StatResult() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getResultYes() {
        return resultYes;
    }

    public void setResultYes(int resultYes) {
        this.resultYes = resultYes;
    }

    public int getResultNo() {
        return resultNo;
    }

    public void setResultNo(int resultNo) {
        this.resultNo = resultNo;
    }

    public int getResultOpen() {
        return resultOpen;
    }

    public void setResultOpen(int resultOpen) {
        this.resultOpen = resultOpen;
    }

    public float getResultPercent() {
        return resultPercent;
    }

    public void setResultPercent(float resultPercent) {
        this.resultPercent = resultPercent;
    }

    public float getResultMoney() {
        return resultMoney;
    }

    public void setResultMoney(float resultMoney) {
        this.resultMoney = resultMoney;
    }

    @Override
    public String toString() {
        return "StatResult{" +
                "date='" + date + '\'' +
                ", resultYes=" + resultYes +
                ", resultNo=" + resultNo +
                ", resultOpen=" + resultOpen +
                ", resultPercent=" + resultPercent +
                ", resultMoney=" + resultMoney +
                '}';
    }
}
