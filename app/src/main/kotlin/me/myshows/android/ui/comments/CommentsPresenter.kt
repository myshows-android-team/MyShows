package me.myshows.android.ui.comments

import com.fasterxml.jackson.databind.ObjectMapper
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.myshows.android.R
import me.myshows.android.api2.client.MyShowsClient
import me.myshows.android.model.serialization.JsonMarshaller
import me.myshows.android.model2.Comment
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
                .subscribeOn(Schedulers.io())
                .flatMap { loadFromCacheAndServer(it) }
                .observeOn(AndroidSchedulers.mainThread())

        val pullToRefreshIntents = intent(CommentsView::pullToRefreshIntent)
                .subscribeOn(Schedulers.io())
                .flatMap { loadFromServerOnly(it) }
                .observeOn(AndroidSchedulers.mainThread())

        val postCommentIntents = intent(CommentsView::postCommentIntent)
                .subscribeOn(Schedulers.io())
                .flatMap { postComment(it) }
                .observeOn(AndroidSchedulers.mainThread())

        val voteCommentIntents = intent(CommentsView::voteIntent)
                .subscribeOn(Schedulers.io())
                .flatMap { voteComment(it) }
                .observeOn(AndroidSchedulers.mainThread())

        val allIntents = Observable.merge(loadCommentsIntents, pullToRefreshIntents,
                postCommentIntents, voteCommentIntents)

        subscribeViewState(allIntents, CommentsView::render)
    }

    private fun postComment(commentData: CommentsView.CommentData): Observable<CommentsViewState> =
            client.showsPostEpisodeComment(commentData.episodeId, commentData.text, commentData.parentCommentId)
                    .map { newComment ->
                        var episodeComments = selectData(commentData.episodeId)
                                ?: EpisodeComments(false, 0, 0, false, emptyList())

                        val comments = episodeComments.comments.toMutableList()
                        comments += newComment
                        episodeComments = EpisodeComments(episodeComments.isTracking, comments.size,
                                episodeComments.newCount, episodeComments.hasSpoilers, comments)

                        upsertData(commentData.episodeId, episodeComments)

                        LoadedComments(episodeComments, newComment) as CommentsViewState
                    }
                    .onErrorReturn { CommentsLoadError(CommentsException(R.string.post_comment_error)) }
                    .toObservable()

    private fun voteComment(voteData: CommentsView.VoteData): Observable<CommentsViewState> =
            client.showsVoteEpisodeComment(voteData.commentId, voteData.isPositive)
                    .map { newRating ->
                        var episodeComments = selectData(voteData.episodeId)!!

                        val comments = episodeComments.comments.toMutableList()
                        val index = comments.indexOfFirst { it.id == voteData.commentId }
                        val comment = comments[index]
                        comments[index] = Comment(comment.id, comment.showId, comment.episodeId,
                                comment.user, comment.comment, comment.image,
                                comment.parentId, comment.createdAt, comment.statusId,
                                comment.isNew, voteData.isPositive, !voteData.isPositive,
                                comment.isMyComment, newRating, comment.isBad,
                                comment.isEditable, comment.language)
                        episodeComments = EpisodeComments(episodeComments.isTracking, episodeComments.count, episodeComments.newCount, episodeComments.hasSpoilers, comments)

                        upsertData(voteData.episodeId, episodeComments)

                        LoadedComments(episodeComments, comments[index]) as CommentsViewState
                    }
                    .onErrorReturn { CommentsLoadError(CommentsException(R.string.vote_comment_error)) }
                    .toObservable()

    private fun loadFromCacheAndServer(episodeId: Int): Observable<CommentsViewState> =
            Observable.concat(loadCache(episodeId), syncWithServer(episodeId))
                    .mapToViewState()
                    .startWith(LoadingComments)
                    .onErrorReturn { CommentsLoadError(CommentsException(R.string.update_comments_error)) }

    private fun loadFromServerOnly(episodeId: Int): Observable<CommentsViewState> =
            syncWithServer(episodeId)
                    .mapToViewState()
                    .startWith(LoadingComments)
                    .onErrorReturn { CommentsLoadError(CommentsException(R.string.update_comments_error)) }

    private fun loadCache(episodeId: Int): Observable<EpisodeComments> =
            Observable.create { subscriber ->
                try {
                    val entity = selectData(episodeId)
                    if (entity != null) subscriber.onNext(entity)
                } catch (e: Exception) {
                    subscriber.onError(e)
                }
                subscriber.onComplete()
            }

    private fun syncWithServer(episodeId: Int): Observable<EpisodeComments> =
            client.showsEpisodeComments(episodeId)
                    .doOnSuccess { upsertData(episodeId, it) }
                    .doOnSuccess { if (it.newCount > 0) client.showsViewEpisodeComments(episodeId).subscribe() }
                    .toObservable()

    private fun selectData(episodeId: Int): EpisodeComments? = RealmManager.selectEntity(
            EpisodeCommentsRealm::class.java, { it.fromRealm(marshaller) }, Predicate("episodeId", episodeId))

    private fun upsertData(episodeId: Int, episodeComments: EpisodeComments): EpisodeComments =
            RealmManager.upsertEntity(episodeComments) { it.toRealm(marshaller, episodeId) }

    private fun Observable<EpisodeComments>.mapToViewState(): Observable<CommentsViewState> =
            map { if (it.count == 0) EmptyComments else LoadedComments(it) }
}
