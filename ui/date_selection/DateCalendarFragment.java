package com.benjaminsommer.dailygoals.ui.date_selection;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

import com.benjaminsommer.dailygoals.Database;
import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.ui.dataset.DataSetActivity;
import com.benjaminsommer.dailygoals.ui.dataset.GoalInput;
import com.benjaminsommer.dailygoals.util.TimeHelperClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class DateCalendarFragment extends Fragment {
    private Database db;
    private DataSetActivity dataSetActivity = new DataSetActivity();

    public DateCalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_date_calendar, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // initialize database
        db = new Database(this.getActivity());

        // get first and last date in database to show in calendar
        ArrayList<String> allDates = db.getAllDatesFromDataSetDB();
        Calendar firstDate = Calendar.getInstance();
        Calendar lastDate = Calendar.getInstance();
        if (allDates != null) {
            firstDate = TimeHelperClass.convertStringToCalendar(allDates.get(0));
            lastDate = TimeHelperClass.convertStringToCalendar(allDates.get(allDates.size()-1));
        }
        long firstDateInMillis = firstDate.getTime().getTime();
        long lastDateInMillis = lastDate.getTime().getTime();

        // initialize CalendarView
        final CalendarView calendarView = (CalendarView) getView().findViewById(R.id.calendarFragment_calendar);
        Button calendarButton = (Button) getView().findViewById(R.id.calendarFragment_button);

        // show calendar weeks
        calendarView.setShowWeekNumber(true);

        // set first day of the week
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);

        // set first and last date available in calendar
        calendarView.setMinDate(firstDateInMillis);
        calendarView.setMaxDate(lastDateInMillis);

        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long selectedDateInMillis = calendarView.getDate();
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.setTimeInMillis(selectedDateInMillis);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date = sdf.format(selectedDate.getTime());

                // sent intent
                Intent intent = new Intent(getActivity(), DataSetActivity.class);
                intent.putExtra(DataSetActivity.DAY_STRING, date);
                startActivity(intent);
            }
        });

    }



}
