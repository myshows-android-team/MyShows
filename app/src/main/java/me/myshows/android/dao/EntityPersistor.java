package me.myshows.android.dao;

import android.content.Context;

import java.util.Collection;

import io.realm.Realm;

public class EntityPersistor {

    protected final Context context;

    public EntityPersistor(Context context) {
        this.context = context;
    }

    public <T> T persistEntity(T entity, PersistConverter<T> converter) {
        Realm realm = Realm.getInstance(context);
        realm.executeTransaction(r -> r.copyToRealmOrUpdate(converter.toRealmObject(entity)));
        realm.close();
        return entity;
    }

    public <T> Collection<T> persistEntityList(Collection<T> entities, PersistConverter<T> converter) {
        Realm realm = Realm.getInstance(context);
        realm.executeTransaction(r -> {
            for (T entity : entities) {
                r.copyToRealmOrUpdate(converter.toRealmObject(entity));
            }
        });
        realm.close();
        return entities;
    }
}
