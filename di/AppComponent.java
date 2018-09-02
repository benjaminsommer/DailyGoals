package com.benjaminsommer.dailygoals.di;

import android.app.Application;

import com.benjaminsommer.dailygoals.MyApplication;
import com.benjaminsommer.dailygoals.workmanager.DailyDataSetReminderWorker;
import com.benjaminsommer.dailygoals.workmanager.InitialOneTimeForDailyReminderWorker;
import com.benjaminsommer.dailygoals.workmanager.ToDoChangeNotificationWorker;
import com.benjaminsommer.dailygoals.workmanager.ToDoFinishNotificationWorker;
import com.benjaminsommer.dailygoals.workmanager.ToDoNotificationWorker;
import com.evernote.android.job.JobManager;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

/**
 * Created by SOMMER on 18.11.2017.
 */

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        AppModule.class,
        ActivityModule.class,
        ServiceModule.class
})
public interface AppComponent {

    @Component.Builder
    interface Builder {
        AppComponent build();
        @BindsInstance
        Builder application(Application application);
    }

    void inject(MyApplication myApplication);
//    void inject(JobManager jobManager);
    void inject(ToDoNotificationWorker toDoNotificationWorker);
    void inject(ToDoChangeNotificationWorker toDoChangeNotificationWorker);
    void inject(ToDoFinishNotificationWorker toDoFinishNotificationWorker);
    void inject(DailyDataSetReminderWorker dailyDataSetReminderWorker);
    void inject(InitialOneTimeForDailyReminderWorker initialOneTimeForDailyReminderWorker);
}
