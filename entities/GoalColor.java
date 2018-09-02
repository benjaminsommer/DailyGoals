package com.benjaminsommer.dailygoals.entities;

import com.benjaminsommer.dailygoals.R;

/**
 * Created by DEU209213 on 29.05.2016.
 */
public class GoalColor {

    private int _id;
    private String _name;
    private int _colorFull;
    private int _colorLight;

    // constructor
    public GoalColor() {
        _id = 9;
        _name = String.valueOf(R.string.color_Transparent);
        _colorFull = R.color.cat_Transparent;
        _colorLight = R.color.cat_Transparent;
    }

    public GoalColor(int id, String name, int colorFull, int colorLight) {
        this._id = id;
        this._name = name;
        this._colorFull = colorFull;
        this._colorLight = colorLight;
    }

    public int getID() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public int getColorFull() {
        return _colorFull;
    }

    public int getColorLight() {
        return _colorLight;
    }

}
