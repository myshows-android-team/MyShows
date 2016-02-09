package me.myshows.android.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import me.myshows.android.utils.DateUtils;

/**
 * @author Whiplash
 * @date 22.06.2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Episode {

    private static final int SPECIAL_EPISODE_NUMBER = 0;

    private final int id;
    private final String title;
    private final int seasonNumber;
    private final int episodeNumber;
    private final String airDate;
    private final String shortName;
    private final String tvrageLink;
    private final String image;
    private final String productionNumber;
    private final int sequenceNumber;
    private final long airDateInMillis;

    // episode information request fields
    private final int totalWatched;
    private final RatingEpisode rating;

    @JsonCreator
    public Episode(@JsonProperty("id") int id, @JsonProperty("title") String title,
                   @JsonProperty("sequenceNumber") int sequenceNumber, @JsonProperty("seasonNumber") int seasonNumber,
                   @JsonProperty("episodeNumber") int episodeNumber, @JsonProperty("airDate") String airDate,
                   @JsonProperty("shortName") String shortName, @JsonProperty("tvrageLink") String tvrageLink,
                   @JsonProperty("image") String image, @JsonProperty("productionNumber") String productionNumber,
                   @JsonProperty("totalWatched") int totalWatched, @JsonProperty("rating") RatingEpisode rating) {
        this.id = id;
        this.title = title;
        this.seasonNumber = seasonNumber;
        this.episodeNumber = episodeNumber;
        this.airDate = airDate;
        this.shortName = shortName;
        this.tvrageLink = tvrageLink;
        this.image = image;
        this.productionNumber = productionNumber;
        this.sequenceNumber = sequenceNumber;
        this.airDateInMillis = DateUtils.parseInMillis(airDate);
        this.totalWatched = totalWatched;
        this.rating = rating;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
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

    @JsonProperty("shortName")
    public String getShortName() {
        return shortName;
    }

    @JsonProperty("tvrageLink")
    public String getTvrageLink() {
        return tvrageLink;
    }

    @JsonProperty("image")
    public String getImage() {
        return image;
    }

    @JsonProperty("productionNumber")
    public String getProductionNumber() {
        return productionNumber;
    }

    @JsonProperty("sequenceNumber")
    public int getSequenceNumber() {
        return sequenceNumber;
    }

    @JsonIgnore
    public long getAirDateInMillis() {
        return airDateInMillis;
    }

    @JsonProperty("totalWatched")
    public int getTotalWatched() {
        return totalWatched;
    }

    @JsonProperty("rating")
    public RatingEpisode getRating() {
        return rating;
    }

    @JsonIgnore
    public boolean isSpecial() {
        return episodeNumber == SPECIAL_EPISODE_NUMBER;
    }
}
