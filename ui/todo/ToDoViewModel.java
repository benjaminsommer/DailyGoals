package com.benjaminsommer.dailygoals.ui.todo;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.benjaminsommer.dailygoals.entities.StatResult;
import com.benjaminsommer.dailygoals.entities.ToDo;
import com.benjaminsommer.dailygoals.entities.ToDoCover;
import com.benjaminsommer.dailygoals.objects.Resource;
import com.benjaminsommer.dailygoals.repository.ToDoRepository;
import com.benjaminsommer.dailygoals.util.AbsentLiveData;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by SOMMER on 31.03.2018.
 */

public class ToDoViewModel extends ViewModel {

    private MutableLiveData<Boolean> selectedTab = new MutableLiveData<>();
    private LiveData<Resource<List<ToDo>>> toDoList = new MutableLiveData<>();

    private LiveData<Resource<List<ToDoCover>>> toDoCoverList = new MutableLiveData<>();

    private MutableLiveData<Byte> selectedResult = new MutableLiveData<>();
    private LiveData<StatResult> resultList = new MutableLiveData<>();

    private ToDoRepository toDoRepository;

    @Inject
    public ToDoViewModel(final ToDoRepository toDoRepo) {
        this.toDoRepository = toDoRepo;

        toDoList = Transformations.switchMap(selectedTab, new Function<Boolean, LiveData<Resource<List<ToDo>>>>() {
            @Override
            public LiveData<Resource<List<ToDo>>> apply(Boolean aBoolean) {
                if (aBoolean == null) {
                    return AbsentLiveData.create();
                } else {
                    if (aBoolean) {
                        return toDoRepository.getOpenToDos();
                    } else {
                        return toDoRepository.getFinishedToDos();
                    }
                }
            }
        });

        toDoCoverList = Transformations.map(toDoList, new Function<Resource<List<ToDo>>, Resource<List<ToDoCover>>>() {
            @Override
            public Resource<List<ToDoCover>> apply(Resource<List<ToDo>> listResource) {

                if (listResource != null && listResource.data != null && selectedTab.getValue() != null) {

                    List<ToDo> toDoList = listResource.data;
                    List<ToDoCover> toDoCoverList = new ArrayList<>();

                    if (selectedTab.getValue()) {
                        long now = DateTime.now().getMillis();
                        long today = DateTime.now().withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(999).getMillis();
                        long week = DateTime.now().withDayOfWeek(7).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(999).getMillis();

                        int tempInt = -1;
                        for (int x = 0; x < toDoList.size(); x++) {
                            int runningInt = isBetweenTimeFrame(toDoList.get(x).getToDoReminderTime(), now, today, week, toDoList.get(x).isToDoHasReminder());
                            if (tempInt != runningInt) {
                                toDoCoverList.add(new ToDoCover(false, sectionHeaderDescription(runningInt), runningInt, null));
                            }
                            toDoCoverList.add(new ToDoCover(true, null, -1, toDoList.get(x)));
                            tempInt = runningInt;
                        }
                        return Resource.success(toDoCoverList);
                    } else {
                        int tempInt = -1;
                        for (int y = 0; y < toDoList.size(); y++) {
                            int runningInt = toDoList.get(y).getToDoState();
                            if (tempInt != runningInt) {
                                toDoCoverList.add(new ToDoCover(false, sectionHeaderDescription(runningInt), runningInt, null));
                            }
                            toDoCoverList.add(new ToDoCover(true, null, -1, toDoList.get(y)));
                            tempInt = runningInt;
                        }
                        return Resource.success(toDoCoverList);
                    }
                } else {
                    return Resource.error("ListResource is null or empty", null);
                }

            }
        });


        resultList = Transformations.switchMap(selectedResult, new Function<Byte, LiveData<StatResult>>() {
            @Override
            public LiveData<StatResult> apply(Byte aByte) {
                if (aByte == null) {
                    return AbsentLiveData.create();
                } else {
                    if (aByte == StatResult.TODAY || aByte == StatResult.WEEK || aByte == StatResult.TOTAL) {
                        return toDoRepository.getToDoStatResultsForToday(aByte);
                    } else {
                        return AbsentLiveData.create();
                    }
                }
            }
        });

    }

    private static int isBetweenTimeFrame(long reminderTime, long now, long today, long week, boolean hasReminder) {
        if (!hasReminder) {
            return 4;
        }
        if (reminderTime < now) {
            return 0;
        } else if (reminderTime < today) {
            return 1;
        } else if (reminderTime < week) {
            return 2;
        } else {
            return 3;
        }
    }

    private static String sectionHeaderDescription(int selector) {
        switch (selector) {
            case 0:
                return "Überfällig";
            case 1:
                return "Heute";
            case 2:
                return "Diese Woche";
            case 4:
                return "Kein Reminder";
            case 10:
                return "Nicht erledigt";
            case 100:
                return "Erledigt";
            default:
                return "Weitere";
        }
    }

    public void setSelectedTab(boolean tabSelected) {
        if (selectedTab.getValue() != null) {
            if (tabSelected != selectedTab.getValue()) {
                selectedTab.setValue(tabSelected);
            }
        } else {
            selectedTab.setValue(tabSelected);
        }
    }

    public LiveData<Boolean> getSelectedTab() {
        return selectedTab;
    }

    public void setSelectedResult(byte resultSelected) {
        if (selectedResult.getValue() != null) {
            if (resultSelected != selectedResult.getValue()) {
                selectedResult.setValue(resultSelected);
            }
        } else {
            selectedResult.setValue(resultSelected);
        }
    }

    public LiveData<Byte> getSelectedResult() {
        return selectedResult;
    }

    public LiveData<StatResult> getResultList() {
        return resultList;
    }

    public LiveData<Resource<List<ToDo>>> getToDoList() {
        return toDoList;
    }

    public LiveData<Resource<List<ToDoCover>>> getToDoCoverList() {
        return toDoCoverList;
    }

    public ToDo getSpecificToDo(int adapterPos) {
        if (toDoList.getValue() != null) {
            return toDoCoverList.getValue().data.get(adapterPos).getToDo();
        } else {
            return new ToDo();
        }
    }

    public ToDo getSpecificToDoFromRoom(int id) {
        return toDoRepository.getSpecificToDo(id);
    }

    public void addToDo(ToDo toDo) {
        ToDo newToDo = toDo;
        newToDo.setToDoTimeCreated(Calendar.getInstance().getTimeInMillis());
        toDoRepository.addToDo(newToDo);
    }

    public void updateToDo(ToDo toDo) {
        toDoRepository.updateToDo(toDo);
    }

    public void deleteToDo(ToDo toDo) {
        toDoRepository.deleteToDo(toDo);
    }

    public void updateButtonClick(ToDo toDo, int newValue) {
        ToDo newToDo = toDo;
        newToDo.setToDoState(newValue);
        long now = Calendar.getInstance().getTimeInMillis();
        newToDo.setToDoTimeFinished(now);
        newToDo.setToDoReminderTime(now);
        toDoRepository.updateToDo(newToDo);
    }

    public void cancelNotification(ToDo toDo) {
        toDoRepository.cancelNotification(toDo);
    }

}
