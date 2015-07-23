package me.myshows.android.dao.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class PersistentUserShow extends RealmObject {

    @PrimaryKey
    private int showId;
    private String title;
    private String ruTitle;
    private int runtime;
    private String showStatus;
    private String watchStatus;
    private int watchedEpisodes;
    private int totalEpisodes;
    private int rating;
    private String image;

    public PersistentUserShow() {
    }

    public PersistentUserShow(int showId, String title, String ruTitle, int runtime,
                              String showStatus, String watchStatus, int watchedEpisodes,
                              int totalEpisodes, int rating, String image) {
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

    public int getShowId() {
        return showId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRuTitle() {
        return ruTitle;
    }

    public void setRuTitle(String ruTitle) {
        this.ruTitle = ruTitle;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public String getShowStatus() {
        return showStatus;
    }

    public void setShowStatus(String showStatus) {
        this.showStatus = showStatus;
    }

    public String getWatchStatus() {
        return watchStatus;
    }

    public void setWatchStatus(String watchStatus) {
        this.watchStatus = watchStatus;
    }

    public int getWatchedEpisodes() {
        return watchedEpisodes;
    }

    public void setWatchedEpisodes(int watchedEpisodes) {
        this.watchedEpisodes = watchedEpisodes;
    }

    public int getTotalEpisodes() {
        return totalEpisodes;
    }

    public void setTotalEpisodes(int totalEpisodes) {
        this.totalEpisodes = totalEpisodes;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
