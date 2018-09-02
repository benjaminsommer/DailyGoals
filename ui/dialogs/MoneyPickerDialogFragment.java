package com.benjaminsommer.dailygoals.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.benjaminsommer.dailygoals.R;
import com.shawnlin.numberpicker.NumberPicker;

/**
 * Created by SOMMER on 01.07.2017.
 */

public class MoneyPickerDialogFragment extends DialogFragment {

    public static final String TAG = MoneyPickerDialogFragment.class.getSimpleName();

    private static final String POSITION = "position";
    private static final String PRE_VALUE = "preValue";
    private static final String CURRENCY = "currency";
    private NumberPicker npFullDigit, npCent;
    private TextView txtSeparator, txtCurrency;
    private IMoneyPickerToActivity mCallback;

    // interface
    public interface IMoneyPickerToActivity {
        void getMoneyAmount(int position, double amount);
    }

    public MoneyPickerDialogFragment() {
        // empty constructor - must stay empty in fragment; work with newInstance for attributes
    }

    public static MoneyPickerDialogFragment newInstance(int position, double preValue, int currency) {
        MoneyPickerDialogFragment fragment = new MoneyPickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        args.putDouble(PRE_VALUE, preValue);
        args.putInt(CURRENCY, currency);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // get forwarded arguments
        final int position = getArguments().getInt(POSITION);
        double preValue = getArguments().getDouble(PRE_VALUE);
        int currency = getArguments().getInt(CURRENCY);

        // initialize interface to activity
        mCallback = (IMoneyPickerToActivity) getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Wähle einen Betrag aus");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_money_picker, null);
        builder.setView(dialogView);

        // get fields from view
        npFullDigit = (NumberPicker) dialogView.findViewById(R.id.moneyPicker_numberPicker_fullDigit);
        txtSeparator = (TextView) dialogView.findViewById(R.id.moneyPicker_textView_numberSeparator);
        npCent = (NumberPicker) dialogView.findViewById(R.id.moneyPicker_numberPicker_cent);
        txtCurrency = (TextView) dialogView.findViewById(R.id.moneyPicker_textView_currency);

        // parse pre value
        int fullDigit = (int) preValue;
        int cent = (int) ((preValue - fullDigit) * 100);
        npFullDigit.setValue(fullDigit);
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

        // set buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                double reward = (double) npFullDigit.getValue() + ((double) npCent.getValue() / 100) ;
                mCallback.getMoneyAmount(position, reward);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        return builder.create();
    }
}
