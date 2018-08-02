package me.myshows.android.model2

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import me.myshows.android.model.WatchStatus

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserShow(
        @JsonProperty("show") val show: ShowSummary,
        @JsonProperty("watchStatus") val watchStatus: WatchStatus,
        @JsonProperty("rating") val rating: Int,
        @JsonProperty("watchCount") val watchCount: Int,
        @JsonProperty("totalEpisodes") val totalEpisodes: Int,
        @JsonProperty("watchedEpisodes") val watchedEpisodes: Int
)
