package me.myshows.android.model;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by Whiplash on 09.08.2015.
 */
public class Feed {

    private final DateTime date;
    private final List<UserFeed> feeds;

    public Feed(String rawDate, List<UserFeed> feeds) {
        this(new DateTime(rawDate), feeds);
    }

    public Feed(DateTime date, List<UserFeed> feeds) {
        this.date = date;
        this.feeds = feeds;
    }

    public DateTime getDate() {
        return date;
    }

    public List<UserFeed> getFeeds() {
        return feeds;
    }
}
