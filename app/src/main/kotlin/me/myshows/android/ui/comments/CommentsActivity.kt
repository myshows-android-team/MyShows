package me.myshows.android.ui.comments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.format.DateUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.comment_layout.view.*
import kotlinx.android.synthetic.main.comments_activity.*
import me.myshows.android.MyShowsApplication
import me.myshows.android.R
import me.myshows.android.model2.Comment
import me.myshows.android.model2.EpisodeComments
import me.myshows.android.ui.MviHomeActivity
import java.util.*
import kotlin.collections.HashMap

private const val INITIAL_NESTING_LEVEL = 1

const val EPISODE_ID = "episodeId"
const val EPISODE_TITLE = "episodeTitle"

class CommentsActivity : MviHomeActivity<CommentsView, CommentsPresenter, CommentsViewState>(), CommentsView {

    private val TAG = CommentsActivity::class.java.simpleName

    private var episodeId: Int = 0
    private var offset: Int = 0

    private lateinit var adapter: CommentsAdapter

    private val commentSubject = PublishSubject.create<CommentsView.CommentData>()
    private val voteCommentSubject = PublishSubject.create<CommentsView.VoteData>()

    private var postDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comments_activity)

        offset = resources.getDimensionPixelSize(R.dimen.default_padding)
        adapter = CommentsAdapter(offset, commentSubject::onNext, voteCommentSubject::onNext) {
            postDialog = it
        }

        setupActionBar(toolbar)
        setupRecyclerView(recycler_view)
        setupRefreshLayout(swipe_layout)
        setupFab(new_comment)
        setupRecyclerViewScrollBehaviour(recycler_view, swipe_layout, new_comment)

        supportActionBar?.title = intent.getStringExtra(EPISODE_TITLE)
        episodeId = intent.getIntExtra(EPISODE_ID, 0)
    }

    @SuppressLint("PrivateResource")
    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
    }

    private fun setupRefreshLayout(swipeLayout: SwipeRefreshLayout) {
        swipeLayout.setColorSchemeResources(R.color.primaryDark, R.color.primary)
    }

    private fun setupFab(fab: FloatingActionButton) {
        fab.setOnClickListener {
            postDialog = showAddCommentDialog(this, episodeId, Comment.ROOT_ID, null, commentSubject::onNext)
        }
    }

    private fun setupRecyclerViewScrollBehaviour(recyclerView: RecyclerView, swipeLayout: SwipeRefreshLayout, fab: FloatingActionButton) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                swipeLayout.isEnabled = (recyclerView.getChildAt(0)?.top ?: 0) >= 0
                when {
                    dy > 0 -> fab.hide()
                    dy < 0 -> fab.show()
                }
            }
        })
    }

    override fun createPresenter(): CommentsPresenter =
            MyShowsApplication.getComponent(this).newCommentsPresenter()

    override fun loadCommentsIntent(): Observable<Int> = Observable.just(episodeId)

    override fun pullToRefreshIntent(): Observable<Int> = RxSwipeRefreshLayout.refreshes(swipe_layout).map { episodeId }

    override fun postCommentIntent(): Observable<CommentsView.CommentData> = commentSubject

    override fun voteIntent(): Observable<CommentsView.VoteData> = voteCommentSubject

    override fun render(state: CommentsViewState) {
        when (state) {
            LoadingComments -> renderLoading()
            is CommentsLoadError -> renderError(state.error)
            EmptyComments -> Log.d(TAG, "Empty comments")
            is LoadedComments -> renderComments(state.episodeComments, state.updatedComment)
        }
    }

    private fun renderLoading() {
        Log.d(TAG, "Comments loading")
        swipe_layout.isRefreshing = true
    }

    private fun renderError(error: CommentsException) {
        Log.d(TAG, "Comments error", error)
        Toast.makeText(this, error.resourceId, Toast.LENGTH_LONG).show()
    }

    private fun renderComments(episodeComments: EpisodeComments, updatedComment: Comment?) {
        Log.d(TAG, "Loaded comments")
        if (updatedComment != null) {
            adapter.upsertComment(updatedComment)
            postDialog?.dismiss()
            postDialog = null
        } else {
            adapter.updateComments(episodeComments)
        }
        swipe_layout.isRefreshing = false
    }
}

