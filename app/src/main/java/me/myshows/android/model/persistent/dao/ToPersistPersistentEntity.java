package me.myshows.android.model.persistent.dao;

import io.realm.RealmObject;

public interface ToPersistPersistentEntity<T> {

    RealmObject toRealmObject(T entity);
}