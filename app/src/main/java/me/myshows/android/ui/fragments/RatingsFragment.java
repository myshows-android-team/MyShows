package me.myshows.android.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.trello.rxlifecycle.components.support.RxFragment;

import org.parceler.Parcels;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.myshows.android.R;
import me.myshows.android.api.MyShowsClient;
import me.myshows.android.api.impl.MyShowsClientImpl;
import me.myshows.android.model.RatingShow;
import me.myshows.android.model.UserShow;
import me.myshows.android.model.WatchStatus;
import me.myshows.android.ui.activities.ShowActivity;

/**
 * Created by warrior on 19.07.15.
 */
public class RatingsFragment extends RxFragment {

    private RecyclerView recyclerView;

    private List<RatingShow> ratingShows;
    private Map<Integer, UserShow> userShows;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ratings_fragment, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecorator(getActivity()));
        recyclerView.setHasFixedSize(true);

        loadData();

        return view;
    }

    private void loadData() {
        MyShowsClient client = MyShowsClientImpl.getInstance();
        client.ratingShows()
                .compose(bindToLifecycle())
                .subscribe(shows -> {
                            ratingShows = shows;
                            if (userShows != null) {
                                setAdapter();
                            }
                        }
                );
        client.profileShows()
                .compose(bindToLifecycle())
                .map(shows -> {
                    Map<Integer, UserShow> userShows = new HashMap<>();
                    for (UserShow show : shows) {
                        userShows.put(show.getShowId(), show);
                    }
                    return userShows;
                })
                .subscribe(shows -> {
                    userShows = shows;
                    if (ratingShows != null) {
                        setAdapter();
                    }
                });
    }

    private void setAdapter() {
        RatingShowAdapter adapter = new RatingShowAdapter(ratingShows, userShows);
        recyclerView.setAdapter(adapter);
    }

    private static class RatingShowAdapter extends RecyclerView.Adapter<RatingShowHolder> {

        private final List<RatingShow> ratingShows;
        private final Map<Integer, UserShow> userShows;

        public RatingShowAdapter(List<RatingShow> ratingShows, Map<Integer, UserShow> userShows) {
            this.ratingShows = ratingShows;
            this.userShows = userShows;
        }

        @Override
        public RatingShowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_rating_show_view, parent, false);
            return new RatingShowHolder(view);
        }

        @Override
        public void onBindViewHolder(RatingShowHolder holder, int position) {
            RatingShow ratingShow = ratingShows.get(position);
            UserShow userShow = userShows.get(ratingShow.getId());
            holder.bind(ratingShow, userShow);
        }

        @Override
        public int getItemCount() {
            return ratingShows.size();
        }
    }

    private static class RatingShowHolder extends RecyclerView.ViewHolder {

        private static final DecimalFormat FORMAT;

        static {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator(' ');
            FORMAT = new DecimalFormat("#,###", symbols);
        }

        private static Typeface typeface;

        private ImageView image;
        private ImageView watchStatusIcon;
        private TextView title;
        private TextView watching;
        private TextView rating;

        public RatingShowHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            watchStatusIcon = (ImageView) itemView.findViewById(R.id.watch_status);
            title = (TextView) itemView.findViewById(R.id.title);
            watching = (TextView) itemView.findViewById(R.id.watching);
            rating = (TextView) itemView.findViewById(R.id.rating);

            if (typeface == null) {
                typeface = Typeface.createFromAsset(itemView.getContext().getAssets(), "Roboto-Medium.ttf");
            }
            title.setTypeface(typeface);
        }

        public void bind(RatingShow ratingShow, UserShow userShow) {
            Context context = itemView.getContext();
            title.setText(ratingShow.getTitle());
            watching.setText(context.getString(R.string.watching, FORMAT.format(ratingShow.getWatching())));
            rating.setText(context.getString(R.string.show_rating, ratingShow.getRating()));
            WatchStatus status = userShow != null ? userShow.getWatchStatus() : WatchStatus.NOT_WATCHING;
            watchStatusIcon.setImageResource(status.getDrawableId());
            Glide.with(context)
                    .load(ratingShow.getImage())
                    .centerCrop()
                    .into(image);
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ShowActivity.class);
                if (userShow != null) {
                    intent.putExtra(ShowActivity.USER_SHOW, Parcels.wrap(userShow));
                } else {
                    intent.putExtra(ShowActivity.SHOW_ID, ratingShow.getId());
                }
                context.startActivity(intent);
            });
        }
    }

    private static class DividerItemDecorator extends RecyclerView.ItemDecoration {

        private final int dividerHeight;

        public DividerItemDecorator(Context context) {
            dividerHeight = context.getResources().getDimensionPixelSize(R.dimen.ratings_divider_height);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position != 0) {
                outRect.set(0, dividerHeight, 0, 0);
            }
        }
    }
}
