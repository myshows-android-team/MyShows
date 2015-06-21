package me.myshows.android.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Whiplash
 * @date 22.06.2015
 */
public class Episode {

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

    @JsonCreator
    public Episode(@JsonProperty("id") int id, @JsonProperty("title") String title,
                   @JsonProperty("sequenceNumber") int sequenceNumber, @JsonProperty("seasonNumber") int seasonNumber,
                   @JsonProperty("episodeNumber") int episodeNumber, @JsonProperty("airDate") String airDate,
                   @JsonProperty("shortName") String shortName, @JsonProperty("tvrageLink") String tvrageLink,
                   @JsonProperty("image") String image, @JsonProperty("productionNumber") String productionNumber) {
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
}
