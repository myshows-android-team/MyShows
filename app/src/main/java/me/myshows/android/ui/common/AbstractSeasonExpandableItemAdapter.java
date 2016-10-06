package me.myshows.android.ui.common;

import android.support.annotation.NonNull;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;

import me.myshows.android.utils.SparseSet;

import static android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * Created by warrior on 27.02.16.
 */
public abstract class AbstractSeasonExpandableItemAdapter<GVH extends ViewHolder, CVH extends ViewHolder> extends AbstractExpandableItemAdapter<GVH, CVH>
        implements Season.OnEpisodeCheckChanges {

    public interface OnUnsavedChangesListener {
        void onChange(boolean hasUnsavedChanges);
    }

    private final SparseSet unsavedCheckedEpisodes = new SparseSet();
    private final SparseSet unsavedUncheckedEpisodes = new SparseSet();

    private final OnUnsavedChangesListener listener;

    public AbstractSeasonExpandableItemAdapter(@NonNull OnUnsavedChangesListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCheckChange(int episodeId, boolean checked) {
        boolean hasUnsavedChangesBefore = hasUnsavedChanges();
        if (checked) {
            changeEpisodeCheckedState(unsavedUncheckedEpisodes, unsavedCheckedEpisodes, episodeId);
        } else {
            changeEpisodeCheckedState(unsavedCheckedEpisodes, unsavedUncheckedEpisodes, episodeId);
        }
        boolean hasUnsavedChangesAfter = hasUnsavedChanges();
        if (hasUnsavedChangesBefore != hasUnsavedChangesAfter) {
            listener.onChange(hasUnsavedChangesAfter);
        }
    }

    public boolean hasUnsavedChanges() {
        return !unsavedCheckedEpisodes.isEmpty() || !unsavedUncheckedEpisodes.isEmpty();
    }

    public int[] getUnsavedCheckedEpisodes() {
        return episodeSparseSetToIdArray(unsavedCheckedEpisodes);
    }

    public int[] getUnsavedUncheckedEpisodes() {
        return episodeSparseSetToIdArray(unsavedUncheckedEpisodes);
    }

    public void onSaveChanges() {
        boolean hasUnsavedChangesBefore = hasUnsavedChanges();
        unsavedCheckedEpisodes.clear();
        unsavedUncheckedEpisodes.clear();
        boolean hasUnsavedChangesAfter = hasUnsavedChanges();
        if (hasUnsavedChangesBefore != hasUnsavedChangesAfter) {
            listener.onChange(hasUnsavedChangesAfter);
        }
    }

    private static int[] episodeSparseSetToIdArray(@NonNull SparseSet episodes) {
        int[] ids = new int[episodes.size()];
        for (int i = 0; i < episodes.size(); i++) {
            ids[i] = episodes.valueAt(i);
        }
        return ids;
    }

    private static void changeEpisodeCheckedState(@NonNull SparseSet toRemove, @NonNull SparseSet toAdd, int episodeId) {
        if (toRemove.contains(episodeId)) {
            toRemove.remove(episodeId);
        } else {
            toAdd.add(episodeId);
        }
    }
}
