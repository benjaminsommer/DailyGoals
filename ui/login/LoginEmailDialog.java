package com.benjaminsommer.dailygoals.ui.login;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.ui.login.LoginActivity;

/**
 * Created by SOMMER on 27.08.2017.
 */

public class LoginEmailDialog extends AppCompatDialogFragment {

    public static final String TAG = "LoginEmailDialog";
    public static final String SIGNIN_REGISTER_TYPE = "loginSigninType";
    public static final String FIREBASE_USER_NAME = "firebaseUserName";
    public static final String FIREBASE_USER_MAIL = "firebaseUserMail";
    private static final String SIGNIN = "Sign In";
    private static final String REGISTER = "Create";

    private int type; // 0 = register/create; 1 = signin/login, 2 = forgot password, 3 = change account, 4 = change password, 5 = unlink account
    private String txtName, txtMail;
    private ImageView ivMail, ivPassword, ivPasswordNew, ivName;
    private TextInputLayout tilMail, tilPassword, tilPasswordNew, tilName;
    private EditText etMail, etPassword, etPasswordNew, etName;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        type = 1; // standard value
        txtName = ""; // standard value
        txtMail = ""; // standard value
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            type = bundle.getInt(SIGNIN_REGISTER_TYPE, 1);
            
            if (type == 3) {
                txtName = bundle.getString(FIREBASE_USER_NAME, "");
                txtMail = bundle.getString(FIREBASE_USER_MAIL, "");
            }
        }

        // initialize layout
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_login_email, null);

        // initialize views
        ivName = (ImageView) view.findViewById(R.id.loginEmailDialog_image_name);
        ivMail = (ImageView) view.findViewById(R.id.loginEmailDialog_image_mail);
        ivPassword = (ImageView) view.findViewById(R.id.loginEmailDialog_image_password);
        ivPasswordNew = (ImageView) view.findViewById(R.id.loginEmailDialog_image_passwordNew);
        tilName = (TextInputLayout) view.findViewById(R.id.loginEmailDialog_textInputLayout_name);
        tilMail = (TextInputLayout) view.findViewById(R.id.loginEmailDialog_textInputLayout_mail);
        tilPassword = (TextInputLayout) view.findViewById(R.id.loginEmailDialog_textInputLayout_password);
        tilPasswordNew =(TextInputLayout) view.findViewById(R.id.loginEmailDialog_textInputLayout_passwordNew);
        etName = (EditText) view.findViewById(R.id.loginEmailDialog_editText_name);
        etMail = (EditText) view.findViewById(R.id.loginEmailDialog_editText_mail);
        etPassword = (EditText) view.findViewById(R.id.loginEmailDialog_editText_password);
        etPasswordNew = (EditText) view.findViewById(R.id.loginEmailDialog_editText_passwordNew);

        // update UI and assign string for positive button
        String strType = updateDialogUI(type);

        // configure builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle(strType);

        builder.setCancelable(true);
        builder.setPositiveButton(strType, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (type) {
                    case 0: // register/create
                        if (validateEmail() && validatePassword()) {
                            ((LoginActivity) getActivity()).getEmailLoginSigninInfo(type, etName.getText().toString(), etMail.getText().toString(), etPassword.getText().toString());
                            dismiss();
                        }
                        break;
                    case 1: // sign in
                        if (validateEmail() & validatePassword()) {
                            ((LoginActivity) getActivity()).getEmailLoginSigninInfo(type, null, etMail.getText().toString(), etPassword.getText().toString());
                            dismiss();
                        }
                        break;
                    case 2: // forgot password
                        if (validateEmail()) {
                            ((LoginActivity) getActivity()).sendEmailPasswordReset(etMail.getText().toString());
                            dismiss();
                        }
                        break;
                    case 3: // change account
                        if (validateEmail()) {
                            if (!etName.getText().toString().equals(txtName)) {
                                ((LoginActivity) getActivity()).updateEmailName(null, etName.getText().toString());
                            }
                            if (!etMail.getText().toString().equals(txtMail)) {
                                ((LoginActivity) getActivity()).updateEmailAddress(etMail.getText().toString());
                            }
                            dismiss();
                        }
                        break;
                    case 4: // change password
                        if (validatePassword() && validatePasswordNew() && etPassword.getText().toString().equals(etPasswordNew.getText().toString())) {
                            ((LoginActivity) getActivity()).updateEmailPassword(etPassword.getText().toString());
                            dismiss();
                        } else {
                            Toast.makeText(getActivity(), "Das Passwort ist leer oder stimmt nicht überein. Bitte gebe das neue Passwort erneut ein", Toast.LENGTH_SHORT);
                        }
                        break;
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        return builder.create();
    }

    // validate Email EditText and TextInputLayout
    private boolean validateEmail() {
        if (etMail.getText().toString().trim().isEmpty()) {
            tilMail.setError("Bitte geben Sie Ihre E-Mail-Adresse ein");
            return false;
        } else {
            tilMail.setErrorEnabled(false);
        }
        return true;
    }

    // validate Password EditText and TextInputLayout
    private boolean validatePassword() {
        if (etPassword.getText().toString().trim().isEmpty()) {
            tilPassword.setError("Bitte geben Sie Ihr Passwort ein");
            return false;
        } else {
            tilPassword.setErrorEnabled(false);
        }
        return true;
    }

    // validate Password New EditText and TextInputLayout
    private boolean validatePasswordNew() {
        if (etPasswordNew.getText().toString().trim().isEmpty()) {
            tilPasswordNew.setError("Bitte geben Sie Ihr neues Passwort ein");
            return false;
        } else {
            tilPasswordNew.setErrorEnabled(false);
        }
        return true;
    }

    // update UI depending on requested dialog (int type)
    private String updateDialogUI(int type) {
        String buttonText = "";
        switch (type) {
            case 0: // register/create
                ivPasswordNew.setVisibility(View.GONE);
                tilPasswordNew.setVisibility(View.GONE);
                buttonText = "Register";
                break;
            case 1: // sign in
                ivName.setVisibility(View.GONE);
                tilName.setVisibility(View.GONE);
                ivPasswordNew.setVisibility(View.GONE);
                tilPasswordNew.setVisibility(View.GONE);
                buttonText = "Sign In";
                break;
            case 2: // forgot password
                ivName.setVisibility(View.GONE);
                tilName.setVisibility(View.GONE);
                ivPassword.setVisibility(View.GONE);
                tilPassword.setVisibility(View.GONE);
                ivPasswordNew.setVisibility(View.GONE);
                tilPasswordNew.setVisibility(View.GONE);
                buttonText=  "Request new Password";
                break;
            case 3: // change account
                ivPassword.setVisibility(View.GONE);
                tilPassword.setVisibility(View.GONE);
                ivPasswordNew.setVisibility(View.GONE);
                tilPasswordNew.setVisibility(View.GONE);
                etName.setText(txtName);
                etMail.setText(txtMail);
                buttonText = "Finish";
                break;
            case 4: // change password
                ivName.setVisibility(View.GONE);
                tilName.setVisibility(View.GONE);
                ivMail.setVisibility(View.GONE);
                tilMail.setVisibility(View.GONE);
                etPassword.setHint("New password");
                buttonText =  "Send new Password";
                break;
        }
        return buttonText;
    }

}
