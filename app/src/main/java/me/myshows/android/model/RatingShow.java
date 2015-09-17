package me.myshows.android.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import me.myshows.android.model.glide.ShowImage;

/**
 * Created by warrior on 09.08.15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RatingShow implements ShowImage {

    private final int id;
    private final String title;
    private final String ruTitle;
    private final ShowStatus showStatus;
    private final int year;
    private final float rating;
    private final int watching;
    private final String image;
    private final int place;

    @JsonCreator
    public RatingShow(@JsonProperty("id") int id, @JsonProperty("title") String title,
                      @JsonProperty("ruTitle") String ruTitle, @JsonProperty("status") ShowStatus showStatus,
                      @JsonProperty("year") int year, @JsonProperty("nav_rating") float rating,
                      @JsonProperty("watching") int watching, @JsonProperty("image") String image,
                      @JsonProperty("place") int place) {
        this.id = id;
        this.title = title;
        this.ruTitle = ruTitle;
        this.showStatus = showStatus;
        this.year = year;
        this.rating = rating;
        this.watching = watching;
        this.image = image;
        this.place = place;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("ruTitle")
    public String getRuTitle() {
        return ruTitle;
    }

    @JsonProperty("status")
    public ShowStatus getShowStatus() {
        return showStatus;
    }

    @JsonProperty("year")
    public int getYear() {
        return year;
    }

    @JsonProperty("nav_rating")
    public float getRating() {
        return rating;
    }

    @JsonProperty("watching")
    public int getWatching() {
        return watching;
    }

    @JsonProperty("image")
    @Override
    public String getImage() {
        return image;
    }

    @JsonProperty("place")
    public int getPlace() {
        return place;
    }
}
