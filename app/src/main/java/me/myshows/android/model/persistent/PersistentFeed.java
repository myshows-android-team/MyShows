package me.myshows.android.model.persistent;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by Whiplash on 09.08.2015.
 */
@RealmClass
public class PersistentFeed extends RealmObject {

    @PrimaryKey
    private long date;
    private byte[] feeds;

    public PersistentFeed() {
    }

    public PersistentFeed(long date, byte[] feeds) {
        this.date = date;
        this.feeds = feeds;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public byte[] getFeeds() {
        return feeds;
    }

    public void setFeeds(byte[] feeds) {
        this.feeds = feeds;
    }
}
