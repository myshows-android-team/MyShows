package me.myshows.android.model2

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Feed(
        @JsonProperty("id") val id: Int,
        @JsonProperty("user") val user: User,
        @JsonProperty("createdAt") val createdAt: String,
        @JsonProperty("type") val type: FeedType,
        @JsonProperty("show") val show: ShowSummary?,
        @JsonProperty("episodes") val episodes: List<EpisodeSummary>?,
        @JsonProperty("rating") val rating: Int?,
        @JsonProperty("showStatus") val showStatus: String?,
        @JsonProperty("commentId") val commentId: Int?,
        @JsonProperty("achievement") val achievement: Achievement?,
        @JsonProperty("affectedUser") val affectedUser: User?
)
