package me.myshows.android.model2

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import me.myshows.android.model.Gender

@JsonIgnoreProperties(ignoreUnknown = true)
data class User(
        @JsonProperty("login") val login: String,
        @JsonProperty("avatar") val avatar: String,
        @JsonProperty("wastedTime") val wastedTime: Int,
        @JsonProperty("gender") val gender: Gender?,
        @JsonProperty("isPro") val isPro: Boolean        
)
