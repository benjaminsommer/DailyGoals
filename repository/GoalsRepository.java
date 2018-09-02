package com.benjaminsommer.dailygoals.repository;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.benjaminsommer.dailygoals.database.DataSetDao;
import com.benjaminsommer.dailygoals.entities.DataSet;
import com.benjaminsommer.dailygoals.entities.Goal;
import com.benjaminsommer.dailygoals.database.GoalsDao;
import com.benjaminsommer.dailygoals.di.AppExecutors;
import com.benjaminsommer.dailygoals.entities.LocalRemoteId;
import com.benjaminsommer.dailygoals.firebase.ApiResponse;
import com.benjaminsommer.dailygoals.firebase.FirebaseService;
import com.benjaminsommer.dailygoals.objects.Resource;
import com.benjaminsommer.dailygoals.util.GoalDataSetCalcHelperClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.android.gms.tasks.Tasks.await;

/**
 * Created by SOMMER on 11.11.2017.
 */

@Singleton
public class GoalsRepository {

    private static final String TAG = GoalsRepository.class.getSimpleName();

    private final GoalsDao goalsDao;
    private final DataSetDao dataSetDao;
    private final AppExecutors appExecutors;
    private final FirebaseService firebaseService;
    private final Executor executor;

    private FirebaseAuth mAuth;
    private GoalDataSetCalcHelperClass calcHelperClass;

    @Inject
    public GoalsRepository(Executor executor, AppExecutors appExecutors, GoalsDao goalsDao, DataSetDao dataSetDao, FirebaseService firebaseService) {
        this.executor = executor;
        this.appExecutors = appExecutors;
        this.goalsDao = goalsDao;
        this.dataSetDao = dataSetDao;
        this.firebaseService = firebaseService;
        mAuth = FirebaseAuth.getInstance();
        calcHelperClass = new GoalDataSetCalcHelperClass();
    }

    public LiveData<Resource<List<Goal>>> getGoalList() {
        return new NetworkBoundResource<List<Goal>, List<Goal>>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull List<Goal> item) {

                List<String> remote_ids = new ArrayList<String>();
                for (int x = 0; x < item.size(); x++) {
                    remote_ids.add(item.get(x).getGoalRemoteId());
                }
                List<LocalRemoteId> temp_ids = goalsDao.receiveLocalIdFromRemoteId(remote_ids);
                Map<String, Integer> ids = new HashMap<String, Integer>();
                for (int z = 0; z < temp_ids.size(); z++) {
                    ids.put(temp_ids.get(z).getRemoteId(), temp_ids.get(z).getLocalId());
                }
                for (int y = 0; y < item.size(); y++) {
                    if (!item.get(y).getGoalRemoteId().equals(null)) {
                        if (ids.containsKey(item.get(y).getGoalRemoteId())) {
                            item.get(y).setGoalId(ids.get(item.get(y).getGoalRemoteId()));
                        } else {
                            item.get(y).setGoalId(0);
                        }

                    }
                }
                goalsDao.insertGoals(item);

            }

            @Override
            protected boolean shouldFetch(@Nullable List<Goal> data) {
                //return data == null || data.isEmpty();
                return true; // to test if fetching is working all time
            }

            @NonNull
            @Override
            protected LiveData<List<Goal>> loadFromDb() {
                LiveData<List<Goal>> temp = goalsDao.loadAllGoals();
                List<Goal> tempList = temp.getValue();
                if (tempList != null) {
                    for (int b = 0; b < tempList.size(); b++) {
                        Log.d("loadFromDb", tempList.get(b).toString());
                    }
                }
                return temp;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Goal>>> createCall() {
                return firebaseService.getGoals();
            }

            @Override
            protected void onFetchFailed() {
                super.onFetchFailed();
            }
        }.asLiveData();
    }

    public Task<List<Goal>> getAllRemoteGoals() {
        return firebaseService.getFirebaseGoalsAsTask();
    }

    public Task<Void> addGoalListToLocal(final List<Goal> list) {
        return Tasks.call(executor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                goalsDao.insertGoals(list);
                return null;
            }
        });
    }

    public void addGoal() {


        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                LocalDate today = LocalDate.now();
                Goal goal = new Goal();
                // insert in Room
                long goalId = goalsDao.addGoal(goal);
                goal.setGoalId((int) goalId);
                if (mAuth.getCurrentUser() != null) {
                    // insert in Firebase and update key as soon as added
                    String goalRemoteId = firebaseService.addGoal(goal);
                    // add remote id to goal
                    goal.setGoalRemoteId(goalRemoteId);
                } else {
                    goal.setGoalRemoteId("LOCAL" + String.valueOf(goalId));
                }
                goalsDao.updateGoal(goal);

                DataSet dataSet = calcHelperClass.generateSingleDataSet(goal, today);
                if (dataSet != null) {
                    long dataSetId = dataSetDao.insertDataSet(dataSet);
                    dataSet.setDataSetId((int) dataSetId);
                    if (mAuth.getCurrentUser() != null) {
                        String dataSetRemoteId = firebaseService.addDataSet(dataSet);
                        dataSet.setDataSetRemoteId(dataSetRemoteId);
                        dataSetDao.updateDataSet(dataSet);
                    } else {
                        dataSet.setDataSetRemoteId("LOCAL" + String.valueOf(dataSetId));
                    }
                }
                return null;
            }
        }.execute();

