package me.myshows.android.ui.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;

import com.bumptech.glide.Glide;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import me.myshows.android.R;
import me.myshows.android.api.impl.MyShowsClientImpl;
import me.myshows.android.model.persistent.dao.RealmManager;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Whiplash on 06.09.2015.
 */
public class SettingsFragment extends PreferenceFragment {

    public static final String COMPACT_MODE = "compact_mode";
    public static final String CLEAR_CACHE = "clear_cache";
    public static final String CHECK_NEW_SERIES = "check_new_series";
    public static final String TIME = "time";
    public static final String RINGTONE = "ringtone";
    public static final String VIBRATION = "vibration";
    public static final String SIGN_OUT = "sign_out";

    public static final int CLEAR_CACHE_REQUEST_CODE = 0;
    public static final int SIGN_OUT_REQUEST_CODE = 1;

    private static final String TAG = SettingsFragment.class.getSimpleName();
    private static final int HOUR = (int) TimeUnit.HOURS.toMinutes(1);
    private static final int DEFAULT_TIME = (int) TimeUnit.HOURS.toMinutes(12);

    private CheckBoxPreference compactModePreference;
    private Preference clearCachePreference;
    private CheckBoxPreference checkNewSeriesPreference;
    private Preference timePreference;
    private RingtonePreference ringtonePreference;
    private CheckBoxPreference vibrationPreference;
    private Preference signOutPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.myshows_preference);

        compactModePreference = (CheckBoxPreference) findPreference(COMPACT_MODE);
        clearCachePreference = findPreference(CLEAR_CACHE);
        checkNewSeriesPreference = (CheckBoxPreference) findPreference(CHECK_NEW_SERIES);
        timePreference = findPreference(TIME);
        ringtonePreference = (RingtonePreference) findPreference(RINGTONE);
        vibrationPreference = (CheckBoxPreference) findPreference(VIBRATION);
        signOutPreference = findPreference(SIGN_OUT);

        clearCachePreferenceInitialize(clearCachePreference);
        checkNewSeriesPreferenceInitialize(checkNewSeriesPreference);
        timePreferenceInitialize(timePreference);
        ringtonePreferenceInitialize(ringtonePreference);
        signOutPreferenceInitialize(signOutPreference);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CLEAR_CACHE_REQUEST_CODE:
                    clearCacheTask();
                    break;
                case SIGN_OUT_REQUEST_CODE:
                    signOutTask();
                    break;
            }
        }
    }

    private void clearCacheTask() {
        Observable.defer(this::clearCache)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> setCacheSize(clearCachePreference));
    }

    //TODO: use fromCallable
    private Observable<Object> clearCache() {
        Glide.get(getActivity()).clearDiskCache();
        return Observable.just(null);
    }

    private void signOutTask() {
        Observable.defer(this::signOut)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    getActivity().setResult(Activity.RESULT_OK, null);
                    getActivity().finish();
                });
    }

    private Observable<Object> signOut() {
        Realm.deleteRealm(new RealmManager().getConfiguration());
        MyShowsClientImpl.getInstance().cleanStorage();
        return Observable.just(null);
    }

    private void clearCachePreferenceInitialize(Preference clearCachePreference) {
        setCacheSize(clearCachePreference);
        clearCachePreference.setOnPreferenceClickListener(preference -> {
            FragmentManager fragmentManager = getFragmentManager();
            DialogFragment fragment = new ClearCacheDialog();
            fragment.setTargetFragment(this, CLEAR_CACHE_REQUEST_CODE);
            fragment.show(fragmentManager, null);
            return true;
        });
    }

    private void setCacheSize(Preference preference) {
        long bytes = getCacheSize(Glide.getPhotoCacheDir(getActivity()));
        preference.setSummary(FileUtils.byteCountToDisplaySize(bytes));
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

    private void checkNewSeriesPreferenceInitialize(CheckBoxPreference checkNewSeriesPreference) {
        setEnabledNotificationsViews(checkNewSeriesPreference.isChecked());
        checkNewSeriesPreference.setOnPreferenceChangeListener((preference, value) -> {
            setEnabledNotificationsViews((Boolean) value);
            return true;
        });
    }

    private void setEnabledNotificationsViews(boolean value) {
        timePreference.setEnabled(value);
        ringtonePreference.setEnabled(value);
        vibrationPreference.setEnabled(value);
    }

    private void timePreferenceInitialize(Preference timePreference) {
        setTime(timePreference);
        timePreference.setOnPreferenceClickListener(preference -> {
            int time = timePreference.getSharedPreferences().getInt(timePreference.getKey(), DEFAULT_TIME);
            new TimePickerDialog(getActivity(), (timePicker, h, m) -> setTime(preference, h, m),
                    time / HOUR, time % HOUR, true).show();
            return true;
        });
    }

    private void setTime(Preference preference) {
        int time = timePreference.getSharedPreferences().getInt(timePreference.getKey(), DEFAULT_TIME);
        setTime(preference, time / HOUR, time % HOUR);
    }

    private void setTime(Preference preference, int hour, int minute) {
        SharedPreferences.Editor editor = preference.getEditor();
        editor.putInt(preference.getKey(), hour * HOUR + minute);
        editor.apply();
        preference.setSummary(String.format("%d:%02d", hour, minute));
    }

    private void ringtonePreferenceInitialize(RingtonePreference ringtonePreference) {
        setRingtoneName(ringtonePreference);
        ringtonePreference.setOnPreferenceChangeListener((preference, value) -> {
            setRingtoneName(preference, value.toString());
            return true;
        });
    }

    private void setRingtoneName(Preference preference) {
        String uri = preference.getSharedPreferences().getString(preference.getKey(), null);
        setRingtoneName(preference, uri);
    }

    private void setRingtoneName(Preference preference, String uri) {
        if (uri == null || uri.isEmpty()) {
            preference.setSummary(R.string.none_ringtone);
        } else {
            Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), Uri.parse(uri));
            preference.setSummary(ringtone.getTitle(getActivity()));
        }
    }

    private void signOutPreferenceInitialize(Preference signOutPreference) {
        signOutPreference.setOnPreferenceClickListener(preference -> {
            FragmentManager fragmentManager = getFragmentManager();
            DialogFragment fragment = new SignOutDialog();
            fragment.setTargetFragment(this, SIGN_OUT_REQUEST_CODE);
            fragment.show(fragmentManager, null);
            return true;
        });
    }
}