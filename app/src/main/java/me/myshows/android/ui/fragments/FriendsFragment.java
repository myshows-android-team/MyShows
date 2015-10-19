package me.myshows.android.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.trello.rxlifecycle.components.RxFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import me.myshows.android.model.UserPreview;
import me.myshows.android.ui.activities.ShowActivity;
import me.myshows.android.ui.decorators.OffsetDecorator;
import me.myshows.android.ui.decorators.SimpleDrawableDecorator;
import rx.Observable;

/**
 * Created by warrior on 19.07.15.
 */
public class FriendsFragment extends RxFragment {

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_fragment, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new FeedAdapter.FeedOffsetDecorator(
                getResources().getDimensionPixelSize(R.dimen.default_padding)));
        recyclerView.addItemDecoration(new FeedAdapter.FeedShadowDecorator(
                getResources().getDrawable(R.drawable.show_screen_shadow)));

        loadData();

        return view;
    }

    private void loadData() {
        MyShowsClient client = MyShowsClientImpl.getInstance();
        Observable<Map<String, String>> friendsAvatarObservable = client.profile()
                .map(FriendsFragment::extractAvatarUrls);
        Observable.combineLatest(client.friendsNews(), friendsAvatarObservable, FriendsFragment::extractData)
                .compose(bindToLifecycle())
                .subscribe(this::setAdapter);
    }

    private void setAdapter(List<FeedDataHolder> feedData) {
        recyclerView.setAdapter(new FeedAdapter(feedData));
    }

    private static Map<String, String> extractAvatarUrls(@NonNull User user) {
        Map<String, String> avatarUrls = new HashMap<>();
        extractAvatarUrls(avatarUrls, user.getFriends());
        extractAvatarUrls(avatarUrls, user.getFollowers());
        return avatarUrls;
    }

    private static void extractAvatarUrls(@NonNull Map<String, String> avatarUrls, @Nullable List<UserPreview> userPreviews) {
        if (userPreviews != null) {
            for (UserPreview userPreview : userPreviews) {
                avatarUrls.put(userPreview.getLogin(), userPreview.getAvatarUrl());
            }
        }
    }

    private static List<FeedDataHolder> extractData(List<Feed> feeds, Map<String, String> friendsAvatar) {
        List<FeedDataHolder> feedData = new ArrayList<>();
        for (Feed feed : feeds) {
            feedData.add(new FeedDataHolder(feed.getDate()));
            for (UserFeed userFeed : feed.getFeeds()) {
                feedData.add(new FeedDataHolder(userFeed, friendsAvatar.get(userFeed.getLogin())));
            }
        }
        return feedData;
    }

    private static class FeedHolder extends RecyclerView.ViewHolder {

        public static final String ROBOTO_REGULAR = "sans-serif";

        private final ImageView avatar;
        private final TextView name;
        private final TextView action;
        private final View actionBackground;
        private final ImageView actionIcon;

        private final View topLine;
        private final View bottomLine;
        private final View divider;

        public FeedHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.friend_avatar);
            name = (TextView) itemView.findViewById(R.id.friend_name);
            action = (TextView) itemView.findViewById(R.id.friend_action);
            actionBackground = itemView.findViewById(R.id.feed_action_background);
            actionIcon = (ImageView) itemView.findViewById(R.id.feed_action_icon);

            topLine = itemView.findViewById(R.id.history_line_top);
            bottomLine = itemView.findViewById(R.id.history_line_bottom);
            divider = itemView.findViewById(R.id.feed_divider);

            action.setMovementMethod(LinkMovementMethod.getInstance());
        }

        public void bind(UserFeed feed, String avatarUrl, boolean isFirst, boolean isLast) {
            setAvatar(avatarUrl);
            name.setText(feed.getLogin());

            topLine.setVisibility(isFirst ? View.INVISIBLE : View.VISIBLE);
            bottomLine.setVisibility(isLast ? View.INVISIBLE : View.VISIBLE);
            divider.setVisibility(isLast ? View.GONE : View.VISIBLE);

            Action feedAction = feed.getAction();
            actionIcon.setImageResource(feedAction.getDrawableId());
            setActionIconBackground(itemView.getResources().getColor(feedAction.getColor()));

            switch (feedAction) {
                case WATCH:
                    setWatchAction(feed);
                    break;
                case NEW:
                    setNewAction(feed);
                    break;
                case WATCH_LATER:
                    setWatchLaterAction(feed);
                    break;
                case RATING:
                    setRatingAction(feed);
                    break;
                case STOP_WATCH:
                    setStopWatchAction(feed);
                    break;
                case ACHIEVEMENT:
                    setAchievementAction(feed);
                    break;
            }
        }

        private void setActionIconBackground(int color) {
            GradientDrawable shape = new GradientDrawable();
            shape.setCornerRadius(actionBackground.getResources()
                    .getDimensionPixelSize(R.dimen.list_feed_action_icon_corner_radius));
            shape.setColor(color);
            actionBackground.setBackground(shape);
        }

        private void setAvatar(String avatarUrl) {
            Glide.with(itemView.getContext())
                    .load(avatarUrl)
                    .into(avatar);
        }

        private void setWatchAction(UserFeed feed) {
            int seriesNumber = feed.getEpisodes();
            String showName = feed.getShow();
            String actionText;
            if (seriesNumber == 1) {
                setWatchOneSeriesAction(feed, showName);
            } else {
                int pluralsId = feed.getGender() == Gender.FEMALE ? R.plurals.f_watch_series : R.plurals.m_watch_series;
                actionText = itemView.getResources().getQuantityString(pluralsId, seriesNumber, seriesNumber, showName);
                setShowActionText(actionText, feed);
            }
        }

        private void setWatchOneSeriesAction(UserFeed feed, String showName) {
            String actionText;
            String episodeName = feed.getEpisode();
            int stringId = feed.getGender() == Gender.FEMALE ? R.string.f_watch_one_series : R.string.m_watch_one_series;
            actionText = itemView.getContext().getString(stringId, episodeName, showName);
            Spannable spannable = setShowActionText(actionText, feed);

            int start = actionText.indexOf(episodeName);
            int end = start + episodeName.length();
            spannable.setSpan(new ForegroundColorSpan(itemView.getResources().getColor(R.color.dark_gray)),
                    start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new TypefaceSpan(ROBOTO_REGULAR), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            action.setText(spannable);
        }

        private void setNewAction(UserFeed feed) {
            int stringId = feed.getGender() == Gender.FEMALE ? R.string.f_started_show : R.string.m_started_show;
            String showName = feed.getShow();
            String actionText = itemView.getContext().getString(stringId, showName);
            setShowActionText(actionText, feed);
        }

        private void setWatchLaterAction(UserFeed feed) {
            int stringId = R.string.watch_later_show;
            String showName = feed.getShow();
            String actionText = itemView.getContext().getString(stringId, showName);
            setShowActionText(actionText, feed);
        }

        private void setRatingAction(UserFeed feed) {
            int stringId = feed.getGender() == Gender.FEMALE ? R.string.f_gave_rating : R.string.m_gave_rating;
            String showName = feed.getShow();
            int rating = 5; //TODO: get correct rating value
            String actionText = itemView.getContext().getString(stringId, showName, rating);
            setShowActionText(actionText, feed);
        }

        private void setStopWatchAction(UserFeed feed) {
            int stringId = feed.getGender() == Gender.FEMALE ? R.string.f_stopped_show : R.string.m_stopped_show;
            String showName = feed.getShow();
            String actionText = itemView.getContext().getString(stringId, showName);
            setShowActionText(actionText, feed);
        }

        private void setAchievementAction(UserFeed feed) {
            int stringId = feed.getGender() == Gender.FEMALE ? R.string.f_got_achievement : R.string.m_got_achievement;
            String achievementName = "${ACHIEVEMENT_NAME}"; //TODO: get correct achievement name
            String actionText = itemView.getContext().getString(stringId, achievementName);
            setAchievementActionText(actionText, feed);
        }

        private Spannable setShowActionText(String actionText, UserFeed feed) {
            return setActionTextLink(actionText, feed.getShow(), new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(itemView.getContext(), ShowActivity.class);
                    intent.putExtra(ShowActivity.SHOW_ID, feed.getShowId());
                    intent.putExtra(ShowActivity.SHOW_TITLE, feed.getShow());
                    itemView.getContext().startActivity(intent);
                }

                @Override
                public void updateDrawState(TextPaint textPaint) {
                    textPaint.setColor(itemView.getResources().getColor(R.color.dark_gray));
                }
            });
        }

        private Spannable setAchievementActionText(String actionText, UserFeed feed) {
            String achievementName = "${ACHIEVEMENT_NAME}"; //TODO: get correct achievement name
            return setActionTextLink(actionText, achievementName, new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Toast.makeText(itemView.getContext(), "There should be achievement screen", Toast.LENGTH_LONG).show();
                }

                @Override
                public void updateDrawState(TextPaint textPaint) {
                    textPaint.setColor(itemView.getResources().getColor(R.color.dark_gray));
                }
            });
        }

        private Spannable setActionTextLink(String actionText, String link, ClickableSpan clickableSpan) {
            int start = actionText.indexOf(link);
            int end = start + link.length();
            SpannableString spannable = new SpannableString(actionText);
            spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new TypefaceSpan(ROBOTO_REGULAR), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            action.setText(spannable, TextView.BufferType.SPANNABLE);
            return spannable;
        }
    }

    private static class FeedHeaderHolder extends RecyclerView.ViewHolder {

        private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd MMMM", Locale.getDefault());
        private static final Calendar TODAY = Calendar.getInstance();
        private static final Calendar YESTERDAY = Calendar.getInstance();

        private static Typeface typeface;

        static {
            YESTERDAY.add(Calendar.DAY_OF_YEAR, -1);
        }

        private final TextView header;

        public FeedHeaderHolder(View itemView) {
            super(itemView);
            header = (TextView) itemView.findViewById(R.id.feed_header);

            if (typeface == null) {
                typeface = Typeface.createFromAsset(itemView.getContext().getAssets(), "Roboto-Medium.ttf");
            }

            header.setTypeface(typeface);
        }

        private static boolean isSameDay(Calendar first, Calendar second) {
            return (first.get(Calendar.YEAR) == second.get(Calendar.YEAR)
                    && first.get(Calendar.DAY_OF_YEAR) == second.get(Calendar.DAY_OF_YEAR));
        }

        public void bind(long feedDate) {
            header.setText(getText(itemView.getContext(), feedDate));
        }

        private String getText(Context context, long feedDate) {
            Calendar feedDateCalendar = Calendar.getInstance();
            feedDateCalendar.setTimeInMillis(feedDate);
            if (isSameDay(TODAY, feedDateCalendar)) {
                return context.getString(R.string.today);
            } else if (isSameDay(YESTERDAY, feedDateCalendar)) {
                return context.getString(R.string.yesterday);
            } else {
                return FORMATTER.format(feedDate);
            }
        }
    }

    private static class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int HEADER_TYPE = 0;
        private static final int FEED_TYPE = 1;

        private final List<FeedDataHolder> feedData;

        public FeedAdapter(List<FeedDataHolder> feedData) {
            this.feedData = feedData;
        }

        @Override
        public int getItemViewType(int position) {
            if (feedData.get(position).isHeader()) {
                return HEADER_TYPE;
            } else {
                return FEED_TYPE;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == HEADER_TYPE) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_feed_header_view, parent, false);
                return new FeedHeaderHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_feed_view, parent, false);
                return new FeedHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            FeedDataHolder data = feedData.get(position);
            if (data.isHeader()) {
                ((FeedHeaderHolder) holder).bind(data.getTimestamp());
            } else {
                boolean isFirst = feedData.get(position - 1).isHeader();
                boolean isLast = feedData.size() - 1 == position || feedData.get(position + 1).isHeader();
                ((FeedHolder) holder).bind(data.getUserFeed(), data.getAvatarUrl(), isFirst, isLast);
            }
        }

        @Override
        public int getItemCount() {
            return feedData.size();
        }

        static class FeedOffsetDecorator extends OffsetDecorator {

            public FeedOffsetDecorator(int offset) {
                super(offset);
            }

            @Override
            protected boolean applyDecorator(View view, RecyclerView parent) {
                RecyclerView.Adapter adapter = parent.getAdapter();
                int position = parent.getChildAdapterPosition(view);
                return position != 0 && adapter.getItemViewType(position) == HEADER_TYPE;
            }
        }

        static class FeedShadowDecorator extends SimpleDrawableDecorator {

            public FeedShadowDecorator(Drawable shadowDrawable) {
                super(shadowDrawable);
            }

            @Override
            protected boolean applyDecorator(View view, RecyclerView parent) {
                RecyclerView.Adapter adapter = parent.getAdapter();
                int position = parent.getChildAdapterPosition(view);
                int size = adapter.getItemCount();
                return size - 1 != position && adapter.getItemViewType(position + 1) == HEADER_TYPE;
            }
        }
    }

    private static class FeedDataHolder {

        private static final long BAD_TIMESTAMP = -1;

        private final long timestamp;
        private final UserFeed userFeed;
        private final String avatarUrl;

        public FeedDataHolder(long timestamp) {
            this(timestamp, null, null);
        }

        public FeedDataHolder(UserFeed userFeed, String avatarUrl) {
            this(BAD_TIMESTAMP, userFeed, avatarUrl);
        }

        private FeedDataHolder(long timestamp, UserFeed userFeed, String avatarUrl) {
            this.timestamp = timestamp;
            this.userFeed = userFeed;
            this.avatarUrl = avatarUrl;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public UserFeed getUserFeed() {
            return userFeed;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public boolean isHeader() {
            return timestamp != BAD_TIMESTAMP;
        }
    }
}