//        new AsyncTask<Void, Void, Long>() {
//            @Override
//            protected Long doInBackground(Void... params) {
//                Goal goal = new Goal();
//                return goalsDao.addGoal(goal);
//            }
//
//            @Override
//            protected void onPostExecute(Long aLong) {
//                if (aLong > 0) {
//                    Goal goal = new Goal();
//                    goal.setGoalId(aLong.intValue());
//                    String remote_id = firebaseService.addGoal(goal);
//                    goal.setGoalRemoteId(remote_id);
//                    replaceRemoteId(goal);
//                }
//            }
//        }.execute();

    }

    public Task<List<LocalRemoteId>> addListOfGoals(List<Goal> list) {
        return firebaseService.addListOfGoals(executor, list);
    }

    public void updateGoal(final Goal goal) {
        new AsyncTask<Goal, Void, Integer>() {
            @Override
            protected Integer doInBackground(Goal... goals) {
                goalsDao.updateGoal(goals[0]);
                return goal.getGoalId();
            }

            @Override
            protected void onPostExecute(Integer aInt) {
                firebaseService.updateGoal(goal);
                super.onPostExecute(aInt);
            }
        }.execute(goal);
    }

    public void setExpandedToFalse(final List<Goal> finalList) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Log.d("GoalsRepository", "setExpandedToFalse is called");
                goalsDao.setExpandedToFalse(finalList);
                return null;
            }
        }.execute();
    }

    public Task<Void> deleteLocalGoals() {
        // TAKE CARE - due to FOREIGN KEY CONSTRAINT both DataSets and Goals are deleted in one method
        final TaskCompletionSource<Void> source = new TaskCompletionSource<>();
        Task<Void> task = source.getTask();
        Task<Void> deleteLocalGoals = Tasks.call(executor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                List<DataSet> dataSetList = dataSetDao.getAllDataSets();
                dataSetDao.deleteAllDataSets(dataSetList);
                List<Goal> list = goalsDao.loadActiveAndNonActiveGoals();
                goalsDao.deleteGoals(list);
                return null;
            }
        });
        deleteLocalGoals
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        source.setResult(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        source.setException(e);
                    }
                });
        return task;
    }

    public Task<Void> deleteRemoteGoals() {
       return firebaseService.deleteAllFirestoreGoals(50, executor);
    }

    public Task<List<Goal>> getLocalGoals() {
        return Tasks.call(executor, new Callable<List<Goal>>() {
            @Override
            public List<Goal> call() throws Exception {
                return goalsDao.loadActiveAndNonActiveGoals();
            }
        });
    }

    public List<Goal> getLocalActiveGoals() {
        return Tasks.call(executor, new Callable<List<Goal>>() {
            @Override
            public List<Goal> call() throws Exception {
                return goalsDao.loadAllActiveGoals();
            }
        }).getResult();
    }

    public Task<List<LocalRemoteId>> insertBatchToRemote(List<Goal> list) {
        return firebaseService.insertLocalGoalsToFirestore(list, executor);
    }

    public Task<Void> updateRemoteIdInRoom(final List<LocalRemoteId> list) {
        return Tasks.call(executor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (int x = 0; x < list.size(); x++) {
                    Goal goal = goalsDao.loadAsGoal(list.get(x).getLocalId());
                    goal.setGoalRemoteId(list.get(x).getRemoteId());
                    goalsDao.updateGoal(goal);
                }
                return null;
            }
        });
    }

    public Task<Integer> getCurrentLocalGoalsCount() {

        final TaskCompletionSource<Integer> localResult = new TaskCompletionSource<>();
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                return goalsDao.countGoals();
            }

            @Override
            protected void onPostExecute(Integer integer) {
                localResult.setResult(integer);
                super.onPostExecute(integer);
            }
        }.execute();

        Task<Integer> localSource = localResult.getTask();
        return localSource;

    }

    public Task<Integer> getCurrentRemoteGoalsCount() {
        return firebaseService.getRemoteGoalsCount();
    }



}
