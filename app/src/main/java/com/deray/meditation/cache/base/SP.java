package com.deray.meditation.cache.base;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 2018/1/5.
 */

public class SP {

    private Context context;

    private SP() {

    }

    private static class getSPInstance {
        private static SP sp = new SP();
    }
    
    public static SP get(){
        return getSPInstance.sp;
    }
    
    public void init(Context context){
        this.context = context;
    }

    private final String CONFIG = "config";

    private SharedPreferences getSharedPreference(String fileName) {
        return context.getSharedPreferences(fileName, 0);
    }

    public void putString( String key, String value) {
        SharedPreferences.Editor editor = getSharedPreference(CONFIG).edit();
        editor.putString(key, value).apply();
    }

    public String getString( String key, String defValue) {
        SharedPreferences sharedPreference = getSharedPreference(CONFIG);
        return sharedPreference.getString(key, defValue);
    }

    public void putBoolean( String key, Boolean value) {
        SharedPreferences.Editor editor = getSharedPreference(CONFIG).edit();
        editor.putBoolean(key, value.booleanValue()).apply();
    }

    public boolean getBoolean( String key, Boolean defValue) {
        SharedPreferences sharedPreference = getSharedPreference(CONFIG);
        return sharedPreference.getBoolean(key, defValue.booleanValue());
    }

    public void putInt( String key, int value) {
        SharedPreferences.Editor editor = getSharedPreference(CONFIG).edit();
        editor.putInt(key, value).apply();
    }

    public int getInt( String key, int defValue) {
        SharedPreferences sharedPreference = getSharedPreference(CONFIG);
        return sharedPreference.getInt(key, defValue);
    }

    public void putFloat( String fileName, String key, float value) {
        SharedPreferences.Editor editor = getSharedPreference(fileName).edit();
        editor.putFloat(key, value).apply();
    }

    public float getFloat( String key, Float defValue) {
        SharedPreferences sharedPreference = getSharedPreference(CONFIG);
        return sharedPreference.getFloat(key, defValue.floatValue());
    }

    public void putLong( String key, long value) {
        SharedPreferences.Editor editor = getSharedPreference(CONFIG).edit();
        editor.putLong(key, value).apply();
    }

    public long getLong( String key, long defValue) {
        SharedPreferences sharedPreference = getSharedPreference(CONFIG);
        return sharedPreference.getLong(key, defValue);
    }

    public List<String> getStrListValue( String key) {
        ArrayList strList = new ArrayList();
        int size = getInt(key + "size", 0);

        for (int i = 0; i < size; ++i) {
            strList.add(getString(key + i, (String) null));
        }

        return strList;
    }

    public void putStrListValue( String key, List<String> strList) {
        if (null != strList) {
            removeStrList(key);
            int size = strList.size();
            putInt(key + "size", size);

            for (int i = 0; i < size; ++i) {
                putString(key + i, (String) strList.get(i));
            }

        }
    }

    public void removeStrList( String key) {
        int size = getInt(key + "size", 0);
        if (0 != size) {
            remove(key + "size");

            for (int i = 0; i < size; ++i) {
                remove(key + i);
            }

        }
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = getSharedPreference(CONFIG).edit();
        editor.remove(key).apply();
    }
}
