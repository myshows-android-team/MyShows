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
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.SwitchPreferenceCompat;

import com.bumptech.glide.Glide;
import com.squareup.leakcanary.RefWatcher;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import me.myshows.android.MyShowsApplication;
import me.myshows.android.MyShowsSettings;
import me.myshows.android.R;
import me.myshows.android.api.MyShowsClient;
import rx.Completable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Whiplash on 06.09.2015.
 */
public class SettingsFragment extends PreferenceFragment {

    public static final int CLEAR_CACHE_REQUEST_CODE = 0;
    public static final int SIGN_OUT_REQUEST_CODE = 1;
    public static final int RINGTONE_REQUEST_CODE = 2;

    private static final String CHECK_NEW_SERIES = MyShowsSettings.CHECK_NEW_SERIES;
    private static final String TIME = MyShowsSettings.TIME;
    private static final String RINGTONE = MyShowsSettings.RINGTONE;
    private static final String VIBRATION = MyShowsSettings.VIBRATION;
    private static final String CLEAR_CACHE = "clear_cache";
    private static final String SIGN_OUT = "sign_out";

    private static final int HOUR = (int) TimeUnit.HOURS.toMinutes(1);

    private Preference clearCachePreference;
    private Preference timePreference;
    private Preference ringtonePreference;
    private CheckBoxPreference vibrationPreference;

    private CompositeSubscription subscriptions;

    @Inject
    MyShowsClient client;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MyShowsApplication.getComponent(activity).inject(this);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.myshows_preference);
        subscriptions = new CompositeSubscription();

        clearCachePreference = findPreference(CLEAR_CACHE);
        SwitchPreferenceCompat checkNewSeriesPreference = (SwitchPreferenceCompat) findPreference(CHECK_NEW_SERIES);
        timePreference = findPreference(TIME);
        ringtonePreference = findPreference(RINGTONE);
        vibrationPreference = (CheckBoxPreference) findPreference(VIBRATION);
        Preference signOutPreference = findPreference(SIGN_OUT);

        clearCachePreferenceInitialize(clearCachePreference);
        checkNewSeriesPreferenceInitialize(checkNewSeriesPreference);
        timePreferenceInitialize(timePreference);
        ringtonePreferenceInitialize(ringtonePreference);
        signOutPreferenceInitialize(signOutPreference);
    }

    @Override
    public void onDestroyView() {
        if (subscriptions != null && !subscriptions.isUnsubscribed()) {
            subscriptions.unsubscribe();
        }
        super.onDestroyView();
    }

    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MyShowsApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
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
                case RINGTONE_REQUEST_CODE:
                    setRingtoneName(ringtonePreference, data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI));
                    break;
            }
        }
    }

    private void clearCacheTask() {
        subscriptions.add(Completable.fromAction(this::clearCache)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> setCacheSize(clearCachePreference)));
    }

    private void clearCache() {
        Glide.get(getActivity()).clearDiskCache();
    }

    private void signOutTask() {
        subscriptions.add(Completable.fromAction(this::signOut)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    getActivity().setResult(Activity.RESULT_OK, null);
                    getActivity().finish();
                }));
    }

    private void signOut() {
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().clear().apply();
    }

    private void clearCachePreferenceInitialize(Preference clearCachePreference) {
        setCacheSize(clearCachePreference);
        clearCachePreference.setOnPreferenceClickListener(preference -> {
            FragmentManager fragmentManager = getFragmentManager();
            DialogFragment fragment = new ClearCacheDialogFragment();
            fragment.setTargetFragment(this, CLEAR_CACHE_REQUEST_CODE);
            fragment.show(fragmentManager, null);
            return true;
        });
    }

    private void setCacheSize(Preference preference) {
        subscriptions.add(Single.<Long>create(subscriber -> {
            File dir = Glide.getPhotoCacheDir(getActivity());
            subscriber.onSuccess(getCacheSize(dir));
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bytes -> {
                    String humanReadableSize = FileUtils.byteCountToDisplaySize(bytes);
                    preference.setSummary(humanReadableSize);
                }));
    }

    private long getCacheSize(File dir) {
        long bytes = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    bytes += getCacheSize(dir);
                } else {
                    bytes += file.length();
                }
            }
        }
        return bytes;
    }

    private void checkNewSeriesPreferenceInitialize(SwitchPreferenceCompat checkNewSeriesPreference) {
        setEnabledNotificationsPreferences(checkNewSeriesPreference.isChecked());
        checkNewSeriesPreference.setOnPreferenceChangeListener((preference, value) -> {
            setEnabledNotificationsPreferences((Boolean) value);
            return true;
        });
    }

    private void setEnabledNotificationsPreferences(boolean value) {
        timePreference.setEnabled(value);
        ringtonePreference.setEnabled(value);
        vibrationPreference.setEnabled(value);
    }

    private void timePreferenceInitialize(Preference timePreference) {
        setTime(timePreference);
        timePreference.setOnPreferenceClickListener(preference -> {
            int time = MyShowsSettings.getTimeValue(getActivity());
            new TimePickerDialog(getActivity(), (timePicker, h, m) -> saveTime(preference, h, m),
                    time / HOUR, time % HOUR, true).show();
            return true;
        });
    }

    private void setTime(Preference preference) {
        int time = MyShowsSettings.getTimeValue(getActivity());
        saveTime(preference, time / HOUR, time % HOUR);
    }

    private void saveTime(Preference preference, int hour, int minute) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(preference.getKey(), hour * HOUR + minute);
        editor.apply();
        preference.setSummary(String.format("%d:%02d", hour, minute));
    }

    private void ringtonePreferenceInitialize(Preference ringtonePreference) {
        setRingtoneName(ringtonePreference, MyShowsSettings.getRingtone(getActivity()));
        ringtonePreference.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_NOTIFICATION_URI);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, MyShowsSettings.getRingtone(getActivity()));
            startActivityForResult(intent, RINGTONE_REQUEST_CODE);
            return true;
        });
    }

    private void setRingtoneName(Preference preference, Uri uri) {
        Uri correctUri = null;
        String correctTitle = getString(R.string.none_ringtone);
        if (uri != null) {
            Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), uri);
            if (ringtone != null) {
                correctUri = uri;
                correctTitle = ringtone.getTitle(getActivity());
            }
        }
        saveRingtoneUri(preference, correctUri);
        preference.setSummary(correctTitle);
    }

    private void saveRingtoneUri(Preference preference, Uri uri) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(preference.getKey(), uri == null ? MyShowsSettings.SILENT_MODE : uri.toString());
        editor.apply();
    }

    private void signOutPreferenceInitialize(Preference signOutPreference) {
        signOutPreference.setOnPreferenceClickListener(preference -> {
            FragmentManager fragmentManager = getFragmentManager();
            DialogFragment fragment = new SignOutDialogFragment();
            fragment.setTargetFragment(this, SIGN_OUT_REQUEST_CODE);
            fragment.show(fragmentManager, null);
            return true;
        });
    }
}
