package com.appacitive.findmychild.infra;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sathley on 3/24/2015.
 */
public class SharedPreferencesManager {

    public static void  WriteUserId(String userId)
    {
        WriteKeyValue("user_id", userId);
    }

    public static String ReadUserId()
    {
        return ReadKeyValue("user_id");
    }

    public static void WriteKeyValue(String key, String value)
    {
        SharedPreferences sharedPreferences = TrackerApplication.getAppContext().getSharedPreferences("findmychild", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void WriteKeyValue(String key, boolean value)
    {
        SharedPreferences sharedPreferences = TrackerApplication.getAppContext().getSharedPreferences("findmychild", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void WriteKeyValue(String key, int value)
    {
        SharedPreferences sharedPreferences = TrackerApplication.getAppContext().getSharedPreferences("findmychild", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static String ReadKeyValue(String key)
    {
        SharedPreferences sharedPreferences = TrackerApplication.getAppContext().getSharedPreferences("findmychild", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    public static int ReadKeyValueAsInteger(String key)
    {
        SharedPreferences sharedPreferences = TrackerApplication.getAppContext().getSharedPreferences("findmychild", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }

    public static void RemoveUserId() {
        SharedPreferences sharedPreferences = TrackerApplication.getAppContext().getSharedPreferences("findmychild", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("user_id");
        editor.commit();
    }
}
