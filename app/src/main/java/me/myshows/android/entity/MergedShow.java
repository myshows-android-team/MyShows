package me.myshows.android.entity;

import java.util.Map;

public class MergedShow {

    private static final String IMAGE_URL = "http://media.myshows.me/shows/%s/%s/%s/%s";
    private static final String NORMAL = "normal";
    private static final String SMALL = "small";

    private final int id;
    private final String title;
    private final String ruTitle;
    private final String status;
    private final String country;
    private final String started;
    private final String ended;
    private final int year;
    private final int kinopoiskId;
    private final int tvrageId;
    private final int imdbId;
    private final int voted;
    private final float rating;
    private final int runtime;
    private final String image;
    private final int[] genres;
    private final Map<String, Episode> episodes;
    private final int watching;
    private final String[] images;
    private final String description;
    private final String watchStatus;
    private final int watchedEpisodes;
    private final int totalEpisodes;
    private final int userRating;

    public MergedShow(Show show, UserShow userShow) {
        this.id = show != null ? show.getId() : userShow.getShowId();
        this.title = show != null ? show.getTitle() : userShow.getTitle();
        this.ruTitle = show != null ? show.getRuTitle() : userShow.getRuTitle();
        this.status = show != null ? show.getStatus() : userShow.getShowStatus();
        this.country = show.getCountry();
        this.started = show.getStarted();
        this.ended = show.getEnded();
        this.year = show.getYear();
        this.kinopoiskId = show.getKinopoiskId();
        this.tvrageId = show.getTvrageId();
        this.imdbId = show.getImdbId();
        this.voted = show.getVoted();
        this.rating = show.getRating();
        this.runtime = show != null ? show.getRuntime() : userShow.getRuntime();
        this.image = show != null ? show.getImage() : userShow.getImage();
        this.genres = show.getGenres();
        this.episodes = show.getEpisodes();
        this.watching = show.getWatching();
        this.images = show.getImages();
        this.description = show.getDescription();
        this.watchStatus = userShow.getWatchStatus();
        this.watchedEpisodes = userShow.getWatchedEpisodes();
        this.totalEpisodes = userShow.getTotalEpisodes();
        this.userRating = userShow.getRating();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getRuTitle() {
        return ruTitle;
    }

    public String getStatus() {
        return status;
    }

    public String getCountry() {
        return country;
    }

    public String getStarted() {
        return started;
    }

    public String getEnded() {
        return ended;
    }

    public int getYear() {
        return year;
    }

    public int getKinopoiskId() {
        return kinopoiskId;
    }

    public int getTvrageId() {
        return tvrageId;
    }

    public int getImdbId() {
        return imdbId;
    }

    public int getVoted() {
        return voted;
    }

    public float getRating() {
        return rating;
    }

    public int getRuntime() {
        return runtime;
    }

    public String getImage() {
        return image;
    }

    public int[] getGenres() {
        return genres;
    }

    public Map<String, Episode> getEpisodes() {
        return episodes;
    }

    public int getWatching() {
        return watching;
    }

    public String[] getImages() {
        return images;
    }

    public String getDescription() {
        return description;
    }

    public String getWatchStatus() {
        return watchStatus;
    }

    public int getWatchedEpisodes() {
        return watchedEpisodes;
    }

    public int getTotalEpisodes() {
        return totalEpisodes;
    }

    public int getUserRating() {
        return userRating;
    }

    public String getNormalImage() {
        return getImageUrl(NORMAL);
    }

    public String getSmallImage() {
        return getImageUrl(SMALL);
    }

    private String getImageUrl(String size) {
        if (images == null || images.length == 0) {
            return image;
        }
        String hash = images[0];
        return String.format(IMAGE_URL, size, hash.substring(0, 1), hash.substring(0, 2), hash);
    }
}
