package me.myshows.android.model2.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import me.myshows.android.model.serialization.Marshaller
import me.myshows.android.model2.Comment
import me.myshows.android.model2.EpisodeComments

open class EpisodeCommentsRealm(
        @PrimaryKey var episodeId: Int = 0,
        var isTracking: Boolean = false,
        var count: Int = 0,
        var newCount: Int = 0,
        var hasSpoilers: Boolean = true,
        var comments: ByteArray? = null
) : RealmObject() {

    fun fromRealm(marshaller: Marshaller): EpisodeComments {
        val comments = marshaller.deserializeList(comments, ArrayList::class.java, Comment::class.java)
        return EpisodeComments(isTracking, count, newCount, hasSpoilers, comments)
    }
}

fun EpisodeComments.toRealm(marshaller: Marshaller, episodeId: Int): EpisodeCommentsRealm {
    val commentsData = marshaller.serialize(comments)
    return EpisodeCommentsRealm(episodeId, isTracking, count, newCount, hasSpoilers, commentsData)
}
