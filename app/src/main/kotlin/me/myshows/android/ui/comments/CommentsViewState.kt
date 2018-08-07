package me.myshows.android.ui.comments

import me.myshows.android.model2.Comment
import me.myshows.android.model2.EpisodeComments

sealed class CommentsViewState

/**
 * Comments start loading indicator.
 */
object LoadingComments : CommentsViewState()

/**
 * Returns in case of occurring an error during comments loading.
 */
data class CommentsLoadError(val error: CommentsException) : CommentsViewState()

/**
 * Returns when an episode has no any comments.
 */
object EmptyComments : CommentsViewState()

/**
 * Returns when comments load successfully.
 */
data class LoadedComments(val episodeComments: EpisodeComments, val updatedComment: Comment? = null) : CommentsViewState()
