package me.myshows.android.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import me.myshows.android.R;
import me.myshows.android.api.MyShowsClient;
import me.myshows.android.api.impl.MyShowsClientImpl;
import me.myshows.android.model.RatingShow;
import me.myshows.android.ui.activities.ShowActivity;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by warrior on 19.07.15.
 */
public class RatingsFragment extends Fragment {

    private RecyclerView recyclerView;

    private Subscription subscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ratings_fragment, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        loadData();

        return view;
    }

    @Override
    public void onDestroyView() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        super.onDestroyView();
    }

    private void loadData() {
        MyShowsClient client = MyShowsClientImpl.getInstance();
        subscription = client.ratingShows()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shows -> {
                            RatingShowAdapter adapter = new RatingShowAdapter(shows);
                            recyclerView.setAdapter(adapter);
                        }
                );
    }

    private static class RatingShowAdapter extends RecyclerView.Adapter<RatingShowHolder> {

        private final List<RatingShow> shows;

        public RatingShowAdapter(List<RatingShow> shows) {
            this.shows = shows;
        }

        @Override
        public RatingShowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_rating_show_view, parent, false);
            return new RatingShowHolder(view);
        }

        @Override
        public void onBindViewHolder(RatingShowHolder holder, int position) {
            holder.bind(shows.get(position));
        }

        @Override
        public int getItemCount() {
            return shows.size();
        }
    }

    private static class RatingShowHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView title;
        private TextView watching;
        private TextView rating;

        public RatingShowHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            watching = (TextView) itemView.findViewById(R.id.watching);
            rating = (TextView) itemView.findViewById(R.id.rating);
        }

        public void bind(RatingShow show) {
            Context context = itemView.getContext();
            title.setText(show.getTitle());
            watching.setText(context.getString(R.string.watching, show.getWatching()));
            rating.setText(context.getString(R.string.show_rating, show.getRating()));
            Glide.with(context)
                    .load(show.getImage())
                    .centerCrop()
                    .into(image);
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ShowActivity.class);
                intent.putExtra(ShowActivity.SHOW_ID, show.getId());
                context.startActivity(intent);
            });
        }
    }
}
