package me.myshows.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import org.parceler.Parcels;

import me.myshows.android.R;
import me.myshows.android.api.MyShowsClient;
import me.myshows.android.api.impl.MyShowsClientImpl;
import me.myshows.android.model.Show;
import me.myshows.android.model.UserShow;
import me.myshows.android.model.WatchStatus;
import rx.Subscription;

/**
 * Created by warrior on 19.07.15.
 */
public class ShowActivity extends RxAppCompatActivity {

    public static final String SHOW_ID = "showId";
    public static final String USER_SHOW = "userShow";

    private static final String OPEN_P_TAG = "<p>";
    private static final String CLOSE_P_TAG = "</p>";

    private MyShowsClient client;

    private ImageView showImage;
    private CollapsingToolbarLayout collapsingToolbar;
    private TextView description;
    private TextView duration;
    private TextView status;
    private TextView rating;
    private RatingBar myRating;
    private FloatingActionButton fab;

    private boolean hasUserShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_activity);

        client = MyShowsClientImpl.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        showImage = (ImageView) findViewById(R.id.show_image);
        description = (TextView) findViewById(R.id.description);
        duration = (TextView) findViewById(R.id.duration);
        status = (TextView) findViewById(R.id.status);
        rating = (TextView) findViewById(R.id.rating);
        myRating = (RatingBar) findViewById(R.id.my_rating);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        Intent intent = getIntent();
        int showId;
        if (intent.hasExtra(USER_SHOW)) {
            hasUserShow = true;
            UserShow userShow = Parcels.unwrap(intent.getParcelableExtra(USER_SHOW));
            bind(userShow);
            showId = userShow.getShowId();
        } else {
            hasUserShow = false;
            showId = intent.getIntExtra(SHOW_ID, 0);
        }
        loadData(showId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadData(int showId) {
        client.showInformation(showId)
                .compose(bindToLifecycle())
                .subscribe(this::bind);
    }

    private void bind(UserShow show) {
        collapsingToolbar.setTitle(show.getTitle());
        myRating.setRating(show.getRating());
        status.setText(show.getShowStatus().getStringId());
        WatchStatus watchStatus = show.getWatchStatus();
        fab.setImageResource(watchStatus.getDrawableId());
        fab.setBackgroundTintList(getResources().getColorStateList(watchStatus.getColorId()));
        Glide.with(this)
                .load(show.getImage())
                .centerCrop()
                .into(showImage);
    }

    private void bind(Show show) {
        if (!hasUserShow) {
            collapsingToolbar.setTitle(show.getTitle());
            status.setText(show.getShowStatus().getStringId());
            Glide.with(this)
                    .load(show.getImage())
                    .centerCrop()
                    .into(showImage);
        }
        CharSequence descriptionText = processDescription(show.getDescription());
        if (descriptionText.length() == 0) {
            description.setVisibility(View.GONE);
        } else {
            description.setText(descriptionText);
        }
        duration.setText(getResources().getQuantityString(R.plurals.duration, show.getRuntime(), show.getRuntime()));
        rating.setText(getString(R.string.rating, show.getRating()));
    }

    private CharSequence processDescription(String description) {
        description = description.trim();
        if (description.startsWith(OPEN_P_TAG)) {
            description = description.substring(OPEN_P_TAG.length());
        }
        if (description.endsWith(CLOSE_P_TAG)) {
            description = description.substring(0, description.length() - CLOSE_P_TAG.length());
        }
        return Html.fromHtml(description);
    }
}
