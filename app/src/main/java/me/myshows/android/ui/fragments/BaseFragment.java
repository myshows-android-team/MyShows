package me.myshows.android.ui.fragments;

import com.squareup.leakcanary.RefWatcher;
import com.trello.rxlifecycle.components.RxFragment;

import me.myshows.android.MyShowsApplication;

/**
 * Created by warrior on 05.12.15.
 */
public abstract class BaseFragment extends RxFragment {

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MyShowsApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
