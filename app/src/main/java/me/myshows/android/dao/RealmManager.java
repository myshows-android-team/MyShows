package me.myshows.android.dao;

import android.content.Context;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public final class RealmManager {

    private final Context context;

    public RealmManager(Context context) {
        this.context = context;
    }

    public <T> T persistEntity(T entity, ToPersistConverter<T> converter) {
        Realm realm = Realm.getInstance(context);
        realm.executeTransaction(r -> r.copyToRealmOrUpdate(converter.toRealmObject(entity)));
        realm.close();
        return entity;
    }

    public <T, E extends RealmObject> List<T> persistEntities(List<T> entities, Class<E> clazz, ToPersistConverter<T> converter) {
        Realm realm = Realm.getInstance(context);
        realm.clear(clazz);
        realm.executeTransaction(r -> {
            for (T entity : entities) {
                r.copyToRealmOrUpdate(converter.toRealmObject(entity));
            }
        });
        realm.close();
        return entities;
    }

    @SafeVarargs
    public final <T, E extends RealmObject> T getEntity(Class<E> clazz, FromPersistConverter<T, E> converter, Pair<String, Object>... where) {
        Realm realm = Realm.getInstance(context);
        E persistentEntity = makeQuery(realm, clazz, where).findFirst();
        T entity = null;
        if (persistentEntity != null) {
            entity = converter.fromRealmObject(persistentEntity);
        }
        realm.close();
        return entity;
    }

    @SafeVarargs
    public final <T, E extends RealmObject> List<T> getEntities(Class<E> clazz, FromPersistConverter<T, E> converter, Pair<String, Object>... where) {
        Realm realm = Realm.getInstance(context);
        RealmResults<E> results = makeQuery(realm, clazz, where).findAll();
        List<T> entities = new ArrayList<>();
        if (results != null) {
            for (E result : results) {
                entities.add(converter.fromRealmObject(result));
            }
        }
        realm.close();
        return entities;
    }

    @SafeVarargs
    private final <E extends RealmObject> RealmQuery<E> makeQuery(Realm realm, Class<E> clazz, Pair<String, Object>... where) {
        RealmQuery<E> query = realm.where(clazz);
        for (Pair<String, Object> pair : where) {
            query = equalTo(query, pair.first, pair.second);
        }
        return query;
    }

    private <E extends RealmObject> RealmQuery<E> equalTo(RealmQuery<E> query, String field, Object value) {
        if (value instanceof Integer) {
            return query.equalTo(field, (int) value);
        } else if (value instanceof String) {
            return query.equalTo(field, (String) value);
        }
        throw new IllegalArgumentException("Unreached statement");
    }
}
