package me.myshows.android.model2.realm.doa

import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmQuery


object RealmManager {

    fun <T> upsertEntity(entity: T, converter: (T) -> RealmObject): T =
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction { it.copyToRealmOrUpdate(converter(entity)) }
                entity
            }

    fun <T, E : RealmObject> selectEntity(clazz: Class<E>, converter: (E) -> T, vararg predicates: Predicate): T? =
            Realm.getDefaultInstance().use { realm ->
                var query = realm.where(clazz)
                predicates.forEach { (field, value) ->
                    query = query.equalTo(field, value)
                }
                val realmObject = query.findFirst()
                if (realmObject != null) converter(realmObject) else null
            }

    fun <T, E : RealmObject> selectEntities(clazz: Class<E>, converter: (E) -> T, vararg predicates: Predicate): List<T> =
            Realm.getDefaultInstance().use { realm ->
                var query = realm.where(clazz)
                predicates.forEach { (field, value) ->
                    query = query.equalTo(field, value)
                }
                query.findAll().map(converter)
            }

    private fun <E> RealmQuery<E>.equalTo(field: String, value: Any): RealmQuery<E> =
            when (value) {
                is String -> equalTo(field, value)
                is Int -> equalTo(field, value)
                is Double -> equalTo(field, value)
                else -> throw IllegalArgumentException("Unsupported value type")
            }
}

data class Predicate(val field: String, val value: Any)
