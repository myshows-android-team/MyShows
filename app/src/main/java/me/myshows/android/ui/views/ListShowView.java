package me.myshows.android.ui.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import me.myshows.android.R;
import me.myshows.android.entities.UserShow;
import rx.Subscription;
import rx.android.view.ViewObservable;

/**
 * Created by warrior on 06.07.15.
 */
public class ListShowView extends CardView {

    private ImageView image;
    private TextView title;
    private ProgressBar progress;

    private Subscription subscription;

    public ListShowView(Context context) {
        super(context);
    }

    public ListShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListShowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        image = (ImageView) findViewById(R.id.image);
        title = (TextView) findViewById(R.id.title);
        progress = (ProgressBar) findViewById(R.id.progress);
    }

    public void bind(UserShow show) {
        title.setText(show.getTitle());
        progress.setMax(show.getTotalEpisodes());
        progress.setProgress(show.getWatchedEpisodes());
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        // temporary workaround
        image.setImageResource(R.drawable.tmp_placeholder);
        subscription = ViewObservable.bindView(this, show.requestImageUrl())
                .subscribe(url -> Glide.with(getContext())
                        .load(url)
                        .centerCrop()
                        .into(image));
    }
}
