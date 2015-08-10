package me.myshows.android.model.persistent.dao;

/**
 * Created by Whiplash on 09.08.2015.
 */
public class Predicate {

    public final String field;
    public final Object value;

    public Predicate(String field, Object value) {
        this.field = field;
        this.value = value;
    }
}
