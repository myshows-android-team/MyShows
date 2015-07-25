package me.myshows.android.dao;

import io.realm.RealmObject;

public interface ToPersistConverter<T> {

    RealmObject toRealmObject(T entity);
}
