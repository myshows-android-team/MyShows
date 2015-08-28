package me.myshows.android.model;

import java.util.List;

/**
 * Created by warrior on 28.08.15.
 */
public class UserShowEpisodes {

    private final int showId;
    private final List<UserEpisode> episodes;

    public UserShowEpisodes(int showId, List<UserEpisode> episodes) {
        this.showId = showId;
        this.episodes = episodes;
    }

    public int getShowId() {
        return showId;
    }

    public List<UserEpisode> getEpisodes() {
        return episodes;
    }
}
