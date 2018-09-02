package com.benjaminsommer.dailygoals.ui.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;

import java.util.Calendar;

/**
 * Created by SOMMER on 25.08.2017.
 */

public class DatePickerFragment extends AppCompatDialogFragment {

    public static final String TAG = DatePickerFragment.class.getSimpleName();
    public static final String DATE_VALUE = "dateTimeInMillis";
    public static final String MIN_DATE_VALUE = "minDateTimeInMillis";
    public static final String MAX_DATE_VALUE = "maxDateTimeInMillis";

    private DatePickerDialog.OnDateSetListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DatePickerDialog.OnDateSetListener) {
            listener = (DatePickerDialog.OnDateSetListener) context;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

//        listener = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                // send selected date to activity
//                ((ToDoActivity) getActivity()).sendDateToDateTimePickerFragment(year, month, dayOfMonth);
//            }
//        };

        // generate dialog and set it to selected date
        Calendar calendar = Calendar.getInstance();
        Calendar minCal = calendar;
        minCal.add(Calendar.YEAR, -1);
        Calendar maxCal = calendar;
        maxCal.add(Calendar.YEAR, 1);
        long maxDateInMillis = maxCal.getTimeInMillis();
        long minDateInMillis = minCal.getTimeInMillis();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            long dateInMillis = bundle.getLong(DATE_VALUE);
            calendar.setTimeInMillis(dateInMillis);
            maxDateInMillis = bundle.getLong(MAX_DATE_VALUE);
            minDateInMillis = bundle.getLong(MIN_DATE_VALUE);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), listener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(minDateInMillis);
        datePickerDialog.getDatePicker().setMaxDate(maxDateInMillis);
        return datePickerDialog;
    }

}
