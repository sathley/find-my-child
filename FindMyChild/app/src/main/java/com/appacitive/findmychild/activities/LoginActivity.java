package com.appacitive.findmychild.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.appacitive.core.AppacitiveUser;
import com.appacitive.core.model.Callback;
import com.appacitive.findmychild.R;
import com.appacitive.findmychild.infra.SharedPreferencesManager;
import com.appacitive.findmychild.infra.SnackBarManager;
import com.appacitive.findmychild.infra.StorageManager;
import com.appacitive.findmychild.model.TrackerUser;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LoginActivity extends ActionBarActivity {

    @InjectView(R.id.edit_email)
    protected EditText mEmail;

    @InjectView(R.id.edit_password)
    protected EditText mPassword;

    @InjectView(R.id.btn_login)
    protected Button mLogin;

    @InjectView(R.id.tv_register)
    protected TextView mRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.btn_login)
    public void onLoginClick()
    {
        mEmail.setError(null);
        mPassword.setError(null);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        if(TextUtils.isEmpty(email) || !isEmailValid(email))
        {
            mEmail.setError("Provide a proper email address.");
            mEmail.requestFocus();
            YoYo.with(Techniques.Shake).duration(700).playOn(mEmail);
            return;
        }

        if(TextUtils.isEmpty(password) || !isPasswordValid(password))
        {
            mPassword.setError("Passwords should be at least 4 characters.");
            mPassword.requestFocus();
            YoYo.with(Techniques.Shake).duration(700).playOn(mPassword);
            return;
        }

        performLogin(email, password);
    }

    private void performLogin(String email, String password) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Logging in");
        dialog.show();

        AppacitiveUser.loginInBackground(email, password, -1, -1, new Callback<AppacitiveUser>() {
            @Override
            public void success(AppacitiveUser user) {
                dialog.dismiss();
                TrackerUser trackerUser = new TrackerUser(user);
                SharedPreferencesManager.WriteUserId(trackerUser.getId());
                new StorageManager().saveUser(trackerUser);

                Intent mapIntent = new Intent(LoginActivity.this, MapActivity.class);
                startActivity(mapIntent);
                overridePendingTransition(R.anim.slide_in_right_fast, R.anim.slide_out_left_fast);
            }

            @Override
            public void failure(AppacitiveUser result, Exception e) {
                dialog.dismiss();
                SnackBarManager.showError("Something went wrong", LoginActivity.this);
            }
        });
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 4;
    }

}
