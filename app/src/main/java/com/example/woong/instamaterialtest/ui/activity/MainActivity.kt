package com.example.woong.instamaterialtest.ui.activity

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import butterknife.BindView;
import butterknife.OnClick;

import com.example.woong.instamaterialtest.R
import com.example.woong.instamaterialtest.Utils
import com.example.woong.instamaterialtest.ui.adapter.FeedAdapter;
import com.example.woong.instamaterialtest.ui.adapter.FeedItemAnimator;
import com.example.woong.instamaterialtest.ui.view.FeedContextMenu;
import com.example.woong.instamaterialtest.ui.view.FeedContextMenuManager;

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*



class MainActivity : BaseDrawerActivity(), FeedAdapter.OnFeedItemClickListener, FeedContextMenu.OnFeedContextMenuItemClickListener {
    val ACTION_SHOW_LOADING_ITEM: String = "action_show_loading_item"
    val ANIM_DURATION_TOOLBAR: Int = 300
    val ANIM_DURATION_FAB: Int = 400

    var feedAdapter: FeedAdapter? = null
    var pendingIntroAnimation: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupFeed()

        if (savedInstanceState == null)
            pendingIntroAnimation = true
        else
            feedAdapter?.updateItems(false)
    }

    fun setupFeed() {
        val linearLayoutManager = object : LinearLayoutManager(this) {
            override fun getExtraLayoutSpace(state: RecyclerView.State): Int {
                    return 300
            }
        }
        rvFeed.layoutManager = linearLayoutManager

        var feedAdapter = FeedAdapter(this)
        feedAdapter.setOnFeedItemClickListener(this as FeedAdapter.OnFeedItemClickListener)
        rvFeed.adapter = feedAdapter
        rvFeed.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                FeedContextMenuManager.instance?.onScrolled(recyclerView, dx, dy)
            }
        })
        rvFeed.itemAnimator = FeedItemAnimator()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (ACTION_SHOW_LOADING_ITEM.equals(intent.getAction())) {
            showFeedLoadingItemDelayed()
        }
    }

    private fun showFeedLoadingItemDelayed() {
        Handler().postDelayed(object: Runnable {
            override fun run() {
                rvFeed.smoothScrollToPosition(0)
                feedAdapter?.showLoadingView()
            }
        }, 500)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        if (pendingIntroAnimation) {
            pendingIntroAnimation = false
            startIntroAnimation()
        }
        return true
    }

    private fun startIntroAnimation() {
        btnCreate.setTranslationY(2f * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size))

        val actionbarSize: Int = Utils.dpToPx(56)
        getToolbar()?.translationY = -actionbarSize.toFloat()
        getIvLogo()?.translationY = -actionbarSize.toFloat()
        getInboxMenuItem()?.getActionView()?.translationY = -actionbarSize.toFloat()

        getToolbar()?.animate()
                ?.translationY(0f)
                ?.setDuration(ANIM_DURATION_TOOLBAR.toLong())
                ?.setStartDelay(300)
        getIvLogo()?.animate()
                ?.translationY(0f)
                ?.setDuration(ANIM_DURATION_TOOLBAR.toLong())
                ?.setStartDelay(400)
        getInboxMenuItem()?.getActionView()?.animate()
                ?.translationY(0f)
                ?.setDuration(ANIM_DURATION_TOOLBAR.toLong())
                ?.setStartDelay(500)
                ?.setListener(object: AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        startContentAnimation()
                    }
                })?.start()
    }

    private fun startContentAnimation() {
        btnCreate.animate()
                .translationY(0f)
                .setInterpolator(OvershootInterpolator(1f))
                .setStartDelay(300)
                .setDuration(ANIM_DURATION_FAB.toLong())
                .start()
        feedAdapter?.updateItems(true)
    }

    override fun onCommentsClick(v: View, position: Int) {
        val intent:Intent = Intent(this, CommentsActivity::class.java)
        var startingLocation: IntArray = intArrayOf(0, 0)
        v.getLocationOnScreen(startingLocation)
        startingLocation[0] += v.getWidth() / 2
        intent.putExtra(CommentsActivity.ARG_DRAWING_START_LOCATION, startingLocation[1])
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    override fun onMoreClick(v: View, itemPosition: Int) {
        FeedContextMenuManager.instance!!.toggleContextMenuFromView(v, itemPosition, this)
    }

    override fun onProfileClick(v: View) {
        val startingLocation = IntArray(2)
        v.getLocationOnScreen(startingLocation)
        startingLocation[0] += v.width / 2
        //UserProfileActivity.startUserProfileFromLocation(startingLocation, this)
        overridePendingTransition(0, 0)
    }

    override fun onReportClick(feedItem: Int) {
        FeedContextMenuManager.instance!!.hideContextMenu()
    }

    override fun onSharePhotoClick(feedItem: Int) {
        FeedContextMenuManager.instance!!.hideContextMenu()
    }

    override fun onCopyShareUrlClick(feedItem: Int) {
        FeedContextMenuManager.instance!!.hideContextMenu()
    }

    override fun onCancelClick(feedItem: Int) {
        FeedContextMenuManager.instance!!.hideContextMenu()
    }
/*
    @OnClick(R.id.btnCreate)
    fun onTakePhotoClick() {
        val startingLocation = IntArray(2)
        fabCreate.getLocationOnScreen(startingLocation)
        startingLocation[0] += fabCreate.getWidth() / 2
        TakePhotoActivity.startCameraFromLocation(startingLocation, this)
        overridePendingTransition(0, 0)
    }
*/
    fun showLikedSnackbar() {
        Snackbar.make(content, "Liked!", Snackbar.LENGTH_SHORT).show()
    }
}
