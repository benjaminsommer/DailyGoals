package com.benjaminsommer.dailygoals.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.ui.todo.ToDoActivity;
import com.benjaminsommer.dailygoals.ui.todo.ToDoInputDialog;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.Calendar;

/**
 * Created by DEU209213 on 12.05.2017.
 */

public class DateTimePickerFragment extends AppCompatDialogFragment {

    public static final String NEW_REMINDER_TIME = "newReminderTime";

    private TimePicker timePicker;
    private Button btnNow;
    private ImageButton btnDateBack, btnDateForward;
    private TextView tvDate;
    private DateTime dateTime;
    private long reminderTime;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // get reminder time from arguments
        reminderTime = System.currentTimeMillis();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            reminderTime = bundle.getLong(ToDoInputDialog.REMINDER_TIME);
        }

        // define date
        dateTime = new DateTime(reminderTime);

        // initialize views
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_to_do_date_time_picker, null);

        btnNow = (Button) view.findViewById(R.id.dateTimePicker_button_now);
        btnDateBack = (ImageButton) view.findViewById(R.id.dateTimePicker_date_button_left);
        btnDateForward = (ImageButton) view.findViewById(R.id.dateTimePicker_date_button_right);
        tvDate = (TextView) view.findViewById(R.id.dateTimePicker_date_textView);
        timePicker = (TimePicker) view.findViewById(R.id.dateTimePicker_timePicker);
        timePicker.setIs24HourView(true);

        // set date and time views
        setDateText(dateTime.toLocalDate());
        timePicker.setCurrentHour(dateTime.getHourOfDay());
        timePicker.setCurrentMinute(dateTime.getMinuteOfHour());

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == btnDateBack.getId()) {
                    dateTime = dateTime.minusDays(1);
                    setDateText(dateTime.toLocalDate());
                } else if (v.getId() == btnDateForward.getId()) {
                    dateTime = dateTime.plusDays(1);
                    setDateText(dateTime.toLocalDate());
                } else if (v.getId() == tvDate.getId()) {
                    long millis = dateTime.getMillis();
                    ((ToDoActivity) getActivity()).openDatePickerFragment(millis);
                } else if (v.getId() == btnNow.getId()) {
                    dateTime = new DateTime(System.currentTimeMillis());
                    setDateText(dateTime.toLocalDate());
                    timePicker.setCurrentHour(dateTime.getHourOfDay());
                    timePicker.setCurrentMinute(dateTime.getMinuteOfHour());
                }
            }
        };

        // OnClickListener for Date View back and forward + TextView
        btnDateBack.setOnClickListener(onClickListener);
        btnDateForward.setOnClickListener(onClickListener);
        tvDate.setOnClickListener(onClickListener);
        btnNow.setOnClickListener(onClickListener);



        // configure builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle("Set reminder");
        builder.setCancelable(true);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Calendar finalDate = Calendar.getInstance();
                finalDate.set(dateTime.getYear(), dateTime.getMonthOfYear() - 1, dateTime.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                long timeInMillis = finalDate.getTimeInMillis();
                ((ToDoActivity) getActivity()).sendSelectedDateToInputDialog(timeInMillis);
            }
        });
        // generate and deliver AlertDialog
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        return builder.create();
    }

    public void receiveNewDate(int year, int month, int day) {
        LocalDate localDate = new LocalDate(year, month + 1, day);
        setDateText(localDate);
        dateTime = new DateTime(year, month + 1, day, dateTime.getHourOfDay(), dateTime.getMinuteOfHour());
    }

    private void setDateText(LocalDate localDate) {
        if (localDate.equals(new LocalDate())) {
            tvDate.setText("Heute");
        } else if (localDate.equals(new LocalDate().plusDays(1))) {
            tvDate.setText("Morgen");
        } else if (localDate.equals(new LocalDate().minusDays(1))) {
            tvDate.setText("Gestern");
        } else {
            tvDate.setText(localDate.toString("EEE, dd.MM.yyyy"));
        }
    }

}
