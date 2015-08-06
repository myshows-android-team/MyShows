package me.myshows.android.ui.activities;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.parceler.Parcels;

import me.myshows.android.R;
import me.myshows.android.api.MyShowsClient;
import me.myshows.android.api.impl.MyShowsClientImpl;
import me.myshows.android.model.Show;
import me.myshows.android.model.UserShow;
import rx.Subscription;

/**
 * Created by warrior on 19.07.15.
 */
public class ShowActivity extends AppCompatActivity {

    public static final String SHOW = "show";

    private static final String CANCELLED = "Canceled/Ended";
    private static final String ONGOING = "Returning Series";
    private static final String ON_BREAK = "On Hiatus";
    private static final String NEW = "New Series";
    private static final String FINAL_SEASON = "Final Season";
    private static final String IN_DEVELOPMENT = "In Development";
    private static final String TBD = "TBD/On The Bubble";
    private static final String PILOT = "Pilot Rejected";

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

    private Subscription subscription;

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

        UserShow show = Parcels.unwrap(getIntent().getParcelableExtra(SHOW));
        loadData(show);
    }

    @Override
    protected void onDestroy() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        super.onDestroy();
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

    private void loadData(UserShow userShow) {
        bind(userShow);
        subscription = client.showInformation(userShow.getShowId())
                .subscribe(this::bind);
    }

    private void bind(UserShow show) {
        collapsingToolbar.setTitle(show.getTitle());
        myRating.setRating(show.getRating());
        status.setText(statusStringId(show.getShowStatus()));
        Glide.with(this)
                .load(show.getImage())
                .centerCrop()
                .into(showImage);
    }

    private void bind(Show show) {
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

    @StringRes
    private int statusStringId(String status) {
        switch (status) {
            case CANCELLED:
                return R.string.cancelled;
            case ONGOING:
                return R.string.ongoing;
            case ON_BREAK:
                return R.string.on_break;
            case NEW:
                return R.string.new_show;
            case FINAL_SEASON:
                return R.string.final_season;
            case IN_DEVELOPMENT:
                return R.string.in_development;
            case TBD:
                return R.string.tbd;
            case PILOT:
                return R.string.pilot;
            default:
                return R.string.unknown_status;
        }
    }
}
