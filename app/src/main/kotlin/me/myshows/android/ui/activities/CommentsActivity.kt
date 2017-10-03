package me.myshows.android.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.comment_layout.view.*
import kotlinx.android.synthetic.main.comments_activity.*
import me.myshows.android.MyShowsApplication
import me.myshows.android.R
import me.myshows.android.api.MyShowsClient
import me.myshows.android.model.Comment
import me.myshows.android.model.EpisodeComments
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import kotlin.collections.HashMap

private const val INITIAL_NESTING_LEVEL = 1

const val EPISODE_ID = "episodeId"
const val EPISODE_TITLE = "episodeTitle"

class CommentsActivity : HomeActivity() {

    private val TAG = CommentsActivity::class.java.simpleName

    private lateinit var client: MyShowsClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comments_activity)

        client = MyShowsApplication.getMyShowsClient(this)

        setupActionBar(toolbar)
        setupRecyclerView(recycler_view)

        val episodeId = extractAndBindEpisodeData(intent)
        val offset = resources.getDimensionPixelSize(R.dimen.default_padding)
        loadData(episodeId, offset)
    }

    @SuppressLint("PrivateResource")
    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
    }

    private fun extractAndBindEpisodeData(intent: Intent): Int {
        supportActionBar?.title = intent.getStringExtra(EPISODE_TITLE)
        return intent.getIntExtra(EPISODE_ID, 0)
    }

    private fun loadData(episodeId: Int, offset: Int) {
        client.comments(episodeId)
                .compose(bindToLifecycle())
                .observeOn(Schedulers.computation())
                .map { extractOrderedComments(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { recycler_view.adapter = CommentsAdapter(it, offset) }
    }

    private fun extractOrderedComments(information: EpisodeComments): List<CommentData> {
        val parentIdToComments = HashMap<Int, MutableList<Comment>>()
        information.comments.forEach {
            val parentId = it.parentCommentId
            val comments = parentIdToComments.getOrPut(parentId) { ArrayList() }
            comments += it
        }

        val orderedComments = ArrayList<CommentData>()
        fun buildComments(parentId: Int, nestingLevel: Int) {
            parentIdToComments[parentId]
                    ?.sortedBy { it.createdAtMillis }
                    ?.forEach {
                        orderedComments += CommentData(it, nestingLevel)
                        buildComments(it.userCommentId, nestingLevel + 1)
                    }
        }
        buildComments(Comment.ROOT_ID, INITIAL_NESTING_LEVEL)
        return orderedComments
    }
}

private data class CommentData(val comment: Comment, val nestingLevel: Int)

private class CommentHolder(
        itemView: View,
        private val offset: Int
) : RecyclerView.ViewHolder(itemView) {

    fun bind(commentData: CommentData, isFirst: Boolean) {
        val (comment, nestedLevel) = commentData
        val leftPadding = offset * nestedLevel
        itemView.setPadding(leftPadding, if (isFirst) offset else 0, 0, 0)

        Glide.with(itemView.context)
                .load(comment.siteUser.avatarUrl)
                .apply(RequestOptions.centerCropTransform())
                .into(itemView.avatar)

        itemView.username.text = comment.siteUser.login
        setDate(itemView.date, comment.createdAtMillis)
        setRating(itemView.rating, comment.rating)
        setCommentText(itemView.comment, comment.comment, comment.isBad)
        setCommentImage(itemView.comment_image, comment.image)

        changeVoteState(itemView.vote_up, comment.isMyPlus, R.drawable.upvote, R.drawable.upvote_active)
        changeVoteState(itemView.vote_down, comment.isMyMinus, R.drawable.downvote, R.drawable.downvote_active)
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
        textView.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(comment, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(comment)
        }
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
                    .into(imageView)
        }
    }

    private fun changeVoteState(textView: TextView, myVote: Boolean,
                                iconId: Int, activeIconId: Int) {
        if (myVote) {
            textView.setCompoundDrawablesWithIntrinsicBounds(activeIconId, 0, 0, 0)
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(iconId, 0, 0, 0)
        }
    }
}

private class CommentsAdapter(
        private val comments: List<CommentData>,
        private val offset: Int
) : RecyclerView.Adapter<CommentHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.comment_layout, parent, false)
        return CommentHolder(view, offset)
    }

    override fun onBindViewHolder(holder: CommentHolder, position: Int) =
            holder.bind(comments[position], position == 0)

    override fun getItemCount(): Int = comments.size
}

