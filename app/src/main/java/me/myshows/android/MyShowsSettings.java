package me.myshows.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.concurrent.TimeUnit;

/**
 * Created by Whiplash on 23.09.2015.
 */
public class MyShowsSettings {

    public static final String COMPACT_MODE = "compact_mode";
    public static final String CHECK_NEW_SERIES = "check_new_series";
    public static final String TIME = "time";
    public static final String RINGTONE = "ringtone";
    public static final String VIBRATION = "vibration";

    private static final boolean DEFAULT_CHECK_NEW_SERIES_STATE = true;
    private static final boolean DEFAULT_COMPACT_MODE_STATE = false;
    private static final boolean DEFAULT_VIBRATION_STATE = true;
    private static final String DEFAULT_RINGTONE_VALUE = null;
    private static final int DEFAULT_TIME_VALUE = (int) TimeUnit.HOURS.toMinutes(12);

    private MyShowsSettings() {
    }

    public static boolean isCheckNewSeries(Context context) {
        return getBooleanPreference(context, CHECK_NEW_SERIES, DEFAULT_CHECK_NEW_SERIES_STATE);
    }

    public static boolean isCompactMode(Context context) {
        return getBooleanPreference(context, COMPACT_MODE, DEFAULT_COMPACT_MODE_STATE);
    }

    public static boolean hasVibration(Context context) {
        return getBooleanPreference(context, VIBRATION, DEFAULT_VIBRATION_STATE);
    }

    public static String getRingtone(Context context) {
        return getStringPreference(context, RINGTONE, DEFAULT_RINGTONE_VALUE);
    }

    public static int getTimeValue(Context context) {
        return getIntPreference(context, TIME, DEFAULT_TIME_VALUE);
    }

    private static boolean getBooleanPreference(Context context, String key, boolean defaultValue) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, defaultValue);
    }

    private static String getStringPreference(Context context, String key, String defaultValue) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, defaultValue);
    }

    private static int getIntPreference(Context context, String key, int defaultValue) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key, defaultValue);
    }
}
