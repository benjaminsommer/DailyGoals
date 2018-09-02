package com.benjaminsommer.dailygoals.ui.todo;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.benjaminsommer.dailygoals.Database;
import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.entities.ToDo;
import com.benjaminsommer.dailygoals.ui.todo.ToDoActivity;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.text.DecimalFormat;

/**
 * Created by DEU209213 on 07.05.2017.
 */

public class ToDoInputDialog extends AppCompatDialogFragment {

    private IToDoDialogFragmentToActivity mCallback;

    public static final String IS_NEW ="isNew";
    public static final String TODO_ID = "toDoPosition";
    public static final String TODO_ADAPTER_POS = "toDoAdapterPos";
    public static final String REMINDER_TIME = "reminderTime";
    public static final int DATETIMEPICKER_FRAGMENT = 1; // class variable

    private static int colorNotActive = R.color.disabledViewColor;
    private static int colorActive = R.color.colorAccent;

    private TextInputLayout textInputLayout;
    private EditText editText;
    private RelativeLayout containerReminder, containerReward;
    private Switch switchReminder, switchReward;
    private TextView textReminder, textReward;
    private ImageView ivReminder, ivReminderDropdown, ivReward, ivRewardDropdown;
    private CheckBox cbSnooze;
    private View underlineReminder, underlineReward;
    private Button btnDelete, btnCancel, btnSave;
    private ToDo toDo;
    private boolean isNew;
    private int adapterPos;

    public static ToDoInputDialog newInstance(boolean isNew, ToDo toDo) {
        ToDoInputDialog frag = new ToDoInputDialog();
        Bundle args = new Bundle();
        args.putBoolean(IS_NEW, isNew);
        frag.setArguments(args);
        frag.setFragmentToDo(toDo);
        return frag;
    }

    private void setFragmentToDo(ToDo toDo) {
        this.toDo = toDo;
    }

    // interface
    public interface IToDoDialogFragmentToActivity {
        void sendToDo(ToDo toDo, boolean isNew);
        void deleteToDo (ToDo toDo);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // to save configuration changes
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Define views
        View view = inflater.inflate(R.layout.dialog_to_do_input, container, false);
        textInputLayout = (TextInputLayout) view.findViewById(R.id.dialogToDoInput_textInputLayout);
        editText = (EditText) view.findViewById(R.id.dialogToDoInput_editText);
        switchReminder = (Switch) view.findViewById(R.id.dialogToDoInput_switch_reminder);
        containerReminder = (RelativeLayout) view.findViewById(R.id.dialogToDoInput_container_reminder);
        ivReminder = (ImageView) view.findViewById(R.id.dialogToDoInput_reminder_alarmImage);
        textReminder = (TextView) view.findViewById(R.id.dialogToDoInput_textView_reminder);
        ivReminderDropdown = (ImageView) view.findViewById(R.id.dialogToDoInput_dropdown_reminder);
        underlineReminder = view.findViewById(R.id.dialogToDoInput_underline_reminder);
        cbSnooze = (CheckBox) view.findViewById(R.id.dialogToDoInput_checkBox_reminder_snooze);
        switchReward = (Switch) view.findViewById(R.id.dialogToDoInput_switch_reward);
        containerReward = (RelativeLayout) view.findViewById(R.id.dialogToDoInput_container_reward);
        ivReward = (ImageView) view.findViewById(R.id.dialogToDoInput_reward_cashImage);
        textReward = (TextView) view.findViewById(R.id.dialogToDoInput_textView_reward);
        ivRewardDropdown = (ImageView) view.findViewById(R.id.dialogToDoInput_dropdown_reward);
        underlineReward = view.findViewById(R.id.dialogToDoInput_underline_reward);
        btnDelete = (Button) view.findViewById(R.id.dialogToDoInput_button_delete);
        btnCancel = (Button) view.findViewById(R.id.dialogToDoInput_button_cancel);
        btnSave = (Button) view.findViewById(R.id.dialogToDoInput_button_save);

        // check if new to do or edit to do
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            isNew = bundle.getBoolean(IS_NEW);
        } else {
            isNew = true;
        }

        // fill data with new/edit to do
        if (!isNew) {
            editText.setText(toDo.getToDoName());
            animateReminderSection(toDo.isToDoHasReminder());
            animateRewardSection(toDo.isToDoHasReward());
            textReminder.setText(fillDateTimeTextView(toDo.getToDoReminderTime(), false));
            DecimalFormat decimalFormat = new DecimalFormat(",##0.00 \u00A4");
            String rewardWithCurrency = decimalFormat.format(toDo.getToDoRewardAmount());
            textReward.setText(rewardWithCurrency);
        } else {
            textReminder.setText(fillDateTimeTextView(0, true));
            btnDelete.setVisibility(View.GONE);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String strRewardValue = prefs.getString("pref_amount", "0.00");
            float rewardValue = Float.valueOf(strRewardValue);
            toDo.setToDoRewardAmount(rewardValue);

        }

