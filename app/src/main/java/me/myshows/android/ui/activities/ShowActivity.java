package me.myshows.android.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import org.parceler.Parcels;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.myshows.android.MyShowsApplication;
import me.myshows.android.R;
import me.myshows.android.api.MyShowsClient;
import me.myshows.android.model.Show;
import me.myshows.android.model.ShowEpisode;
import me.myshows.android.model.UserEpisode;
import me.myshows.android.model.UserShow;
import me.myshows.android.model.UserShowEpisodes;
import me.myshows.android.model.WatchStatus;
import me.myshows.android.ui.common.Season;
import me.myshows.android.ui.common.SeasonViewHolder;
import me.myshows.android.ui.common.SeriesViewHolder;
import me.myshows.android.ui.decorators.OffsetDecorator;
import me.myshows.android.ui.decorators.SimpleDrawableDecorator;
import me.myshows.android.utils.Numbers;
import me.myshows.android.utils.SparseSet;
import rx.Observable;

/**
 * Created by warrior on 19.07.15.
 */
public class ShowActivity extends HomeActivity {

    private static final String TAG = ShowActivity.class.getSimpleName();

    public static final String SHOW_ID = "showId";
    public static final String SHOW_TITLE = "showTitle";
    public static final String USER_SHOW = "userShow";

    private static final String OPEN_P_TAG = "<p>";
    private static final String CLOSE_P_TAG = "</p>";

    private MyShowsClient client;

    private ImageView showImage;
    private CollapsingToolbarLayout collapsingToolbar;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;

    private View showInformationView;
    private TextView description;
    private View divider;
    private TextView duration;
    private TextView status;
    private TextView rating;
    private RatingBar myRating;

    private RecyclerViewExpandableItemManager expandableItemManager;
    private RecyclerView.Adapter wrappedAdapter;

