package me.myshows.android.model.persistent;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class PersistentShowEpisode extends RealmObject {

    @PrimaryKey
    private int id;
    private String title;
    private int seasonNumber;
    private int episodeNumber;
    private String airDate;
    private String shortName;
    private String tvrageLink;
    private String image;
    private String productionNumber;
    private int sequenceNumber;

    private int totalWatched;
    private byte[] rating;

    public PersistentShowEpisode() {
    }

    public PersistentShowEpisode(int id, String title, int seasonNumber, int episodeNumber, String airDate, String shortName, String tvrageLink, String image, String productionNumber, int sequenceNumber,
                                 int totalWatched, byte[] rating) {
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

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(int episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public String getAirDate() {
        return airDate;
    }

    public void setAirDate(String airDate) {
        this.airDate = airDate;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getTvrageLink() {
        return tvrageLink;
    }

    public void setTvrageLink(String tvrageLink) {
        this.tvrageLink = tvrageLink;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProductionNumber() {
        return productionNumber;
    }

    public void setProductionNumber(String productionNumber) {
        this.productionNumber = productionNumber;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public int getTotalWatched() {
        return totalWatched;
    }

    public void setTotalWatched(int totalWatched) {
        this.totalWatched = totalWatched;
    }

    public byte[] getRating() {
        return rating;
    }

    public void setRating(byte[] rating) {
        this.rating = rating;
    }
}
