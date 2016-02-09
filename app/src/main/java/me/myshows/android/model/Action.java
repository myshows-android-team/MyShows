package me.myshows.android.model;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

import me.myshows.android.R;

/**
 * Created by Whiplash on 13.08.2015.
 */
public enum Action {
    WATCH(R.drawable.action_watch, R.color.state_green),
    NEW(R.drawable.action_new, R.color.state_red),
    RATING(R.drawable.action_rating, R.color.state_orange),
    WATCH_LATER(R.drawable.action_watch_later, R.color.state_blue),
    STOP_WATCH(R.drawable.action_stop_watch, R.color.state_red),
    ACHIEVEMENT(R.drawable.action_achievement, R.color.state_orange);

    private static Map<String, Action> strToAction = new HashMap<>(Action.values().length);

    static {
        strToAction.put("watch", WATCH);
        strToAction.put("new", NEW);
        strToAction.put("rating", RATING);
        strToAction.put("later", WATCH_LATER);
        strToAction.put("stop", WATCH_LATER);
        strToAction.put("achievement", WATCH_LATER);
    }

    private final int drawableId;
    private final int color;

    Action(@DrawableRes int drawableId, @ColorRes int color) {
        this.drawableId = drawableId;
        this.color = color;
    }

    @JsonCreator
    public static Action fromString(String value) {
        return strToAction.get(value);
    }

    @DrawableRes
    public int getDrawableId() {
        return drawableId;
    }

    @ColorRes
    public int getColor() {
        return color;
    }

    @JsonValue
    @Override
    public String toString() {
        for (Map.Entry<String, Action> entry : strToAction.entrySet()) {
            if (entry.getValue() == this) {
                return entry.getKey();
            }
        }
        return null;
    }
}
