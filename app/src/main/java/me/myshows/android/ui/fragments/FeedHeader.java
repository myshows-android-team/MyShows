package me.myshows.android.ui.fragments;

import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import me.myshows.android.R;

/**
 * Created by Whiplash on 15.08.2015.
 */
class FeedHeader {

    private static final DateTimeFormatter MONTH_DATE_FORMAT = DateTimeFormat.forPattern("MMMM");

    private static final int TODAY = R.string.today;
    private static final int YESTERDAY = R.string.yesterday;
    private static final int AT_THIS_WEEK = R.string.at_this_week;

    private final Context context;
    private final DateTime now;

    public FeedHeader(Context context) {
        this(context, new DateTime());
    }

    public FeedHeader(Context context, DateTime now) {
        this.context = context;
        this.now = now;
    }

    public String getText(DateTime feedDate) {
        int days = Days.daysBetween(feedDate, now).getDays();
        if (days == 0) {
            return context.getString(TODAY);
        } else if (days == 1) {
            return context.getString(YESTERDAY);
        } else if (new Interval(now.withDayOfWeek(DateTimeConstants.MONDAY), now).contains(feedDate)) {
            return context.getString(AT_THIS_WEEK);
        } else {
            return MONTH_DATE_FORMAT.print(feedDate);
        }
    }

    public int getId(DateTime feedDate) {
        return Math.abs(getText(feedDate).hashCode());
    }
}
