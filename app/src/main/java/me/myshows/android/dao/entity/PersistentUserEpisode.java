package me.myshows.android.dao.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class PersistentUserEpisode extends RealmObject {

    @PrimaryKey
    private int id;
    private String watchDate;
    private int rating;

    public PersistentUserEpisode() {
    }

    public PersistentUserEpisode(int id, String watchDate, int rating) {
        this.id = id;
        this.watchDate = watchDate;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWatchDate() {
        return watchDate;
    }

    public void setWatchDate(String watchDate) {
        this.watchDate = watchDate;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
