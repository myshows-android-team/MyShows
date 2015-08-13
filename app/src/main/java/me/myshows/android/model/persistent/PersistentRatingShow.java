package me.myshows.android.model.persistent;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by warrior on 09.08.15.
 */
@RealmClass
public class PersistentRatingShow extends RealmObject {

    @PrimaryKey
    private int id;
    private String title;
    private String ruTitle;
    private String showStatus;
    private int year;
    private float rating;
    private int watching;
    private String image;
    private int place;

    public PersistentRatingShow() {
    }

    public PersistentRatingShow(int id, String title, String ruTitle, String showStatus,
                                int year, float rating, int watching, String image, int place) {
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

    public String getShowStatus() {
        return showStatus;
    }

    public void setShowStatus(String showStatus) {
        this.showStatus = showStatus;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getWatching() {
        return watching;
    }

    public void setWatching(int watching) {
        this.watching = watching;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }
}
