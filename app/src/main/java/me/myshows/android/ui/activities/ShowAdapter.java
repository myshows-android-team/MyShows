package me.myshows.android.ui.activities;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.myshows.android.R;
import me.myshows.android.model.Episode;
import me.myshows.android.model.Show;
import me.myshows.android.model.UserEpisode;
import me.myshows.android.model.UserShowEpisodes;
import me.myshows.android.ui.decorators.OffsetDecorator;
import me.myshows.android.ui.decorators.SimpleDrawableDecorator;
import me.myshows.android.utils.Numbers;
import me.myshows.android.utils.SparseSet;

import static me.myshows.android.ui.activities.ShowAdapter.SeasonViewHolder.OnSeasonCheckedChangeListener;
import static me.myshows.android.ui.activities.ShowAdapter.SeriesViewHolder.OnEpisodeCheckedChangeListener;

/**
 * Created by warrior on 13.08.15.
 */
class ShowAdapter extends AbstractExpandableItemAdapter<AbstractExpandableItemViewHolder, ShowAdapter.SeriesViewHolder> {

    private static final int SHOW_INFORMATION_TYPE = 0;
    private static final int SEASON_TYPE = 1;

    private static final Comparator<Episode> EPISODE_COMPARATOR = (e1, e2) -> {
        int res = Numbers.compare(e1.getAirDateInMillis(), e2.getAirDateInMillis());
        return res != 0 ? res : Numbers.compare(e1.getSequenceNumber(), e2.getSequenceNumber());
    };

    private final View showInformationView;

    private final List<List<Episode>> seasons;
    private final SparseSet[] uncheckedEpisodes;
    private final SparseSet checkedSpecialEpisodes;
    private final int seasonCount;
    private final boolean[] expandedSeasons;

    private final OnEpisodeCheckedChangeListener seriesListener = this::onEpisodeCheckedChanged;
    private final OnSeasonCheckedChangeListener seasonListener = this::onSeasonCheckedChanged;

    private ShowAdapter(@NonNull View showInformationView, @NonNull List<List<Episode>> seasons,
                        @NonNull SparseSet[] uncheckedEpisodes, @NonNull SparseSet checkedSpecialEpisodes) {
        this.showInformationView = showInformationView;
        this.seasons = seasons;
        this.checkedSpecialEpisodes = checkedSpecialEpisodes;
        this.uncheckedEpisodes = uncheckedEpisodes;
        this.seasonCount = seasons.size();
        this.expandedSeasons = new boolean[seasons.size()];
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
        return new SeasonViewHolder(seasonView, seasonListener);
    }

