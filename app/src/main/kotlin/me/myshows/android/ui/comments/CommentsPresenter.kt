package me.myshows.android.ui.comments

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import hu.akarnokd.rxjava.interop.RxJavaInterop
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class CommentsPresenter @Inject constructor(
        private val oldClient: me.myshows.android.api.MyShowsClient
) : MviBasePresenter<CommentsView, CommentsViewState>() {

    override fun bindIntents() {
        val loadCommentsIntents = intent(CommentsView::loadCommentsIntent)
                .flatMap { id -> loadComments(id) }
                .observeOn(AndroidSchedulers.mainThread())

        val pullToRefreshIntents = intent(CommentsView::pullToRefreshIntent)
                .flatMap { id -> loadComments(id) }
                .observeOn(AndroidSchedulers.mainThread())

        val allIntents = Observable.merge(loadCommentsIntents, pullToRefreshIntents)

        subscribeViewState(allIntents, CommentsView::render)
    }

    private fun loadComments(episodeId: Int): Observable<CommentsViewState> = oldClient.comments(episodeId)
            .map { if (it.count == 0) EmptyComments else LoadedComments(it) }
            .startWith(Loading)
            .onErrorReturn { Error(it) }
            .toV2()
}

private fun <T> rx.Single<T>.toV2(): Single<T> = RxJavaInterop.toV2Single(this)

private fun <T> rx.Observable<T>.toV2(): Observable<T> = RxJavaInterop.toV2Observable(this)
