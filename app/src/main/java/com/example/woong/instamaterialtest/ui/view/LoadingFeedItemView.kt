package com.example.woong.instamaterialtest.ui.view

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.woong.instamaterialtest.R
import com.example.woong.instamaterialtest.ui.view.LoadingFeedItemView
import kotlinx.android.synthetic.main.item_feed_loader.view.*

/**
 * Created by woong on 2017. 8. 30..
 */
open class LoadingFeedItemView: FrameLayout {
    var onLoadingFinishedListener: OnLoadingFinishedListener? = null
      set(onLoadingFinishedListener) {
          this.onLoadingFinishedListener = onLoadingFinishedListener
      }

    //private var vSendingProgressv: SendingProgressView = vSendingProgress

    constructor(context: Context) : super(context) {init()}
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {init()}
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {init()}
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes) {init()}

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.item_feed_loader, this, true)
    }

    open fun startLoading() {
        vSendingProgress.viewTreeObserver.addOnPreDrawListener(object: ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                vSendingProgress.viewTreeObserver.removeOnPreDrawListener(this)
                vSendingProgress.simulateProgress()
                return true
            }
        })
        vSendingProgress.setOnLoadingFinishedListener(object: SendingProgressView.OnLoadingFinishedListener {
            override fun onLoadingFinished() {
                vSendingProgress.animate().scaleY(0f).scaleX(0f).setDuration(200).setStartDelay(100)
                vProgressBg.animate().alpha(0.0f).setDuration(200).setStartDelay(100)
                        .setListener(object: AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                vSendingProgress.scaleX = 1f
                                vSendingProgress.scaleY = 1f
                                vProgressBg.alpha = 1f
                                if (onLoadingFinishedListener != null) {
                                    onLoadingFinishedListener?.onLoadingFinished()
                                    onLoadingFinishedListener = null
                                }
                            }
                        }).start()
            }
        })
    }

    open interface OnLoadingFinishedListener {
        fun onLoadingFinished()
    }


}
