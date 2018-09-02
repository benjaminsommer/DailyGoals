package com.benjaminsommer.dailygoals.entities;

/**
 * Created by SOMMER on 04.08.2017.
 */

public class Reward {

    private String _date;
    private int _type; // 0 = all, 1 = Input Daily Goal, 2 = Input To Do; 3 = Output Reward
    private float _reward_value;

    public Reward() {
        _reward_value = 0.0f;
    }

    public String getDate() {
        return _date;
    }

    public void setDate(String date) {
        this._date = date;
    }

    public int getType() {
        return _type;
    }

    public void setType(int type) {
        this._type = type;
    }

    public float getRewardValue() {
        return _reward_value;
    }

    public void setRewardValue(float rewardValue) {
        this._reward_value = rewardValue;
    }
}
