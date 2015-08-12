package me.myshows.android.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.myshows.android.R;
import me.myshows.android.api.MyShowsClient;
import me.myshows.android.api.impl.MyShowsClientImpl;
import me.myshows.android.model.Action;
import me.myshows.android.model.Feed;
import me.myshows.android.model.Gender;
import me.myshows.android.model.User;
import me.myshows.android.model.UserFeed;
import me.myshows.android.ui.activities.ShowActivity;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by warrior on 19.07.15.
 */
public class FriendsFragment extends RxFragment {

    private MyShowsClient client;

    private RecyclerView recyclerView;

    private List<UserFeed> feeds;
    private Map<String, String> friendsAvatar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_shows_fragment, container, false);
        client = MyShowsClientImpl.getInstance();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        loadData();

        return view;
    }

    private void loadData() {
        client.friendsNews()
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .map(this::extractUserFeeds)
                .subscribe(feeds -> {
                            this.feeds = feeds;
                            trySetAdapter();
                        }
                );
        client.profile()
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .map(this::extractFriendsAvatar)
                .subscribe(friendsAvatar -> {
                    this.friendsAvatar = friendsAvatar;
                    trySetAdapter();
                });
    }

    private void trySetAdapter() {
        if (feeds != null && friendsAvatar != null) {
            FeedAdapter adapter = new FeedAdapter(feeds, friendsAvatar);
            recyclerView.setAdapter(adapter);
        }
    }

    private List<UserFeed> extractUserFeeds(List<Feed> feeds) {
        List<UserFeed> userFeeds = new ArrayList<>();
        for (Feed feed : feeds) {
            userFeeds.addAll(feed.getFeeds());
        }
        return userFeeds;
    }

    private Map<String, String> extractFriendsAvatar(User user) {
        Map<String, String> friendsAvatar = new HashMap<>();
        for (User friend : user.getFriends()) {
            friendsAvatar.put(friend.getLogin(), friend.getAvatarUrl());
        }
        return friendsAvatar;
    }

    private class FeedAdapter extends RecyclerView.Adapter<FeedHolder> {

        private final List<UserFeed> userFeeds;
        private final Map<String, String> friendsAvatar;

        public FeedAdapter(List<UserFeed> userFeeds, Map<String, String> friendsAvatar) {
            this.userFeeds = userFeeds;
            this.friendsAvatar = friendsAvatar;
        }

        @Override
        public FeedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_feed_view, parent, false);
            return new FeedHolder(view);
        }

        @Override
        public void onBindViewHolder(FeedHolder holder, int position) {
            UserFeed userFeed = userFeeds.get(position);
            holder.bind(userFeed, friendsAvatar.get(userFeed.getLogin()));
        }

        @Override
        public int getItemCount() {
            return userFeeds.size();
        }
    }

    private class FeedHolder extends RecyclerView.ViewHolder {

        private ImageView avatar;
        private TextView name;
        private TextView action;
        private ImageView actionIcon;

        public FeedHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.friend_avatar);
            name = (TextView) itemView.findViewById(R.id.friend_name);
            action = (TextView) itemView.findViewById(R.id.friend_action);
            actionIcon = (ImageView) itemView.findViewById(R.id.feed_action_icon);
        }

        public void bind(UserFeed feed, String avatarUrl) {
            if (avatarUrl == null) {
                client.profile(feed.getLogin())
                        .compose(bindToLifecycle())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(user -> setAvatar(user.getAvatarUrl()));
            } else {
                setAvatar(avatarUrl);
            }

            name.setText(feed.getLogin());

            switch (feed.getAction()) {
                case WATCH:
                    setWatchAction(feed);
                    break;
                case NEW:
                    setWatchAction(feed);
                    break;
                default:
                    throw new RuntimeException("Illegal action");
            }
        }

        private void setAvatar(String avatarUrl) {
            Glide.with(itemView.getContext())
                    .load(avatarUrl)
                    .into(avatar);
        }

        private void setNewAction(UserFeed feed) {
            // api never returns anything except "watch"
            actionIcon.setImageResource(Action.NEW.getDrawableId());
        }

        private void setWatchAction(UserFeed feed) {
            int pluralsId = feed.getGender() == Gender.MALE ? R.plurals.m_watch_series : R.plurals.f_watch_series;
            int seriesNumber = feed.getEpisodes();
            String showName = feed.getShow();

            actionIcon.setImageResource(Action.WATCH.getDrawableId());

            String actionText = getResources().getQuantityString(pluralsId, seriesNumber, seriesNumber, showName);
            int start = actionText.indexOf(showName);
            int end = start + showName.length();
            SpannableString ss = new SpannableString(actionText);
            ss.setSpan(new ShowSpan(feed), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            action.setText(ss, TextView.BufferType.SPANNABLE);
            action.setMovementMethod(LinkMovementMethod.getInstance());
        }

        private class ShowSpan extends ClickableSpan {

            private final UserFeed feed;

            private ShowSpan(UserFeed feed) {
                this.feed = feed;
            }

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(itemView.getContext(), ShowActivity.class);
                intent.putExtra(ShowActivity.SHOW_ID, feed.getShowId());
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setColor(getResources().getColor(R.color.primary));
            }
        }
    }
}
