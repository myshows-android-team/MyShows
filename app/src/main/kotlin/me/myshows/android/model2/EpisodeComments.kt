package me.myshows.android.model2

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class EpisodeComments(
        @JsonProperty("isTracking") val isTracking: Boolean,
        @JsonProperty("count") val count: Int,
        @JsonProperty("newCount") val newCount: Int,
        @JsonProperty("hasSpoilers") val hasSpoilers: Boolean,
        @JsonProperty("comments") val comments: List<Comment>
)
