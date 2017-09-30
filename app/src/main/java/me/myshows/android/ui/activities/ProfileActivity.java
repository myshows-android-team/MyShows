package me.myshows.android.ui.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import org.parceler.Parcels;

import java.util.List;

import me.myshows.android.MyShowsApplication;
import me.myshows.android.R;
import me.myshows.android.api.MyShowsClient;
import me.myshows.android.model.User;
import me.myshows.android.model.UserPreview;
import me.myshows.android.model.UserShow;
import me.myshows.android.ui.fragments.AvatarProfileFragment;
import me.myshows.android.ui.fragments.ProfileHeaderFragment;
import me.myshows.android.ui.fragments.StatisticProfileFragment;
import me.relex.circleindicator.CircleIndicator;
import rx.Observable;

/**
 * Created by warrior on 20.09.15.
 */
public class ProfileActivity extends HomeActivity {

    private static final float SEMI_TRANSPARENT_ALPHA = 127.5F;

    private static final int SPAN_COUNT = 3;

    private ViewPager viewPager;
    private CircleIndicator indicator;
    private GradientDrawable gradient;
    private RecyclerView recyclerView;

    private View friendLayout;
    private RecyclerView friendList;
    private View friendHeader;
    private View friendShadow;

    private HeaderPageAdapter headerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupActionBar(toolbar);

