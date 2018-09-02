package com.benjaminsommer.dailygoals.ui.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.benjaminsommer.dailygoals.entities.DataSet;
import com.benjaminsommer.dailygoals.entities.Goal;
import com.benjaminsommer.dailygoals.entities.LocalRemoteId;
import com.benjaminsommer.dailygoals.entities.ToDo;
import com.benjaminsommer.dailygoals.objects.Resource;
import com.benjaminsommer.dailygoals.repository.DataSetRepository;
import com.benjaminsommer.dailygoals.repository.GoalsRepository;
import com.benjaminsommer.dailygoals.repository.ToDoRepository;
import com.benjaminsommer.dailygoals.ui.dataset.DataSetActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by SOMMER on 24.12.2017.
 */

public class LoginViewModel extends ViewModel {

    public static final String TAG = LoginViewModel.class.getSimpleName();

    private GoalsRepository goalsRepository;
    private DataSetRepository dataSetRepository;
    private ToDoRepository toDoRepository;
    private MutableLiveData<Resource<List<Integer>>> goalsStatus;
    private MutableLiveData<Resource<Integer>> syncStatus;

    @Inject
    public LoginViewModel(GoalsRepository goalsRepo, DataSetRepository dataSetRepo, ToDoRepository toDoRepo) {
        this.goalsRepository = goalsRepo;
        this.dataSetRepository = dataSetRepo;
        this.toDoRepository = toDoRepo;
        if (goalsStatus == null) {
            goalsStatus = new MutableLiveData<>();
            goalsStatus.setValue(Resource.loading(Collections.<Integer>emptyList()));
        }
        if (syncStatus == null) {
            syncStatus = new MutableLiveData<>();
            syncStatus.setValue(Resource.loading(-1));
        }

    }

