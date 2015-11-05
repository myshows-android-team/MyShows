package me.myshows.android.ui.common;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.myshows.android.model.Episode;
import me.myshows.android.utils.Numbers;
import me.myshows.android.utils.SparseSet;

/**
 * Created by warrior on 04.11.15.
 */
public class Season<T extends Episode> {

    private final int seasonNumber;
    private final List<T> episodes;
    private final int specialEpisodesCount;

    private SparseSet checkedEpisodes = new SparseSet();
    private SparseSet checkedSpecialEpisodes = new SparseSet();

    private boolean expanded;

    public Season(int seasonNumber, @NonNull List<T> episodes) {
        this.seasonNumber = seasonNumber;
        this.episodes = episodes;
        this.specialEpisodesCount = countSpecialEpisodes(episodes);
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public T get(int i) {
        return episodes.get(i);
    }

    public int size() {
        return episodes.size();
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public void setCheckedEpisodes(@NonNull SparseSet checkedEpisodes) {
        this.checkedEpisodes = checkedEpisodes;
    }

    public void setCheckedSpecialEpisodes(@NonNull SparseSet checkedSpecialEpisodes) {
        this.checkedSpecialEpisodes = checkedSpecialEpisodes;
    }

    public boolean isChecked() {
        return checkedEpisodes.size() + specialEpisodesCount == episodes.size();
    }

    public void setChecked(boolean checked) {
        if (checked) {
            for (Episode episode : episodes) {
                if (!episode.isSpecial()) {
                    checkedEpisodes.add(episode.getId());
                }
            }
        } else {
            checkedEpisodes.clear();
        }
    }

    public boolean isEpisodeChecked(int i) {
        Episode episode = episodes.get(i);
        if (episode.isSpecial()) {
            return checkedSpecialEpisodes.contains(episode.getId());
        } else {
            return checkedEpisodes.contains(episode.getId());
        }
    }

    public void setEpisodeChecked(int i, boolean checked) {
        Episode episode = episodes.get(i);
        if (episode.isSpecial()) {
            if (checked) {
                checkedSpecialEpisodes.add(episode.getId());
            } else {
                checkedSpecialEpisodes.remove(episode.getId());
            }
        } else {
            if (checked) {
                checkedEpisodes.add(episode.getId());
            } else {
                checkedEpisodes.remove(episode.getId());
            }
        }
    }

    public static <T extends Episode> List<Season<T>> splitToSeasons(@NonNull Collection<T> allEpisodes) {
        return splitToSeasons(allEpisodes, (e1, e2) -> {
            int res = Numbers.compare(e1.getAirDateInMillis(), e2.getAirDateInMillis());
            return res != 0 ? res : Numbers.compare(e1.getEpisodeNumber(), e2.getEpisodeNumber());
        });
    }

    public static <T extends Episode> List<Season<T>> splitToSeasons(@NonNull Collection<T> allEpisodes, Comparator<T> episodeComparator) {
        if (allEpisodes.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> episodes = new ArrayList<>(allEpisodes);
        Collections.sort(episodes, (e1, e2) -> {
            int cmp = Numbers.compare(e1.getSeasonNumber(), e2.getSeasonNumber());
            return cmp != 0 ? cmp : episodeComparator.compare(e1, e2);
        });

        List<Season<T>> seasons = new ArrayList<>();
        List<T> seasonEpisodes = new ArrayList<>();
        int seasonNumber = episodes.get(0).getSeasonNumber();
        for (T episode : episodes) {
            if (episode.getSeasonNumber() != seasonNumber) {
                seasons.add(new Season<>(seasonNumber, seasonEpisodes));
                seasonNumber = episode.getSeasonNumber();
                seasonEpisodes = new ArrayList<>();
            }
            seasonEpisodes.add(episode);
        }
        seasons.add(new Season<>(seasonNumber, seasonEpisodes));
        return seasons;
    }

    private static <T extends Episode> int countSpecialEpisodes(@NonNull List<T> episodes) {
        int count = 0;
        for (Episode episode : episodes) {
            if (episode.isSpecial()) {
                count++;
            }
        }
        return count;
    }
}
