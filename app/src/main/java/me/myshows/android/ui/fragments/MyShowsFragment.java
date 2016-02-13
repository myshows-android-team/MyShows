package me.myshows.android.ui.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.myshows.android.MyShowsApplication;
import me.myshows.android.R;
import me.myshows.android.api.MyShowsClient;
import me.myshows.android.model.UnwatchedEpisode;
import me.myshows.android.model.UserShow;
import me.myshows.android.ui.activities.ShowActivity;
import me.myshows.android.ui.common.Season;
import me.myshows.android.ui.common.SeasonViewHolder;
import me.myshows.android.ui.common.SeriesViewHolder;
import me.myshows.android.ui.decorators.OffsetDecorator;
import me.myshows.android.ui.decorators.SimpleDrawableDecorator;
import rx.Observable;

import static me.myshows.android.ui.common.SeasonViewHolder.OnSeasonCheckedChangeListener;
import static me.myshows.android.ui.common.SeriesViewHolder.OnEpisodeCheckedChangeListener;

/**
 * Created by warrior on 03.11.15.
 */
public class MyShowsFragment extends BaseFragment {

    private static final String TAG = MyShowsFragment.class.getSimpleName();

    private RecyclerView recyclerView;

    private MyShowsAdapter originalAdapter;
    private RecyclerViewExpandableItemManager expandableItemManager;
    private RecyclerView.Adapter wrappedAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_shows_fragment, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(false);
        int offset = getResources().getDimensionPixelSize(R.dimen.default_padding);
        recyclerView.addItemDecoration(new ShowOffsetDecorator(offset));
        recyclerView.addItemDecoration(new SeasonDividerDecorator(ContextCompat.getDrawable(getActivity(), R.drawable.season_divider), offset));
        recyclerView.addItemDecoration(new ShadowDecorator(ContextCompat.getDrawable(getActivity(), R.drawable.show_screen_shadow)));
        SimpleItemAnimator animator = new RefactoredDefaultItemAnimator();
        animator.setSupportsChangeAnimations(false);
        recyclerView.setItemAnimator(animator);

        loadData();

