package com.appacitive.findmychild.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;

import com.appacitive.core.AppacitiveUser;
import com.appacitive.core.model.Callback;
import com.appacitive.findmychild.R;
import com.appacitive.findmychild.infra.SharedPreferencesManager;
import com.appacitive.findmychild.infra.SnackBarManager;
import com.appacitive.findmychild.infra.StorageManager;
import com.appacitive.findmychild.model.TrackerUser;


public class SplashScreenActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Message msg = new Message();
        splashHandler.sendMessageDelayed(msg, 1000);
    }

    private Handler splashHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String userId = SharedPreferencesManager.ReadUserId();

            if (userId == null) {
                Intent loginIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                overridePendingTransition(R.anim.slide_in_right_fast, R.anim.slide_out_left_fast);
            } else {
                TrackerUser user = new StorageManager().getUser(userId);
                if (user == null) {
//                    fetchUserFromServer(userId);
                    Intent loginIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    overridePendingTransition(R.anim.slide_in_right_fast, R.anim.slide_out_left_fast);
                } else {
                    Intent homeIntent = new Intent(SplashScreenActivity.this, MapActivity.class);
                    startActivity(homeIntent);
                    overridePendingTransition(R.anim.slide_in_right_fast, R.anim.slide_out_left_fast);
                }
            }
            finish();
        }
    };

    private void fetchUserFromServer(String userId) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Logging in");
        dialog.show();

        AppacitiveUser.getByIdInBackground(Long.valueOf(userId), null, new Callback<AppacitiveUser>() {
            @Override
            public void success(AppacitiveUser result) {
                dialog.dismiss();
                TrackerUser trackerUser = new TrackerUser(result);
                new StorageManager().saveUser(trackerUser);

                Intent mapIntent = new Intent(SplashScreenActivity.this, MapActivity.class);
                startActivity(mapIntent);
                overridePendingTransition(R.anim.slide_in_right_fast, R.anim.slide_out_left_fast);
            }

            @Override
            public void failure(AppacitiveUser result, Exception e) {
                dialog.dismiss();
                SnackBarManager.showError("Something went wrong", SplashScreenActivity.this);
            }
        });
    }
}



