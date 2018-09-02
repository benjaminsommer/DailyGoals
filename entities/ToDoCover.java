package com.benjaminsommer.dailygoals.entities;

import android.support.annotation.Nullable;

/**
 * Created by SOMMER on 27.04.2018.
 */

public class ToDoCover {

    private boolean objectType; // false = header; true = To Do
    private String headerText;
    private int selectorCategory; // 0 = überfällig, 1 = heute, 2 = diese Woche, 3 = Rest, 4 = ohne Reminder,
    private ToDo toDo;

    public ToDoCover(boolean objectType, @Nullable String headerText, @Nullable int selectorCategory, @Nullable ToDo toDo) {
        this.objectType = objectType;
        this.headerText = headerText;
        this.selectorCategory = selectorCategory;
        this.toDo = toDo;
    }

    public boolean isObjectType() {
        return objectType;
    }

    public void setObjectType(boolean objectType) {
        this.objectType = objectType;
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    public int getSelectorCategory() {
        return selectorCategory;
    }

    public void setSelectorCategory(int selectorCategory) {
        this.selectorCategory = selectorCategory;
    }

    public ToDo getToDo() {
        return toDo;
    }

    public void setToDo(ToDo toDo) {
        this.toDo = toDo;
    }

    @Override
    public String toString() {
        return "ToDoCover{" +
                "objectType=" + objectType +
                ", headerText='" + headerText + '\'' +
                ", selectorCategory=" + selectorCategory +
                ", toDo=" + toDo +
                '}';
    }
}
