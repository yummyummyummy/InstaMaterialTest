package com.example.woong.instamaterialtest.ui.activity

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

import com.example.woong.instamaterialtest.R
import com.example.woong.instamaterialtest.ui.adapter.CommentsAdapter
import com.example.woong.instamaterialtest.R.layout.activity_comments
import com.example.woong.instamaterialtest.Utils
import com.example.woong.instamaterialtest.ui.view.SendCommentButton

import kotlinx.android.synthetic.main.activity_comments.*

import kotlinx.android.synthetic.main.activity_comments.view.*

/**
 * Created by woong on 2017. 8. 29..
 */
class CommentsActivity: BaseDrawerActivity(), SendCommentButton.OnSendClickListener {
    companion object{
        val ARG_DRAWING_START_LOCATION = "arg_drawing_start_location"
    }
    private var commentsAdapter: CommentsAdapter? = null
    private var drawingStartLocation: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        setupComments()
        setupSendCommentButton()

        drawingStartLocation = intent.getIntExtra(ARG_DRAWING_START_LOCATION, 0)
        if (savedInstanceState == null) {
            contentRoot.getViewTreeObserver().addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    contentRoot.getViewTreeObserver().removeOnPreDrawListener(this)
                    startIntroAnimation()
                    return true
                }
            })
        }
    }

    private fun setupComments() {
        val linearLayoutManager = LinearLayoutManager(this)
        rvComments.setLayoutManager(linearLayoutManager)
        rvComments.setHasFixedSize(true)

        commentsAdapter = CommentsAdapter(this)
        rvComments.setAdapter(commentsAdapter)
        rvComments.setOverScrollMode(View.OVER_SCROLL_NEVER)
        rvComments.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    commentsAdapter!!.setAnimationsLocked(true)
                }
            }
        })
    }

    private fun setupSendCommentButton() {
        btnSendComment.setOnSendClickListener(this)
    }

    private fun startIntroAnimation() {
        ViewCompat.setElevation(getToolbar(), 0f)
        contentRoot.setScaleY(0.1f)
        contentRoot.setPivotY(drawingStartLocation.toFloat())
        llAddComment.setTranslationY(200f)

        contentRoot.animate()
                .scaleY(1f)
                .setDuration(200)
                .setInterpolator(AccelerateInterpolator())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        ViewCompat.setElevation(getToolbar(), Utils.dpToPx(8).toFloat())
                        animateContent()
                    }
                })
                .start()
    }

    private fun animateContent() {
        commentsAdapter!!.updateItems()
        llAddComment.animate().translationY(0f)
                .setInterpolator(DecelerateInterpolator())
                .setDuration(200)
                .start()
    }

    override fun onBackPressed() {
        ViewCompat.setElevation(getToolbar(), 0f)
        contentRoot.animate()
                .translationY(Utils.getScreenHeight(this).toFloat())
                .setDuration(200)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super@CommentsActivity.onBackPressed()
                        overridePendingTransition(0, 0)
                    }
                })
                .start()
    }

    override fun onSendClickListener(v: View) {
        if (validateComment()) {
            commentsAdapter!!.addItem()
            commentsAdapter!!.setAnimationsLocked(false)
            commentsAdapter!!.setDelayEnterAnimation(false)
            rvComments.smoothScrollBy(0, rvComments.getChildAt(0).getHeight() * commentsAdapter!!.getItemCount())

            etComment.setText(null)
            btnSendComment.setCurrentState(SendCommentButton.STATE_DONE)
        }
    }

    private fun validateComment(): Boolean {
        if (TextUtils.isEmpty(etComment.getText())) {
            btnSendComment.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error))
            return false
        }

        return true
    }
}