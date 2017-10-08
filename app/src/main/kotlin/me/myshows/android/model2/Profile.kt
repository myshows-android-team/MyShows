package me.myshows.android.model2

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import me.myshows.android.model.Statistics

@JsonIgnoreProperties(ignoreUnknown = true)
data class Profile(
        @JsonProperty("user") val user: User,
        @JsonProperty("stats") val stats: Statistics
)
