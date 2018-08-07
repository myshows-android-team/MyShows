package me.myshows.android.ui.comments

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable

interface CommentsView : MvpView {

    fun loadCommentsIntent(): Observable<Int>
    fun pullToRefreshIntent(): Observable<Int>

    fun postCommentIntent(): Observable<CommentData>
    fun voteIntent(): Observable<VoteData>

    fun render(state: CommentsViewState)

    data class CommentData(val episodeId: Int, val text: String, val parentCommentId: Int)
    data class VoteData(val episodeId: Int, val commentId: Int, val isPositive: Boolean)
}
