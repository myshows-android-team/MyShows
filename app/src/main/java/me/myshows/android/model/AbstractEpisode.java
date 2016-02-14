package me.myshows.android.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import me.myshows.android.utils.DateUtils;

/**
 * Created by warrior on 04.11.15.
 */
public abstract class AbstractEpisode implements Episode {

    private static final int SPECIAL_EPISODE_NUMBER = 0;

    private final long airDateInMillis;

    public AbstractEpisode(String airDate) {
        airDateInMillis = DateUtils.parseInMillis(airDate);
    }

    @JsonIgnore
    @Override
    public long getAirDateInMillis() {
        return airDateInMillis;
    }

    @JsonIgnore
    @Override
    public boolean isSpecial() {
        return getEpisodeNumber() == SPECIAL_EPISODE_NUMBER;
    }
}
