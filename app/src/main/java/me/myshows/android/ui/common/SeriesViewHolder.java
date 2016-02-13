package me.myshows.android.ui.common;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;

import me.myshows.android.R;
import me.myshows.android.model.Episode;

/**
 * Created by warrior on 01.11.15.
 */
public class SeriesViewHolder<T extends Episode> extends AbstractExpandableItemViewHolder {

    public interface OnEpisodeCheckedChangeListener<T extends Episode> {
        void onEpisodeCheckedChanged(@NonNull Season<T> season, int position, int adapterPosition);
    }

    private final OnEpisodeCheckedChangeListener<T> listener;

    private final TextView seriesNumber;
    private final TextView seriesTitle;
    private final TextView airDate;
    private final CheckBox checkBox;
    private final View specialIcon;
    private final View divider;

    private boolean isLast;

    public SeriesViewHolder(@NonNull View itemView, @NonNull OnEpisodeCheckedChangeListener<T> listener) {
        super(itemView);
        this.listener = listener;
        seriesNumber = (TextView) itemView.findViewById(R.id.series_number);
        seriesTitle = (TextView) itemView.findViewById(R.id.series_title);
        airDate = (TextView) itemView.findViewById(R.id.air_date);
        checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        specialIcon = itemView.findViewById(R.id.special_icon);
        divider = itemView.findViewById(R.id.divider);
    }

    public void bind(@NonNull Season<T> season, int position) {
        T episode = season.get(position);
        isLast = season.size() - 1 == position;
        seriesTitle.setText(episode.getTitle());
        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(season.isEpisodeChecked(position));
        checkBox.setOnCheckedChangeListener((v, checked) -> {
            season.setEpisodeChecked(position, checked);
            listener.onEpisodeCheckedChanged(season, position, getAdapterPosition());
        });
        divider.setVisibility(isLast ? View.GONE : View.VISIBLE);
        setSeriesNumber(episode);
        setAirDate(episode.getAirDate());
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
