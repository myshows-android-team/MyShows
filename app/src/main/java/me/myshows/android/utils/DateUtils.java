package me.myshows.android.utils;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by warrior on 13.09.15.
 */
public class DateUtils {

    private static final String TAG = DateUtils.class.getSimpleName();

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    @Nullable
    public static Date parse(@Nullable String rawDate) {
        if (!TextUtils.isEmpty(rawDate)) {
            try {
                return FORMATTER.parse(rawDate);
            } catch (ParseException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return null;
    }

    public static long parseInMillis(@Nullable String rawDate) {
        Date date = parse(rawDate);
        return date != null ? date.getTime() : Long.MAX_VALUE;
    }
}
