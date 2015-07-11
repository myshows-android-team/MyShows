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
import rx.android.schedulers.AndroidSchedulers;

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
        int p = (int) Math.min(100, ((double) show.getWatchedEpisodes()) / show.getTotalEpisodes() * 100);
        progress.setProgress(p);
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        // temporary workaround
        image.setImageResource(R.drawable.tmp_placeholder);
        subscription = show.requestImageUrl()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(url -> Glide.with(getContext())
                        .load(url)
                        .centerCrop()
                        .into(image));

    }
}
