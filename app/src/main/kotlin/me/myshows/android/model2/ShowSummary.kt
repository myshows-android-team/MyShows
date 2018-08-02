package me.myshows.android.model2

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ShowSummary(
        @JsonProperty("id") val id: Int,
        @JsonProperty("title") val title: String,
        @JsonProperty("titleOriginal") val titleOriginal: String,
        @JsonProperty("status") val status: String,
        @JsonProperty("totalSeasons") val totalSeasons: Int,
        @JsonProperty("year") val year: Int?,
        @JsonProperty("watching") val watching: Int?,
        @JsonProperty("voted") val voted: Int?,
        @JsonProperty("rating") val rating: Float?,
        @JsonProperty("image") val image: String
)
