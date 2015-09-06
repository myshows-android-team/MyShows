package me.myshows.android.ui.fragments;

import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.util.Log;

import com.bumptech.glide.Glide;

import org.apache.commons.io.FileUtils;

import java.io.File;

import me.myshows.android.R;

/**
 * Created by Whiplash on 06.09.2015.
 */
public class SettingsFragment extends PreferenceFragment {

    public static final String RINGTONE = "ringtone";
    public static final String CLEAR_CACHE = "clear_cache";

    private static final String TAG = SettingsFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.myshows_preference);
        ringtonePreferenceInitialize();
        cachePreferenceInitialize();
    }

    private void ringtonePreferenceInitialize() {
        RingtonePreference preference = (RingtonePreference) findPreference(RINGTONE);
        setRingtoneName(preference);
        preference.setOnPreferenceChangeListener((pref, value) -> {
            setRingtoneName(preference, value.toString());
            return true;
        });
    }

    private void setRingtoneName(RingtonePreference preference) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setRingtoneName(preference, preferences.getString(RINGTONE, null));
    }

    private void setRingtoneName(RingtonePreference preference, String uri) {
        if (uri == null || uri.isEmpty()) {
            preference.setSummary(getString(R.string.none_ringtone));
        } else {
            Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), Uri.parse(uri));
            preference.setSummary(ringtone.getTitle(getActivity()));
        }
    }

    private void cachePreferenceInitialize() {
        Preference cachePreference = findPreference(CLEAR_CACHE);
        long bytes = getCacheSize(Glide.getPhotoCacheDir(getActivity()));
        cachePreference.setSummary(FileUtils.byteCountToDisplaySize(bytes));
        cachePreference.setOnPreferenceClickListener(preference -> {
            Log.d(TAG, "OLOLO");
            return true;
        });
    }

    private long getCacheSize(File dir) {
        long bytes = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                bytes += getCacheSize(dir);
            } else {
                bytes += file.length();
            }
        }
        return bytes;
    }

}
