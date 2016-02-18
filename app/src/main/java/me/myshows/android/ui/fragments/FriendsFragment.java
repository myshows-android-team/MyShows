package me.myshows.android.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.TypefaceSpan;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.myshows.android.MyShowsApplication;
import me.myshows.android.R;
import me.myshows.android.api.MyShowsClient;
import me.myshows.android.model.Action;
import me.myshows.android.model.Feed;
import me.myshows.android.model.Gender;
import me.myshows.android.model.User;
import me.myshows.android.model.UserFeed;
import me.myshows.android.model.UserPreview;
import me.myshows.android.ui.activities.EpisodeActivity;
import me.myshows.android.ui.activities.ShowActivity;
import me.myshows.android.ui.decorators.OffsetDecorator;
import me.myshows.android.ui.decorators.SimpleDrawableDecorator;
import rx.Observable;

/**
 * Created by warrior on 19.07.15.
 */
public class FriendsFragment extends BaseFragment {

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
                ContextCompat.getDrawable(getActivity(), R.drawable.show_screen_shadow)));

        loadData();

        return view;
    }

    private void loadData() {
        MyShowsClient client = MyShowsApplication.getMyShowsClient(getActivity());
        Observable<Map<String, String>> friendsAvatarObservable = client.profile()
                .map(FriendsFragment::extractAvatarUrls);
        Observable.combineLatest(client.friendsNews(), friendsAvatarObservable, FriendsFragment::makeAdapter)
                .compose(bindToLifecycle())
                .subscribe(this::setAdapter);
    }

    private void setAdapter(FeedAdapter adapter) {
        recyclerView.setAdapter(adapter);
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

    private static FeedAdapter makeAdapter(List<Feed> feeds, Map<String, String> friendsAvatar) {
        SparseArray<Long> headersData = new SparseArray<>(feeds.size());
        SparseArray<FeedData> feedsData = new SparseArray<>();
        int i = 0;
        for (Feed feed : feeds) {
            headersData.append(i++, feed.getDate());
            for (UserFeed userFeed : feed.getFeeds()) {
                if (userFeed.getAction() != null) {
                    feedsData.append(i++, new FeedData(userFeed, friendsAvatar.get(userFeed.getLogin())));
                }
            }
        }
        return new FeedAdapter(headersData, feedsData);
    }

    private static class FeedHolder extends RecyclerView.ViewHolder {

        private static final String ROBOTO_REGULAR = "sans-serif";

        private final Context context;

        private final ImageView avatar;
        private final TextView name;
        private final TextView action;
        private final ImageView actionIcon;

        private final View topLine;
        private final View bottomLine;
        private final View divider;

        public FeedHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            avatar = (ImageView) itemView.findViewById(R.id.friend_avatar);
            name = (TextView) itemView.findViewById(R.id.friend_name);
            action = (TextView) itemView.findViewById(R.id.friend_action);
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
            setActionIconBackground(ContextCompat.getColor(context, feedAction.getColor()));

            switch (feedAction) {
                case WATCH:
                    setWatchAction(feed);
                    break;
                case NEW:
                case WATCH_LATER:
                case RATING:
                case STOP_WATCH:
                case ACHIEVEMENT:
            }
        }

        private void setActionIconBackground(int color) {
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.OVAL);
            shape.setColor(color);
            actionIcon.setBackground(shape);
        }

        private void setAvatar(String avatarUrl) {
            Glide.with(context)
                    .load(avatarUrl)
                    .into(avatar);
        }

        private void setWatchAction(UserFeed feed) {
            Spannable spannable = feed.getEpisodes() == 1 ?
                    getOneEpisodeSpannable(feed) : getManyEpisodeSpannable(feed);
            action.setText(spannable, TextView.BufferType.SPANNABLE);
        }

        private Spannable getOneEpisodeSpannable(UserFeed feed) {
            int stringId = feed.getGender() == Gender.FEMALE ? R.string.f_watch_one_series : R.string.m_watch_one_series;
            String actionText = context.getString(stringId, feed.getEpisode(), feed.getShow());
            SpannableString spannable = new SpannableString(actionText);
            highlightShow(spannable, actionText, feed);
            highlightEpisodeName(spannable, actionText, feed);
            return spannable;
        }

        private Spannable getManyEpisodeSpannable(UserFeed feed) {
            int pluralsId = feed.getGender() == Gender.FEMALE ? R.plurals.f_watch_series : R.plurals.m_watch_series;
            String actionText = context.getResources().getQuantityString(pluralsId, feed.getEpisodes(), feed.getEpisodes(), feed.getShow());
            SpannableString spannable = new SpannableString(actionText);
            highlightShow(spannable, actionText, feed);
            return spannable;
        }

        private void highlightEpisodeName(Spannable spannable, String actionText, UserFeed feed) {
            int start = actionText.indexOf(feed.getEpisode());
            int end = start + feed.getEpisode().length();
            spannable.setSpan(new EpisodeActionSpannable(feed), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new TypefaceSpan(ROBOTO_REGULAR), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        private void highlightShow(Spannable spannable, String actionText, UserFeed feed) {
            int start = actionText.indexOf(feed.getShow());
            int end = start + feed.getShow().length();
            spannable.setSpan(new ShowActionSpannable(feed), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new TypefaceSpan(ROBOTO_REGULAR), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        private class EpisodeActionSpannable extends ClickableSpan {

            private final UserFeed feed;

            private EpisodeActionSpannable(UserFeed feed) {
                this.feed = feed;
            }

            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(context, EpisodeActivity.class);
                intent.putExtra(EpisodeActivity.EPISODE_ID, feed.getEpisodeId());
                intent.putExtra(EpisodeActivity.EPISODE_TITLE, feed.getTitle());
                intent.putExtra(EpisodeActivity.SHOW_ID, feed.getShowId());
                context.startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setColor(ContextCompat.getColor(context, R.color.dark_gray));
            }
        }

        private class ShowActionSpannable extends ClickableSpan {

            private final UserFeed feed;

            private ShowActionSpannable(UserFeed feed) {
                this.feed = feed;
            }

            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(context, ShowActivity.class);
                intent.putExtra(ShowActivity.SHOW_ID, feed.getShowId());
                intent.putExtra(ShowActivity.SHOW_TITLE, feed.getShow());
                context.startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setColor(ContextCompat.getColor(context, R.color.primary));
            }
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

        private static boolean isSameDay(Calendar first, Calendar second) {
            return (first.get(Calendar.YEAR) == second.get(Calendar.YEAR)
                    && first.get(Calendar.DAY_OF_YEAR) == second.get(Calendar.DAY_OF_YEAR));
        }
    }

    private static class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int HEADER_TYPE = 0;
        private static final int FEED_TYPE = 1;

        private final SparseArray<Long> headersData;
        private final SparseArray<FeedData> feedsData;

        public FeedAdapter(SparseArray<Long> headersData, SparseArray<FeedData> feedsData) {
            this.headersData = headersData;
            this.feedsData = feedsData;
        }

        @Override
        public int getItemViewType(int position) {
            if (feedsData.get(position) == null) {
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
            FeedData feedData = feedsData.get(position);
            if (feedData == null) {
                ((FeedHeaderHolder) holder).bind(headersData.get(position));
            } else {
                ((FeedHolder) holder).bind(feedData.getUserFeed(), feedData.getAvatarUrl(),
                        feedsData.get(position - 1) == null, feedsData.get(position + 1) == null);
            }
        }

        @Override
        public int getItemCount() {
            return headersData.size() + feedsData.size();
        }

        static class FeedOffsetDecorator extends OffsetDecorator {

            public FeedOffsetDecorator(int offset) {
                super(offset, TOP_OFFSET);
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
                super(shadowDrawable, Border.BOTTOM);
            }

            @Override
            protected boolean applyDecorator(View view, RecyclerView parent) {
                RecyclerView.Adapter adapter = parent.getAdapter();
                int position = parent.getChildAdapterPosition(view);
                return adapter.getItemViewType(position + 1) == HEADER_TYPE;
            }
        }
    }

    private static class FeedData {

        private final UserFeed userFeed;
        private final String avatarUrl;

        public FeedData(UserFeed userFeed, String avatarUrl) {
            this.userFeed = userFeed;
            this.avatarUrl = avatarUrl;
        }

        public UserFeed getUserFeed() {
            return userFeed;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }
    }
}
