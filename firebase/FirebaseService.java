package com.benjaminsommer.dailygoals.firebase;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.benjaminsommer.dailygoals.entities.CombinedDataSet;
import com.benjaminsommer.dailygoals.entities.DataSet;
import com.benjaminsommer.dailygoals.entities.Goal;
import com.benjaminsommer.dailygoals.entities.LocalRemoteId;
import com.benjaminsommer.dailygoals.entities.ToDo;
import com.benjaminsommer.dailygoals.util.AbsentLiveData;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

/**
 * Created by SOMMER on 26.11.2017.
 */

public class FirebaseService {

    private final String TAG = FirebaseService.class.getSimpleName();

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private CollectionReference colRefGoals;
    private CollectionReference colRefDataSets;
    private CollectionReference colRefToDos;
    private String userId;

    public FirebaseService() {

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    userId = firebaseAuth.getCurrentUser().getUid();
                    colRefGoals = firestore.collection(userId).document("data").collection("goals");
                    colRefDataSets = firestore.collection(userId).document("data").collection("datasets");
                    colRefToDos = firestore.collection(userId).document("data").collection("todos");
                }
            }
        });
        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
            colRefGoals = firestore.collection(userId).document("data").collection("goals");
            colRefDataSets = firestore.collection(userId).document("data").collection("datasets");
            colRefToDos = firestore.collection(userId).document("data").collection("todos");
        }
    }

    //// start goal section

    public LiveData<ApiResponse<List<Goal>>> getGoals() {
        final MutableLiveData<ApiResponse<List<Goal>>> list = new MutableLiveData<>();
        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
            colRefGoals = firestore.collection(userId).document("data").collection("goals");
            colRefGoals
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<Goal> goalsList = new ArrayList<Goal>();
                                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                    Goal goal = documentSnapshot.toObject(Goal.class);
                                    goalsList.add(goal);
                                }
                                ApiResponse<List<Goal>> apiResponse = new ApiResponse<List<Goal>>(goalsList);
                                list.setValue(apiResponse);
                            }
                        }
                    });
        } else {
            Throwable error = new Throwable("No user logged in");
            ApiResponse<List<Goal>> apiResponse = new ApiResponse<List<Goal>>(error);
            list.setValue(apiResponse);
            return list;
        }
        return list;
    }

    public Task<List<Goal>> getFirebaseGoalsAsTask() {
        final TaskCompletionSource<List<Goal>> dbSource = new TaskCompletionSource<>();
        Task dbTask = dbSource.getTask();

        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
            colRefGoals = firestore.collection(userId).document("data").collection("goals");
            colRefGoals
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot documentSnapshots) {
                            List<Goal> goalsList = new ArrayList<Goal>();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                Goal goal = documentSnapshot.toObject(Goal.class);
                                goalsList.add(goal);
                            }
                            dbSource.setResult(goalsList);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dbSource.setException(e);
                        }
                    });
        } else {
            Exception e = new NoSuchFieldException("Firebase user not logged in");
            dbSource.setException(e);
        }
        return dbTask;
    }

    public Task<LiveData<ApiResponse<List<Goal>>>> getGoalsAsTask() {
        return Tasks.call(new Callable<LiveData<ApiResponse<List<Goal>>>>() {
            @Override
            public LiveData<ApiResponse<List<Goal>>> call() throws Exception {
                final MutableLiveData<ApiResponse<List<Goal>>> list = new MutableLiveData<>();
                if (mAuth.getCurrentUser() != null) {
                    userId = mAuth.getCurrentUser().getUid();
                    colRefGoals = firestore.collection(userId).document("data").collection("goals");
                    colRefGoals
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        List<Goal> goalsList = new ArrayList<Goal>();
                                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                            Goal goal = documentSnapshot.toObject(Goal.class);
                                            goalsList.add(goal);
                                        }
                                        ApiResponse<List<Goal>> apiResponse = new ApiResponse<List<Goal>>(goalsList);
                                        list.setValue(apiResponse);
                                    }
                                }
                            });
                } else {
                    Throwable error = new Throwable("No user logged in");
                    ApiResponse<List<Goal>> apiResponse = new ApiResponse<List<Goal>>(error);
                    list.setValue(apiResponse);
                    return list;
                }
                return list;
            }
        });
    }

    public void updateGoal(Goal goal) {
        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
            colRefGoals = firestore.collection(userId).document("data").collection("goals");
            colRefGoals.document(goal.getGoalRemoteId())
                    .set(goal)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
        }
    }

    public void updateDataSet(DataSet dataSet) {
        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
            colRefDataSets = firestore.collection(userId).document("data").collection("datasets");
            colRefDataSets.document(dataSet.getDataSetRemoteId()).set(dataSet);
        }
    }

    public void deleteDataSet(DataSet dataSet) {
        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
            colRefDataSets = firestore.collection(userId).document("data").collection("datasets");
            colRefDataSets.document(dataSet.getDataSetRemoteId()).delete();
        }
    }

    public String addGoal(Goal goal) {
        String remote_id = "";
        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
            colRefGoals = firestore.collection(userId).document("data").collection("goals");
            remote_id = colRefGoals.document().getId();
            goal.setGoalRemoteId(remote_id);
            colRefGoals.document(remote_id)
                    .set(goal)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });
        }
        return remote_id;
    }

    public Task<List<LocalRemoteId>> addListOfGoals(Executor executor, final List<Goal> list) {
        return Tasks.call(executor, new Callable<List<LocalRemoteId>>() {
            @Override
            public List<LocalRemoteId> call() throws Exception {
                userId = mAuth.getCurrentUser().getUid();
                colRefGoals = firestore.collection(userId).document("data").collection("goals");
                List<LocalRemoteId> localRemoteIdList = new ArrayList<>(list.size());
                for (int x = 0; x < list.size(); x++) {
                    String remote_id = list.get(x).getGoalRemoteId();
                    Goal goal = list.get(x);
                    if (remote_id.equals("") || (remote_id.contains("LOCAL"))) {
                        remote_id = colRefGoals.document().getId();
                        goal.setGoalRemoteId(remote_id);
                    }
                    localRemoteIdList.add(new LocalRemoteId(goal.getGoalRemoteId(), goal.getGoalId()));
                    colRefGoals.document(remote_id).set(goal, SetOptions.merge());
                }
                return localRemoteIdList;
            }
        });
    }

    public Task<Integer> getRemoteGoalsCount() {
        final TaskCompletionSource<Integer> endResult = new TaskCompletionSource<>();
        final Task<Integer> result = endResult.getTask();

        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
            colRefGoals = firestore.collection(userId).document("data").collection("goals");
            colRefGoals
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                endResult.setResult(task.getResult().size());
                            }
                        }
                    });
        }
        return result;
    }

    /**
     * Delete all documents in a collection. Uses an Executor to perform work on a background
     * thread. This does *not* automatically discover and delete subcollections.
     */
    public Task<Void> deleteAllFirestoreGoals(final int batchSize, Executor executor) {

        colRefGoals = firestore.collection(userId).document("data").collection("goals");

        // Perform the delete operation on the provided Executor, which allows us to use
        // simpler synchronous logic without blocking the main thread.
        return Tasks.call(executor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // Get the first batch of documents in the collection
                Query query = colRefGoals.orderBy(FieldPath.documentId()).limit(batchSize);

                // Get a list of deleted documents
                List<DocumentSnapshot> deleted = deleteQueryBatch(query);

                // While the deleted documents in the last batch indicate that there
                // may still be more documents in the collection, page down to the
                // next batch and delete again
                while (deleted.size() >= batchSize) {
                    // Move the query cursor to start after the last doc in the batch
                    DocumentSnapshot last = deleted.get(deleted.size() - 1);
                    query = colRefGoals.orderBy(FieldPath.documentId())
                            .startAfter(last.getId())
                            .limit(batchSize);

                    deleted = deleteQueryBatch(query);
                }

                return null;
            }
        });

    }

    /**
     * Delete all results from a query in a single WriteBatch. Must be run on a worker thread
     * to avoid blocking/crashing the main thread.
     */
    @WorkerThread
    private List<DocumentSnapshot> deleteQueryBatch(final Query query) throws Exception {
        QuerySnapshot querySnapshot = Tasks.await(query.get());

        WriteBatch batch = query.getFirestore().batch();
        for (DocumentSnapshot snapshot : querySnapshot) {
            batch.delete(snapshot.getReference());
        }
        Tasks.await(batch.commit());

        return querySnapshot.getDocuments();
    }

    public Task<List<LocalRemoteId>> insertLocalGoalsToFirestore(final List<Goal> goalsList, Executor executor) {

        return Tasks.call(executor, new Callable<List<LocalRemoteId>>() {
            @Override
            public List<LocalRemoteId> call() throws Exception {
                List<Goal> list = goalsList;
                List<LocalRemoteId> localRemoteIdList = new ArrayList<>(list.size());
                String remote_id = "";
                if (mAuth.getCurrentUser() != null) {
                    userId = mAuth.getCurrentUser().getUid();
                    colRefGoals = firestore.collection(userId).document("data").collection("goals");

                    WriteBatch batch = firestore.batch();
                    for (int x = 0; x < list.size(); x++) {
                        remote_id = colRefGoals.document().getId();
                        list.get(x).setGoalRemoteId(remote_id);
                        DocumentReference docRef = colRefGoals.document(remote_id);
                        batch.set(docRef, goalsList.get(x));
                        LocalRemoteId localRemoteId = new LocalRemoteId(remote_id, list.get(x).getGoalId());
                        localRemoteIdList.add(localRemoteId);
                    }

                    Tasks.await(batch.commit());
                }
                return localRemoteIdList;
            }
        });

    }

    //// end goal section

    //// start dataset section

    // get datasets first and afterwards corresponding goals
    public Task<List<DataSet>> getFirebaseDataSetsByDate(String date) {
        final TaskCompletionSource<List<DataSet>> dbSource = new TaskCompletionSource<>();
        Task dbTask = dbSource.getTask();

        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
            colRefDataSets = firestore.collection(userId).document("data").collection("datasets");
            if (date.equals("all")) {
                colRefDataSets
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot documentSnapshots) {
                                List<DataSet> dataSetsList = new ArrayList<DataSet>();
                                for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                    Log.d(TAG, "getFirebaseDataSetsByDate - documentSnapshots size: " + documentSnapshots.size());
                                    DataSet dataSet = documentSnapshot.toObject(DataSet.class);
                                    dataSet.setDataSetRemoteId(documentSnapshot.getId());
                                    dataSetsList.add(dataSet);
                                }
                                dbSource.setResult(dataSetsList);
                                Log.d(TAG, "getFirebaseDataSetsBydate - dataSetsList: " + dataSetsList.toString());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dbSource.setException(e);
                                Log.d(TAG, "getFirebaseDataSetsBydate - dbSource Exception: " + e.getMessage());

                            }
                        });
            } else  {
                colRefDataSets
                        .whereEqualTo("dataSetDate", date)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot documentSnapshots) {
                                List<DataSet> dataSetsList = new ArrayList<DataSet>();
                                for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                    Log.d(TAG, "getFirebaseDataSetsByDate - documentSnapshots size: " + documentSnapshots.size());
                                    DataSet dataSet = documentSnapshot.toObject(DataSet.class);
                                    dataSet.setDataSetRemoteId(documentSnapshot.getId());
                                    dataSetsList.add(dataSet);
                                }
                                dbSource.setResult(dataSetsList);
                                Log.d(TAG, "getFirebaseDataSetsBydate - dataSetsList: " + dataSetsList.toString());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dbSource.setException(e);
                                Log.d(TAG, "getFirebaseDataSetsBydate - dbSource Exception: " + e.getMessage());

                            }
                        });
            }
        } else {
            Exception e = new NoSuchFieldException("Firebase user not logged in");
            dbSource.setException(e);
            Log.d(TAG, "getFirebaseDataSetsBydate - dbSource Exception: " + e.getMessage());
        }
        return dbTask;
    }


    private Task<LiveData<ApiResponse<List<DataSet>>>> getRemoteDataSetsByDate(final String date) {
        return Tasks.call(new Callable<LiveData<ApiResponse<List<DataSet>>>>() {
            @Override
            public LiveData<ApiResponse<List<DataSet>>> call() throws Exception {
                final MutableLiveData<ApiResponse<List<DataSet>>> list = new MutableLiveData<>();
                if (mAuth.getCurrentUser() != null) {
                    userId = mAuth.getCurrentUser().getUid();
                    colRefDataSets = firestore.collection(userId).document("data").collection("datasets");
                    colRefDataSets
                            .whereEqualTo("dataSetDate", date)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        List<DataSet> dataSetsList = new ArrayList<DataSet>();
                                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                            DataSet dataSet = documentSnapshot.toObject(DataSet.class);
                                            dataSetsList.add(dataSet);
                                        }
                                        ApiResponse<List<DataSet>> apiResponse = new ApiResponse<List<DataSet>>(dataSetsList);
                                        list.setValue(apiResponse);
                                    }
                                }
                            });
                } else {
                    Throwable error = new Throwable("No user logged in");
                    ApiResponse<List<DataSet>> apiResponse = new ApiResponse<List<DataSet>>(error);
                    list.setValue(apiResponse);
                }
                return list;
            }
        });
    }

    public LiveData<ApiResponse<List<CombinedDataSet>>> combinedDataSetsAndGoalsByDate(String date) {
        Log.d(TAG, "String date: " + date);
        final MutableLiveData<ApiResponse<List<CombinedDataSet>>> liveData = new MutableLiveData<>();
        final Task<List<DataSet>> dataSetTask = getFirebaseDataSetsByDate(date);
        final Task<List<Goal>> goalTask = getFirebaseGoalsAsTask();
        Task<Void> task = Tasks.whenAll(dataSetTask, goalTask);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (dataSetTask.getResult() != null && goalTask.getResult() != null) {
                    List<Goal> goalList = goalTask.getResult();
                    List<DataSet> dataSetList = dataSetTask.getResult();
                    Log.d(TAG, "goalList size: " + goalList.size());
                    Log.d(TAG, "dataSetList size: " + dataSetList.size());

                    Map<String, Goal> goalMap = new HashMap<>(goalList.size());
                    for (int x = 0; x < goalList.size(); x++) {
                        Log.d(TAG, "goalList item " + String.valueOf(x) + " is: " + goalList.get(x).toString());
                        goalMap.put(goalList.get(x).getGoalRemoteId(), goalList.get(x));
                    }
                    List<CombinedDataSet> list = new ArrayList<>();
                    for (int y = 0; y < dataSetList.size(); y++) {
                        Log.d(TAG, "dataSetList item " + String.valueOf(y) + " is: " + dataSetList.get(y).toString());
                        CombinedDataSet combinedDataSet = new CombinedDataSet(dataSetList.get(y), goalMap.get(dataSetList.get(y).getGoalKey()));
                        list.add(combinedDataSet);
                    }
                    ApiResponse<List<CombinedDataSet>> apiResponse = new ApiResponse<>(list);
                    liveData.setValue(apiResponse);
                }
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Throwable error = new Throwable(e);
                ApiResponse<List<CombinedDataSet>> apiResponse = new ApiResponse<>(error);
                liveData.setValue(apiResponse);
            }
        });
        return liveData;
    }

    public String addDataSet(DataSet dataSet) {
        String remote_id = "";
        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
            colRefDataSets = firestore.collection(userId).document("data").collection("datasets");
            remote_id = colRefDataSets.document().getId();
            dataSet.setDataSetRemoteId(remote_id);
            colRefDataSets.document(remote_id)
                    .set(dataSet)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });
        }
        return remote_id;
    }

    //// end dataset section

    //// start To Do section
    public Task<List<ToDo>> getAllToDos() {
        final TaskCompletionSource<List<ToDo>> dbSource = new TaskCompletionSource<>();
        Task dbTask = dbSource.getTask();

        if (mAuth.getCurrentUser() != null) {
            colRefToDos = firestore.collection(userId).document("data").collection("todos");
            colRefToDos
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<ToDo> toDoList = new ArrayList<ToDo>();
                                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                    ToDo toDo = documentSnapshot.toObject(ToDo.class);
                                    toDoList.add(toDo);
                                }
                                dbSource.setResult(toDoList);
                            } else {
                                dbSource.setException(task.getException());
                            }
                        }
                    });
        } else {
            dbSource.setException(new Exception("No such user logged in"));
        }
        return dbTask;
    }

    public LiveData<ApiResponse<List<ToDo>>> getOpenToDos() {
        final MutableLiveData<ApiResponse<List<ToDo>>> list = new MutableLiveData<>();
        if (mAuth.getCurrentUser() != null) {
            colRefToDos = firestore.collection(userId).document("data").collection("todos");
            colRefToDos
                    .whereEqualTo("toDoState", 1)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<ToDo> toDoList = new ArrayList<ToDo>();
                                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                    ToDo toDo = documentSnapshot.toObject(ToDo.class);
                                    toDoList.add(toDo);
                                }
                                ApiResponse<List<ToDo>> apiResponse = new ApiResponse<List<ToDo>>(toDoList);
                                list.setValue(apiResponse);
                            }
                        }
                    });
        } else {
            Throwable error = new Throwable("No user logged in");
            ApiResponse<List<ToDo>> apiResponse = new ApiResponse<List<ToDo>>(error);
            list.setValue(apiResponse);
            return list;
        }
        return list;
    }

    public LiveData<ApiResponse<List<ToDo>>> getFinishedToDos() {
        final MutableLiveData<ApiResponse<List<ToDo>>> list = new MutableLiveData<>();
        if (mAuth.getCurrentUser() != null) {
            colRefToDos = firestore.collection(userId).document("data").collection("todos");
            colRefToDos
                    .whereGreaterThan("toDoState", 1)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<ToDo> toDoList = new ArrayList<ToDo>();
                                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                    ToDo toDo = documentSnapshot.toObject(ToDo.class);
                                    toDoList.add(toDo);
                                }
                                ApiResponse<List<ToDo>> apiResponse = new ApiResponse<List<ToDo>>(toDoList);
                                list.setValue(apiResponse);
                            }
                        }
                    });
        } else {
            Throwable error = new Throwable("No user logged in");
            ApiResponse<List<ToDo>> apiResponse = new ApiResponse<List<ToDo>>(error);
            list.setValue(apiResponse);
            return list;
        }
        return list;
    }

    public String addToDo(ToDo toDo) {
        String remote_id = "";
        if (mAuth.getCurrentUser() != null) {
            remote_id = colRefToDos.document().getId();
            toDo.setToDoRemoteId(remote_id);
            colRefToDos.document(remote_id).set(toDo);
        }
        return remote_id;
    }

    public void updateToDo(ToDo toDo) {
        if (mAuth.getCurrentUser() != null) {
            colRefToDos = firestore.collection(userId).document("data").collection("todos");
            colRefToDos.document(toDo.getToDoRemoteId()).set(toDo);
        }
    }

    public void deleteToDo(ToDo toDo) {
        if (mAuth.getCurrentUser() != null) {
            colRefToDos = firestore.collection(userId).document("data").collection("todos");
            colRefToDos.document(toDo.getToDoRemoteId()).delete();
        }
    }

    //// end To Do section

}
