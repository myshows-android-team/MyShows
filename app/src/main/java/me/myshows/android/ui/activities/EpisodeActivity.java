package me.myshows.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import me.myshows.android.R;
import me.myshows.android.api.MyShowsClient;
import me.myshows.android.api.impl.MyShowsClientImpl;
import me.myshows.android.model.CommentsInformation;
import me.myshows.android.model.Episode;
import me.myshows.android.model.RatingEpisode;

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

        client = MyShowsClientImpl.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupActionBar(toolbar);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        episodeImage = (ImageView) findViewById(R.id.episode_image);
        fab = (FloatingActionButton) findViewById(R.id.fab);

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
                .subscribe(this::bindEpisode);

        client.comments(episodeId)
                .compose(bindToLifecycle())
                .subscribe(this::bindComments);
    }

    private void bindEpisode(Episode episode) {
        Glide.with(this)
                .load(episode.getImage())
                .centerCrop()
                .into(episodeImage);

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

    private void bindRating(RatingEpisode ratingEpisode) {
        if (ratingEpisode != null && ratingEpisode.getRating() != 0) {
            rating.setText(getString(R.string.episode_rating, ratingEpisode.getRating()));
        } else {
            rating.setText(getString(R.string.episode_empty_rating));
        }
        //TODO: set correct rating
        myRating.setRating(0);
    }

    private void bindComments(CommentsInformation information) {
        int count = information.getCount();
        if (count == 0) {
            commentsInformation.setText(getString(R.string.empty_comments));
        } else {
            commentsInformation.setText(getResources().getQuantityString(R.plurals.read_comments, count, count));
        }
    }
}
