package me.myshows.android.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.trello.rxlifecycle.components.support.RxFragment;

import org.parceler.Parcels;

import java.util.List;

import me.myshows.android.R;
import me.myshows.android.api.MyShowsClient;
import me.myshows.android.api.impl.MyShowsClientImpl;
import me.myshows.android.model.UserShow;
import me.myshows.android.ui.activities.ShowActivity;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by warrior on 06.07.15.
 */
public class MyShowsFragment extends RxFragment {

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_shows_fragment, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        loadData();

        return view;
    }

    private void loadData() {
        MyShowsClient client = MyShowsClientImpl.getInstance();
        client.profileShows()
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shows -> {
                            ShowAdapter adapter = new ShowAdapter(shows);
                            recyclerView.setAdapter(adapter);
                        }
                );
    }

    private static class ShowAdapter extends RecyclerView.Adapter<ShowHolder> {

        private final List<UserShow> shows;

        public ShowAdapter(List<UserShow> shows) {
            this.shows = shows;
        }

        @Override
        public ShowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_show_view, parent, false);
            return new ShowHolder(view);
        }

        @Override
        public void onBindViewHolder(ShowHolder holder, int position) {
            holder.bind(shows.get(position), position);
        }

        @Override
        public int getItemCount() {
            return shows.size();
        }
    }

    private static class ShowHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView title;
        private ProgressBar progress;
        private View shadow;

        public ShowHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            progress = (ProgressBar) itemView.findViewById(R.id.progress);
            shadow = itemView.findViewById(R.id.shadow);
        }

        public void bind(UserShow show, int position) {
            title.setText(show.getTitle());
            progress.setMax(show.getTotalEpisodes());
            progress.setProgress(show.getWatchedEpisodes());
            shadow.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            // temporary workaround
            image.setImageResource(R.drawable.tmp_placeholder);
            Glide.with(itemView.getContext())
                    .load(show.getImage())
                    .centerCrop()
                    .into(image);

            itemView.setOnClickListener(v -> {
                Context context = v.getContext();
                Intent intent = new Intent(context, ShowActivity.class);
                intent.putExtra(ShowActivity.USER_SHOW, Parcels.wrap(show));
                context.startActivity(intent);
            });
        }
    }
}
