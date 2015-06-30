package me.myshows.android.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * @author Whiplash
 * @date 20.06.2015
 */
public class UserShow {

    public static final String REQUEST_URL = "http://myshows.me/view/%d/";
    public static final String SELECT_QUERY = "div.presentBlockImg";
    public static final String ATTR_NAME = "style";
    public static final String URL_PREFIX = "http:";
    public static final String URL_SUFFIX = ")";

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
        return Observable.just(showId)
                .subscribeOn(Schedulers.newThread())
                .flatMap(id -> {
                    try {
                        // TODO: persist url before return
                        return Observable.just(getImageUrl(id));
                    } catch (IOException e) {
                        return Observable.error(e);
                    }
                });
    }

    private static String getImageUrl(int id) throws IOException {
        Document doc = Jsoup.connect(String.format(REQUEST_URL, id)).get();
        Elements imageDiv = doc.select(SELECT_QUERY);
        String attrValue = imageDiv.attr(ATTR_NAME);
        return attrValue.substring(attrValue.indexOf(URL_PREFIX), attrValue.indexOf(URL_SUFFIX));
    }
}
