package me.myshows.android.model.persistent.dao;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class RealmManager {

    private final Context context;

    public RealmManager(Context context) {
        this.context = context;
    }

    public <T> T persistEntity(T entity, ToPersistPersistentEntity<T> converter) {
        Realm realm = Realm.getInstance(context);
        realm.executeTransaction(r -> r.copyToRealmOrUpdate(converter.toRealmObject(entity)));
        realm.close();
        return entity;
    }

    public <T, E extends RealmObject> List<T> persistEntities(List<T> entities, Class<E> clazz, ToPersistPersistentEntity<T> converter) {
        List<RealmObject> persistentEntities = new ArrayList<>(entities.size());
        for (T entity : entities) {
            persistentEntities.add(converter.toRealmObject(entity));
        }
        Realm realm = Realm.getInstance(context);
        realm.executeTransaction(r -> {
            r.clear(clazz);
            r.copyToRealmOrUpdate(persistentEntities);
        });
        realm.close();
        return entities;
    }

    public <T, E extends RealmObject> T getEntity(Class<E> clazz, FromPersistentEntity<T, E> converter, Predicate... predicates) {
        Realm realm = Realm.getInstance(context);
        E persistentEntity = makeQuery(realm, clazz, predicates).findFirst();
        T entity = null;
        if (persistentEntity != null) {
            entity = converter.fromRealmObject(persistentEntity);
        }
        realm.close();
        return entity;
    }

    public <T, E extends RealmObject> List<T> getEntities(Class<E> clazz, FromPersistentEntity<T, E> converter, Predicate... predicates) {
        Realm realm = Realm.getInstance(context);
        RealmResults<E> results = makeQuery(realm, clazz, predicates).findAll();
        List<T> entities = null;
        if (results != null) {
            entities = new ArrayList<>();
            for (E result : results) {
                entities.add(converter.fromRealmObject(result));
            }
        }
        realm.close();
        return entities;
    }

    private <E extends RealmObject> RealmQuery<E> makeQuery(Realm realm, Class<E> clazz, Predicate... predicates) {
        RealmQuery<E> query = realm.where(clazz);
        for (Predicate predicate : predicates) {
            query = equalTo(query, predicate.getField(), predicate.getValue());
        }
        return query;
    }

    private <E extends RealmObject> RealmQuery<E> equalTo(RealmQuery<E> query, String fieldName, Object value) {
        if (value instanceof String) {
            return query.equalTo(fieldName, (String) value);
        } else if (value instanceof Integer) {
            return query.equalTo(fieldName, (int) value);
        } else if (value instanceof Double) {
            return query.equalTo(fieldName, (double) value);
        }
        throw new IllegalArgumentException("Unreached statement");
    }
}
