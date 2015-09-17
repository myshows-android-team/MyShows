package me.myshows.android.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import me.myshows.android.model.glide.ShowImage;

/**
 * @author Whiplash
 * @date 20.06.2015
 */
@Parcel(Parcel.Serialization.BEAN)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserShow implements ShowImage {

    private final int showId;
    private final String title;
    private final String ruTitle;
    private final int runtime;
    private final ShowStatus showStatus;
    private final WatchStatus watchStatus;
    private final int watchedEpisodes;
    private final int totalEpisodes;
    private final int rating;
    private final String image;

    @ParcelConstructor
    @JsonCreator
    public UserShow(@JsonProperty("showId") int showId, @JsonProperty("title") String title,
                    @JsonProperty("ruTitle") String ruTitle, @JsonProperty("runtime") int runtime,
                    @JsonProperty("showStatus") ShowStatus showStatus, @JsonProperty("watchStatus") WatchStatus watchStatus,
                    @JsonProperty("watchedEpisodes") int watchedEpisodes, @JsonProperty("totalEpisodes") int totalEpisodes,
                    @JsonProperty("nav_rating") int rating, @JsonProperty("image") String image) {
        this.showId = showId;
        this.title = title;
        this.ruTitle = ruTitle;
        this.runtime = runtime;
        this.showStatus = showStatus;
        this.watchStatus = watchStatus;
        this.watchedEpisodes = watchedEpisodes;
        this.totalEpisodes = totalEpisodes;
        this.rating = rating;
        this.image = image;
    }

    @JsonProperty("showId")
    public int getShowId() {
        return showId;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("ruTitle")
    public String getRuTitle() {
        return ruTitle;
    }

    @JsonProperty("runtime")
    public int getRuntime() {
        return runtime;
    }

    @JsonProperty("showStatus")
    public ShowStatus getShowStatus() {
        return showStatus;
    }

    @JsonProperty("watchStatus")
    public WatchStatus getWatchStatus() {
        return watchStatus;
    }

    @JsonProperty("watchedEpisodes")
    public int getWatchedEpisodes() {
        return watchedEpisodes;
    }

    @JsonProperty("totalEpisodes")
    public int getTotalEpisodes() {
        return totalEpisodes;
    }

    @JsonProperty("nav_rating")
    public int getRating() {
        return rating;
    }

    @JsonProperty("image")
    @Override
    public String getImage() {
        return image;
    }
}