    private UserShow userShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_activity);

        client = MyShowsApplication.getMyShowsClient(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupActionBar(toolbar);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        showImage = (ImageView) findViewById(R.id.show_image);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        setupRecyclerView();

        showInformationView = LayoutInflater.from(this)
                .inflate(R.layout.show_information_layout, recyclerView, false);
        description = (TextView) showInformationView.findViewById(R.id.description);
        divider = showInformationView.findViewById(R.id.divider);
        duration = (TextView) showInformationView.findViewById(R.id.duration);
        status = (TextView) showInformationView.findViewById(R.id.status);
        rating = (TextView) showInformationView.findViewById(R.id.rating);
        myRating = (RatingBar) showInformationView.findViewById(R.id.my_rating);

        int showId = extractAndBindShowData(getIntent());
        loadData(showId);
    }

    private int extractAndBindShowData(Intent intent) {
        int showId;
        if (intent.hasExtra(USER_SHOW)) {
            userShow = Parcels.unwrap(intent.getParcelableExtra(USER_SHOW));
            bind(userShow);
            showId = userShow.getShowId();
        } else {
            showId = intent.getIntExtra(SHOW_ID, 0);
            String showTitle = intent.getStringExtra(SHOW_TITLE);
            collapsingToolbar.setTitle(showTitle);
        }
        return showId;
    }

    @Override
    public void onDestroy() {
        if (expandableItemManager != null) {
            expandableItemManager.release();
        }
        if (wrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(wrappedAdapter);
        }
        super.onDestroy();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(false);
        recyclerView.addItemDecoration(new SeasonOffsetDecorator(getResources().getDimensionPixelSize(R.dimen.default_padding)));
        recyclerView.addItemDecoration(new ShadowDecorator(ContextCompat.getDrawable(this, R.drawable.show_screen_shadow)));
        SimpleItemAnimator animator = new RefactoredDefaultItemAnimator();
        animator.setSupportsChangeAnimations(false);
        recyclerView.setItemAnimator(animator);
    }

    private void loadData(int showId) {
        Observable<Show> showObservable = client.showInformation(showId)
                .doOnNext(this::bind);
        Observable<UserShowEpisodes> userShowEpisodesObservable = client.profileEpisodesOfShow(showId)
                .defaultIfEmpty(new UserShowEpisodes(showId, Collections.emptyList()));
        Observable.combineLatest(showObservable, userShowEpisodesObservable,
                (show, episodes) -> ShowAdapter.create(showInformationView, show, episodes))
                .compose(bindToLifecycle())
                .subscribe(this::setAdapter);
        if (userShow == null) {
            loadUserShow(showId);
        }
    }

    private void loadUserShow(int showId) {
        client.profileShows()
                .compose(bindToLifecycle())
                .flatMap(Observable::from)
                .filter(show -> show.getShowId() == showId)
                .subscribe(this::bind);
    }

    @SuppressLint("NewApi")
    private void bind(UserShow show) {
        collapsingToolbar.setTitle(show.getTitle());
        WatchStatus watchStatus = show.getWatchStatus();
        fab.setImageResource(watchStatus.getDrawableId());
        fab.setBackgroundTintList(ContextCompat.getColorStateList(this, watchStatus.getColorId()));
        myRating.setRating(show.getRating());
    }

    private void bind(Show show) {
        collapsingToolbar.setTitle(show.getTitle());
        status.setText(show.getShowStatus().getStringId());

        CharSequence descriptionText = processDescription(show.getDescription());
        if (TextUtils.isEmpty(descriptionText)) {
            description.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        } else {
            description.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
            description.setText(descriptionText);
        }
        duration.setText(getString(R.string.duration, show.getRuntime()));
        rating.setText(getString(R.string.rating, show.getRating()));
        Glide.with(this)
                .load(show)
                .centerCrop()
                .into(showImage);
    }

    private void setAdapter(ShowAdapter adapter) {
        expandableItemManager = new RecyclerViewExpandableItemManager(null);
        wrappedAdapter = expandableItemManager.createWrappedAdapter(adapter);
        recyclerView.setAdapter(wrappedAdapter);
        expandableItemManager.attachRecyclerView(recyclerView);
    }

    @Nullable
    private static CharSequence processDescription(@Nullable String description) {
        if (description == null) {
            return null;
        }
        description = description.trim();
        if (description.startsWith(OPEN_P_TAG)) {
            description = description.substring(OPEN_P_TAG.length());
        }
        if (description.endsWith(CLOSE_P_TAG)) {
            description = description.substring(0, description.length() - CLOSE_P_TAG.length());
        }
        return Html.fromHtml(description);
    }

    private static class ShowAdapter extends AbstractExpandableItemAdapter<AbstractExpandableItemViewHolder, SeriesViewHolder<ShowEpisode>> {

        private static final int SHOW_INFORMATION_TYPE = 0;
        private static final int SEASON_TYPE = 1;

        private static final Comparator<ShowEpisode> EPISODE_COMPARATOR = (e1, e2) -> {
            int res = Numbers.compare(e2.getAirDateInMillis(), e1.getAirDateInMillis());
            return res != 0 ? res : Numbers.compare(e2.getSequenceNumber(), e1.getSequenceNumber());
        };

        private final View showInformationView;

        private final List<Season<ShowEpisode>> seasons;

        private final SeriesViewHolder.OnEpisodeCheckedChangeListener<ShowEpisode> seriesListener = this::onEpisodeCheckedChanged;
        private final SeasonViewHolder.OnSeasonCheckedChangeListener<ShowEpisode> seasonListener = season -> notifyDataSetChanged();

        private ShowAdapter(@NonNull View showInformationView, @NonNull List<Season<ShowEpisode>> seasons) {
            this.showInformationView = showInformationView;
            this.seasons = seasons;
            setHasStableIds(true);
        }

        @Override
        public int getGroupCount() {
            return seasons.size() + 1;
        }

        @Override
        public int getChildCount(int groupPosition) {
            if (groupPosition == 0) {
                return 0;
            }
            return seasons.get(seasonIndex(groupPosition)).size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition + 1;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return seasons.get(seasonIndex(groupPosition)).get(childPosition).getId();
        }

        @Override
        public int getGroupItemViewType(int groupPosition) {
            if (groupPosition == 0) {
                return SHOW_INFORMATION_TYPE;
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
            if (viewType == SHOW_INFORMATION_TYPE) {
                return new ShowInformationViewHolder(showInformationView);
            }
            View seasonView = inflater.inflate(R.layout.list_season_view, parent, false);
            return new SeasonViewHolder<>(seasonView, seasonListener);
        }

        @Override
        public SeriesViewHolder<ShowEpisode> onCreateChildViewHolder(ViewGroup parent, int viewType) {
            View seriesView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_series_view, parent, false);
            return new SeriesViewHolder<>(seriesView, seriesListener);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onBindGroupViewHolder(AbstractExpandableItemViewHolder holder, int groupPosition, int viewType) {
            if (viewType == SEASON_TYPE) {
                ((SeasonViewHolder<ShowEpisode>) holder).bind(seasons.get(seasonIndex(groupPosition)));
            }
        }

        @Override
        public void onBindChildViewHolder(SeriesViewHolder<ShowEpisode> holder, int groupPosition, int childPosition, int viewType) {
            holder.bind(seasons.get(seasonIndex(groupPosition)), childPosition);
        }

        @Override
        public boolean onCheckCanExpandOrCollapseGroup(AbstractExpandableItemViewHolder holder, int groupPosition, int x, int y, boolean expand) {
            if (groupPosition == 0) {
                return false;
            }
            SeasonViewHolder seasonHolder = (SeasonViewHolder) holder;
            boolean canExpandOrCollapse = seasonHolder.canExpandOrCollapse(x, y);
            if (canExpandOrCollapse) {
                seasons.get(seasonIndex(groupPosition)).setExpanded(expand);
            }
            return canExpandOrCollapse;
        }

        public void onEpisodeCheckedChanged(@NonNull Season<ShowEpisode> season, int position, int adapterPosition) {
            if (season.get(position).isSpecial()) {
                notifyItemChanged(position);
            } else {
                notifyDataSetChanged();
            }
        }

        private int seasonIndex(int groupPosition) {
            return seasons.size() - groupPosition;
        }

        public static ShowAdapter create(@NonNull View showInformationView, @NonNull Show show, @NonNull UserShowEpisodes watchedEpisodes) {
            List<Season<ShowEpisode>> seasons = Season.splitToSeasons(show.getEpisodes().values(), EPISODE_COMPARATOR);
            int seasonCount = seasons.size();

            SparseSet[] checkedEpisodes = new SparseSet[seasonCount];
            SparseSet[] checkedSpecialEpisodes = new SparseSet[seasonCount];
            for (int i = 0; i < seasonCount; i++) {
                checkedEpisodes[i] = new SparseSet();
                checkedSpecialEpisodes[i] = new SparseSet();
            }

            for (UserEpisode userEpisode : watchedEpisodes.getEpisodes()) {
                ShowEpisode episode = show.getEpisodes().get(String.valueOf(userEpisode.getId()));
                if (episode != null) {
                    int seasonIndex = episode.getSeasonNumber() - 1;
                    if (episode.isSpecial()) {
                        checkedSpecialEpisodes[seasonIndex].add(episode.getId());
                    } else {
                        checkedEpisodes[seasonIndex].add(episode.getId());
                    }
                }
            }
            for (int i = 0; i < seasonCount; i++) {
                seasons.get(i).setCheckedEpisodes(checkedEpisodes[i]);
                seasons.get(i).setCheckedSpecialEpisodes(checkedSpecialEpisodes[i]);
            }
            return new ShowAdapter(showInformationView, seasons);
        }
    }

    private static class ShowInformationViewHolder extends AbstractExpandableItemViewHolder {

        public ShowInformationViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private static class SeasonOffsetDecorator extends OffsetDecorator {

        public SeasonOffsetDecorator(int offset) {
            super(offset, TOP_OFFSET);
        }

        @Override
        protected boolean applyDecorator(View view, RecyclerView parent) {
            RecyclerView.ViewHolder holder = parent.getChildViewHolder(view);
            return holder instanceof SeasonViewHolder && holder.getAdapterPosition() != 1;
        }
    }

    private static class ShadowDecorator extends SimpleDrawableDecorator {

        public ShadowDecorator(Drawable shadowDrawable) {
            super(shadowDrawable, Border.BOTTOM);
        }

        @Override
        protected boolean applyDecorator(View view, RecyclerView parent) {
            RecyclerView.ViewHolder holder = parent.getChildViewHolder(view);
            return holder instanceof SeasonViewHolder ||
                    holder instanceof SeriesViewHolder && ((SeriesViewHolder) holder).isLast();
        }
    }
}
