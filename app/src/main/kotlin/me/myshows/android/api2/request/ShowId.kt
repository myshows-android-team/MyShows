package me.myshows.android.api2.request

import com.fasterxml.jackson.annotation.JsonProperty

data class ShowId(
        @JsonProperty("showId") val showId: Int
)
