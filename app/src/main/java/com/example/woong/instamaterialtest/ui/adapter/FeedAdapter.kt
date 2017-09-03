package com.example.woong.instamaterialtest.ui.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextSwitcher

import java.util.ArrayList
import java.util.Arrays
import java.util.List

import butterknife.BindView
import butterknife.ButterKnife
import com.example.woong.instamaterialtest.R
import com.example.woong.instamaterialtest.ui.activity.MainActivity
import com.example.woong.instamaterialtest.ui.adapter.FeedAdapter.FeedItem
import com.example.woong.instamaterialtest.ui.view.LoadingFeedItemView

import kotlinx.android.synthetic.main.item_feed.view.*
//import kotlinx.android.synthetic.main.item_feed_loader.view.*

/**
 * Created by woong on 2017. 8. 30..
 */
class FeedAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder> {
    companion object {
        val ACTION_LIKE_BUTTON_CLICKED = "action_like_button_button"
        val ACTION_LIKE_IMAGE_CLICKED = "action_like_image_button"

        val VIEW_TYPE_DEFAULT = 1
        val VIEW_TYPE_LOADER = 2
    }

    private var feedItems = ArrayList<FeedItem>()

    private var context: Context
    private var onFeedItemClickListener: OnFeedItemClickListener? = null

    private var showLoadingView = false

    constructor (context: Context) {
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        if (viewType == VIEW_TYPE_DEFAULT) {
            var view: View = LayoutInflater.from(context).inflate(R.layout.item_feed, parent, false)
            var cellFeedViewHolder: CellFeedViewHolder = CellFeedViewHolder(view)
            setupClickableViews(view, cellFeedViewHolder)
            return cellFeedViewHolder
        } else if (viewType == VIEW_TYPE_LOADER) {
            var view: LoadingFeedItemView = LoadingFeedItemView(context)
            view.layoutParams = LinearLayoutCompat.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            return LoadingCellFeedViewHolder(view)
        }
        return null
    }

    private fun setupClickableViews(view: View, cellFeedViewHolder: CellFeedViewHolder) {

        cellFeedViewHolder.itemView.btnComments?.setOnClickListener() {
            onFeedItemClickListener?.onCommentsClick(view, cellFeedViewHolder.getAdapterPosition())
        }
        cellFeedViewHolder.itemView.btnMore?.setOnClickListener() {
            onFeedItemClickListener?.onMoreClick(view, cellFeedViewHolder.getAdapterPosition())
        }
        cellFeedViewHolder.itemView.ivFeedCenter?.setOnClickListener() {
            var adapterPosition: Int = cellFeedViewHolder.getAdapterPosition()
            feedItems[adapterPosition].likesCount++
            notifyItemChanged(adapterPosition, ACTION_LIKE_IMAGE_CLICKED)
            if (context is MainActivity) {
                (context as MainActivity).showLikedSnackbar()
            }
        }
        cellFeedViewHolder.itemView.btnLike?.setOnClickListener() {
            var adapterPosition: Int = cellFeedViewHolder.getAdapterPosition()
            feedItems[adapterPosition].likesCount++
            notifyItemChanged(adapterPosition, ACTION_LIKE_BUTTON_CLICKED)
            if (context is MainActivity) {
                (context as MainActivity).showLikedSnackbar()
            }
        }
        cellFeedViewHolder.itemView.ivUserProfile?.setOnClickListener() {
            onFeedItemClickListener?.onProfileClick(view)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        (viewHolder as CellFeedViewHolder).bindView(feedItems[position])

        if (getItemViewType(position) == VIEW_TYPE_LOADER) {
            bindLoadingFeedItem(viewHolder as LoadingCellFeedViewHolder)
        }
    }

    private fun bindLoadingFeedItem(holder: LoadingCellFeedViewHolder) {
        holder.loadingFeedItemView?.onLoadingFinishedListener = (object : LoadingFeedItemView.OnLoadingFinishedListener {
            override fun onLoadingFinished() {
                showLoadingView = false
                notifyItemChanged(0)
            }
        })
        holder.loadingFeedItemView?.startLoading()
    }

    override fun getItemViewType(position: Int): Int {
        return (if (showLoadingView && position == 0) VIEW_TYPE_LOADER else VIEW_TYPE_DEFAULT)
    }

    override fun getItemCount(): Int {
        return feedItems.size
    }

    fun updateItems(animated: Boolean) {
        feedItems.clear()
        feedItems.addAll(Arrays.asList(
                FeedItem(33, false),
                FeedItem(1, false),
                FeedItem(223, false),
                FeedItem(2, false),
                FeedItem(6, false),
                FeedItem(8, false),
                FeedItem(99, false)
        ))
        if (animated) {
            notifyItemRangeInserted(0, feedItems.size)
        } else {
            notifyDataSetChanged()
        }
    }

    fun setOnFeedItemClickListener(onFeedItemClickListener: OnFeedItemClickListener) {
        this.onFeedItemClickListener = onFeedItemClickListener
    }

    fun showLoadingView() {
        showLoadingView = true
        notifyItemChanged(0)
    }

    inner open class CellFeedViewHolder : RecyclerView.ViewHolder {

        var feedItem: FeedItem = FeedItem(0, false)

        constructor (view: View): super(view)

        open fun bindView(feedItem: FeedItem) {
            this.feedItem = feedItem
            var adapterPosition: Int = adapterPosition
            itemView.ivFeedCenter?.setImageResource(
                    if (adapterPosition % 2 == 0) R.drawable.img_feed_center_1 else R.drawable.img_feed_center_2)
            itemView.ivFeedBottom?.setImageResource(
                    if (adapterPosition % 2 == 0) R.drawable.img_feed_bottom_1 else R.drawable.img_feed_bottom_2)
            itemView.btnLike?.setImageResource(
                    if (feedItem.isLiked) R.drawable.ic_heart_red else R.drawable.ic_heart_outline_grey)
            itemView.tsLikesCounter?.setCurrentText(itemView.vImageRoot?.getResources()?.getQuantityString(
                    R.plurals.likes_count, feedItem.likesCount, feedItem.likesCount
            ))
        }

    }

    inner class LoadingCellFeedViewHolder : CellFeedViewHolder {
        var loadingFeedItemView: LoadingFeedItemView? = null

        constructor (view: LoadingFeedItemView) : super(view) {
            this.loadingFeedItemView = view
        }

        override fun bindView(feedItem: FeedItem) {
            super.bindView(feedItem)
        }
    }

    class FeedItem {

        var likesCount: Int = 0
        var isLiked: Boolean = false

        constructor (likesCount: Int, isLiked: Boolean) {
            this.likesCount = likesCount
            this.isLiked = isLiked

    }
}

    interface OnFeedItemClickListener {
        fun onCommentsClick(v: View, position: Int){}
        fun onMoreClick(v: View, position: Int){}
        fun onProfileClick(v: View){}
    }

}