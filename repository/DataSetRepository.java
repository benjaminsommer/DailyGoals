package com.benjaminsommer.dailygoals.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.benjaminsommer.dailygoals.database.DataSetDao;
import com.benjaminsommer.dailygoals.di.AppExecutors;
import com.benjaminsommer.dailygoals.entities.CombinedDataSet;
import com.benjaminsommer.dailygoals.entities.DataSet;
import com.benjaminsommer.dailygoals.entities.Goal;
import com.benjaminsommer.dailygoals.entities.StatResult;
import com.benjaminsommer.dailygoals.firebase.ApiResponse;
import com.benjaminsommer.dailygoals.firebase.FirebaseService;
import com.benjaminsommer.dailygoals.objects.Resource;
import com.benjaminsommer.dailygoals.util.AbsentLiveData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by SOMMER on 25.01.2018.
 */

@Singleton
public class DataSetRepository {

    private final String TAG = DataSetRepository.class.getSimpleName();

    private final DataSetDao dataSetDao;
    private final AppExecutors appExecutors;
    private final FirebaseService firebaseService;
    private final Executor executor;

    @Inject
    public DataSetRepository(DataSetDao dataSetDao, AppExecutors appExecutors, FirebaseService firebaseService, Executor executor) {
        this.dataSetDao = dataSetDao;
        this.appExecutors = appExecutors;
        this.firebaseService = firebaseService;
        this.executor = executor;
    }

    public LiveData<Resource<List<CombinedDataSet>>> getDataSetListByDate(final String date) {
          return new NetworkBoundResource<List<CombinedDataSet>, List<CombinedDataSet>>(appExecutors) {
              @Override
              protected void saveCallResult(@NonNull List<CombinedDataSet> item) {
                  List<DataSet> localList = dataSetDao.getDataSetsByDate(date);
                  Map<String, Integer> ids = new HashMap<String, Integer>();
                  for (int z = 0; z < localList.size(); z++) {
                      Log.d(TAG, "saveCallResult, localList: " + localList.get(z).toString());
                      ids.put(localList.get(z).getDataSetRemoteId(), localList.get(z).getDataSetId());
                  }
                  List<DataSet> list = new ArrayList<>();
                  Log.d(TAG, "saveCallResult item size: " + item.size());
                  for (int x = 0; x < item.size(); x++) {
                      list.add(item.get(x).getDataSet());
                      String remoteId = list.get(x).getDataSetRemoteId();
                      if (ids.containsKey(remoteId)) {
                          list.get(x).setDataSetId(ids.get(remoteId));
                      }
                      Log.d("DataSetRepository", "saveCallResult: " + item.get(x).toString());

                  }
                  Log.d(TAG, "List: " + list.toString());
                  dataSetDao.insertDataSets(list);
              }

              @Override
              protected boolean shouldFetch(@Nullable List<CombinedDataSet> data) {
                  return true;
              }

              @NonNull
              @Override
              protected LiveData<List<CombinedDataSet>> loadFromDb() {
                  LiveData<List<CombinedDataSet>> liveData = dataSetDao.getDataSetListByDate(date);
                  if (liveData.getValue() == null) {
                      Log.d(TAG, "loadFromDb: liveData empty");
                  } else {
                      Log.d(TAG, "loadFromDb size: " + liveData.getValue().size());
                      for (int y = 0; y < liveData.getValue().size(); y++) {
                          Log.d(TAG, "loadFromDb: " + liveData.getValue().get(y).toString());
                      }
                  }
                  return liveData;
              }

              @NonNull
              @Override
              protected LiveData<ApiResponse<List<CombinedDataSet>>> createCall() {
                  LiveData<ApiResponse<List<CombinedDataSet>>> tempList =  firebaseService.combinedDataSetsAndGoalsByDate(date);
                  if (tempList.getValue() == null) {
                      Log.d("DataSetRepository", "tempList is empty");
                  } else {
                      Log.d("DataSetRepository", "List Length tempList: " + tempList.getValue().body.size());
                      for (int x = 0; x < tempList.getValue().body.size(); x++) {
                          Log.d("DataSetRepository", tempList.getValue().body.get(x).toString());
                      }
                  }
                  return tempList;
              }
          }.asLiveData();
    }