        View backgroundGradient = findViewById(R.id.background_gradient);
        gradient = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.TRANSPARENT, Color.argb((int) SEMI_TRANSPARENT_ALPHA, 0, 0, 0)});
        backgroundGradient.setBackground(gradient);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
        setupViewPager();

        ImageView headerImage = (ImageView) findViewById(R.id.header_image);
        setHeaderBackground(headerImage);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        setupRecyclerView();

        friendLayout = LayoutInflater.from(this)
                .inflate(R.layout.friends_layout, recyclerView, false);
        friendList = (RecyclerView) friendLayout.findViewById(R.id.friend_list);
        friendHeader = friendLayout.findViewById(R.id.friend_header);
        friendShadow = friendLayout.findViewById(R.id.friend_shadow);
        friendList.setNestedScrollingEnabled(false);
        friendList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        loadData();
    }

    private void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, SPAN_COUNT);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? SPAN_COUNT : 1;
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new ProfileOffsetDecorator(SPAN_COUNT, getResources().getDimensionPixelSize(R.dimen.default_padding)));
    }

    @Override
    protected void onPostSetupActionBar(@NonNull ActionBar actionBar) {
        actionBar.setTitle(R.string.profile);
    }

    private void setupViewPager() {
        headerAdapter = new HeaderPageAdapter(getFragmentManager());
        viewPager.setAdapter(headerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int alpha = (int) (SEMI_TRANSPARENT_ALPHA * (1 + position + positionOffset));
                int bottomColor = Color.argb(alpha, 0, 0, 0);
                gradient.setColors(new int[]{Color.TRANSPARENT, bottomColor});
            }
        });
        indicator.setViewPager(viewPager);
    }

    private void setHeaderBackground(ImageView headerImage) {
        // TODO: we need use background which user will select
        Glide.with(this)
                .load("http://media.myshows.me/shows/normal/d/da/da3e7aee7483129e27208bd8e36c0b64.jpg")
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(headerImage);
    }

    private void loadData() {
        MyShowsClient client = MyShowsApplication.getMyShowsClient(this);
        Observable<User> profile = client.profile()
                .doOnNext(this::onUserLoaded);
        Observable.combineLatest(profile, client.profileShows(), (user, shows) -> ProfileAdapter.create(friendLayout, shows))
                .compose(bindToLifecycle())
                .subscribe(recyclerView::setAdapter);
    }

    private void onUserLoaded(@NonNull User user) {
        headerAdapter.setUser(user);
        List<UserPreview> friends = user.getFriends();
        boolean hasFriends = friends != null && !friends.isEmpty();
        int visibility = hasFriends ? View.VISIBLE : View.GONE;
        friendList.setVisibility(visibility);
        friendHeader.setVisibility(visibility);
        friendShadow.setVisibility(visibility);
        if (hasFriends) {
            friendList.setAdapter(new FriendAdapter(friends));
        }
    }

    private static class HeaderPageAdapter extends FragmentStatePagerAdapter {

        private User user;

        public HeaderPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return AvatarProfileFragment.newInstance(user);
            } else {
                return StatisticProfileFragment.newInstance(user);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public int getItemPosition(Object object) {
            ProfileHeaderFragment fragment = (ProfileHeaderFragment) object;
            if (user != null) {
                fragment.setUser(user);
            }
            return super.getItemPosition(object);
        }

        public void setUser(User user) {
            this.user = user;
            notifyDataSetChanged();
        }
    }

    private static class ProfileOffsetDecorator extends RecyclerView.ItemDecoration {

        private final int spanCount;
        private final int offset;

        public ProfileOffsetDecorator(int spanCount, int offset) {
            this.spanCount = spanCount;
            this.offset = offset;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position != 0) {
                int gridPosition = position - 1;

                int leftOffset = offset / 2;
                int rightOffset = offset / 2;
                int topOffset = gridPosition < spanCount ? offset : 0;

                outRect.set(leftOffset, topOffset, rightOffset, offset);
            }
        }
    }

    private static class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FRIEND_LAYOUT_VIEW_TYPE = 0;
        private static final int SERIES_VIEW_TYPE = 1;

        private final View friendLayout;

        private final List<UserShow> shows;

        private ProfileAdapter(@NonNull View friendLayout, @NonNull List<UserShow> shows) {
            this.friendLayout = friendLayout;
            this.shows = shows;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            if (viewType == FRIEND_LAYOUT_VIEW_TYPE) {
                return new FriendListViewHolder(friendLayout);
            }
            View view = inflater.inflate(R.layout.grid_show_view, parent, false);
            return new ShowViewHolder(view);

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ShowViewHolder) {
                ((ShowViewHolder) holder).bind(shows.get(position - 1));
            }
        }

        @Override
        public int getItemCount() {
            return shows.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? FRIEND_LAYOUT_VIEW_TYPE : SERIES_VIEW_TYPE;
        }

        public static ProfileAdapter create(@NonNull View friendLayout, @NonNull List<UserShow> shows) {
            return new ProfileAdapter(friendLayout, shows);
        }
    }

    private static class FriendListViewHolder extends RecyclerView.ViewHolder {

        public FriendListViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class ShowViewHolder extends RecyclerView.ViewHolder {

        private final ImageView image;
        private final TextView title;
        private final ProgressBar progress;

        public ShowViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            progress = (ProgressBar) itemView.findViewById(R.id.progress);
        }

        public void bind(@NonNull UserShow show) {
            title.setText(show.getTitle());
            progress.setMax(show.getTotalEpisodes());
            progress.setProgress(show.getWatchedEpisodes());
            Glide.with(itemView.getContext())
                    .load(show.getImage())
                    .into(image);

            itemView.setOnClickListener(v -> {
                Context context = v.getContext();
                Intent intent = new Intent(context, ShowActivity.class);
                intent.putExtra(ShowActivity.USER_SHOW, Parcels.wrap(show));
                context.startActivity(intent);
            });
        }
    }

    private static class FriendAdapter extends RecyclerView.Adapter<FriendViewHolder> {

        private final List<UserPreview> friends;

        private FriendAdapter(List<UserPreview> friends) {
            this.friends = friends;
        }

        @Override
        public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_friend_view, parent, false);
            return new FriendViewHolder(view);
        }

        @Override
        public void onBindViewHolder(FriendViewHolder holder, int position) {
            holder.bind(friends.get(position));
        }

        @Override
        public int getItemCount() {
            return friends.size();
        }
    }

    private static class FriendViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final ImageView avatar;

        public FriendViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
        }

        public void bind(UserPreview friend) {
            name.setText(friend.getLogin());
            Glide.with(itemView.getContext())
                    .load(friend.getAvatarUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(RequestOptions.centerCropTransform())
                    .into(avatar);
        }
    }
}
