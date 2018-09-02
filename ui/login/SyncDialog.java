package com.benjaminsommer.dailygoals.ui.login;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.benjaminsommer.dailygoals.R;

/**
 * Created by SOMMER on 18.01.2018.
 */

public class SyncDialog extends AppCompatDialogFragment {

    public static final String TAG = SyncDialog.class.getSimpleName();
    public static final String COUNT_LOCAL = "countLocal";
    public static final String COUNT_REMOTE = "countRemote";

    private ISyncDialogToActivity mCallback;

    private Button btnNext, btnCancel;
    private TextView txtCountLocal, txtCountRemote;
    private RadioGroup radioGroup;
    private int localCount = 0, remoteCount = 0, selection = -1;

    public interface ISyncDialogToActivity {
        void syncSelected(int selection);
        void syncCanceled();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // define views
        View view = inflater.inflate(R.layout.dialog_login_decide_on_data, container, false);
        btnNext = (Button) view.findViewById(R.id.dialogSync_button_next);
        btnCancel = (Button) view.findViewById(R.id.dialogSync_button_cancel);
        txtCountLocal = (TextView) view.findViewById(R.id.dialogSync_textView_countLocal);
        txtCountRemote = (TextView) view.findViewById(R.id.dialogSync_textView_countOnline);
        radioGroup = (RadioGroup) view.findViewById(R.id.dialogSync_radioGroup);

        // get bundle information
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            localCount = bundle.getInt(COUNT_LOCAL);
            remoteCount = bundle.getInt(COUNT_REMOTE);
        }

        // fill in data
        txtCountLocal.setText(String.valueOf(localCount));
        txtCountRemote.setText(String.valueOf(remoteCount));

        // radio button check logic
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                btnNext.setEnabled(true);
                if (checkedId == R.id.dialogSync_radioButton_selectRemote) {
                    selection = 0;
                } else if (checkedId == R.id.dialogSync_radioButton_selectLocal) {
                    selection = 1;
                } else if (checkedId == R.id.dialogSync_radioButton_selectMerge) {
                    selection = 2;
                }
            }
        });

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == btnNext.getId()) {
                    mCallback.syncSelected(selection);
                    dismiss();
                } else if (v.getId() == btnCancel.getId()) {
                    mCallback.syncCanceled();
                    dismiss();
                }
            }
        };
        btnNext.setOnClickListener(onClickListener);
        btnCancel.setOnClickListener(onClickListener);

        return view;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (ISyncDialogToActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ISyncDialogToActivity");
        }
    }
}
