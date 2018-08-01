package me.myshows.android.ui.comments

import me.myshows.android.model.EpisodeComments

sealed class CommentsViewState

/**
 * Comments start loading indicator.
 */
object Loading : CommentsViewState()

/**
 * Returns in case of occurring an error during comments loading.
 */
data class Error(val t: Throwable) : CommentsViewState()

/**
 * Returns when an episode has no any comments.
 */
object EmptyComments : CommentsViewState()

/**
 * Returns when comments load successfully.
 */
data class LoadedComments(val episodeComments: EpisodeComments) : CommentsViewState()
