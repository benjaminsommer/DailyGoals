package com.benjaminsommer.dailygoals.ui.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.widget.Toast;

import com.benjaminsommer.dailygoals.services.DatabaseUpdateService;

/**
 * Created by DEU209213 on 30.08.2016.
 */
public class DialogPreferenceAlert extends DialogPreference implements DialogInterface.OnClickListener {

    private Context mContext;

    public DialogPreferenceAlert(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setPersistent(false);
        setPositiveButtonText("Ja");
        setNegativeButtonText("Nein");
        setTitle("Datenbank löschen");
        setDialogMessage("Möchtest Du die Datenbank wirklich löschen? \nAlle DailyGoals gehen verloren!");
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            Intent updateService = new Intent(mContext, DatabaseUpdateService.class);
            updateService.putExtra(DatabaseUpdateService.BOOL_DELETE, true);
            mContext.startService(updateService);
            Toast.makeText(mContext, "Alle Ziele gelöscht!", Toast.LENGTH_SHORT).show();

        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
            dialog.dismiss();
        }
    }


}
