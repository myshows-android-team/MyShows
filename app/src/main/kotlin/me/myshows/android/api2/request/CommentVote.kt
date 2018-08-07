package me.myshows.android.api2.request

import com.fasterxml.jackson.annotation.JsonProperty

data class CommentVote(@JsonProperty("commentId") val commentId: Int,
                       @get:JsonProperty("isPositive") val isPositive: Boolean)
