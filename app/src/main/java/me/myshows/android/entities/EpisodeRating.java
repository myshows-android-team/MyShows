package me.myshows.android.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Whiplash
 * @date 21.06.2015
 */
public class EpisodeRating {

    private final int id;
    private final String watchDate;
    private final int rating;

    @JsonCreator
    public EpisodeRating(@JsonProperty("id") int id, @JsonProperty("watchDate") String watchDate,
                         @JsonProperty("rating") int rating) {
        this.id = id;
        this.watchDate = watchDate;
        this.rating = rating;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("watchDate")
    public String getWatchDate() {
        return watchDate;
    }

    @JsonProperty("rating")
    public int getRating() {
        return rating;
    }
}
