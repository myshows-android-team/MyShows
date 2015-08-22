package me.myshows.android.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Whiplash on 09.08.2015.
 */
public class Feed {

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    private final Date date;
    private final List<UserFeed> feeds;

    public Feed(String rawDate, List<UserFeed> feeds) {
        this(parseRawDate(rawDate), feeds);
    }

    public Feed(Date date, List<UserFeed> feeds) {
        this.date = date;
        this.feeds = feeds;
    }

    private static Date parseRawDate(String rawDate) {
        try {
            return FORMATTER.parse(rawDate);
        } catch (ParseException e) {
            throw new RuntimeException("Incorrect date format in friend's feed", e);
        }
    }

    public Date getDate() {
        return date;
    }

    public List<UserFeed> getFeeds() {
        return feeds;
    }
}
