package me.myshows.android.model2

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class FeedType(private val stringValue: String) {
    EPISODE_CHECK("e.check"),
    EPISODE_UNCHECK("e.uncheck"),
    EPISODE_RATE("e.rate"),
    EPISODE_COMMENT("e.comment"),
    SHOW_STATUS("s.status"),
    SHOW_RATE("s.rate"),
    ACHIEVEMENT("achievement"),
    FOLLOWING("following"),
    UNFOLLOW("unfollow");

    @JsonValue
    override fun toString(): String = stringValue

    companion object {
        private val strToFeedType: Map<String, FeedType> = values().associate { it.stringValue to it }

        @JsonCreator
        @JvmStatic
        fun fromString(value: String): FeedType {
            return strToFeedType[value] ?: error("Unsupported value $value")
        }
    }
}
