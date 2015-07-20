package me.myshows.android.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Whiplash
 * @date 20.06.2015
 */
public class Statistics {

    private final float watchedHours;
    private final float remainingHours;
    private final int watchedEpisodes;
    private final int remainingEpisodes;
    private final int totalEpisodes;
    private final float totalDays;
    private final float totalHours;
    private final float remainingDays;
    private final float watchedDays;

    @JsonCreator
    public Statistics(@JsonProperty("watchedHours") float watchedHours, @JsonProperty("remainingHours") float remainingHours,
                      @JsonProperty("watchedEpisodes") int watchedEpisodes, @JsonProperty("remainingEpisodes") int remainingEpisodes,
                      @JsonProperty("totalEpisodes") int totalEpisodes, @JsonProperty("totalDays") float totalDays,
                      @JsonProperty("totalHours") float totalHours, @JsonProperty("remainingDays") float remainingDays,
                      @JsonProperty("watchedDays") float watchedDays) {
        this.watchedHours = watchedHours;
        this.remainingHours = remainingHours;
        this.watchedEpisodes = watchedEpisodes;
        this.remainingEpisodes = remainingEpisodes;
        this.totalEpisodes = totalEpisodes;
        this.totalDays = totalDays;
        this.totalHours = totalHours;
        this.remainingDays = remainingDays;
        this.watchedDays = watchedDays;
    }

    @JsonProperty("watchedHours")
    public float getWatchedHours() {
        return watchedHours;
    }

    @JsonProperty("remainingHours")
    public float getRemainingHours() {
        return remainingHours;
    }

    @JsonProperty("watchedEpisodes")
    public int getWatchedEpisodes() {
        return watchedEpisodes;
    }

    @JsonProperty("remainingEpisodes")
    public int getRemainingEpisodes() {
        return remainingEpisodes;
    }

    @JsonProperty("totalEpisodes")
    public int getTotalEpisodes() {
        return totalEpisodes;
    }

    @JsonProperty("totalDays")
    public float getTotalDays() {
        return totalDays;
    }

    @JsonProperty("totalHours")
    public float getTotalHours() {
        return totalHours;
    }

    @JsonProperty("remainingDays")
    public float getRemainingDays() {
        return remainingDays;
    }

    @JsonProperty("watchedDays")
    public float getWatchedDays() {
        return watchedDays;
    }
}
