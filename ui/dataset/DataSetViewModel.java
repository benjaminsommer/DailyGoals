package com.benjaminsommer.dailygoals.ui.dataset;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.benjaminsommer.dailygoals.entities.CombinedDataSet;
import com.benjaminsommer.dailygoals.entities.DataSet;
import com.benjaminsommer.dailygoals.entities.StatResult;
import com.benjaminsommer.dailygoals.objects.Resource;
import com.benjaminsommer.dailygoals.repository.DataSetRepository;
import com.benjaminsommer.dailygoals.util.AbsentLiveData;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by SOMMER on 25.01.2018.
 */

public class DataSetViewModel extends ViewModel {

    private DataSetRepository dataSetRepository;

    private MutableLiveData<String> currentDate = new MutableLiveData<>();
    private LiveData<Resource<List<CombinedDataSet>>> dataSetList;
    private LiveData<String> minDate;
    private LiveData<String> maxDate;

    private MutableLiveData<Byte> selectedResult = new MutableLiveData<>();
    private LiveData<StatResult> resultList = new MutableLiveData<>();

    @Inject
    public DataSetViewModel(DataSetRepository dataSetRepo) {
        this.dataSetRepository = dataSetRepo;
        dataSetList = Transformations.switchMap(currentDate, new Function<String, LiveData<Resource<List<CombinedDataSet>>>>() {
            @Override
            public LiveData<Resource<List<CombinedDataSet>>> apply(String date) {
                if (date == null) {
                    return AbsentLiveData.create();
                } else {
                    return dataSetRepository.getDataSetListByDate(date);
                }
            }
        });
        minDate = dataSetRepository.getMinDate();
        maxDate = dataSetRepository.getMaxDate();

        resultList = Transformations.switchMap(selectedResult, new Function<Byte, LiveData<StatResult>>() {
            @Override
            public LiveData<StatResult> apply(Byte aByte) {
                if (aByte == null) {
                    return AbsentLiveData.create();
                } else {
                    if (aByte == StatResult.TODAY || aByte == StatResult.WEEK || aByte == StatResult.TOTAL) {
                        return dataSetRepository.getDataSetStatResultsForToday(aByte);
                    } else {
                        return AbsentLiveData.create();
                    }
                }
            }
        });

    }

    public void setDate(String date) {
        if (date.equals(this.currentDate.getValue())) {
            return;
        }
        this.currentDate.setValue(date);
    }

    public LiveData<String> getDate() {
        return currentDate;
    }

    public LiveData<Resource<List<CombinedDataSet>>> getDataSetList() {
        return dataSetList;
    }

    public void setGoalSelection(int position, int newValue, int oldValue) {
        DataSet dataSet = dataSetList.getValue().data.get(position).getDataSet();
        dataSet.setDataSetValue(newValue);
        updateDataSet(dataSet);
    }

    public void updateDataSet(DataSet dataSet) {
        dataSetRepository.updateDataSet(dataSet);
    }

    public void deleteDataSet(DataSet dataSet) {
        dataSetRepository.deleteDataSet(dataSet);
    }

    public void changeDataSetNotice(int position, String notice) {
        DataSet dataSet = dataSetList.getValue().data.get(position).getDataSet();
        dataSet.setDataSetNotice(notice);
        updateDataSet(dataSet);
    }

    public LiveData<String> getMinDate() {
        return minDate;
    }

    public LiveData<String> getMaxDate() {
        return maxDate;
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

    public LiveData<StatResult> getResultList() {
        return resultList;
    }
}
