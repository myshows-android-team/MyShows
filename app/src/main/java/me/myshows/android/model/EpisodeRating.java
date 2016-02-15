package me.myshows.android.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Whiplash on 2/9/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EpisodeRating {

    private final int showId;
    private final int episodeId;
    private final int r1;
    private final int r2;
    private final int r3;
    private final int r4;
    private final int r5;
    private final int votes;
    private final float rating;

    @JsonCreator
    public EpisodeRating(@JsonProperty("showId") int showId, @JsonProperty("episodeId") int episodeId,
                         @JsonProperty("r1") int r1, @JsonProperty("r2") int r2,
                         @JsonProperty("r3") int r3, @JsonProperty("r4") int r4,
                         @JsonProperty("r5") int r5, @JsonProperty("votes") int votes,
                         @JsonProperty("rating") float rating) {
        this.showId = showId;
        this.episodeId = episodeId;
        this.r1 = r1;
        this.r2 = r2;
        this.r3 = r3;
        this.r4 = r4;
        this.r5 = r5;
        this.votes = votes;
        this.rating = rating;
    }

    @JsonProperty("showId")
    public int getShowId() {
        return showId;
    }

    @JsonProperty("episodeId")
    public int getEpisodeId() {
        return episodeId;
    }

    @JsonProperty("r1")
    public int getR1() {
        return r1;
    }

    @JsonProperty("r2")
    public int getR2() {
        return r2;
    }

    @JsonProperty("r3")
    public int getR3() {
        return r3;
    }

    @JsonProperty("r4")
    public int getR4() {
        return r4;
    }

    @JsonProperty("r5")
    public int getR5() {
        return r5;
    }

    @JsonProperty("votes")
    public int getVotes() {
        return votes;
    }

    @JsonProperty("rating")
    public float getRating() {
        return rating;
    }
}
