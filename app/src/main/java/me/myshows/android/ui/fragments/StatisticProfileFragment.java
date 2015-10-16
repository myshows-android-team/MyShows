package me.myshows.android.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trello.rxlifecycle.FragmentEvent;

import me.myshows.android.R;
import me.myshows.android.model.Statistics;
import me.myshows.android.model.User;
import me.myshows.android.ui.view.StatisticView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by warrior on 20.09.15.
 */
public class StatisticProfileFragment extends ProfileHeaderFragment {

    private StatisticView episodes;
    private StatisticView hours;
    private StatisticView days;
    private TextView place;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.profile_fragment_statistic, container, false);
        episodes = (StatisticView) view.findViewById(R.id.episodes);
        hours = (StatisticView) view.findViewById(R.id.hours);
        days = (StatisticView) view.findViewById(R.id.days);
        place = (TextView) view.findViewById(R.id.place);
        return view;
    }

    @Override
    protected void bind(@NonNull User user) {
        Statistics statistics = user.getStats();
        episodes.setValue(statistics.getWatchedEpisodes());
        hours.setValue((int) Math.ceil(statistics.getWatchedHours()));
        days.setValue((int) Math.ceil(statistics.getWatchedDays()));

        Observable.from(user.getFriends())
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.computation())
                .filter(f -> f.getWastedTime() > user.getWastedTime())
                .count()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(n -> place.setText(getActivity().getString(R.string.place, n + 1)));
    }

    public static Fragment newInstance(@Nullable User user) {
        StatisticProfileFragment fragment = new StatisticProfileFragment();
        if (user != null) {
            fragment.setUser(user);
        }
        return fragment;
    }
}
