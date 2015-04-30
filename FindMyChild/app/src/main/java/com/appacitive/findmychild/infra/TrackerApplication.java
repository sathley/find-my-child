package com.appacitive.findmychild.infra;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.appacitive.android.AppacitiveContext;
import com.appacitive.core.model.Environment;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;

/**
 * Created by sathley on 4/29/2015.
 */
public class TrackerApplication extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        TrackerApplication.context = getApplicationContext();
        checkForMigration();
        AppacitiveContext.initialize("edjymmkp7vX1hDUVPDZeZc9mp7we7SYsZYVG7m/s7Rk=", Environment.sandbox, getAppContext());
    }

    private void checkForMigration() {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;
        SharedPreferences sharedPreferences = TrackerApplication.getAppContext().getSharedPreferences("findmychild", Context.MODE_PRIVATE);
        boolean migrationDone = sharedPreferences.getBoolean("migration_done_" + version, false);
        if(migrationDone == false){
            Realm.deleteRealmFile(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("migration_done_" + version , true);
            editor.commit();
        }
    }

    public static Context getAppContext() {
        return TrackerApplication.context;
    }
}