    @Override
    public SeriesViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View seriesView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_series_view, parent, false);
        return new SeriesViewHolder(seriesView, seriesListener);
    }

    @Override
    public void onBindGroupViewHolder(AbstractExpandableItemViewHolder holder, int groupPosition, int viewType) {
        if (viewType == SEASON_TYPE) {
            ((SeasonViewHolder) holder).bind(seasonIndex(groupPosition),
                    uncheckedEpisodes[seasonIndex(groupPosition)].isEmpty(),
                    expandedSeasons[seasonIndex(groupPosition)]);
        }
    }

    @Override
    public void onBindChildViewHolder(SeriesViewHolder holder, int groupPosition, int childPosition, int viewType) {
        List<Episode> season = seasons.get(seasonIndex(groupPosition));
        Episode episode = season.get(childPosition);
        boolean isChecked;
        if (episode.isSpecial()) {
            isChecked = checkedSpecialEpisodes.contains(episode.getId());
        } else {
            isChecked = !uncheckedEpisodes[seasonIndex(groupPosition)].contains(episode.getId());
        }
        holder.bind(episode, childPosition == season.size() - 1, isChecked);
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(AbstractExpandableItemViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        if (groupPosition == 0) {
            return false;
        }
        SeasonViewHolder seasonHolder = (SeasonViewHolder) holder;
        boolean canExpandOrCollapse = seasonHolder.canExpandOrCollapse(x, y);
        if (canExpandOrCollapse) {
            expandedSeasons[seasonIndex(groupPosition)] = expand;
        }
        return canExpandOrCollapse;
    }

    public void onEpisodeCheckedChanged(int position, @NonNull Episode episode, boolean checked) {
        if (episode.isSpecial()) {
            if (checked) {
                checkedSpecialEpisodes.add(episode.getId());
            } else {
                checkedSpecialEpisodes.remove(episode.getId());
            }
            notifyItemChanged(position);
        } else {
            SparseSet uncheckedSeasonEpisodes = uncheckedEpisodes[episode.getSeasonNumber() - 1];
            if (checked) {
                uncheckedSeasonEpisodes.remove(episode.getId());
            } else {
                uncheckedSeasonEpisodes.add(episode.getId());
            }
            notifyDataSetChanged();
        }
    }

    public void onSeasonCheckedChanged(int seasonIndex, boolean checked) {
        SparseSet uncheckedSeasonEpisodes = uncheckedEpisodes[seasonIndex];
        if (checked) {
            uncheckedSeasonEpisodes.clear();
        } else {
            for (Episode episode : seasons.get(seasonIndex)) {
                if (!episode.isSpecial()) {
                    uncheckedSeasonEpisodes.add(episode.getId());
                }
            }
        }
        notifyDataSetChanged();
    }

    private int seasonIndex(int groupPosition) {
        return seasonCount - groupPosition;
    }

    public static ShowAdapter create(@NonNull View showInformationView, @NonNull Show show, @NonNull UserShowEpisodes watchedEpisodes) {
        List<List<Episode>> seasons = getSeasons(show);
        int seasonCount = seasons.size();

        SparseSet[] uncheckedEpisodes = new SparseSet[seasonCount];
        for (int i = 0; i < seasonCount; i++) {
            uncheckedEpisodes[i] = new SparseSet();
        }
        for (List<Episode> season : seasons) {
            for (Episode episode : season) {
                if (!episode.isSpecial()) {
                    uncheckedEpisodes[episode.getSeasonNumber() - 1].add(episode.getId());
                }
            }
        }
        SparseSet checkedSpecialEpisodes = new SparseSet();
        for (UserEpisode userEpisode : watchedEpisodes.getEpisodes()) {
            Episode episode = show.getEpisodes().get(String.valueOf(userEpisode.getId()));
            if (episode != null) {
                if (episode.isSpecial()) {
                    checkedSpecialEpisodes.add(episode.getId());
                } else {
                    uncheckedEpisodes[episode.getSeasonNumber() - 1].remove(episode.getId());
                }
            }
        }
        return new ShowAdapter(showInformationView, seasons, uncheckedEpisodes, checkedSpecialEpisodes);
    }

    private static List<List<Episode>> getSeasons(@NonNull Show show) {
        List<List<Episode>> seasons = new ArrayList<>();
        for (Episode episode : show.getEpisodes().values()) {
            int seasonIndex = episode.getSeasonNumber() - 1;
            while (seasonIndex >= seasons.size()) {
                seasons.add(new ArrayList<>());
            }
            List<Episode> seasonList = seasons.get(seasonIndex);
            seasonList.add(episode);
        }
        for (List<Episode> season : seasons) {
            Collections.sort(season, (e1, e2) -> EPISODE_COMPARATOR.compare(e2, e1));
        }
        return seasons;
    }

    static class SeasonOffsetDecorator extends OffsetDecorator {

        public SeasonOffsetDecorator(int offset) {
            super(offset, TOP_OFFSET);
        }

        @Override
        protected boolean applyDecorator(View view, RecyclerView parent) {
            RecyclerView.ViewHolder holder = parent.getChildViewHolder(view);
            return holder instanceof SeasonViewHolder && holder.getAdapterPosition() != 1;
        }
    }

    static class ShadowDecorator extends SimpleDrawableDecorator {

        public ShadowDecorator(Drawable shadowDrawable) {
            super(shadowDrawable);
        }

        @Override
        protected boolean applyDecorator(View view, RecyclerView parent) {
            RecyclerView.ViewHolder holder = parent.getChildViewHolder(view);
            return holder instanceof SeasonViewHolder ||
                    holder instanceof SeriesViewHolder && ((SeriesViewHolder) holder).isLast();
        }
    }

    static class ShowInformationViewHolder extends AbstractExpandableItemViewHolder {

        public ShowInformationViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class SeasonViewHolder extends AbstractExpandableItemViewHolder {

        public interface OnSeasonCheckedChangeListener {
            void onSeasonCheckedChanged(int seasonNumber, boolean isChecked);
        }

        private static final float ROTATION_COLLAPSED = 0;
        private static final float ROTATION_EXPANDED = 90;

        private static final long ANIMATION_DURATION = 250;

        private final OnSeasonCheckedChangeListener listener;

        private final TextView seasonNumber;
        private final CheckBox checkBox;
        private final View arrow;

        private int seasonIndex = -Integer.MAX_VALUE;
        private boolean expanded;
        private ObjectAnimator animator;

        public SeasonViewHolder(@NonNull View itemView, @NonNull OnSeasonCheckedChangeListener listener) {
            super(itemView);
            this.listener = listener;
            seasonNumber = (TextView) itemView.findViewById(R.id.season_number);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            arrow = itemView.findViewById(R.id.arrow);
        }

        public void bind(int seasonIndex, boolean isChecked, boolean expanded) {
            seasonNumber.setText(itemView.getResources().getString(R.string.season_number, seasonIndex + 1));
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(isChecked);
            checkBox.setOnCheckedChangeListener((v, checked) -> listener.onSeasonCheckedChanged(seasonIndex, checked));
            setExpanded(seasonIndex, expanded);
        }

        public boolean canExpandOrCollapse(int x, int y) {
            Rect hitRect = new Rect();
            checkBox.getHitRect(hitRect);
            return !hitRect.contains(x, y);
        }

        private void setExpanded(int seasonIndex, boolean expanded) {
            if (this.seasonIndex != seasonIndex) {
                cancelAnimation();
                arrow.setRotation(expanded ? ROTATION_EXPANDED : ROTATION_COLLAPSED);
            } else if (this.expanded != expanded) {
                cancelAnimation();
                animator = ObjectAnimator.ofFloat(arrow, View.ROTATION, expanded ? ROTATION_EXPANDED : ROTATION_COLLAPSED)
                        .setDuration(ANIMATION_DURATION);
                animator.start();
            }
            this.seasonIndex = seasonIndex;
            this.expanded = expanded;
        }

        private void cancelAnimation() {
            if (animator != null) {
                animator.cancel();
            }
        }
    }

    static class SeriesViewHolder extends AbstractExpandableItemViewHolder {

        public interface OnEpisodeCheckedChangeListener {
            void onEpisodeCheckedChanged(int position, @NonNull Episode episode, boolean isChecked);
        }

        private final OnEpisodeCheckedChangeListener listener;

        private final TextView seriesNumber;
        private final TextView seriesTitle;
        private final TextView airDate;
        private final CheckBox checkBox;
        private final View specialIcon;
        private final View divider;

        private boolean isLast;

        public SeriesViewHolder(@NonNull View itemView, @NonNull OnEpisodeCheckedChangeListener listener) {
            super(itemView);
            this.listener = listener;
            seriesNumber = (TextView) itemView.findViewById(R.id.series_number);
            seriesTitle = (TextView) itemView.findViewById(R.id.series_title);
            airDate = (TextView) itemView.findViewById(R.id.air_date);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            specialIcon = itemView.findViewById(R.id.special_icon);
            divider = itemView.findViewById(R.id.divider);
        }

        public void bind(@NonNull Episode episode, boolean isLast, boolean checked) {
            this.isLast = isLast;
            seriesTitle.setText(episode.getTitle());
            itemView.setOnClickListener(v -> startEpisodeActivity(episode));
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(checked);
            checkBox.setOnCheckedChangeListener((v, isChecked) -> listener.onEpisodeCheckedChanged(getAdapterPosition(), episode, isChecked));
            divider.setVisibility(isLast ? View.GONE : View.VISIBLE);
            setSeriesNumber(episode);
            setAirDate(episode.getAirDate());
        }

        private void startEpisodeActivity(Episode episode) {
            Context context = itemView.getContext();
            Intent intent = new Intent(context, EpisodeActivity.class);
            intent.putExtra(EpisodeActivity.EPISODE_ID, episode.getId());
            intent.putExtra(EpisodeActivity.EPISODE_TITLE, episode.getTitle());
            context.startActivity(intent);
        }

        public boolean isLast() {
            return isLast;
        }

        private void setAirDate(@Nullable String date) {
            if (TextUtils.isEmpty(date)) {
                airDate.setText(R.string.unknown_date);
            } else {
                airDate.setText(date);
            }
        }

        private void setSeriesNumber(@NonNull Episode episode) {
            if (episode.isSpecial()) {
                seriesNumber.setVisibility(View.GONE);
                specialIcon.setVisibility(View.VISIBLE);
            } else {
                seriesNumber.setText(String.valueOf(episode.getEpisodeNumber()));
                seriesNumber.setVisibility(View.VISIBLE);
                specialIcon.setVisibility(View.GONE);
            }
        }
    }
}
