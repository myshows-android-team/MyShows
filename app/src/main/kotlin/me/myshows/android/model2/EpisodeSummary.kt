package me.myshows.android.model2

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class EpisodeSummary(
        @JsonProperty("id") val id: Int,
        @JsonProperty("title") val title: String,
        @JsonProperty("showId") val showId: Int,
        @JsonProperty("seasonNumber") val seasonNumber: Int,
        @JsonProperty("episodeNumber") val episodeNumber: Int,
        @JsonProperty("airDate") val airDate: String,
        @JsonProperty("image") val image: String,
        @JsonProperty("shortName") val shortName: String,
        @JsonProperty("commentsCount") val commentsCount: Int?
)
