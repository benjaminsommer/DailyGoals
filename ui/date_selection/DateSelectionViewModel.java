package com.benjaminsommer.dailygoals.ui.date_selection;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.benjaminsommer.dailygoals.entities.StatResult;
import com.benjaminsommer.dailygoals.entities.Week;
import com.benjaminsommer.dailygoals.objects.Resource;
import com.benjaminsommer.dailygoals.objects.Status;
import com.benjaminsommer.dailygoals.repository.DataSetRepository;
import com.benjaminsommer.dailygoals.util.AbsentLiveData;
import com.benjaminsommer.dailygoals.util.TimeHelperClass;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class DateSelectionViewModel extends ViewModel {

    private DataSetRepository dataSetRepository;

    private LiveData<Resource<List<StatResult>>> dateList;
    private LiveData<Resource<List<Week>>> weekList;


    @Inject
    public DateSelectionViewModel(DataSetRepository dataSetRepository) {
        this.dataSetRepository = dataSetRepository;

        dateList = getDateList();

        weekList = Transformations.map(dateList, new Function<Resource<List<StatResult>>, Resource<List<Week>>>() {
            @Override
            public Resource<List<Week>> apply(Resource<List<StatResult>> input) {

                if (input != null && input.data != null && input.status == Status.SUCCESS) {

                    List<StatResult> liveList = input.data;
                    List<Week> listOfWeeks = new ArrayList<>();
                    String weekText = "";
                    int iterator = -1;

                    for (int x = 0; x < liveList.size(); x++) {
                        if (x == 0) {
                            LocalDate date = LocalDate.parse(liveList.get(x).getDate());
                            weekText = TimeHelperClass.getIsoWeekString(date);
                            iterator = 0;
                        } else if (x == (input.data.size() - 1)) {
                            listOfWeeks.add(new Week(weekText, liveList.subList(iterator, x+1)));
                        } else if (!TimeHelperClass.compareCalWeeks(LocalDate.parse(liveList.get(x).getDate()), LocalDate.parse(liveList.get(x-1).getDate()))) {
                            listOfWeeks.add(new Week(weekText, liveList.subList(iterator, x)));
                            LocalDate date = LocalDate.parse(liveList.get(x).getDate());
                            weekText = TimeHelperClass.getIsoWeekString(date);
                            iterator = x;
                        }
                    }

                    return Resource.success(listOfWeeks);
                } else {
                    return Resource.error("Input is null or empty", null);
                }
            }
        });

    }

    private LiveData<Resource<List<StatResult>>> getDateList () {
        return dataSetRepository.getDataSetStatList();
    }

    public LiveData<Resource<List<Week>>> getWeekList() {
        return weekList;
    }
}
