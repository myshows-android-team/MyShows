package me.myshows.android.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Whiplash on 13.08.2015.
 */
public enum Gender {
    MALE,
    FEMALE;

    private static Map<String, Gender> strToGender = new HashMap<>(Gender.values().length);

    static {
        strToGender.put("m", MALE);
        strToGender.put("f", FEMALE);
    }

    @JsonCreator
    public static Gender fromString(String value) {
        return strToGender.get(value);
    }

    @JsonValue
    @Override
    public String toString() {
        for (Map.Entry<String, Gender> entry : strToGender.entrySet()) {
            if (entry.getValue() == this) {
                return entry.getKey();
            }
        }
        return null;
    }
}