        return view;
    }

    @Override
    public void onDestroyView() {
        if (expandableItemManager != null) {
            expandableItemManager.release();
        }
        if (wrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(wrappedAdapter);
        }
        originalAdapter = null;
        super.onDestroyView();
    }

    private void loadData() {
        MyShowsClient client = MyShowsApplication.getMyShowsClient(getActivity());
        Observable<SparseArray<UserShow>> userShows = client.profileShows()
                .map(showList -> {
                    SparseArray<UserShow> shows = new SparseArray<>(showList.size());
                    for (UserShow show : showList) {
                        shows.put(show.getShowId(), show);
                    }
                    return shows;
                });
        Observable.combineLatest(userShows, client.profileUnwatchedEpisodes(), MyShowsAdapter::prepareData)
                .compose(bindToLifecycle())
                .subscribe(this::bindData);
    }

    public void bindData(@NonNull AdapterData data) {
        if (originalAdapter == null) {
            originalAdapter = new MyShowsAdapter(data);
            expandableItemManager = new RecyclerViewExpandableItemManager(null);
            wrappedAdapter = expandableItemManager.createWrappedAdapter(originalAdapter);
            recyclerView.setAdapter(wrappedAdapter);
            expandableItemManager.attachRecyclerView(recyclerView);
        } else {
            originalAdapter.changeData(data);
        }
    }

    private static class MyShowsAdapter extends AbstractExpandableItemAdapter<AbstractExpandableItemViewHolder, SeriesViewHolder<UnwatchedEpisode>> {

        private static final int SHOW_HEADER_TYPE = 0;
        private static final int SEASON_TYPE = 1;

        private final OnEpisodeCheckedChangeListener<UnwatchedEpisode> seriesListener = this::onEpisodeCheckedChanged;
        private final OnSeasonCheckedChangeListener<UnwatchedEpisode> seasonListener = season -> notifyDataSetChanged();

        private SparseArray<ShowData> shows;
        private SparseArray<Season<UnwatchedEpisode>> seasons;

        public MyShowsAdapter(@NonNull AdapterData data) {
            shows = data.shows;
            seasons = data.seasons;
            setHasStableIds(true);
        }

        public void changeData(@NonNull AdapterData data) {
            shows = data.shows;
            seasons = data.seasons;
            notifyDataSetChanged();
        }

        @Override
        public int getGroupCount() {
            return shows.size() + seasons.size();
        }

        @Override
        public int getChildCount(int groupPosition) {
            Season<UnwatchedEpisode> season = seasons.get(groupPosition);
            return season != null ? season.size() : 0;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition + 1;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return seasons.get(groupPosition).get(childPosition).getId();
        }

        @Override
        public int getGroupItemViewType(int groupPosition) {
            ShowData data = shows.get(groupPosition);
            if (data != null) {
                return SHOW_HEADER_TYPE;
            }
            return SEASON_TYPE;
        }

        @Override
        public int getChildItemViewType(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public AbstractExpandableItemViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            switch (viewType) {
                case SHOW_HEADER_TYPE: {
                    View view = inflater.inflate(R.layout.list_show_header_view, parent, false);
                    return new ShowHeaderViewHolder(view);
                }
                case SEASON_TYPE: {
                    View view = inflater.inflate(R.layout.list_season_view, parent, false);
                    return new ShowSeasonViewHolder(view, seasonListener);
                }
                default:
                    throw new IllegalArgumentException("unknown viewType: " + viewType);
            }
        }

        @Override
        public SeriesViewHolder<UnwatchedEpisode> onCreateChildViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_series_view, parent, false);
            return new SeriesViewHolder<>(view, seriesListener);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onBindGroupViewHolder(AbstractExpandableItemViewHolder holder, int groupPosition, int viewType) {
            switch (viewType) {
                case SHOW_HEADER_TYPE:
                    ((ShowHeaderViewHolder) holder).bind(shows.get(groupPosition));
                    break;
                case SEASON_TYPE:
                    ((ShowSeasonViewHolder) holder).bind(seasons.get(groupPosition), seasons.get(groupPosition - 1) == null);
                    break;
                default:
                    Log.wtf(TAG, "unexpected class: " + holder.getClass().getCanonicalName());
            }
        }

        @Override
        public void onBindChildViewHolder(SeriesViewHolder<UnwatchedEpisode> holder, int groupPosition, int childPosition, int viewType) {
            Season<UnwatchedEpisode> season = seasons.get(groupPosition);
            holder.bind(season, childPosition);
        }

        @Override
        public boolean onCheckCanExpandOrCollapseGroup(AbstractExpandableItemViewHolder holder, int groupPosition, int x, int y, boolean expand) {
            if (holder instanceof ShowSeasonViewHolder) {
                ShowSeasonViewHolder seasonHolder = (ShowSeasonViewHolder) holder;
                boolean canExpandOrCollapse = seasonHolder.canExpandOrCollapse(x, y);
                if (canExpandOrCollapse) {
                    seasons.get(groupPosition).setExpanded(expand);
                }
                return canExpandOrCollapse;
            }
            return false;
        }

        private void onEpisodeCheckedChanged(@NonNull Season<UnwatchedEpisode> season, int position, int adapterPosition) {
            if (season.get(position).isSpecial()) {
                notifyItemChanged(adapterPosition);
            } else {
                notifyDataSetChanged();
            }
        }

        public static AdapterData prepareData(@NonNull SparseArray<UserShow> userShows, @NonNull List<UnwatchedEpisode> unwatchedEpisodes) {
            LinkedHashMap<Integer, List<UnwatchedEpisode>> groupedEpisodes = new LinkedHashMap<>();
            for (UnwatchedEpisode episode : unwatchedEpisodes) {
                List<UnwatchedEpisode> showEpisodes = groupedEpisodes.get(episode.getShowId());
                if (showEpisodes == null) {
                    showEpisodes = new ArrayList<>();
                    groupedEpisodes.put(episode.getShowId(), showEpisodes);
                }
                showEpisodes.add(episode);
            }

            SparseArray<ShowData> shows = new SparseArray<>(groupedEpisodes.size());
            SparseArray<Season<UnwatchedEpisode>> seasons = new SparseArray<>();
            int position = 0;
            for (Map.Entry<Integer, List<UnwatchedEpisode>> entry : groupedEpisodes.entrySet()) {
                UserShow show = userShows.get(entry.getKey());
                if (show != null) {
                    shows.append(position, new ShowData(show, entry.getValue().size()));
                    position++;
                    List<Season<UnwatchedEpisode>> showSeasons = Season.splitToSeasons(entry.getValue());
                    for (Season<UnwatchedEpisode> season : showSeasons) {
                        seasons.append(position, season);
                        position++;
                    }
                }
            }

            return new AdapterData(shows, seasons);
        }
    }

    private static class ShowData {

        public final UserShow show;
        public final int seriesLeft;

        public ShowData(UserShow show, int seriesLeft) {
            this.show = show;
            this.seriesLeft = seriesLeft;
        }
    }

    private static class AdapterData {
        public final SparseArray<ShowData> shows;
        public final SparseArray<Season<UnwatchedEpisode>> seasons;

        public AdapterData(SparseArray<ShowData> shows, SparseArray<Season<UnwatchedEpisode>> seasons) {
            this.shows = shows;
            this.seasons = seasons;
        }
    }

    private static class ShowHeaderViewHolder extends AbstractExpandableItemViewHolder {

        private final ImageView image;
        private final TextView title;
        private final TextView remain;

        public ShowHeaderViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            remain = (TextView) itemView.findViewById(R.id.remain);
        }

        public void bind(@NonNull ShowData showData) {
            title.setText(showData.show.getTitle());
            remain.setText(itemView.getResources().getQuantityString(R.plurals.remain, showData.seriesLeft, showData.seriesLeft));
            Glide.with(itemView.getContext())
                    .load(showData.show)
                    .crossFade()
                    .into(image);
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ShowActivity.class);
                intent.putExtra(ShowActivity.USER_SHOW, Parcels.wrap(showData.show));
                v.getContext().startActivity(intent);
            });
        }
    }

    private static class ShowSeasonViewHolder extends SeasonViewHolder<UnwatchedEpisode> {

        private boolean isFirst;

        public ShowSeasonViewHolder(@NonNull View itemView, @NonNull OnSeasonCheckedChangeListener<UnwatchedEpisode> listener) {
            super(itemView, listener);
        }

        public void bind(@NonNull Season<UnwatchedEpisode> season, boolean isFirst) {
            super.bind(season);
            this.isFirst = isFirst;
        }

        public boolean isFirst() {
            return isFirst;
        }
    }

    private static class ShowOffsetDecorator extends OffsetDecorator {

        public ShowOffsetDecorator(int offset) {
            super(offset, TOP_OFFSET);
        }

        @Override
        protected boolean applyDecorator(View view, RecyclerView parent) {
            RecyclerView.ViewHolder holder = parent.getChildViewHolder(view);
            return holder instanceof ShowHeaderViewHolder && holder.getAdapterPosition() != 0;
        }
    }

    private static class SeasonDividerDecorator extends SimpleDrawableDecorator {

        private final int offset;

        public SeasonDividerDecorator(Drawable drawable, int offset) {
            super(drawable, Border.TOP);
            this.offset = offset;
        }

        @Override
        protected boolean applyDecorator(View view, RecyclerView parent) {
            RecyclerView.ViewHolder holder = parent.getChildViewHolder(view);
            return holder instanceof ShowSeasonViewHolder && !((ShowSeasonViewHolder) holder).isFirst();
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (applyDecorator(view, parent)) {
                outRect.set(0, offset, 0, 0);
            }
        }
    }

    private static class ShadowDecorator extends SimpleDrawableDecorator {

        public ShadowDecorator(Drawable shadowDrawable) {
            super(shadowDrawable, Border.BOTTOM);
        }

        @Override
        protected boolean applyDecorator(View view, RecyclerView parent) {
            RecyclerView.ViewHolder holder = parent.getChildViewHolder(view);
            return holder instanceof ShowSeasonViewHolder ||
                    holder instanceof SeriesViewHolder && ((SeriesViewHolder) holder).isLast();
        }
    }
}
