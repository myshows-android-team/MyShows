package me.myshows.android.dao;

import io.realm.RealmObject;

public interface PersistConverter<T> {

    RealmObject toRealmObject(T entity);
}
