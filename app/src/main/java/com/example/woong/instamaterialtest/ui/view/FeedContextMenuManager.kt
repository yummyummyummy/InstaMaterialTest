package com.example.woong.instamaterialtest.ui.view

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.example.woong.instamaterialtest.Utils

/**
 * Created by woong on 2017. 9. 1..
 */
class FeedContextMenuManager: RecyclerView.OnScrollListener, View.OnAttachStateChangeListener {
    companion object {
        var instance: FeedContextMenuManager? = null
            get() = if (instance == null) FeedContextMenuManager() else instance
            private set
    }


    private var contextMenuView: FeedContextMenu? = null

    private var isContextMenuDismissing: Boolean = false
    private var isContextMenuShowing: Boolean = false



    constructor () {

    }

    fun toggleContextMenuFromView(openingView: View, feedItem: Int, listener: FeedContextMenu.OnFeedContextMenuItemClickListener) {
        if (contextMenuView == null) {
            showContextMenuFromView(openingView, feedItem, listener)
        } else {
            hideContextMenu()
        }
    }

    private fun showContextMenuFromView(openingView: View, feedItem: Int, listener: FeedContextMenu.OnFeedContextMenuItemClickListener) {
        if (!isContextMenuShowing) {
            isContextMenuShowing = true
            contextMenuView = FeedContextMenu(openingView.getContext())
            contextMenuView?.bindToItem(feedItem)
            contextMenuView?.addOnAttachStateChangeListener(this)
            contextMenuView?.setOnFeedMenuItemClickListener(listener)

            openingView.getRootView().findViewById<ViewGroup>(android.R.id.content).addView(contextMenuView)

            contextMenuView?.getViewTreeObserver()?.addOnPreDrawListener(object: ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    contextMenuView!!.getViewTreeObserver().removeOnPreDrawListener(this)
                    setupContextMenuInitialPosition(openingView)
                    performShowAnimation()
                    return false
                }
            })

        }
    }

    private fun setupContextMenuInitialPosition(openingView: View) {
        var openingViewLocation: IntArray = intArrayOf(0, 0)
        openingView.getLocationOnScreen(openingViewLocation)
        var additionalBottomMargin: Int = Utils.dpToPx(16)
        contextMenuView!!.setTranslationX(openingViewLocation[0] - contextMenuView!!.getWidth() / 3f)
        contextMenuView!!.setTranslationY(openingViewLocation[1] - contextMenuView!!.getHeight() - additionalBottomMargin.toFloat())
    }

    private fun performShowAnimation() {
        contextMenuView!!.setPivotX(contextMenuView!!.getWidth() / 2f)
        contextMenuView!!.setPivotY(contextMenuView!!.getHeight().toFloat())
        contextMenuView!!.setScaleX(0.1f)
        contextMenuView!!.setScaleY(0.1f)
        contextMenuView!!.animate()
                .scaleX(1f).scaleY(1f)
                .setInterpolator(OvershootInterpolator())
                .setListener(object: AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        isContextMenuShowing = false
                    }
                })
    }

    fun hideContextMenu() {
        if (!isContextMenuDismissing) {
            isContextMenuDismissing = true
            performDismissAnimation()
        }
    }

    private fun performDismissAnimation() {
        contextMenuView!!.setPivotX(contextMenuView!!.getWidth() / 2f)
        contextMenuView!!.setPivotY(contextMenuView!!.getHeight().toFloat())
        contextMenuView!!.animate()
                .scaleX(0.1f).scaleY(0.1f)
                .setDuration(150)
                .setInterpolator(AccelerateInterpolator())
                .setStartDelay(100)
                .setListener(object: AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        if (contextMenuView != null) {
                            contextMenuView!!.dismiss()
                        }
                        isContextMenuDismissing = false
                    }
                })
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (contextMenuView != null) {
            hideContextMenu()
            contextMenuView!!.setTranslationY(contextMenuView!!.getTranslationY() - dy)
        }
    }

    override fun onViewAttachedToWindow(v: View) {

    }

    override fun onViewDetachedFromWindow(v: View) {
        contextMenuView = null
    }

}