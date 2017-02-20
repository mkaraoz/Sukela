package org.bok.mk.sukela.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by mk on 23.08.2015.
 */
public class SukelaPrefs
{
    private SharedPreferences prefs = null;

    private SukelaPrefs(Context ctex)
    {
        prefs = PreferenceManager.getDefaultSharedPreferences(ctex);
    }

    public static SukelaPrefs instance(Context ctex)
    {
        SukelaPrefs sukelaPrefs = new SukelaPrefs(ctex);
        return sukelaPrefs;
    }

    public void putInt(String key, int val)
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, val);
        editor.apply();
    }

    public int getInt(String key, int defValue)
    {
        return prefs.getInt(key, defValue);
    }

    public boolean getBoolean(String key, boolean defValue)
    {
        return prefs.getBoolean(key, defValue);
    }

    public void putBoolean(String key, boolean val)
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, val);
        editor.apply();
    }

    public void putString(String key, String defValue)
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, defValue);
        editor.apply();
    }

    public String getString(String key, String defValue)
    {
        return prefs.getString(key, defValue);
    }
}