private data class CommentLevelData(val comment: Comment, val nestingLevel: Int)

private class CommentHolder(itemView: View, private val offset: Int) : RecyclerView.ViewHolder(itemView) {

    fun bind(commentLevelData: CommentLevelData, isFirst: Boolean,
             onPostComment: (CommentsView.CommentData) -> Unit,
             onVoteComment: (CommentsView.VoteData) -> Unit,
             onOpenDialog: (AlertDialog) -> Unit) {

        val (comment, nestedLevel) = commentLevelData
        val leftPadding = offset * nestedLevel
        itemView.setPadding(leftPadding, if (isFirst) offset else 0, 0, 0)

        Glide.with(itemView.context)
                .load(comment.user.avatar)
                .apply(RequestOptions.centerCropTransform())
                .into(itemView.avatar)

        itemView.username.text = comment.user.login
        setDate(itemView.date, comment.createdAtMillis)
        setRating(itemView.rating, comment.rating)
        setCommentText(itemView.comment, comment.comment, comment.isBad)
        setCommentImage(itemView.comment_attach_image, comment.image)

        itemView.reply.setOnClickListener {
            onOpenDialog(showAddCommentDialog(itemView.context, comment.episodeId, comment.id, comment.user.login, onPostComment))
        }
        setVoteState(itemView.vote_up, comment.isMyPlus, R.drawable.upvote, R.drawable.upvote_active) {
            onVoteComment(CommentsView.VoteData(comment.episodeId, comment.id, true))
        }
        setVoteState(itemView.vote_down, comment.isMyMinus, R.drawable.downvote, R.drawable.downvote_active) {
            onVoteComment(CommentsView.VoteData(comment.episodeId, comment.id, false))
        }
    }

    private fun setDate(textView: TextView, time: Long) {
        textView.text = DateUtils.getRelativeTimeSpanString(time, System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS)
    }

    private fun setRating(textView: TextView, rating: Int) {
        textView.text = when {
            rating > 0 -> "+$rating"
            else -> rating.toString()
        }
        val color = when {
            rating < -Comment.BOUND -> ContextCompat.getColor(textView.context, R.color.negative_comment)
            rating > Comment.BOUND -> ContextCompat.getColor(textView.context, R.color.positive_comment)
            else -> ContextCompat.getColor(textView.context, R.color.neutral_comment)
        }
        textView.setTextColor(color)
    }

    private fun setCommentText(textView: TextView, comment: String, hideComment: Boolean) {
        if (hideComment) {
            textView.text = textView.resources.getString(R.string.hidden_comment)
            textView.alpha = 0.4F
            textView.setOnClickListener {
                setCommentText(textView, comment)
            }
        } else {
            setCommentText(textView, comment)
        }
    }

    private fun setCommentText(textView: TextView, comment: String) {
        textView.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.fromHtml(comment, Html.FROM_HTML_MODE_LEGACY) else Html.fromHtml(comment)
        textView.alpha = 1F
        textView.setOnClickListener(null)
    }

    private fun setCommentImage(imageView: ImageView, imageUrl: String?) {
        if (imageUrl == null) {
            imageView.visibility = View.GONE
        } else {
            imageView.visibility = View.VISIBLE
            Glide.with(imageView.context)
                    .load(imageUrl)
                    .apply(RequestOptions.fitCenterTransform())
                    .into(imageView)
        }
    }

    private fun setVoteState(textView: TextView, myVote: Boolean,
                             iconId: Int, activeIconId: Int, onVoteComment: (View) -> Unit) {
        if (myVote) {
            textView.setCompoundDrawablesWithIntrinsicBounds(activeIconId, 0, 0, 0)
            textView.setOnClickListener(null)
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(iconId, 0, 0, 0)
            textView.setOnClickListener(onVoteComment)
        }
    }
}

