package me.myshows.android.model;

import java.util.List;

import me.myshows.android.utils.DateUtils;

/**
 * Created by Whiplash on 09.08.2015.
 */
public class Feed {

    private final long date;
    private final List<UserFeed> feeds;

    public Feed(String rawDate, List<UserFeed> feeds) {
        this(DateUtils.parseInMillis(rawDate), feeds);
    }

    public Feed(long date, List<UserFeed> feeds) {
        this.date = date;
        this.feeds = feeds;
    }

    public long getDate() {
        return date;
    }

    public List<UserFeed> getFeeds() {
        return feeds;
    }
}
