package me.myshows.android.model;

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
    WATCH(R.drawable.action_watch),
    NEW(R.drawable.action_new);

    private static Map<String, Action> strToAction = new HashMap<>(Action.values().length);

    static {
        strToAction.put("watch", WATCH);
        strToAction.put("new", NEW);
    }

    private final int drawableId;

    Action(@DrawableRes int drawableId) {
        this.drawableId = drawableId;
    }

    @JsonCreator
    public static Action fromString(String value) {
        return strToAction.get(value);
    }

    @DrawableRes
    public int getDrawableId() {
        return drawableId;
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
