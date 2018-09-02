package com.benjaminsommer.dailygoals.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.benjaminsommer.dailygoals.R;

/**
 * Created by SOMMER on 01.07.2017.
 */

public class EditTextDialogFragment extends DialogFragment {

    public static final String TAG = EditTextDialogFragment.class.getSimpleName();
    public static final String POSITION = "editText_position";
    public static final String PREV_TEXT = "editText_previous_text";
    public static final String TEXT_SELECTION_TYPE = "text_selection_type";
    private IEditTextDialogToActicity mCallback;
    private EditText editText;
    private TextInputLayout textInputLayout;

    public EditTextDialogFragment() {}

    // interface to activity
    public interface IEditTextDialogToActicity {
        void getTextInput (int position, String text, int type);
    }

    public static EditTextDialogFragment newInstance(int position, String previousText, int type) {
        EditTextDialogFragment fragment = new EditTextDialogFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        args.putString(PREV_TEXT, previousText);
        args.putInt(TEXT_SELECTION_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // get forwarded arguments
        final int position = getArguments().getInt(POSITION);
        String prevString = getArguments().getString(PREV_TEXT);
        final int textSelectionType = getArguments().getInt(TEXT_SELECTION_TYPE);

        // initialize OnClickListener with activity
        mCallback = (IEditTextDialogToActicity) getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (textSelectionType == 0) {
            builder.setTitle("Name des Ziels");
        } else if (textSelectionType == 1) {
            builder.setTitle("Beschreibung des Ziels");
        } else if (textSelectionType == 2){
            builder.setTitle("Erstelle eine Notiz");
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_simple_edit_text, null);
        builder.setView(dialogView);

        // get fields from view
        editText = (EditText) dialogView.findViewById(R.id.dialogSimpleEditText_editText);
        textInputLayout = (TextInputLayout) dialogView.findViewById(R.id.dialogSimpleEditText_textInputLayout);

        // fill EditText if previous notice is sent
        if (!prevString.equals("")) {
            editText.setText(prevString);
        }

        // set focus to edit text and show keyboard
        editText.requestFocus();

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (validateName()) {
                    mCallback.getTextInput(position, editText.getText().toString(), textSelectionType);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    // validate EditText and TextInputLayout
    private boolean validateName() {
        if (editText.getText().toString().trim().isEmpty()) {
            textInputLayout.setError("Text eingeben!");
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

}