private class CommentsAdapter(
        private val offset: Int,
        private val onPostComment: (CommentsView.CommentData) -> Unit,
        private val onVoteComment: (CommentsView.VoteData) -> Unit,
        private val onOpenDialog: (AlertDialog) -> Unit
) : RecyclerView.Adapter<CommentHolder>() {

    private val comments: MutableList<CommentLevelData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.comment_layout, parent, false)
        return CommentHolder(view, offset)
    }

    override fun onBindViewHolder(holder: CommentHolder, position: Int) =
            holder.bind(comments[position], position == 0, onPostComment, onVoteComment, onOpenDialog)

    override fun getItemCount(): Int = comments.size

    fun updateComments(episodeComments: EpisodeComments) {
        comments.clear()
        comments += extractOrderedComments(episodeComments)
        notifyDataSetChanged()
    }

    fun upsertComment(comment: Comment) {
        var index = comments.indexOfFirst { it.comment.id == comment.id }
        if (index == -1) {
            val parentIndex = comments.indexOfFirst { it.comment.id == comment.parentId }
            index = if (parentIndex == -1) {
                comments += CommentLevelData(comment, INITIAL_NESTING_LEVEL)
                comments.size - 1
            } else {
                comments.add(parentIndex + 1, CommentLevelData(comment, comments[parentIndex].nestingLevel + 1))
                parentIndex + 1
            }
            notifyItemInserted(index)
        } else {
            comments[index] = CommentLevelData(comment, comments[index].nestingLevel)
            notifyItemChanged(index)
        }
    }

    private fun extractOrderedComments(episodeComments: EpisodeComments): List<CommentLevelData> {
        val parentIdToComments = HashMap<Int, MutableList<Comment>>()
        episodeComments.comments.forEach {
            val parentId = it.parentId ?: Comment.ROOT_ID
            val comments = parentIdToComments.getOrPut(parentId) { ArrayList() }
            comments += it
        }

        val orderedComments = ArrayList<CommentLevelData>()
        fun buildComments(parentId: Int, nestingLevel: Int) {
            parentIdToComments[parentId]
                    ?.sortedBy { it.createdAtMillis }
                    ?.forEach {
                        orderedComments += CommentLevelData(it, nestingLevel)
                        buildComments(it.id, nestingLevel + 1)
                    }
        }
        buildComments(Comment.ROOT_ID, INITIAL_NESTING_LEVEL)
        return orderedComments
    }
}

private fun showAddCommentDialog(context: Context, episodeId: Int,
                                 replyCommentId: Int, replyUsername: String?,
                                 onSuccess: (CommentsView.CommentData) -> Unit): AlertDialog {

    val commentEditText = buildCommentEditText(context, replyUsername)

    return AlertDialog.Builder(context)
            .setTitle(R.string.add_comment)
            .setView(commentEditText)
            .setCancelable(true)
            .setPositiveButton(R.string.post_comment, null)
            .setNegativeButton(R.string.cancel_comment) { dialog, _ -> dialog.dismiss() }
            .create()
            .apply {
                setOnShowListener {
                    getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val episodeComment = CommentsView.CommentData(episodeId, commentEditText.text.toString(), replyCommentId)
                        onSuccess(episodeComment)
                    }
                }
                show()
            }
}

@SuppressLint("SetTextI18n")
private fun buildCommentEditText(context: Context, replyUsername: String?): EditText {
    val marginTop = context.resources.getDimensionPixelSize(R.dimen.comment_dialog_padding_top)
    val margin = context.resources.getDimensionPixelSize(R.dimen.comment_dialog_padding)

    return EditText(context).apply {
        layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        )
        background = null
        gravity = Gravity.LEFT or Gravity.TOP
        setPadding(margin, marginTop, margin, margin)
        setLines(5)
        setTextColor(ContextCompat.getColor(context, R.color.dark_gray))
        setHint(R.string.comment_hint)
        if (replyUsername != null) append("@$replyUsername: ")
    }
}
