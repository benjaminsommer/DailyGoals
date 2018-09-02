package com.benjaminsommer.dailygoals.ui.goals;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.benjaminsommer.dailygoals.entities.Goal;
import com.benjaminsommer.dailygoals.repository.GoalsRepository;
import com.benjaminsommer.dailygoals.objects.Resource;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by SOMMER on 11.11.2017.
 */

public class GoalsViewModel extends ViewModel {

    private GoalsRepository goalsRepository;
    private LiveData<Resource<List<Goal>>> goalsList;

    @Inject
    public GoalsViewModel(GoalsRepository goalsRepo) {
        goalsRepository = goalsRepo;
        goalsList = goalsRepo.getGoalList();

//// TEST OBJECT
//        Goal goal = new Goal();
//        goal.setId(1);
//        goal.setGoalName("Test ViewModel");
//
//        List<Goal> list = new ArrayList<>();
//        list.add(goal);
//
//        Resource<List<Goal>> resource = new Resource<>(Status.SUCCESS, list, null);
//
//        goalsList = new MutableLiveData<>();
//        goalsList.setValue(resource);
//
//        Log.d("GoalsViewModel", String.valueOf(goalsList.getValue().data.size()));
//// TEST OBJECT

    }

    public LiveData<Resource<List<Goal>>> getGoalsList() {
        if (goalsList == null || goalsList.getValue().data == null) {
            goalsList = goalsRepository.getGoalList();
        }
        if (goalsList != null && goalsList.getValue().data != null) {
            for (int i = 0; i < goalsList.getValue().data.size(); i++) {
                Log.d("GoalsViewModel", goalsList.getValue().data.toString());
            }
        }
        return goalsList;
    }

    public Goal getSingleGoal(int position) {
        if (goalsList != null && goalsList.getValue().data != null) {
            return goalsList.getValue().data.get(position);
        } else {
            return new Goal();
        }
    }

    public void addGoal() {
        goalsRepository.addGoal();
    }

    public void updateGoal(Goal goal) {
        goalsRepository.updateGoal(goal);
    }

    public void setExpandedToFalse() {
        Log.d("GoalsViewModel", "setExpandedToFalse is called");
        List<Goal> finalList = goalsList.getValue().data;
        for (int x = 0; x < finalList.size(); x++) {
            finalList.get(x).setGoalExpanded(false);
        }
        goalsRepository.setExpandedToFalse(finalList);

        if (goalsList != null && goalsList.getValue().data != null) {
            for (int i = 0; i < goalsList.getValue().data.size(); i++) {
                Log.d("GoalsViewModel", goalsList.getValue().data.get(i).toString());
            }
        }

    }

}
