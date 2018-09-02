package com.benjaminsommer.dailygoals.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.benjaminsommer.dailygoals.MyApplication;
import com.benjaminsommer.dailygoals.ui.dialogs.MoneyPickerPreference;
import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.ui.dialogs.TimePickerDialog;
import com.benjaminsommer.dailygoals.util.SharedPrefHelperClass;

import java.text.DecimalFormat;

/**
 * Created by DEU209213 on 30.04.2016.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ISettingFragmentToActivity mCallback;
    private SharedPrefHelperClass sphc;
    private Context context;
    private SharedPreferences sharedPreferences;
    private Resources resources;

    public interface ISettingFragmentToActivity {
        void handleStandardAmountChange(double newRewardAmount);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize global variables
        context = getActivity();
        resources = context.getResources();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sphc = new SharedPrefHelperClass(sharedPreferences, resources);

        // Load the preferences from XML resource
        addPreferencesFromResource(R.xml.preferences);

        // register Preference Change Listener
        //sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        // run through all preferences and update summaries
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i) {
            Preference preference = getPreferenceScreen().getPreference(i);

            if (preference instanceof PreferenceGroup) {
                PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
                for (int j = 0; j < preferenceGroup.getPreferenceCount(); ++j) {
                    Preference singlePref = preferenceGroup.getPreference(j);
                    updatePreferenceText(singlePref, singlePref.getKey());
                }
            } else {
                updatePreferenceText(preference, preference.getKey());
            }
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (ISettingFragmentToActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ISettingFragmentToActivity");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        // update preference text
        updatePreferenceText(findPreference(key), key);

        // activate or deactivate daily goals reminder
        if (key.equals(resources.getString(R.string.PREF_KEY_NOTIFICATION_TIME)) || key.equals(resources.getString(R.string.PREF_KEY_NOTIFICATION_DECISION))) {
            boolean alarmDecision = sharedPreferences.getBoolean(resources.getString(R.string.PREF_KEY_NOTIFICATION_DECISION), true);
            if (alarmDecision) {
                sphc.startInititalDataSetReminder();
            } else {
                sphc.cancelDailyDataSetReminder();
            }
        }

        // change standard money amount for all goals
        if (key.equals(resources.getString(R.string.PREF_KEY_AMOUNT_MONEY))) {
            String value = sharedPreferences.getString(resources.getString(R.string.PREF_KEY_AMOUNT_MONEY), "0");
            double dblValue = Double.valueOf(value);
            mCallback.handleStandardAmountChange(dblValue);
        }

    }

    private void updatePreferenceText(Preference preference, String key) {

        /*
        * Relevant Preference types for update:
        * - ListPreference
        * - MoneyPickerPreference
        * - TimePickerDialog
        * - ToDoReminderPreference
        */

        if (preference == null) {
            return;
        }
        
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            listPreference.setSummary(listPreference.getEntry());
            return;
        }

        if (preference instanceof TimePickerDialog) {
            TimePickerDialog timePickerDialog = (TimePickerDialog) preference;
            timePickerDialog.setSummary(timePickerDialog.getSharedPreferences().getString(key, "00:00"));
        }

        if (preference instanceof MoneyPickerPreference) {
            MoneyPickerPreference moneyPickerPreference = (MoneyPickerPreference) preference;
            String value = PreferenceManager.getDefaultSharedPreferences(context).getString(resources.getString(R.string.PREF_KEY_AMOUNT_MONEY), "0");
            double dblValue = Double.valueOf(value);
            DecimalFormat df = new DecimalFormat(",##0.00 \u00A4");
            moneyPickerPreference.setSummary(df.format(dblValue));
        }

        if (preference instanceof ToDoReminderPreference) {
            ToDoReminderPreference toDoReminderPreference = (ToDoReminderPreference) preference;
            String toDoReminderKey = toDoReminderPreference.getKey();
            String value = PreferenceManager.getDefaultSharedPreferences(context).getString(toDoReminderKey, "0;1");
            toDoReminderPreference.setSummary(sphc.generateReminderTimeframe(value));
        }

        if (preference instanceof EditTextPreference) {
            EditTextPreference editTextPreference = (EditTextPreference) preference;
            editTextPreference.setSummary(editTextPreference.getText());
        }

    }

}
