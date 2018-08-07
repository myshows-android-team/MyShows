package me.myshows.android.model2

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import me.myshows.android.utils.parseInMillisISO8601

@JsonIgnoreProperties(ignoreUnknown = true)
data class Comment(
        @JsonProperty("id") val id: Int,
        @JsonProperty("showId") val showId: Int,
        @JsonProperty("episodeId") val episodeId: Int,
        @JsonProperty("user") val user: User,

        @JsonProperty("comment") val comment: String,
        @JsonProperty("image") val image: String?,

        @JsonProperty("parentId") val parentId: Int?,
        @JsonProperty("createdAt") val createdAt: String,
        @JsonProperty("statusId") val statusId: Int,

        @get:JsonProperty("isNew") val isNew: Boolean,
        @get:JsonProperty("isMyPlus") val isMyPlus: Boolean,
        @get:JsonProperty("isMyMinus") val isMyMinus: Boolean,
        @get:JsonProperty("isMyComment") val isMyComment: Boolean,

        @JsonProperty("rating") val rating: Int,

        @get:JsonProperty("isBad") val isBad: Boolean,
        @get:JsonProperty("isEditable") val isEditable: Boolean,

        @JsonProperty("language") val language: String?,

        @JsonIgnore val createdAtMillis: Long = parseInMillisISO8601(createdAt)) {

    companion object {
        /**
         * Id of top level comments.
         */
        var ROOT_ID = 0
        /**
         * If comment rating greater than {@value BOUND} it considers to be positive.
         * If less than -{@value BOUND} it is negative. Otherwise it neutral.
         */
        var BOUND = 25
    }
}
