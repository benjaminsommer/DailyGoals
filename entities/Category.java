package com.benjaminsommer.dailygoals.entities;

import android.graphics.drawable.Drawable;

import com.benjaminsommer.dailygoals.R;

/**
 * Created by DEU209213 on 29.05.2016.
 */
public class Category {

    private int _id;
    private String _name;
    private Drawable _icon;

    public Category() {
        _id = 15;
        _name = String.valueOf(R.string.cat_others);
        _icon = null;
    }

    public Category (int id, String name, Drawable icon) {
        this._id = id;
        this._name = name;
        this._icon = icon;
    }

    public int getID() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public Drawable getIcon() {
        return _icon;
    }

}
