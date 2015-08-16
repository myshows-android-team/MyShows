package me.myshows.android.ui.fragments;

import android.content.Context;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

/**
 * Created by warrior on 19.07.15.
 */
public class FriendsFragment extends RxFragment {

    private RecyclerView recyclerView;
    private StickyRecyclerHeadersDecoration itemDecoration;

    private List<Feed> feeds;
    private Map<String, String> friendsAvatar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_fragment, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        loadData();

        return view;
    }

    private void loadData() {
        MyShowsClient client = MyShowsClientImpl.getInstance();
        client.friendsNews()
                .compose(bindToLifecycle())
                .subscribe(feeds -> {
                    this.feeds = feeds;
                    trySetAdapter();
                });
        client.profile()
                .compose(bindToLifecycle())
                .map(this::extractFriendsAvatar)
                .subscribe(friendsAvatar -> {
                    this.friendsAvatar = friendsAvatar;
                    trySetAdapter();
                });
    }

    private void trySetAdapter() {
        if (feeds != null && friendsAvatar != null) {
            FeedAdapter adapter = new FeedAdapter(getActivity(), feeds, friendsAvatar);
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
        for (User follower : user.getFollowers()) {
            friendsAvatar.put(follower.getLogin(), follower.getAvatarUrl());
        }
        return friendsAvatar;
    }

    private static class FeedHolder extends RecyclerView.ViewHolder {

        private static Typeface typeface;

        private final ImageView avatar;
        private final TextView name;
        private final TextView action;
        private final ImageView actionIcon;

        public FeedHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.friend_avatar);
            name = (TextView) itemView.findViewById(R.id.friend_name);
            action = (TextView) itemView.findViewById(R.id.friend_action);
            actionIcon = (ImageView) itemView.findViewById(R.id.feed_action_icon);

            if (typeface == null) {
                typeface = Typeface.createFromAsset(itemView.getContext().getAssets(), "Roboto-Medium.ttf");
            }
            name.setTypeface(typeface);
        }

        public void bind(UserFeed feed, String avatarUrl) {
            setAvatar(avatarUrl);
            name.setText(feed.getLogin());

            switch (feed.getAction()) {
                case WATCH:
                    setWatchAction(feed);
                    break;
                case NEW:
                    setNewAction(feed);
                    break;
            }
        }

        private void setAvatar(String avatarUrl) {
            Glide.with(itemView.getContext())
                    .load(avatarUrl)
                    .placeholder(R.drawable.default_avatar)
                    .into(avatar);
        }

        private void setNewAction(UserFeed feed) {
            actionIcon.setImageResource(Action.NEW.getDrawableId());
            int stringId = feed.getGender() == Gender.FEMALE ? R.string.f_started_show : R.string.m_started_show;
            String showName = feed.getShow();
            String actionText = itemView.getContext().getString(stringId, showName);
            setShowActionText(actionText, feed);
        }

        private void setWatchAction(UserFeed feed) {
            actionIcon.setImageResource(Action.WATCH.getDrawableId());
            int pluralsId = feed.getGender() == Gender.FEMALE ? R.plurals.f_watch_series : R.plurals.m_watch_series;
            int seriesNumber = feed.getEpisodes();
            String showName = feed.getShow();
            String episodeName = "[" + feed.getEpisode() + "]";
            String actionText = itemView.getResources().getQuantityString(pluralsId, seriesNumber, seriesNumber, showName, episodeName);
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
                itemView.getContext().startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setColor(itemView.getContext().getResources().getColor(R.color.primary));
            }
        }
    }

    private static class FeedHeaderHolder extends RecyclerView.ViewHolder {

        private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd MMMM", Locale.getDefault());
        private static final Calendar TODAY = Calendar.getInstance();
        private static final Calendar YESTERDAY = Calendar.getInstance();

        static {
            YESTERDAY.add(Calendar.DAY_OF_YEAR, -1);
        }

        private final TextView header;

        public FeedHeaderHolder(View itemView) {
            super(itemView);
            header = (TextView) itemView.findViewById(R.id.feed_header);
        }

        private static String getText(Context context, Date feedDate) {
            Calendar feedDateCalendar = Calendar.getInstance();
            feedDateCalendar.setTime(feedDate);
            if (isSameDay(TODAY, feedDateCalendar)) {
                return context.getString(R.string.today);
            } else if (isSameDay(YESTERDAY, feedDateCalendar)) {
                return context.getString(R.string.yesterday);
            } else {
                return FORMATTER.format(feedDate);
            }
        }

        private static boolean isSameDay(Calendar first, Calendar second) {
            return (first.get(Calendar.YEAR) == second.get(Calendar.YEAR)
                    && first.get(Calendar.DAY_OF_YEAR) == second.get(Calendar.DAY_OF_YEAR));
        }

        private static int getId(Context context, Date feedDate) {
            return Math.abs(getText(context, feedDate).hashCode());
        }

        public void bind(Date feedDate) {
            header.setText(getText(itemView.getContext(), feedDate));
        }
    }

    private static class FeedAdapter extends RecyclerView.Adapter<FeedHolder> implements StickyRecyclerHeadersAdapter<FeedHeaderHolder> {

        private final Context context;
        private final List<UserFeed> userFeeds;
        private final List<Date> feedsDate;
        private final Map<String, String> friendsAvatar;

        public FeedAdapter(Context context, List<Feed> feeds, Map<String, String> friendsAvatar) {
            this.context = context;
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
            Date feedDate = feedsDate.get(position);
            return FeedHeaderHolder.getId(context, feedDate);
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
