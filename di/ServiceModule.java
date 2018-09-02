package com.benjaminsommer.dailygoals.di;

import com.benjaminsommer.dailygoals.services.DatabaseUpdateService;
import com.benjaminsommer.dailygoals.services.ToDoBootService;
import com.benjaminsommer.dailygoals.services.ToDoChangeReminderService;
import com.benjaminsommer.dailygoals.services.ToDoReminderService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by SOMMER on 10.05.2018.
 */

@Module
abstract class ServiceModule {

    @ContributesAndroidInjector
    abstract DatabaseUpdateService contributeDatabaseUpdateService();

    @ContributesAndroidInjector
    abstract ToDoReminderService contributeToDoReminderService();

    @ContributesAndroidInjector
    abstract ToDoBootService contributeToDoBootService();

    @ContributesAndroidInjector
    abstract ToDoChangeReminderService contributeToDoChangeReminderService();

}
