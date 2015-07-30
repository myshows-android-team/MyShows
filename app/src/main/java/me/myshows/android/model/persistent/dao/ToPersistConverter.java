package me.myshows.android.model.persistent.dao;

import io.realm.RealmObject;

public interface ToPersistConverter<T> {

    RealmObject toRealmObject(T entity);
}
