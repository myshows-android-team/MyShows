package me.myshows.android.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import me.myshows.android.R;
import me.myshows.android.model.User;

/**
 * Created by warrior on 20.09.15.
 */
public class AvatarProfileFragment extends ProfileHeaderFragment {

    private ImageView avatar;
    private TextView username;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment_avatar, container, false);
        avatar = (ImageView) view.findViewById(R.id.avatar);
        username = (TextView) view.findViewById(R.id.username);
        return view;
    }

    @Override
    protected void bind(@NonNull User user) {
        username.setText(user.getLogin());
        Glide.with(this)
                .load(user.getAvatarUrl())
                .into(avatar);
    }

    public static Fragment newInstance(@Nullable User user) {
        AvatarProfileFragment fragment = new AvatarProfileFragment();
        if (user != null) {
            fragment.setUser(user);
        }
        return fragment;
    }
}
