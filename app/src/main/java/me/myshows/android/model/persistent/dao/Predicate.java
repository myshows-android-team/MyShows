package me.myshows.android.model.persistent.dao;

/**
 * Created by Whiplash on 09.08.2015.
 */
public class Predicate {

    private final String field;
    private final Object value;

    public Predicate(String field, Object value) {
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }
}
