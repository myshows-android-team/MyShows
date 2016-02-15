package me.myshows.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import me.myshows.android.MyShowsApplication;
import me.myshows.android.R;
import me.myshows.android.api.MyShowsClient;
import me.myshows.android.model.EpisodeComments;
import me.myshows.android.model.EpisodeInformation;
import me.myshows.android.model.EpisodeRating;
import me.myshows.android.model.UserEpisode;
import me.myshows.android.model.UserShowEpisodes;
import rx.Observable;

/**
 * Created by Whiplash on 2/9/2016.
 */
public class EpisodeActivity extends HomeActivity {

    private static final String TAG = EpisodeActivity.class.getSimpleName();

    public static final String EPISODE_ID = "episodeId";
    public static final String EPISODE_TITLE = "episodeTitle";

    private MyShowsClient client;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.episode_activity);

        client = MyShowsApplication.getMyShowsClient(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupActionBar(toolbar);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        episodeImage = (ImageView) findViewById(R.id.episode_image);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        episodeInformationLayout = findViewById(R.id.episode_information);
        watched = (TextView) findViewById(R.id.watched);
        airDate = (TextView) findViewById(R.id.air_date);
        rating = (TextView) findViewById(R.id.rating);
        myRating = (RatingBar) findViewById(R.id.my_rating);
        commentsLayout = findViewById(R.id.comments_layout);
        commentsInformation = (TextView) findViewById(R.id.comments_information);

        int episodeId = extractAndBindEpisodeData(getIntent());
        loadData(episodeId);
    }

    private int extractAndBindEpisodeData(Intent intent) {
        int episodeId = intent.getIntExtra(EPISODE_ID, 0);
        String episodeTitle = intent.getStringExtra(EPISODE_TITLE);
        collapsingToolbar.setTitle(episodeTitle);
        return episodeId;
    }

    private void loadData(int episodeId) {
        client.episodeInformation(episodeId)
                .compose(bindToLifecycle())
                .subscribe(episode -> {
                    bindEpisodePreviewImage(episode.getImage());
                    Observable.combineLatest(client.comments(episodeId),
                            client.profileEpisodesOfShow(episode.getShodId()), Pair::create)
                            .compose(bindToLifecycle())
                            .subscribe(pair -> bind(episode, pair));
                });
    }

    private void bindEpisodePreviewImage(String url) {
        Glide.with(this)
                .load(url)
                .centerCrop()
                .into(episodeImage);
    }

    private void bind(@NonNull EpisodeInformation episode,
                      @NonNull Pair<EpisodeComments, UserShowEpisodes> pair) {
        bindEpisode(episode);
        bindComments(pair.first);
        bindMyRating(episode.getId(), pair.second);
        episodeInformationLayout.setVisibility(View.VISIBLE);
    }

    private void bindEpisode(@NonNull EpisodeInformation episode) {
        bindWatched(episode.getTotalWatched());
        airDate.setText(episode.getAirDate());
        bindRating(episode.getRating());
    }

    private void bindWatched(int watched) {
        if (watched == 0) {
            this.watched.setText(getString(R.string.no_one_watched));
        } else {
            this.watched.setText(getResources().getQuantityString(R.plurals.watched, watched, watched));
        }
    }

    private void bindRating(@Nullable EpisodeRating episodeRating) {
        if (episodeRating != null && episodeRating.getRating() != 0) {
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

    private void bindMyRating(int episodeId, @NonNull UserShowEpisodes userShowEpisodes) {
        for (UserEpisode userEpisode : userShowEpisodes.getEpisodes()) {
            if (userEpisode.getId() == episodeId) {
                myRating.setRating(userEpisode.getRating());
                break;
            }
        }
    }
}
