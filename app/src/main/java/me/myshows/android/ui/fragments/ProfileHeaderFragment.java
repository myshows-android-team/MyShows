package me.myshows.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import me.myshows.android.model.User;

/**
 * Created by warrior on 19.07.15.
 */
public abstract class ProfileHeaderFragment extends BaseFragment {

    private User user;

    private boolean viewCreated;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewCreated = true;
        if (user != null) {
            bind(user);
        }
    }

    @Override
    public void onDestroyView() {
        viewCreated = false;
        user = null;
        super.onDestroyView();
    }

    public void setUser(@NonNull User user) {
        this.user = user;
        if (viewCreated) {
            bind(user);
        }
    }

    protected abstract void bind(@NonNull User user);
}
