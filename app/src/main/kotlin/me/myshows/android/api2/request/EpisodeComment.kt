package me.myshows.android.api2.request

import com.fasterxml.jackson.annotation.JsonProperty

data class EpisodeComment(@JsonProperty("episodeId") val episodeId: Int,
                          @JsonProperty("text") val text: String,
                          @JsonProperty("parentCommentId") val parentCommentId: Int)
