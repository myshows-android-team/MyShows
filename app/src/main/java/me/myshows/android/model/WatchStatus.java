package me.myshows.android.model;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

import me.myshows.android.R;

/**
 * Created by warrior on 08.08.15.
 */
public enum WatchStatus {
    WATCHING(R.drawable.ic_status_watching, R.color.fab_watching),
    FINISHED(R.drawable.ic_status_watching, R.color.fab_watching),
    LATER(R.drawable.ic_status_later, R.color.fab_later),
    CANCELLED(R.drawable.ic_status_cancelled, R.color.fab_cancelled),
    NOT_WATCHING(R.drawable.ic_status_not_watching, R.color.fab_not_watching);

    private static Map<String, WatchStatus> strToStatus = new HashMap<>(WatchStatus.values().length);

    static {
        strToStatus.put("watching", WATCHING);
        strToStatus.put("later", LATER);
        strToStatus.put("cancelled", CANCELLED);
        strToStatus.put("finished", FINISHED);
        strToStatus.put("not watching", NOT_WATCHING);
    }

    private final int drawableId;
    private final int colorId;

    WatchStatus(@DrawableRes int drawableId, @ColorRes int colorId) {
        this.drawableId = drawableId;
        this.colorId = colorId;
    }

    @DrawableRes
    public int getDrawableId() {
        return drawableId;
    }

    @ColorRes
    public int getColorId() {
        return colorId;
    }

    @JsonValue
    @Override
    public String toString() {
        for (Map.Entry<String, WatchStatus> entry : strToStatus.entrySet()) {
            if (entry.getValue() == this) {
                return entry.getKey();
            }
        }
        return null;
    }

    @JsonCreator
    public static WatchStatus fromString(String value) {
        WatchStatus status = strToStatus.get(value);
        if (status == null) {
            status = NOT_WATCHING;
        }
        return status;
    }
}
