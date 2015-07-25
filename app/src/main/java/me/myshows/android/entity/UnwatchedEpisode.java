package me.myshows.android.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Whiplash
 * @date 22.06.2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnwatchedEpisode {

    private final int episodeId;
    private final String title;
    private final int showId;
    private final int seasonNumber;
    private final int episodeNumber;
    private final String airDate;

    @JsonCreator
    public UnwatchedEpisode(@JsonProperty("episodeId") int episodeId, @JsonProperty("title") String title,
                            @JsonProperty("showId") int showId, @JsonProperty("seasonNumber") int seasonNumber,
                            @JsonProperty("episodeNumber") int episodeNumber, @JsonProperty("airDate") String airDate) {
        this.episodeId = episodeId;
        this.title = title;
        this.showId = showId;
        this.seasonNumber = seasonNumber;
        this.episodeNumber = episodeNumber;
        this.airDate = airDate;
    }

    @JsonProperty("episodeId")
    public int getEpisodeId() {
        return episodeId;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("showId")
    public int getShowId() {
        return showId;
    }

    @JsonProperty("seasonNumber")
    public int getSeasonNumber() {
        return seasonNumber;
    }

    @JsonProperty("episodeNumber")
    public int getEpisodeNumber() {
        return episodeNumber;
    }

    @JsonProperty("airDate")
    public String getAirDate() {
        return airDate;
    }
}
