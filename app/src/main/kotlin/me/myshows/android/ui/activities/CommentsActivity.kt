package me.myshows.android.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.format.DateUtils
import android.util.Pair
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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

private const val ROOT_COMMENTS = 0
private const val INITIAL_NESTING_LEVEL = 1

private const val COMMENT_BOUND = 25

const val EPISODE_ID = "episodeId"
const val EPISODE_TITLE = "episodeTitle"

class CommentsActivity : HomeActivity() {

    private val TAG = CommentsActivity::class.java.simpleName

    private lateinit var client: MyShowsClient

    private var episodeId: Int = 0
    private var offset: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comments_activity)

        client = MyShowsApplication.getMyShowsClient(this)

        setupActionBar(toolbar)
        setupRecyclerView(recycler_view)
        setupRefreshLayout(swipe_layout)
        setupFab(new_comment)
        setupRecyclerViewScrollBehaviour(recycler_view, swipe_layout, new_comment)

        episodeId = extractAndBindEpisodeData(intent)
        offset = resources.getDimensionPixelSize(R.dimen.default_padding)
        loadData()
    }

    @SuppressLint("PrivateResource")
    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
    }

    private fun setupRefreshLayout(swipeLayout: SwipeRefreshLayout) {
        swipeLayout.setColorSchemeResources(R.color.primaryDark, R.color.primary)
        swipeLayout.setOnRefreshListener { loadData() }
    }

    private fun setupFab(fab: FloatingActionButton) {
        fab.setOnClickListener { showAddCommentDialog(this, null) }
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

    private fun extractAndBindEpisodeData(intent: Intent): Int {
        supportActionBar?.title = intent.getStringExtra(EPISODE_TITLE)
        return intent.getIntExtra(EPISODE_ID, 0)
    }

    private fun loadData() {
        client.comments(episodeId)
                .compose(bindToLifecycle())
                .observeOn(Schedulers.computation())
                .map { extractOrderedComments(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    recycler_view.adapter = CommentsAdapter(it, offset)
                    swipe_layout.isRefreshing = false
                }
    }

    private fun extractOrderedComments(information: EpisodeComments): List<Pair<Comment, Int>> {
        val parentIdToComments = TreeMap<Int, MutableList<Comment>>()
        information.comments.forEach {
            val parentId = it.parentCommentId
            val comments = parentIdToComments.getOrPut(parentId) { ArrayList() }
            comments += it
        }

        val comments = ArrayList<Pair<Comment, Int>>()
        buildComments(comments, INITIAL_NESTING_LEVEL, parentIdToComments[ROOT_COMMENTS], parentIdToComments)
        return comments
    }

    private fun buildComments(result: MutableList<Pair<Comment, Int>>, nestingLevel: Int,
                              comments: List<Comment>?, parentIdToComments: Map<Int, List<Comment>>) {
        comments?.sortedBy { it.createdAtMillis }
                ?.forEach {
                    result += Pair(it, nestingLevel)
                    val id = it.userCommentId
                    if (parentIdToComments.contains(id)) {
                        buildComments(result, nestingLevel + 1, parentIdToComments[id], parentIdToComments)
                    }
                }
    }

    private class CommentHolder(
            itemView: View,
            private val offset: Int
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind(pair: Pair<Comment, Int>, isFirst: Boolean) {
            val leftPadding = offset * pair.second
            itemView.setPadding(leftPadding, if (isFirst) offset else 0, 0, 0)

            val comment = pair.first
            Glide.with(itemView.context)
                    .load(comment.siteUser.avatarUrl)
                    .apply(RequestOptions.centerCropTransform())
                    .into(itemView.avatar)

            itemView.username.text = comment.siteUser.login
            setDate(itemView.date, comment.createdAtMillis)
            setRating(itemView.rating, comment.rating)
            setCommentText(itemView.comment, comment.comment, comment.isBad)
            setCommentImage(itemView.comment_attach_image, comment.image)

            itemView.reply.setOnClickListener { showAddCommentDialog(itemView.context, comment.siteUser.login) }
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
            textView.setTextColor(
                    when {
                        rating < -COMMENT_BOUND -> ContextCompat.getColor(textView.context, R.color.negative_comment)
                        rating > COMMENT_BOUND -> ContextCompat.getColor(textView.context, R.color.positive_comment)
                        else -> ContextCompat.getColor(textView.context, R.color.neutral_comment)
                    }
            )
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
                        .apply(RequestOptions.centerCropTransform())
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
            private val comments: List<Pair<Comment, Int>>,
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

    companion object {
        private fun showAddCommentDialog(context: Context, replyUsername: String?) {
            val commentEditText = buildCommentEditText(context, replyUsername)

            AlertDialog.Builder(context)
                    .setTitle(R.string.add_comment)
                    .setView(commentEditText)
                    .setCancelable(true)
                    .setPositiveButton(R.string.post_comment) { dialog, _ ->
                        Toast.makeText(context, commentEditText.text, Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.cancel_comment) { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
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
                if (replyUsername != null) {
                    append("@$replyUsername: ")
                }
            }
        }
    }
}
