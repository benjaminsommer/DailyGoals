package com.benjaminsommer.dailygoals.ui.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.benjaminsommer.dailygoals.R;
import com.shawnlin.numberpicker.NumberPicker;

/**
 * Created by SOMMER on 23.07.2017.
 */

public class MoneyPickerPreference extends DialogPreference {

    private int euro = 0;
    private int cent = 0;
    private NumberPicker npFullDigit, npCent;
    private TextView txtSeparator, txtCurrency;
    private Context mContext;

    public MoneyPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setPositiveButtonText(R.string.ok);
        setNegativeButtonText(R.string.cancel);
        setDialogLayoutResource(R.layout.dialog_money_picker);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        // get fields from view
        npFullDigit = (NumberPicker) view.findViewById(R.id.moneyPicker_numberPicker_fullDigit);
        txtSeparator = (TextView) view.findViewById(R.id.moneyPicker_textView_numberSeparator);
        npCent = (NumberPicker) view.findViewById(R.id.moneyPicker_numberPicker_cent);
        txtCurrency = (TextView) view.findViewById(R.id.moneyPicker_textView_currency);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        double amount = Double.valueOf(prefs.getString("pref_amount", "0"));
        int currency = Integer.valueOf(prefs.getString("pref_currency", "1"));

        euro = (int) amount;
        cent = (int) ((amount - euro) * 100);

        npFullDigit.setValue(euro);
        npCent.setValue(cent);


        // set currency and separator (depending on currency)
        if (currency == 1) {
            // EURO
            txtCurrency.setText("€");
            txtSeparator.setText(",");
        } else if (currency == 2) {
            // US-DOLLAR
            txtCurrency.setText("$");
            txtSeparator.setText(".");
        }

    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {

            euro = npFullDigit.getValue();
            cent = npCent.getValue();
            float amount = euro + ((float) cent / 100);
            String strAmount = String.valueOf(amount);

            SharedPreferences.Editor editor = getEditor();
            editor.putString("pref_amount", strAmount);
            editor.commit();

            if (callChangeListener(strAmount)) {
                persistString(strAmount);
            }

        }

    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        String amount = "";
        if (restorePersistedValue) {
            if (defaultValue == null) {
                amount = getPersistedString("0");
            } else {
                amount = getPersistedString(defaultValue.toString());
            }
        } else {
            amount = defaultValue.toString();
        }

        double dblAmount = Double.valueOf(amount);
        euro = (int) dblAmount;
        cent = (int) ((dblAmount - euro) * 100);

    }
}