        // switch listener
        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView == switchReminder) {
                    animateReminderSection(isChecked);
                } else if (buttonView == switchReward) {
                    animateRewardSection(isChecked);
                }
            }
        };
        switchReminder.setOnCheckedChangeListener(onCheckedChangeListener);
        switchReward.setOnCheckedChangeListener(onCheckedChangeListener);

        // button on click listeners
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.dialogToDoInput_button_cancel:
                        dismiss();
                        break;
                    case R.id.dialogToDoInput_button_save:
                        // validate EditText
                        if (!validateName()) {
                            break;
                        }
                        if (!validateDate()) {
                            Toast.makeText(getActivity(), R.string.toDo_error_timeInPast, Toast.LENGTH_SHORT).show();
                            break;
                        }
                        // save data in To Do
                        toDo.setToDoName(editText.getText().toString());
                        toDo.setToDoHasReminder(switchReminder.isChecked());
                        toDo.setToDoHasSnooze(cbSnooze.isChecked());
                        toDo.setToDoHasReward(switchReward.isChecked());
                        toDo.setToDoMoney(0.0f);

                        // call interface to send finished To Do to Activity
                        mCallback.sendToDo(toDo, isNew);

                        dismiss();
                        break;
                    case R.id.dialogToDoInput_button_delete:
                        mCallback.deleteToDo(toDo);
                        dismiss();
                        break;
                    case R.id.dialogToDoInput_container_reminder:
                        Log.d("To Do Container", "Pushed");
                        // start method in activity to open DateTimePickerFragment
                        ((ToDoActivity) getActivity()).openDateTimePickerDialog(toDo.getToDoReminderTime());
                        break;
                    case R.id.dialogToDoInput_container_reward:
                        ((ToDoActivity) getActivity()).openMoneyPickerDialog(toDo.getToDoRewardAmount());

                }
            }
        };
        btnSave.setOnClickListener(onClickListener);
        btnCancel.setOnClickListener(onClickListener);
        btnDelete.setOnClickListener(onClickListener);
        containerReminder.setOnClickListener(onClickListener);
        containerReward.setOnClickListener(onClickListener);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (IToDoDialogFragmentToActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement IToDoDialogFragmentToActivity");
        }
    }

    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }

    // method receiving Date Input data from Activity via DateTimePickerFragment
    public void receiveDateTimeInfo(long reminderTime) {
        toDo.setToDoReminderTime(reminderTime);
        textReminder.setText(fillDateTimeTextView(reminderTime, false));
    }

    // method receiving Reward data from Activity via MoneyPickerDialogFragment
    public void receiveRewardInfo(double value) {
        toDo.setToDoRewardAmount(value);
        DecimalFormat df = new DecimalFormat(",##0.00 \u00A4");
        textReward.setText(df.format(value));
    }

    // fill date + time text view
    private String fillDateTimeTextView(long timeInMillis, boolean isNew) {
        String txtDate = "";
        String txtTime = "";
        DateTime selectedDateTime;
        LocalDate today = new LocalDate(DateTime.now());

        if (isNew) {
            selectedDateTime = new DateTime(DateTime.now());
            int diff = 0;
            if (selectedDateTime.getMinuteOfHour() < 15) {
                diff = 15 - selectedDateTime.getMinuteOfHour();
            } else if (selectedDateTime.getMinuteOfHour() < 30) {
                diff = 30 - selectedDateTime.getMinuteOfHour();
            } else if (selectedDateTime.getMinuteOfHour() < 45) {
                diff = 45 - selectedDateTime.getMinuteOfHour();
            } else {
                diff = 60 - selectedDateTime.getMinuteOfHour();
            }
            LocalTime modifiedTime = selectedDateTime.toLocalTime().plusMinutes(diff);
            txtTime = modifiedTime.toString("HH:mm");
            // set to do reminder time
            toDo.setToDoReminderTime(selectedDateTime.plusMinutes(diff).getMillis());

        } else {
            selectedDateTime = new DateTime(timeInMillis);
            txtTime = selectedDateTime.toString("HH:mm");
        }
        LocalDate selectedDate = selectedDateTime.toLocalDate();
        if (today.isEqual(selectedDate)) {
            txtDate = "Heute";
        } else if (today.isEqual(selectedDate.minusDays(1))) {
            txtDate = "Morgen";
        } else {
            txtDate = selectedDate.toString("dd.MM.yyyy");
        }

        return txtDate + ", " + txtTime + " Uhr";
    }

    // sets the reminder section to clickable or not
    private void animateReminderSection(boolean active) {
        switchReminder.setChecked(active);
        textReminder.setEnabled(active);
        cbSnooze.setEnabled(active);
        containerReminder.setClickable(active);
        if (active) {
            ivReminder.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent));
            ivReminderDropdown.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent));
            underlineReminder.setBackgroundResource(colorActive);
        } else {
            ivReminder.getDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.disabledViewColor), PorterDuff.Mode.MULTIPLY);
            ivReminderDropdown.getDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.disabledViewColor), PorterDuff.Mode.MULTIPLY);
            underlineReminder.setBackgroundResource(colorNotActive);
        }
    }

    // sets the reminder section to clickable or not
    private void animateRewardSection(boolean active) {
        switchReward.setChecked(active);
        textReward.setEnabled(active);
        containerReward.setClickable(active);
        if (active) {
            ivReward.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent));
            ivRewardDropdown.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent));
            underlineReward.setBackgroundResource(colorActive);
        } else {
            ivReward.getDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.disabledViewColor), PorterDuff.Mode.MULTIPLY);
            ivRewardDropdown.getDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.disabledViewColor), PorterDuff.Mode.MULTIPLY);
            underlineReward.setBackgroundResource(colorNotActive);
        }
    }

    // validate EditText and TextInputLayout
    private boolean validateName() {
        if (editText.getText().toString().trim().isEmpty()) {
            textInputLayout.setError(getString(R.string.toDo_textInputLayout_errMsg));
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    // validate if date / time is in the past
    private boolean validateDate() {
        long now = System.currentTimeMillis();
        return (now < toDo.getToDoReminderTime());
    }

}