    public LiveData<Resource<List<StatResult>>> getDataSetStatList() {
        return new NetworkBoundResource<List<StatResult>, List<StatResult>>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull List<StatResult> item) {
            }

            @Override
            protected boolean shouldFetch(@Nullable List<StatResult> data) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<StatResult>> loadFromDb() {
                return dataSetDao.getStatResultOnDate();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<StatResult>>> createCall() {
                return AbsentLiveData.create();
            }
        }.asLiveData();
    }

    public Task<List<DataSet>> getAllRemoteDataSets() {
        return firebaseService.getFirebaseDataSetsByDate("all");
    }

    public Task<Void> addDataSetList(final List<DataSet> list) {
        return Tasks.call(executor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (int x = 0; x < list.size(); x++) {
                    Log.d(TAG, "Method addDataSetList: x = " + x + ": " + list.get(x).toString());
                    dataSetDao.insertDataSet(list.get(x));
                }
                //dataSetDao.insertDataSets(list);
                return null;
            }
        });
    }

    public Task<Void> deleteAllLocalDataSets() {
        final TaskCompletionSource<Void> source = new TaskCompletionSource<>();
        Task<Void> task = source.getTask();
        Task<Void> deleteLocalGoals = Tasks.call(executor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                List<DataSet> dataSetList = dataSetDao.getAllDataSets();
                dataSetDao.deleteAllDataSets(dataSetList);
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
    
    public void updateDataSet(DataSet dataSet) {
        new updateDataSetAsync(dataSetDao, firebaseService).execute(dataSet);
    }

    public void deleteDataSet(DataSet dataSet) {
        new deleteDataSetAsync(dataSetDao, firebaseService).execute(dataSet);
    }

    private static class updateDataSetAsync extends AsyncTask<DataSet, Void, Void> {

        DataSetDao dataSetDao;
        FirebaseService firebaseService;

        public updateDataSetAsync(DataSetDao dataSetDao, FirebaseService firebaseService) {
            super();
            this.dataSetDao = dataSetDao;
            this.firebaseService = firebaseService;
        }

        @Override
        protected Void doInBackground(DataSet... dataSets) {
            dataSetDao.updateDataSet(dataSets[0]);
            firebaseService.updateDataSet(dataSets[0]);
            return null;
        }
    }

    private static class deleteDataSetAsync extends AsyncTask<DataSet, Void, Void> {

        DataSetDao dataSetDao;
        FirebaseService firebaseService;

        public deleteDataSetAsync(DataSetDao dataSetDao, FirebaseService firebaseService) {
            super();
            this.dataSetDao = dataSetDao;
            this.firebaseService = firebaseService;
        }

        @Override
        protected Void doInBackground(DataSet... dataSets) {
            dataSetDao.deleteDataSet(dataSets[0]);
            firebaseService.deleteDataSet(dataSets[0]);
            return null;
        }
    }

    public LiveData<String> getMaxDate() {
        return dataSetDao.getMaxDate();
    }

    public LiveData<String> getMinDate() {
        return dataSetDao.getMinDate();
    }

    public LiveData<StatResult> getDataSetStatResultsForToday(byte period) {
        LiveData<StatResult> liveData;
        switch (period) {
            case StatResult.TODAY:
                liveData = dataSetDao.getStatResultForToday();
                break;
            case StatResult.WEEK:
                liveData = dataSetDao.getStatResultForWeek();
                break;
            case StatResult.TOTAL:
                liveData = dataSetDao.getStatResultForTotal();
                break;
            default:
                liveData = AbsentLiveData.create();
                break;
        }
        return liveData;
    }

    @WorkerThread
    public int getOpenDataSetCount(final String date) {
        return dataSetDao.getOpenDataSetsPerDate(date);
    }

}
