package com.example.woong.instamaterialtest.ui.view

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by woong on 2017. 9. 3..
 */

class RevealBackgroundView: View {
    val STATE_NOT_STARTED = 0
    val STATE_FILL_STARTED = 1
    val STATE_FINISHED = 2

    private val INTERPOLATOR = AccelerateInterpolator()
    private val FILL_TIME = 400

    private var state = STATE_NOT_STARTED

    private var fillPaint: Paint? = null
    private var currentRadius: Int = 0
    var revealAnimator: ObjectAnimator? = null

    private var startLocationX: Int = 0
    private var startLocationY: Int = 0


    private var onStateChangeListener: OnStateChangeListener? = null

    constructor (context: Context): super(context) {init()}

    constructor (context: Context, attrs: AttributeSet): super(context, attrs) {init()}

    constructor (context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) {init()}

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor (context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int):
            super(context, attrs, defStyleAttr, defStyleRes) {init()}

    private fun init() {
        fillPaint = Paint()
        fillPaint!!.style = Paint.Style.FILL
        fillPaint!!.color = Color.WHITE
    }

    fun setFillPaintColor(color: Int) {
        fillPaint!!.color = color
    }

    fun startFromLocation(tapLocationOnScreen: IntArray) {
        changeState(STATE_FILL_STARTED)
        startLocationX = tapLocationOnScreen[0]
        startLocationY = tapLocationOnScreen[1]
        revealAnimator = ObjectAnimator.ofInt(this, "currentRadius", 0, width + height).setDuration(FILL_TIME.toLong())
        revealAnimator?.interpolator = INTERPOLATOR
        revealAnimator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                changeState(STATE_FINISHED)
            }
        })
        revealAnimator?.start()
    }

    fun setToFinishedFrame() {
        changeState(STATE_FINISHED)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (state == STATE_FINISHED) {
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), fillPaint!!)
        } else {
            canvas.drawCircle(startLocationX.toFloat(), startLocationY.toFloat(), currentRadius.toFloat(), fillPaint!!)
        }
    }

    private fun changeState(state: Int) {
        if (this.state == state) {
            return
        }

        this.state = state
        if (onStateChangeListener != null) {
            onStateChangeListener!!.onStateChange(state)
        }
    }

    fun setOnStateChangeListener(onStateChangeListener: OnStateChangeListener) {
        this.onStateChangeListener = onStateChangeListener
    }

    fun setCurrentRadius(radius: Int) {
        this.currentRadius = radius
        invalidate()
    }

    interface OnStateChangeListener {
        fun onStateChange(state: Int)
    }

}