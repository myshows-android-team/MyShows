package me.myshows.android.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import rx.Observable;

/**
 * @author Whiplash
 * @date 20.06.2015
 */
@Parcel(Parcel.Serialization.BEAN)
public class UserShow {

    private final int showId;
    private final String title;
    private final String ruTitle;
    private final int runtime;
    private final String showStatus;
    private final String watchStatus;
    private final int watchedEpisodes;
    private final int totalEpisodes;
    private final int rating;
    private final String image;

    private String cachedImageUrl;

    @ParcelConstructor
    @JsonCreator
    public UserShow(@JsonProperty("showId") int showId, @JsonProperty("title") String title,
                    @JsonProperty("ruTitle") String ruTitle, @JsonProperty("runtime") int runtime,
                    @JsonProperty("showStatus") String showStatus, @JsonProperty("watchStatus") String watchStatus,
                    @JsonProperty("watchedEpisodes") int watchedEpisodes, @JsonProperty("totalEpisodes") int totalEpisodes,
                    @JsonProperty("rating") int rating, @JsonProperty("image") String image) {
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
    public String getShowStatus() {
        return showStatus;
    }

    @JsonProperty("watchStatus")
    public String getWatchStatus() {
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

    @JsonProperty("rating")
    public int getRating() {
        return rating;
    }

    @JsonProperty("image")
    public String getImage() {
        return image;
    }

    public Observable<String> requestImageUrl() {
        if (cachedImageUrl != null) {
            return Observable.just(cachedImageUrl);
        }
        return ImageRequester.requestImageUrl(showId)
                // TODO: remove this stuff
                .map(s -> {
                    cachedImageUrl = s;
                    return s;
                });
    }

    // just for Parceler lib
    public String getCachedImageUrl() {
        return cachedImageUrl;
    }

    // just for Parceler lib
    public void setCachedImageUrl(String cachedImageUrl) {
        this.cachedImageUrl = cachedImageUrl;
    }
}
