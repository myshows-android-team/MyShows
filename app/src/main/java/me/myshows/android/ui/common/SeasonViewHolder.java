package me.myshows.android.ui.common;

import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;

import me.myshows.android.R;
import me.myshows.android.model.Episode;

/**
 * Created by warrior on 01.11.15.
 */
public class SeasonViewHolder<T extends Episode> extends AbstractExpandableItemViewHolder {

    public interface OnSeasonCheckedChangeListener<T extends Episode> {
        void onSeasonCheckedChanged(@NonNull Season<T> season);
    }

    private static final float ROTATION_COLLAPSED = 0;
    private static final float ROTATION_EXPANDED = 90;

    private static final long ANIMATION_DURATION = 250;

    private final OnSeasonCheckedChangeListener<T> listener;

    private final TextView seasonNumber;
    private final CheckBox checkBox;
    private final View arrow;

    private Season<T> season;
    private boolean expanded;
    private ObjectAnimator animator;

    public SeasonViewHolder(@NonNull View itemView, @NonNull OnSeasonCheckedChangeListener<T> listener) {
        super(itemView);
        this.listener = listener;
        seasonNumber = (TextView) itemView.findViewById(R.id.season_number);
        checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        arrow = itemView.findViewById(R.id.arrow);
    }

    public void bind(@NonNull Season<T> season) {
        seasonNumber.setText(itemView.getResources().getString(R.string.season_number, season.getSeasonNumber()));
        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(season.isChecked());
        checkBox.setOnCheckedChangeListener((v, checked) -> {
            season.setChecked(checked);
            listener.onSeasonCheckedChanged(season);
        });
        setExpanded(season);
    }

    public boolean canExpandOrCollapse(int x, int y) {
        Rect hitRect = new Rect();
        checkBox.getHitRect(hitRect);
        return !hitRect.contains(x, y);
    }

    private void setExpanded(@NonNull Season<T> season) {
        if (this.season != season) {
            cancelAnimation();
            arrow.setRotation(season.isExpanded() ? ROTATION_EXPANDED : ROTATION_COLLAPSED);
        } else if (season.isExpanded() != expanded) {
            cancelAnimation();
            animator = ObjectAnimator.ofFloat(arrow, View.ROTATION, season.isExpanded() ? ROTATION_EXPANDED : ROTATION_COLLAPSED)
                    .setDuration(ANIMATION_DURATION);
            animator.start();
        }
        this.season = season;
        this.expanded = season.isExpanded();
    }

    private void cancelAnimation() {
        if (animator != null) {
            animator.cancel();
        }
    }
}
