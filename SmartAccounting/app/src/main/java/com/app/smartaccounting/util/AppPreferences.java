package com.app.smartaccounting.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.smartaccounting.model.userModel;
import com.google.gson.Gson;

public class AppPreferences {

    public static String PREF_NAME = "APP_PREFERENCES";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public AppPreferences(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean getBoolean(String key, boolean defFlag) {
        return pref.getBoolean(key, defFlag);
    }

    public String getString(String key) {
        return pref.getString(key, "");
    }

    public int getInteger(String key) {
        return pref.getInt(key, 0);
    }

    public int getInteger(String key, int defaultVal) {
        return pref.getInt(key, defaultVal);
    }

    public long getLong(String key) {
        return pref.getLong(key, 0);
    }

    public void removeValue(String key) {
        editor = pref.edit();
        editor.remove(key);
        editor.commit();
        editor = null;
    }

    public void set(String key, boolean flag) {
        editor = pref.edit();
        editor.putBoolean(key, flag);
        editor.commit();
        editor = null;
    }

    public void set(String key, long value) {
        editor = pref.edit();
        editor.putLong(key, value);
        editor.commit();
        editor = null;
    }

    public void set(String key, String val) {
        editor = pref.edit();
        editor.putString(key, val);
        editor.commit();
        editor = null;
    }

    public void set(String key, int val) {
        editor = pref.edit();
        editor.putInt(key, val);
        editor.commit();
        editor = null;
    }
    private Gson getGson() {
        return new Gson();
    }
    public userModel getUser(){
        Gson gson = new Gson();
        String json = pref.getString("userObj","");
        return gson.fromJson(json, userModel.class);
    }
}
