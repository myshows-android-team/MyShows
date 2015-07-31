package me.myshows.android.model.persistent;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class PersistentShow extends RealmObject {

    @PrimaryKey
    private int id;
    private String title;
    private String ruTitle;
    private String status;
    private String country;
    private String started;
    private String ended;
    private int year;
    private int kinopoiskId;
    private int tvrageId;
    private int imdbId;
    private int voted;
    private float rating;
    private int runtime;
    private String image;
    private byte[] genres;
    private RealmList<PersistentEpisode> episodes;
    private int watching;
    private byte[] images;
    private String description;

    public PersistentShow() {
    }

    public PersistentShow(int id, String title, String ruTitle, String status, String country,
                          String started, String ended, int year, int kinopoiskId, int tvrageId,
                          int imdbId, int voted, float rating, int runtime, String image,
                          byte[] genres, RealmList<PersistentEpisode> episodes, int watching, byte[] images, String description) {
        this.id = id;
        this.title = title;
        this.ruTitle = ruTitle;
        this.status = status;
        this.country = country;
        this.started = started;
        this.ended = ended;
        this.year = year;
        this.kinopoiskId = kinopoiskId;
        this.tvrageId = tvrageId;
        this.imdbId = imdbId;
        this.voted = voted;
        this.rating = rating;
        this.runtime = runtime;
        this.image = image;
        this.genres = genres;
        this.episodes = episodes;
        this.watching = watching;
        this.images = images;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }

    public String getEnded() {
        return ended;
    }

    public void setEnded(String ended) {
        this.ended = ended;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getKinopoiskId() {
        return kinopoiskId;
    }

    public void setKinopoiskId(int kinopoiskId) {
        this.kinopoiskId = kinopoiskId;
    }

    public int getTvrageId() {
        return tvrageId;
    }

    public void setTvrageId(int tvrageId) {
        this.tvrageId = tvrageId;
    }

    public int getImdbId() {
        return imdbId;
    }

    public void setImdbId(int imdbId) {
        this.imdbId = imdbId;
    }

    public int getVoted() {
        return voted;
    }

    public void setVoted(int voted) {
        this.voted = voted;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public byte[] getGenres() {
        return genres;
    }

    public void setGenres(byte[] genres) {
        this.genres = genres;
    }

    public RealmList<PersistentEpisode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(RealmList<PersistentEpisode> episodes) {
        this.episodes = episodes;
    }

    public int getWatching() {
        return watching;
    }

    public void setWatching(int watching) {
        this.watching = watching;
    }

    public byte[] getImages() {
        return images;
    }

    public void setImages(byte[] images) {
        this.images = images;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
