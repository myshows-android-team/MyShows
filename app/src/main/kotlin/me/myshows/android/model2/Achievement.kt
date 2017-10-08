package me.myshows.android.model2

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Achievement(
        @JsonProperty("title") val title: String,
        @JsonProperty("description") val description: String,
        @JsonProperty("image") val image: String
)
