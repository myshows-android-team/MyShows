package me.myshows.android.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
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
public class ListShowView extends FrameLayout {

    private ImageView image;
    private TextView title;
    private ProgressBar progress;
    private View shadow;

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
        shadow = findViewById(R.id.shadow);
    }

    public void bind(UserShow show, int position) {
        title.setText(show.getTitle());
        progress.setMax(show.getTotalEpisodes());
        progress.setProgress(show.getWatchedEpisodes());
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        shadow.setVisibility(position == 0 ? GONE : VISIBLE);
        // temporary workaround
        image.setImageResource(R.drawable.tmp_placeholder);
        subscription = ViewObservable.bindView(this, show.requestImageUrl())
                .subscribe(url -> Glide.with(getContext())
                        .load(url)
                        .centerCrop()
                        .into(image));
    }
}
