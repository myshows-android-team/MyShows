package me.myshows.android.model;

import android.support.annotation.StringRes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

import me.myshows.android.R;

/**
 * Created by warrior on 08.08.15.
 */
public enum ShowStatus {
    CANCELLED(R.string.cancelled),
    ONGOING(R.string.ongoing),
    ON_BREAK(R.string.ongoing),
    NEW(R.string.new_show),
    FINAL_SEASON(R.string.final_season),
    IN_DEVELOPMENT(R.string.in_development),
    TBD(R.string.tbd),
    PILOT(R.string.pilot),
    UNKNOWN(R.string.unknown_status);

    private static Map<String, ShowStatus> strToStatus = new HashMap<>(ShowStatus.values().length);

    static {
        strToStatus.put("Canceled/Ended", CANCELLED);
        strToStatus.put("Returning Series", ONGOING);
        strToStatus.put("On Hiatus", ON_BREAK);
        strToStatus.put("New Series", NEW);
        strToStatus.put("Final Season", FINAL_SEASON);
        strToStatus.put("In Development", IN_DEVELOPMENT);
        strToStatus.put("TBD/On The Bubble", TBD);
        strToStatus.put("Pilot Rejected", PILOT);
        strToStatus.put("Unknown", UNKNOWN);
    }

    private final int stringId;

    ShowStatus(@StringRes int stringId) {
        this.stringId = stringId;
    }

    @StringRes
    public int getStringId() {
        return stringId;
    }

    @JsonValue
    @Override
    public String toString() {
        for (Map.Entry<String, ShowStatus> entry : strToStatus.entrySet()) {
            if (entry.getValue() == this) {
                return entry.getKey();
            }
        }
        return null;
    }

    @JsonCreator
    public static ShowStatus fromString(String value) {
        ShowStatus status = strToStatus.get(value);
        if (status == null) {
            status = UNKNOWN;
        }
        return status;
    }
}