    public void startGoalsCount() {
        final Task<Integer> remoteGoals = goalsRepository.getCurrentRemoteGoalsCount();
        final Task<Integer> localGoals = goalsRepository.getCurrentLocalGoalsCount();
        //Task<Void> timeLimit = goalsRepository.getTimeLimit(5000);


        Task<Void> allTasks = Tasks.whenAll(remoteGoals, localGoals);
        allTasks.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                List<Integer> list = new ArrayList<Integer>(2);
                list.add(0, localGoals.getResult());
                list.add(1, remoteGoals.getResult());
                goalsStatus.setValue(Resource.success(list));
            }
        });
        allTasks.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                goalsStatus.setValue(Resource.error(e.getMessage(), Collections.<Integer>emptyList()));
            }
        });

    }

    public LiveData<Resource<List<Integer>>> getGoalsCount() {
        if (goalsStatus == null) {
            goalsStatus = new MutableLiveData<>();
            goalsStatus.setValue(Resource.loading(Collections.<Integer>emptyList()));
        }
        return goalsStatus;
    }

    public LiveData<Resource<Integer>> getSyncState() {
        if (syncStatus == null) {
            syncStatus = new MutableLiveData<>();
            syncStatus.setValue(Resource.loading(-1));
        }
        return syncStatus;
    }

    public void startSyncProcess(final int selection) {
        syncStatus.setValue(Resource.loading(-1));

        if (selection == 0) {
            Task<Void> deleteLocalGoals = goalsRepository.deleteLocalGoals()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Step 1 in deleteLocalGoals is successful");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Step 1 in deleteLocalGoals is not successful", e.fillInStackTrace());

                        }
                    })
                    .continueWithTask(new Continuation<Void, Task<List<Goal>>>() {
                        @Override
                        public Task<List<Goal>> then(@NonNull Task<Void> task) throws Exception {
                            return goalsRepository.getAllRemoteGoals();
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<List<Goal>>() {
                        @Override
                        public void onSuccess(List<Goal> goals) {
                            Log.d(TAG, "Step 2 in deleteLocalGoals is successful");
                            Log.d(TAG, "Step 2 Goal list: " + goals.toString());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Step 2 in deleteLocalGoals is not successful", e.fillInStackTrace());

                        }
                    })
                    .continueWithTask(new Continuation<List<Goal>, Task<Void>>() {
                        @Override
                        public Task<Void> then(@NonNull Task<List<Goal>> task) throws Exception {
                            return goalsRepository.addGoalListToLocal(task.getResult());
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Step 3 in deleteLocalGoals is successful");

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Step 3 in deleteLocalGoals is not successful", e.fillInStackTrace());

                        }
                    })
                    .continueWithTask(new Continuation<Void, Task<List<DataSet>>>() {
                        @Override
                        public Task<List<DataSet>> then(@NonNull Task<Void> task) throws Exception {
                            return dataSetRepository.getAllRemoteDataSets();
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<List<DataSet>>() {
                        @Override
                        public void onSuccess(List<DataSet> dataSets) {
                            Log.d(TAG, "Step 4 in deleteLocalGoals is successful");

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Step 4 in deleteLocalGoals is not successful", e.fillInStackTrace());

                        }
                    })
                    .continueWithTask(new Continuation<List<DataSet>, Task<Void>>() {
                        @Override
                        public Task<Void> then(@NonNull Task<List<DataSet>> task) throws Exception {
                            for (int x = 0; x < task.getResult().size(); x++) {
                                Log.d(TAG, task.getResult().get(x).toString());
                            }
                            return dataSetRepository.addDataSetList(task.getResult());
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Step 5 in deleteLocalGoals is successful");

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Step 5 in deleteLocalGoals is not successful", e.fillInStackTrace());

                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "deleteLocalGoals is successful");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "deleteLocalGoals is not successful", e.fillInStackTrace());
                        }
                    });

            Task<Void> deleteLocalToDos = toDoRepository.deleteAllLocalToDos()
                    .continueWithTask(new Continuation<Void, Task<List<ToDo>>>() {
                        @Override
                        public Task<List<ToDo>> then(@NonNull Task<Void> task) throws Exception {
                            return toDoRepository.getAllRemoteToDos();
                        }
                    })
                    .continueWithTask(new Continuation<List<ToDo>, Task<Void>>() {
                        @Override
                        public Task<Void> then(@NonNull Task<List<ToDo>> task) throws Exception {
                            return toDoRepository.addToDoList(task.getResult());
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "deleteLocalToDos is successful");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "deleteLocalToDos is not successful", e.fillInStackTrace());
                        }
                    });


            Task<Void> allTasks = Tasks.whenAll(deleteLocalGoals, deleteLocalToDos);
            allTasks.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    syncStatus.setValue(Resource.success(selection));
                    Log.d(TAG, "allTasks is successful");
                }
            });
            allTasks.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    syncStatus.setValue(Resource.error(e.getMessage(), selection));
                    Log.d(TAG, "allTasks is not successful");
                }
            });
        } else if (selection == 1) {
            Task<Void> deleteRemoteGoals = goalsRepository.deleteRemoteGoals()
                    .continueWithTask(new Continuation<Void, Task<List<Goal>>>() {
                        @Override
                        public Task<List<Goal>> then(@NonNull Task<Void> task) throws Exception {
                            return goalsRepository.getLocalGoals();
                        }
                    })
                    .continueWithTask(new Continuation<List<Goal>, Task<List<LocalRemoteId>>>() {
                        @Override
                        public Task<List<LocalRemoteId>> then(@NonNull Task<List<Goal>> task) throws Exception {
                            return goalsRepository.insertBatchToRemote(task.getResult());
                        }
                    })
                    .continueWithTask(new Continuation<List<LocalRemoteId>, Task<Void>>() {
                        @Override
                        public Task<Void> then(@NonNull Task<List<LocalRemoteId>> task) throws Exception {
                            return goalsRepository.updateRemoteIdInRoom(task.getResult());
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            syncStatus.setValue(Resource.success(selection));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            syncStatus.setValue(Resource.error(e.getMessage(), selection));
                        }
                    });
        } else if (selection == 2) {
            Task<LiveData<Resource<List<Goal>>>> getLocalGoals = goalsRepository.getLocalGoals()
                    .continueWithTask(new Continuation<List<Goal>, Task<List<LocalRemoteId>>>() {
                        @Override
                        public Task<List<LocalRemoteId>> then(@NonNull Task<List<Goal>> task) throws Exception {
                            return goalsRepository.addListOfGoals(task.getResult());
                        }
                    })
                    .continueWithTask(new Continuation<List<LocalRemoteId>, Task<Void>>() {
                        @Override
                        public Task<Void> then(@NonNull Task<List<LocalRemoteId>> task) throws Exception {
                            return goalsRepository.updateRemoteIdInRoom(task.getResult());
                        }
                    })
                    .continueWith(new Continuation<Void, LiveData<Resource<List<Goal>>>>() {
                        @Override
                        public LiveData<Resource<List<Goal>>> then(@NonNull Task<Void> task) throws Exception {
                            return goalsRepository.getGoalList();
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<LiveData<Resource<List<Goal>>>>() {
                        @Override
                        public void onSuccess(LiveData<Resource<List<Goal>>> resourceLiveData) {
                            syncStatus.setValue(Resource.success(selection));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            syncStatus.setValue(Resource.error(e.getMessage(), selection));
                        }
                    });
        }
    }



}
