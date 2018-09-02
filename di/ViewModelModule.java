package com.benjaminsommer.dailygoals.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.benjaminsommer.dailygoals.ui.dataset.DataSetViewModel;
import com.benjaminsommer.dailygoals.ui.date_selection.DateSelectionViewModel;
import com.benjaminsommer.dailygoals.ui.todo.ToDoViewModel;
import com.benjaminsommer.dailygoals.viewmodel.DailyGoalsViewModelFactory;
import com.benjaminsommer.dailygoals.ui.goals.GoalsViewModel;
import com.benjaminsommer.dailygoals.ui.login.LoginViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Created by SOMMER on 18.11.2017.
 */

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(GoalsViewModel.class)
    abstract ViewModel bindGoalsViewModel(GoalsViewModel goalsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    abstract ViewModel bindLoginViewModel(LoginViewModel loginViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DataSetViewModel.class)
    abstract ViewModel bindDataSetViewModel(DataSetViewModel dataSetViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ToDoViewModel.class)
    abstract ViewModel bindToDoViewModel(ToDoViewModel toDoViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DateSelectionViewModel.class)
    abstract ViewModel bindDateSelectionViewModel(DateSelectionViewModel dateSelectionViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(DailyGoalsViewModelFactory factory);

}
