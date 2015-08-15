package me.myshows.android.ui.fragments;

import android.content.Intent;
import android.graphics.Typeface;
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
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.trello.rxlifecycle.components.support.RxFragment;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
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

    private static Typeface typeface;
    private static MyShowsClient client;

    private RecyclerView recyclerView;
    private StickyRecyclerHeadersDecoration itemDecoration;
    private FeedHeader feedHeader;

    private List<Feed> feeds;
    private Map<String, String> friendsAvatar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_fragment, container, false);
        client = MyShowsClientImpl.getInstance();
        if (typeface == null) {
            typeface = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Medium.ttf");
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        feedHeader = new FeedHeader(getActivity());

        loadData();

        return view;
    }

    private void loadData() {
        client.friendsNews()
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(feeds -> {
                    this.feeds = feeds;
                    trySetAdapter();
                });
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
            if (itemDecoration != null) {
                recyclerView.removeItemDecoration(itemDecoration);
            }
            recyclerView.setAdapter(adapter);
            itemDecoration = new StickyRecyclerHeadersDecoration(adapter);
            recyclerView.addItemDecoration(itemDecoration);
        }
    }

    private Map<String, String> extractFriendsAvatar(User user) {
        Map<String, String> friendsAvatar = new HashMap<>();
        for (User friend : user.getFriends()) {
            friendsAvatar.put(friend.getLogin(), friend.getAvatarUrl());
        }
        return friendsAvatar;
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
            name.setTypeface(typeface);
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
                    setNewAction(feed);
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
            actionIcon.setImageResource(Action.NEW.getDrawableId());
            int stringId = feed.getGender() == Gender.MALE ? R.string.m_started_show : R.string.f_started_show;
            String showName = feed.getShow();
            String actionText = getString(stringId, showName);
            setShowActionText(actionText, feed);
        }

        private void setWatchAction(UserFeed feed) {
            actionIcon.setImageResource(Action.WATCH.getDrawableId());
            int pluralsId = feed.getGender() == Gender.MALE ? R.plurals.m_watch_series : R.plurals.f_watch_series;
            int seriesNumber = feed.getEpisodes();
            String showName = feed.getShow();
            String actionText = getResources().getQuantityString(pluralsId, seriesNumber, seriesNumber, showName);
            setShowActionText(actionText, feed);
        }

        private void setShowActionText(String actionText, UserFeed feed) {
            int start = actionText.indexOf(feed.getShow());
            int end = start + feed.getShow().length();
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

    private class FeedHeaderHolder extends RecyclerView.ViewHolder {

        private TextView header;

        public FeedHeaderHolder(View itemView) {
            super(itemView);
            header = (TextView) itemView.findViewById(R.id.feed_header);
        }

        public void bind(DateTime feedDate) {
            header.setText(feedHeader.getText(feedDate));
        }
    }

    private class FeedAdapter extends RecyclerView.Adapter<FeedHolder> implements StickyRecyclerHeadersAdapter<FeedHeaderHolder> {

        private final List<UserFeed> userFeeds;
        private final List<DateTime> feedsDate;
        private final Map<String, String> friendsAvatar;

        public FeedAdapter(List<Feed> feeds, Map<String, String> friendsAvatar) {
            this.userFeeds = new ArrayList<>();
            this.feedsDate = new ArrayList<>();
            this.friendsAvatar = friendsAvatar;
            for (Feed feed : feeds) {
                userFeeds.addAll(feed.getFeeds());
                feedsDate.addAll(Collections.nCopies(feed.getFeeds().size(), feed.getDate()));
            }
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
        public long getHeaderId(int position) {
            DateTime feedDate = feedsDate.get(position);
            return feedHeader.getId(feedDate);
        }

        @Override
        public FeedHeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_feed_header_view, parent, false);
            return new FeedHeaderHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(FeedHeaderHolder feedHeaderHolder, int position) {
            feedHeaderHolder.bind(feedsDate.get(position));
        }

        @Override
        public int getItemCount() {
            return userFeeds.size();
        }
    }
}
