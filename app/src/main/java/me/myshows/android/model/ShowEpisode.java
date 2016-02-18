package me.myshows.android.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Whiplash
 * @date 22.06.2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShowEpisode extends AbstractEpisode {

    private final int id;
    private final int showId;
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
    public ShowEpisode(@JsonProperty("id") int id, @JsonProperty("showId") int showId, @JsonProperty("title") String title,
                       @JsonProperty("sequenceNumber") int sequenceNumber, @JsonProperty("seasonNumber") int seasonNumber,
                       @JsonProperty("episodeNumber") int episodeNumber, @JsonProperty("airDate") String airDate,
                       @JsonProperty("shortName") String shortName, @JsonProperty("tvrageLink") String tvrageLink,
                       @JsonProperty("image") String image, @JsonProperty("productionNumber") String productionNumber) {
        super(airDate);
        this.id = id;
        this.showId = showId;
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
    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getShowId() {
        return showId;
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
