package me.myshows.android.ui.comments

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import me.myshows.android.api2.client.MyShowsClient
import javax.inject.Inject

class CommentsPresenter @Inject constructor(
        private val client: MyShowsClient
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

    private fun loadComments(episodeId: Int): Observable<CommentsViewState> = client.showsEpisodeComments(episodeId)
            .toObservable()
            .map { if (it.count == 0) EmptyComments else LoadedComments(it) }
//            .doOnNext { if (it is LoadedComments && it.episodeComments.newCount > 0) client.showsViewEpisodeComments(episodeId).subscribe() }
            .startWith(Loading)
            .onErrorReturn { Error(it) }
}
