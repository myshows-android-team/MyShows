package me.myshows.android.ui.activities;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.parceler.Parcels;

import me.myshows.android.R;
import me.myshows.android.entities.UserShow;
import rx.android.view.ViewObservable;

/**
 * Created by warrior on 19.07.15.
 */
public class ShowActivity extends AppCompatActivity {

    public static final String SHOW = "show";

    private ImageView showImage;
    private CollapsingToolbarLayout collapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        showImage = (ImageView) findViewById(R.id.show_image);

        UserShow show = Parcels.unwrap(getIntent().getParcelableExtra(SHOW));
        loadData(show);
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

    private void loadData(UserShow show) {
        if (show != null) {
            collapsingToolbar.setTitle(show.getTitle());
            ViewObservable.bindView(showImage, show.requestImageUrl())
                    .subscribe(url -> Glide.with(this)
                            .load(url)
                            .centerCrop()
                            .into(showImage));
        }
    }
}
