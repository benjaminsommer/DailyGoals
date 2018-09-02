package com.benjaminsommer.dailygoals.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.benjaminsommer.dailygoals.R;
import com.shawnlin.numberpicker.NumberPicker;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

/**
 * Created by SOMMER on 23.07.2017.
 */

public class ToDoReminderPreference extends DialogPreference {

    private String exchangeValue = "0;1";
    private int minute = 1, hour = 1, day = 1, type = 0;
    private long timeValue = 100, nextDay = 1000 * 60 * 60 * 10;
    private NumberPicker npHour, npMainValue;
    private TextView txtUnit, divider;
    private RadioGroup radioGroup;
    private RadioButton rbMin, rbHour, rbDay, rbNextDay;
    private Context mContext;
    private int[] rbIDs = {R.id.reminderSelection_radioButton_min, R.id.reminderSelection_radioButton_hour, R.id.reminderSelection_radioButton_day, R.id.reminderSelection_radioButton_nextDay};

    public ToDoReminderPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPositiveButtonText(R.string.ok);
        setNegativeButtonText(R.string.cancel);
        setDialogLayoutResource(R.layout.dialog_reminder_selection);
        mContext = context;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        npHour = (NumberPicker) view.findViewById(R.id.reminderSelection_hourPicker);
        npMainValue = (NumberPicker) view.findViewById(R.id.reminderSelection_NumberPicker);
        divider = (TextView) view.findViewById(R.id.reminderSelection_divider);
        txtUnit = (TextView) view.findViewById(R.id.reminderSelection_text_unit);
        radioGroup = (RadioGroup) view.findViewById(R.id.reminderSelection_radioGroup);
        rbMin = (RadioButton) view.findViewById(R.id.reminderSelection_radioButton_min);

        String[] strParts = exchangeValue.split(";");
        type = Integer.valueOf(strParts[0]);
        timeValue = Long.valueOf(strParts[1]);

        switch (type) {
            case 0:
                minute = (int) timeValue;
                break;
            case 1:
                hour = (int) timeValue;
                break;
            case 2:
                day = (int) timeValue;
                break;
            case 3:
                nextDay = timeValue;
                break;
        }
        if (type <= rbIDs.length) {
            radioGroup.check(rbIDs[type]);
            setRadioButton(rbIDs[type]);
        }


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                setRadioButton(checkedId);
            }
        });

    }

    private void setRadioButton(int checkedId) {
        switch (checkedId) {
            case R.id.reminderSelection_radioButton_min:
                npHour.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
                npMainValue.setValue(minute);
                npMainValue.setMinValue(1);
                npMainValue.setMaxValue(59);
                txtUnit.setText("min");
                type = 0;
                break;
            case R.id.reminderSelection_radioButton_hour:
                npHour.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
                npMainValue.setValue(hour);
                npMainValue.setMinValue(1);
                npMainValue.setMaxValue(23);
                txtUnit.setText("h");
                type = 1;
                break;
            case R.id.reminderSelection_radioButton_day:
                npHour.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
                npMainValue.setValue(day);
                npMainValue.setMinValue(1);
                npMainValue.setMaxValue(99);
                txtUnit.setText("Tag(e)");
                type = 2;
                break;
            case R.id.reminderSelection_radioButton_nextDay:
                npHour.setVisibility(View.VISIBLE);
                divider.setVisibility(View.VISIBLE);
                LocalTime localTime = new LocalTime(nextDay);
                npHour.setValue(localTime.getHourOfDay());
                npHour.setMinValue(0);
                npHour.setMaxValue(23);
                npMainValue.setValue(localTime.getMinuteOfHour());
                npMainValue.setMinValue(0);
                npMainValue.setMaxValue(59);
                txtUnit.setText("Uhr");
                type = 3;
                break;
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {

            if (type == 3) {
                int tempHour = npHour.getValue();
                int tempMin = npMainValue.getValue();
                DateTime dateTime = new DateTime(1, 1, 1, tempHour, tempMin);
                timeValue = dateTime.getMillis();
            } else  {
                timeValue = npMainValue.getValue();
            }

            exchangeValue = String.valueOf(type) + ";" + String.valueOf(timeValue);

//            SharedPreferences.Editor editor = getEditor();
//            editor.putString("pref_reminder1", prefString);
//            editor.commit();

            if (callChangeListener(exchangeValue)) {
                persistString(exchangeValue);
            }

        }
    }

    public void setValue(String value) {
        this.exchangeValue = value;
    }

    public String getValue() {
        return this.exchangeValue;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {

        setValue(restorePersistedValue ? getPersistedString(exchangeValue) : (String) defaultValue);

//        String amount = "";
//        if (restorePersistedValue) {
//            if (defaultValue == null) {
//                amount = getPersistedString("0;1");
//            } else {
//                amount = getPersistedString(defaultValue.toString());
//            }
//        } else {
//            amount = defaultValue.toString();
//        }

        String[] strParts = exchangeValue.split(";");
        type = Integer.valueOf(strParts[0]);
        timeValue = Long.valueOf(strParts[1]);

    }

}
