package me.myshows.android.model.persistent.dao;

import io.realm.RealmObject;

public interface FromPersistentEntity<T, E extends RealmObject> {

    T fromRealmObject(E persistentEntity);
}
