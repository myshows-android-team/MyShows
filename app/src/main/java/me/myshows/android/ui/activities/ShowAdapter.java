package me.myshows.android.ui.activities;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.myshows.android.R;
import me.myshows.android.model.Episode;
import me.myshows.android.model.Show;
import me.myshows.android.model.UserEpisode;
import me.myshows.android.model.UserShow;
import me.myshows.android.model.UserShowEpisodes;
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

    private final Show show;
    private final List<List<Episode>> seasons;
    private final SparseSet[] uncheckedEpisodes;
    private final SparseSet checkedSpecialEpisodes;
    private final boolean[] expandedSeasons;

    private final OnEpisodeCheckedChangeListener seriesListener = this::onEpisodeCheckedChanged;
    private final OnSeasonCheckedChangeListener seasonListener = this::onSeasonCheckedChanged;

    private UserShow userShow;

    private ShowAdapter(@NonNull Show show, @NonNull List<List<Episode>> seasons,
                        @NonNull SparseSet[] uncheckedEpisodes, @NonNull SparseSet checkedSpecialEpisodes) {
        this.show = show;
        this.seasons = seasons;
        this.checkedSpecialEpisodes = checkedSpecialEpisodes;
        this.uncheckedEpisodes = uncheckedEpisodes;
        this.expandedSeasons = new boolean[seasons.size()];
        setHasStableIds(true);
    }

    public void setUserShow(@NonNull UserShow userShow) {
        this.userShow = userShow;
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
        return seasons.get(groupPosition - 1).size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition + 1;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return seasons.get(groupPosition - 1).get(childPosition).getId();
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
            View showInformationView = inflater.inflate(R.layout.show_information_layout, parent, false);
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
        if (viewType == SHOW_INFORMATION_TYPE) {
            ((ShowInformationViewHolder) holder).bind(show, userShow);
        } else {
            ((SeasonViewHolder) holder).bind(groupPosition,
                    uncheckedEpisodes[groupPosition - 1].isEmpty(),
                    expandedSeasons[groupPosition - 1]);
        }
    }

    @Override
    public void onBindChildViewHolder(SeriesViewHolder holder, int groupPosition, int childPosition, int viewType) {
        Episode episode = seasons.get(groupPosition - 1).get(childPosition);
        boolean isChecked;
        if (episode.isSpecial()) {
            isChecked = checkedSpecialEpisodes.contains(episode.getId());
        } else {
            isChecked = !uncheckedEpisodes[groupPosition - 1].contains(episode.getId());
        }
        holder.bind(episode, isChecked);
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(AbstractExpandableItemViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        if (groupPosition == 0) {
            return false;
        }
        SeasonViewHolder seasonHolder = (SeasonViewHolder) holder;
        boolean canExpandOrCollapse = seasonHolder.canExpandOrCollapse(x, y);
        if (canExpandOrCollapse) {
            expandedSeasons[groupPosition - 1] = expand;
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

    public void onSeasonCheckedChanged(int seasonNumber, boolean checked) {
        int seasonIndex = seasonNumber - 1;
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

    public static ShowAdapter create(@NonNull Show show, @NonNull UserShowEpisodes watchedEpisodes) {
        List<List<Episode>> seasons = getSeasons(show);
        SparseSet[] uncheckedEpisodes = new SparseSet[seasons.size()];
        for (int i = 0; i < seasons.size(); i++) {
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
            if (episode.isSpecial()) {
                checkedSpecialEpisodes.add(episode.getId());
            } else {
                uncheckedEpisodes[episode.getSeasonNumber() - 1].remove(episode.getId());
            }
        }
        return new ShowAdapter(show, seasons, uncheckedEpisodes, checkedSpecialEpisodes);
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
            Collections.sort(season, (e1, e2) -> {
                int res = Numbers.compare(e1.getAirDateInMillis(), e2.getAirDateInMillis());
                return res != 0 ? res : Numbers.compare(e1.getSequenceNumber(), e2.getSequenceNumber());
            });
        }
        return seasons;
    }

    public static class DividerDecorator extends RecyclerView.ItemDecoration {

        private final int offset;

        public DividerDecorator(Context context) {
            offset = context.getResources().getDimensionPixelSize(R.dimen.default_half_padding);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            RecyclerView.ViewHolder holder = parent.getChildViewHolder(view);
            if (holder instanceof SeasonViewHolder) {
                if (holder.getAdapterPosition() != 1) {
                    outRect.set(0, offset, 0, 0);
                }
            }
        }
    }

    static class ShowInformationViewHolder extends AbstractExpandableItemViewHolder {

        private static final String OPEN_P_TAG = "<p>";
        private static final String CLOSE_P_TAG = "</p>";

        private final TextView description;
        private final TextView duration;
        private final TextView status;
        private final TextView rating;
        private final RatingBar myRating;

        public ShowInformationViewHolder(@NonNull View itemView) {
            super(itemView);
            description = (TextView) itemView.findViewById(R.id.description);
            duration = (TextView) itemView.findViewById(R.id.duration);
            status = (TextView) itemView.findViewById(R.id.status);
            rating = (TextView) itemView.findViewById(R.id.rating);
            myRating = (RatingBar) itemView.findViewById(R.id.my_rating);
        }

        public void bind(@NonNull Show show, @Nullable UserShow userShow) {
            if (userShow != null) {
                myRating.setRating(show.getRating());
            }
            status.setText(show.getShowStatus().getStringId());
            CharSequence descriptionText = processDescription(show.getDescription());
            if (descriptionText.length() == 0) {
                description.setVisibility(View.GONE);
            } else {
                description.setVisibility(View.VISIBLE);
                description.setText(descriptionText);
            }
            Context context = itemView.getContext();
            duration.setText(context.getString(R.string.duration, show.getRuntime()));
            rating.setText(context.getString(R.string.rating, show.getRating()));
        }

        private static CharSequence processDescription(@NonNull String description) {
            description = description.trim();
            if (description.startsWith(OPEN_P_TAG)) {
                description = description.substring(OPEN_P_TAG.length());
            }
            if (description.endsWith(CLOSE_P_TAG)) {
                description = description.substring(0, description.length() - CLOSE_P_TAG.length());
            }
            return Html.fromHtml(description);
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

        private int number = -Integer.MAX_VALUE;
        private boolean expanded;
        private ObjectAnimator animator;

        public SeasonViewHolder(@NonNull View itemView, @NonNull OnSeasonCheckedChangeListener listener) {
            super(itemView);
            this.listener = listener;
            seasonNumber = (TextView) itemView.findViewById(R.id.season_number);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            arrow = itemView.findViewById(R.id.arrow);
        }

        public void bind(int number, boolean isChecked, boolean expanded) {
            seasonNumber.setText(itemView.getResources().getString(R.string.season_number, number));
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(isChecked);
            checkBox.setOnCheckedChangeListener((v, checked) -> listener.onSeasonCheckedChanged(number, checked));
            setExpanded(number, expanded);
        }

        public boolean canExpandOrCollapse(int x, int y) {
            Rect hitRect = new Rect();
            checkBox.getHitRect(hitRect);
            return !hitRect.contains(x, y);
        }

        private void setExpanded(int number, boolean expanded) {
            if (this.number != number) {
                cancelAnimation();
                arrow.setRotation(expanded ? ROTATION_EXPANDED : ROTATION_COLLAPSED);
            } else if (this.expanded != expanded) {
                cancelAnimation();
                animator = ObjectAnimator.ofFloat(arrow, View.ROTATION, expanded ? ROTATION_EXPANDED : ROTATION_COLLAPSED)
                        .setDuration(ANIMATION_DURATION);
                animator.start();
            }
            this.number = number;
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

        public SeriesViewHolder(@NonNull View itemView, @NonNull OnEpisodeCheckedChangeListener listener) {
            super(itemView);
            this.listener = listener;
            seriesNumber = (TextView) itemView.findViewById(R.id.series_number);
            seriesTitle = (TextView) itemView.findViewById(R.id.series_title);
            airDate = (TextView) itemView.findViewById(R.id.air_date);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            specialIcon = itemView.findViewById(R.id.special_icon);
        }

        public void bind(@NonNull Episode episode, boolean checked) {
            seriesTitle.setText(episode.getTitle());
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(checked);
            checkBox.setOnCheckedChangeListener((v, isChecked) -> listener.onEpisodeCheckedChanged(getAdapterPosition(), episode, isChecked));
            setSeriesNumber(episode);
            setAirDate(episode.getAirDate());
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