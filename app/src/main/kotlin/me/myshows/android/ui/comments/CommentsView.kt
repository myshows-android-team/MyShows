package me.myshows.android.ui.comments

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable

interface CommentsView : MvpView {

    fun loadCommentsIntent(): Observable<Int>
    fun pullToRefreshIntent(): Observable<Int>

    /*fun voteUpIntent()
    fun voteDownIntent()
    fun writeNewCommentIntent()
    fun replyCommentIntent()*/

    fun render(state: CommentsViewState)
}
