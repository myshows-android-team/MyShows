package me.myshows.android.model;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by Whiplash on 09.08.2015.
 */
public class Feed extends ArrayList<UserFeed> {

    private final DateTime date;

    public Feed(String rawDate) {
        this(new DateTime(rawDate));
    }

    public Feed(DateTime date) {
        super();
        this.date = date;
    }

    public DateTime getDate() {
        return date;
    }
}
