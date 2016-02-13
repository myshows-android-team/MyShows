package me.myshows.android.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Whiplash
 * @date 22.06.2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnwatchedEpisode extends AbstractEpisode {

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
        super(airDate);
        this.episodeId = episodeId;
        this.title = title;
        this.showId = showId;
        this.seasonNumber = seasonNumber;
        this.episodeNumber = episodeNumber;
        this.airDate = airDate;
    }

    @JsonProperty("episodeId")
    @Override
    public int getId() {
        return episodeId;
    }

    @JsonProperty("title")
    @Override
    public String getTitle() {
        return title;
    }

    @JsonProperty("seasonNumber")
    @Override
    public int getSeasonNumber() {
        return seasonNumber;
    }

    @JsonProperty("episodeNumber")
    @Override
    public int getEpisodeNumber() {
        return episodeNumber;
    }

    @JsonProperty("airDate")
    @Override
    public String getAirDate() {
        return airDate;
    }

    @JsonProperty("showId")
    public int getShowId() {
        return showId;
    }
}
