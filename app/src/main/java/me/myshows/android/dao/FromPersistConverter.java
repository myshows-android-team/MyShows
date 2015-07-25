package me.myshows.android.dao;

import io.realm.RealmObject;

public interface FromPersistConverter<T, E extends RealmObject> {

    T fromRealmObject(E persistentEntity);
}
