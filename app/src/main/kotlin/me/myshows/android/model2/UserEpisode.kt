package me.myshows.android.model2

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserEpisode(
        @JsonProperty("id") val id: Int,
        @JsonProperty("watchDate") val watchDate: String,
        @JsonProperty("rating") val rating: Int
)
