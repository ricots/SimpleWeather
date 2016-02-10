package com.vasskob.simpleweather.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    private static final String CITY_PREFERENCE = "CITY_NAME";

    @SuppressWarnings("deprecation")
    private static final int MODE = Context.MODE_WORLD_WRITEABLE;

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(CITY_PREFERENCE, MODE);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }

    public static void setStringPreferences(Context context, String key, String value) {
        getEditor(context).putString(key, value).commit();

    }

    public static String getStringPreferences(Context context, String key) {
        return getPreferences(context).getString(key, null);
    }
}