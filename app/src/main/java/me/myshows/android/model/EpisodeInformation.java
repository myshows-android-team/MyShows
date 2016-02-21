package me.myshows.android.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.util.Map;

import me.myshows.android.model.serialization.JsonMarshaller;
import me.myshows.android.model.serialization.Marshaller;

/**
 * @author Whiplash
 * @date 22.06.2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EpisodeInformation extends AbstractEpisode {

    private static final Marshaller MARSHALLER = new JsonMarshaller();

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
    private final int totalWatched;
    private final EpisodeRating rating;
    private final int showId;

    public EpisodeInformation(int id, String title,
                              int sequenceNumber, int seasonNumber,
                              int episodeNumber, String airDate,
                              String shortName, String tvrageLink,
                              String image, String productionNumber,
                              int totalWatched, EpisodeRating rating,
                              int showId) {
        super(airDate);
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
        this.totalWatched = totalWatched;
        this.rating = rating;
        this.showId = showId;
    }

    @JsonCreator
    public EpisodeInformation(@JsonProperty("id") int id, @JsonProperty("title") String title,
                              @JsonProperty("sequenceNumber") int sequenceNumber, @JsonProperty("seasonNumber") int seasonNumber,
                              @JsonProperty("episodeNumber") int episodeNumber, @JsonProperty("airDate") String airDate,
                              @JsonProperty("shortName") String shortName, @JsonProperty("tvrageLink") String tvrageLink,
                              @JsonProperty("image") String image, @JsonProperty("productionNumber") String productionNumber,
                              @JsonProperty("totalWatched") int totalWatched, @JsonProperty("rating") Object rating,
                              @JsonProperty("showId") int showId) {
        super(airDate);
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
        this.totalWatched = totalWatched;
        this.rating = processRating(rating);
        this.showId = showId;
    }

    private EpisodeRating processRating(Object rating) {
        if (rating instanceof Map) {
            try {
                byte[] data = MARSHALLER.serialize(rating);
                return MARSHALLER.deserialize(data, EpisodeRating.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @JsonProperty("id")
    @Override
    public int getId() {
        return id;
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

    @JsonProperty("totalWatched")
    public int getTotalWatched() {
        return totalWatched;
    }

    @JsonProperty("rating")
    public EpisodeRating getRating() {
        return rating;
    }

    @JsonProperty("showId")
    public int getShowId() {
        return showId;
    }
}
