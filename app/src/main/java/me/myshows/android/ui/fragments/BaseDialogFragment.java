package me.myshows.android.ui.fragments;

import android.app.DialogFragment;

import com.squareup.leakcanary.RefWatcher;

import me.myshows.android.MyShowsApplication;

/**
 * Created by warrior on 05.12.15.
 */
public abstract class BaseDialogFragment extends DialogFragment {

    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MyShowsApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
