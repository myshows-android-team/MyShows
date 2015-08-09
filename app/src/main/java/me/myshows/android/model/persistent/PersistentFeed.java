package me.myshows.android.model.persistent;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Created by Whiplash on 09.08.2015.
 */
@RealmClass
public class PersistentFeed extends RealmObject {

    private Date date;
    private byte[] feeds;

    public PersistentFeed() {
    }

    public PersistentFeed(Date date, byte[] feeds) {
        this.date = date;
        this.feeds = feeds;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public byte[] getFeeds() {
        return feeds;
    }

    public void setFeeds(byte[] feeds) {
        this.feeds = feeds;
    }
}
