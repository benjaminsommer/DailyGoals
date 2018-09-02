package com.benjaminsommer.dailygoals.di;

import com.benjaminsommer.dailygoals.ui.dataset.DataSetActivity;
import com.benjaminsommer.dailygoals.ui.date_selection.DateSelectionActivity;
import com.benjaminsommer.dailygoals.ui.goals.GoalsActivity;
import com.benjaminsommer.dailygoals.ui.login.LoginActivity;
import com.benjaminsommer.dailygoals.ui.main.MainActivity;
import com.benjaminsommer.dailygoals.ui.settings.SettingsActivity;
import com.benjaminsommer.dailygoals.ui.statistics.StatisticActivity;
import com.benjaminsommer.dailygoals.ui.todo.ToDoActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by SOMMER on 19.11.2017.
 */

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract GoalsActivity contributeGoalsActivity();

    @ContributesAndroidInjector
    abstract DataSetActivity contributeDataSetActivity();

    @ContributesAndroidInjector
    abstract LoginActivity contributeLoginActivity();

    @ContributesAndroidInjector
    abstract ToDoActivity contributeToDoActivity();

    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector
    abstract SettingsActivity settingsActivity();

    @ContributesAndroidInjector
    abstract DateSelectionActivity dateSelectionActivity();

    @ContributesAndroidInjector
    abstract StatisticActivity statisticActivity();

}
