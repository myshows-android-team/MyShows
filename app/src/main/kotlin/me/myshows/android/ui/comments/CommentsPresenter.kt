package me.myshows.android.ui.comments

import com.fasterxml.jackson.databind.ObjectMapper
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import me.myshows.android.api2.client.MyShowsClient
import me.myshows.android.model.serialization.JsonMarshaller
import me.myshows.android.model2.EpisodeComments
import me.myshows.android.model2.realm.EpisodeCommentsRealm
import me.myshows.android.model2.realm.doa.Predicate
import me.myshows.android.model2.realm.doa.RealmManager
import me.myshows.android.model2.realm.toRealm
import javax.inject.Inject

class CommentsPresenter @Inject constructor(
        private val client: MyShowsClient,
        objectMapper: ObjectMapper
) : MviBasePresenter<CommentsView, CommentsViewState>() {

    private val marshaller = JsonMarshaller(objectMapper)

    override fun bindIntents() {
        val loadCommentsIntents = intent(CommentsView::loadCommentsIntent)
                .flatMap { id -> loadFromCacheAndServer(id) }
                .observeOn(AndroidSchedulers.mainThread())

        val pullToRefreshIntents = intent(CommentsView::pullToRefreshIntent)
                .flatMap { id -> loadFromServerOnly(id) }
                .observeOn(AndroidSchedulers.mainThread())

        val allIntents = Observable.merge(loadCommentsIntents, pullToRefreshIntents)

        subscribeViewState(allIntents, CommentsView::render)
    }

    private fun loadFromCacheAndServer(episodeId: Int): Observable<CommentsViewState> =
            Observable.concat(loadCache(episodeId), syncWithServer(episodeId))
                    .mapToViewState()
                    .startWith(Loading)
                    .onErrorReturn { Error(it) }

    private fun loadFromServerOnly(episodeId: Int): Observable<CommentsViewState> =
            syncWithServer(episodeId)
                    .mapToViewState()
                    .startWith(Loading)
                    .onErrorReturn { Error(it) }

    private fun loadCache(episodeId: Int): Observable<EpisodeComments> =
            Observable.create { subscriber ->
                try {
                    val entity = RealmManager.selectEntity(EpisodeCommentsRealm::class.java,
                            { it.fromRealm(marshaller) },
                            Predicate("episodeId", episodeId))

                    if (entity != null) subscriber.onNext(entity)
                } catch (e: Exception) {
                    subscriber.onError(e)
                }
                subscriber.onComplete()
            }

    private fun syncWithServer(episodeId: Int): Observable<EpisodeComments> =
            client.showsEpisodeComments(episodeId)
                    .doOnSuccess { remoteEntity -> RealmManager.upsertEntity(remoteEntity) { it.toRealm(marshaller, episodeId) } }
                    .doOnSuccess { if (it.newCount > 0) client.showsViewEpisodeComments(episodeId).subscribe() }
                    .toObservable()

    private fun Observable<EpisodeComments>.mapToViewState(): Observable<CommentsViewState> =
            map { if (it.count == 0) EmptyComments else LoadedComments(it) }
}
