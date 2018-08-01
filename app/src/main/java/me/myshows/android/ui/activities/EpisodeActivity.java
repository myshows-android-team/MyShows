package me.myshows.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Collections;

import javax.inject.Inject;

import me.myshows.android.MyShowsApplication;
import me.myshows.android.R;
import me.myshows.android.api.MyShowsClient;
import me.myshows.android.model.Episode;
import me.myshows.android.model.EpisodeComments;
import me.myshows.android.model.EpisodeInformation;
import me.myshows.android.model.EpisodeRating;
import me.myshows.android.model.UserEpisode;
import me.myshows.android.model.UserShowEpisodes;
import me.myshows.android.model.WatchStatus;
import me.myshows.android.ui.comments.CommentsActivity;
import me.myshows.android.ui.comments.CommentsActivityKt;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Whiplash on 2/9/2016.
 */
public class EpisodeActivity extends HomeActivity {

    private static final String TAG = EpisodeActivity.class.getSimpleName();

    public static final String EPISODE_ID = "episodeId";
    public static final String EPISODE_TITLE = "episodeTitle";
    public static final String SHOW_ID = "showId";

    private static final int NOT_WATCHED_EPISODE_RATING = -1;

    private ImageView episodeImage;
    private CollapsingToolbarLayout collapsingToolbar;
    private FloatingActionButton fab;

    private View episodeInformationLayout;
    private TextView watched;
    private TextView airDate;
    private TextView rating;
    private RatingBar myRating;
    private View commentsLayout;
    private TextView commentsInformation;

    @Inject
    MyShowsClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.episode_activity);

        MyShowsApplication.getComponent(this).inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupActionBar(toolbar);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        episodeImage = (ImageView) findViewById(R.id.episode_image);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        hideFab();

        episodeInformationLayout = findViewById(R.id.episode_information);
        watched = (TextView) findViewById(R.id.watched);
        airDate = (TextView) findViewById(R.id.air_date);
        rating = (TextView) findViewById(R.id.rating);
        myRating = (RatingBar) findViewById(R.id.my_rating);
        commentsLayout = findViewById(R.id.comments_layout);
        commentsInformation = (TextView) findViewById(R.id.comments_information);

        Pair<Integer, Integer> pair = extractAndBindEpisodeData(getIntent());
        loadData(pair.first, pair.second);
    }

    private void hideFab() {
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        p.setBehavior(null);
        p.setAnchorId(View.NO_ID);
        fab.setLayoutParams(p);
        fab.setVisibility(View.GONE);
    }

    private void showFab() {
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        p.setBehavior(new FloatingActionButton.Behavior());
        p.setAnchorId(R.id.appbar);
        fab.setLayoutParams(p);
    }

    private Pair<Integer, Integer> extractAndBindEpisodeData(@NonNull Intent intent) {
        int episodeId = intent.getIntExtra(EPISODE_ID, 0);
        String episodeTitle = intent.getStringExtra(EPISODE_TITLE);
        int showId = intent.getIntExtra(SHOW_ID, 0);
        collapsingToolbar.setTitle(episodeTitle);
        return Pair.create(episodeId, showId);
    }

    private void loadData(int episodeId, int showId) {
        Observable<Integer> userShowEpisodesObservable = client.profileEpisodesOfShow(showId)
                .defaultIfEmpty(new UserShowEpisodes(showId, Collections.emptyList()))
                .observeOn(Schedulers.computation())
                .map(userShowEpisodes -> extractMyRating(userShowEpisodes, episodeId))
                .observeOn(AndroidSchedulers.mainThread());

        Observable.combineLatest(client.episodeInformation(episodeId), client.comments(episodeId),
                userShowEpisodesObservable, ActivityInformation::new)
                .compose(bindToLifecycle())
                .subscribe(this::bind);
    }

    private int extractMyRating(@NonNull UserShowEpisodes userShowEpisodes, int episodeId) {
        for (UserEpisode userEpisode : userShowEpisodes.getEpisodes()) {
            if (userEpisode.getId() == episodeId) {
                return userEpisode.getRating();
            }
        }
        return NOT_WATCHED_EPISODE_RATING;
    }

    private void bind(@NonNull ActivityInformation information) {
        bindEpisode(information.episode);
        bindComments(information.comments);

        myRating.setRating(information.myRating);
        bindWatchStatus(information.myRating != NOT_WATCHED_EPISODE_RATING);

        episodeInformationLayout.setVisibility(View.VISIBLE);
        showFab();
    }

    private void bindEpisode(@NonNull EpisodeInformation episode) {
        bindEpisodePreviewImage(episode.getImage());
        bindWatched(episode.getTotalWatched());
        airDate.setText(episode.getAirDate());
        bindRating(episode.getRating());

        commentsLayout.setOnClickListener(v -> startCommentActivity(episode));
    }

    private void bindEpisodePreviewImage(String url) {
        Glide.with(this)
                .load(url)
                .apply(RequestOptions.centerCropTransform())
                .into(episodeImage);
    }

    private void bindWatched(int watched) {
        if (watched == 0) {
            this.watched.setText(getString(R.string.no_one_watched));
        } else {
            this.watched.setText(getResources().getQuantityString(R.plurals.watched, watched, watched));
        }
    }

    private void bindRating(@NonNull EpisodeRating episodeRating) {
        if (episodeRating.getVotes() != 0) {
            rating.setText(getString(R.string.episode_rating, episodeRating.getRating()));
        } else {
            rating.setText(getString(R.string.episode_empty_rating));
        }
    }

    private void bindComments(@NonNull EpisodeComments information) {
        int count = information.getCount();
        if (count == 0) {
            commentsInformation.setText(getString(R.string.empty_comments));
        } else {
            commentsInformation.setText(getResources().getQuantityString(R.plurals.read_comments, count, count));
        }
    }

    private void startCommentActivity(Episode episode) {
        Intent intent = new Intent(this, CommentsActivity.class);
        intent.putExtra(CommentsActivityKt.EPISODE_ID, episode.getId());
        intent.putExtra(CommentsActivityKt.EPISODE_TITLE, episode.getTitle());
        startActivity(intent);
    }

    private void bindWatchStatus(boolean isWatched) {
        WatchStatus watchStatus = isWatched ? WatchStatus.WATCHING : WatchStatus.NOT_WATCHING;
        fab.setImageResource(watchStatus.getDrawableId());
        fab.setBackgroundTintList(ContextCompat.getColorStateList(this, watchStatus.getColorId()));
    }

    private static class ActivityInformation {

        public final EpisodeInformation episode;
        public final EpisodeComments comments;
        public final int myRating;

        public ActivityInformation(@NonNull EpisodeInformation episode,
                                   @NonNull EpisodeComments comments, int myRating) {
            this.episode = episode;
            this.comments = comments;
            this.myRating = myRating;
        }
    }
}
