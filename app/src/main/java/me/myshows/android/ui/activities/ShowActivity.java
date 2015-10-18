package me.myshows.android.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import org.parceler.Parcels;

import java.util.Collections;

import me.myshows.android.R;
import me.myshows.android.api.MyShowsClient;
import me.myshows.android.api.impl.MyShowsClientImpl;
import me.myshows.android.model.Show;
import me.myshows.android.model.UserShow;
import me.myshows.android.model.UserShowEpisodes;
import me.myshows.android.model.WatchStatus;
import rx.Observable;

/**
 * Created by warrior on 19.07.15.
 */
public class ShowActivity extends HomeActivity {

    private static final String TAG = ShowActivity.class.getSimpleName();

    public static final String SHOW_ID = "showId";
    public static final String SHOW_TITLE = "showTitle";
    public static final String USER_SHOW = "userShow";

    private MyShowsClient client;

    private ImageView showImage;
    private CollapsingToolbarLayout collapsingToolbar;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;

    private RecyclerViewExpandableItemManager expandableItemManager;
    private RecyclerView.Adapter wrappedAdapter;
    private ShowAdapter adapter;

    private UserShow userShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_activity);

        client = MyShowsClientImpl.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupActionBar(toolbar);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        showImage = (ImageView) findViewById(R.id.show_image);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        setupRecyclerView();

        int showId = extractAndBindShowData(getIntent());
        loadData(showId);
    }

    private int extractAndBindShowData(Intent intent) {
        int showId;
        if (intent.hasExtra(USER_SHOW)) {
            userShow = Parcels.unwrap(intent.getParcelableExtra(USER_SHOW));
            bind(userShow);
            showId = userShow.getShowId();
        } else {
            showId = intent.getIntExtra(SHOW_ID, 0);
            String showTitle = intent.getStringExtra(SHOW_TITLE);
            collapsingToolbar.setTitle(showTitle);
        }
        return showId;
    }

    @Override
    public void onDestroy() {
        if (expandableItemManager != null) {
            expandableItemManager.release();
        }
        if (wrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(wrappedAdapter);
        }
        super.onDestroy();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(false);
        recyclerView.addItemDecoration(new ShowAdapter.SeasonOffsetDecorator(getResources().getDimensionPixelSize(R.dimen.default_padding)));
        recyclerView.addItemDecoration(new ShowAdapter.ShadowDecorator(getDrawableById(R.drawable.show_screen_shadow)));
        SimpleItemAnimator animator = new RefactoredDefaultItemAnimator();
        animator.setSupportsChangeAnimations(false);
        recyclerView.setItemAnimator(animator);
    }

    @SuppressLint("NewApi")
    private Drawable getDrawableById(@DrawableRes int id) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return getResources().getDrawable(id);
        }
        return getDrawable(id);
    }

    private void loadData(int showId) {
        Observable<Show> showObservable = client.showInformation(showId)
                .doOnNext(this::bind);
        Observable<UserShowEpisodes> userShowEpisodesObservable = client.profileEpisodesOfShow(showId)
                .defaultIfEmpty(new UserShowEpisodes(showId, Collections.emptyList()));
        Observable.combineLatest(showObservable, userShowEpisodesObservable, ShowAdapter::create)
                .compose(bindToLifecycle())
                .subscribe(this::setAdapter);
        if (userShow == null) {
            loadUserShow(showId);
        }
    }

    private void loadUserShow(int showId) {
        client.profileShows()
                .compose(bindToLifecycle())
                .flatMap(Observable::from)
                .filter(show -> show.getShowId() == showId)
                .doOnNext(this::bind)
                .subscribe(userShow -> {
                    this.userShow = userShow;
                    if (adapter != null) {
                        adapter.setUserShow(userShow);
                        adapter.notifyItemChanged(0);
                    }
                });
    }

    @SuppressLint("NewApi")
    private void bind(UserShow show) {
        collapsingToolbar.setTitle(show.getTitle());
        WatchStatus watchStatus = show.getWatchStatus();
        fab.setImageResource(watchStatus.getDrawableId());
        fab.setBackgroundTintList(getResources().getColorStateList(watchStatus.getColorId()));
    }

    private void bind(Show show) {
        collapsingToolbar.setTitle(show.getTitle());
        Glide.with(this)
                .load(show)
                .centerCrop()
                .into(showImage);
    }

    private void setAdapter(ShowAdapter adapter) {
        this.adapter = adapter;
        if (userShow != null) {
            adapter.setUserShow(userShow);
        }
        expandableItemManager = new RecyclerViewExpandableItemManager(null);
        wrappedAdapter = expandableItemManager.createWrappedAdapter(adapter);
        recyclerView.setAdapter(wrappedAdapter);
        expandableItemManager.attachRecyclerView(recyclerView);
    }
}
