package com.example.woong.instamaterialtest.ui.view


import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.ButterKnife;
import butterknife.OnClick;
import com.example.woong.instamaterialtest.R;
import com.example.woong.instamaterialtest.Utils;
/**
 * Created by woong on 2017. 9. 1..
 */
open class FeedContextMenu: LinearLayout{
    private val CONTEXT_MENU_WIDTH: Int = Utils.dpToPx(240)

    private var feedItem: Int = -1

    private var onItemClickListener: OnFeedContextMenuItemClickListener? = null

    constructor (context: Context): super(context) { init() }

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.view_context_menu, this, true)
        setBackgroundResource(R.drawable.bg_container_shadow)
        orientation = (VERTICAL)
        layoutParams = LayoutParams(CONTEXT_MENU_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun bindToItem(feedItem: Int) { this.feedItem = feedItem}

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    fun dismiss() {(getParent() as ViewGroup).removeView(this)}

    fun setOnFeedMenuItemClickListener(onItemClickListener: OnFeedContextMenuItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnFeedContextMenuItemClickListener {
        fun onReportClick(feedItem: Int)

        fun onSharePhotoClick(feedItem: Int)

        fun onCopyShareUrlClick(feedItem: Int)

        fun onCancelClick(feedItem: Int)
    }
}