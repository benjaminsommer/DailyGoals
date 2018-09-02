package com.benjaminsommer.dailygoals.entities;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DEU209213 on 30.11.2016.
 */
public class Week implements ParentListItem {

    private String weekText;
    private List<StatResult> listDays;

    public Week(String weekText, List<StatResult> listDays) {
        this.weekText = weekText;
        this.listDays = listDays;
    }

    public String getWeekText() {
        return weekText;
    }

    public void setWeekText(String weekText) {
        this.weekText = weekText;
    }

    public List<StatResult> getListDays() {
        return listDays;
    }

    public void setListDays(List<StatResult> listDays) {
        this.listDays = listDays;
    }

    @Override
    public List<StatResult> getChildItemList() {
        return this.listDays;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
