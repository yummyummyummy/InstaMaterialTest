package com.example.woong.instamaterialtest.ui.view

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ViewAnimator;
import com.example.woong.instamaterialtest.R

/**
 * Created by woong on 2017. 9. 3..
 */
open class SendCommentButton: ViewAnimator, View.OnClickListener {
    companion object {
        val STATE_SEND = 0
        val STATE_DONE = 1
    }

    private val RESET_STATE_DELAY_MILLIS: Long = 2000

    private var currentState: Int = 0

    private var onSendClickListener: OnSendClickListener? = null

    private val revertStateRunnable = Runnable { setCurrentState(STATE_SEND) }

    constructor (context: Context): super(context) {init()}

    constructor (context:Context, attrs: AttributeSet): super(context, attrs) {init()}

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.view_send_comment_button, this, true)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        currentState = STATE_SEND
        super.setOnClickListener(this)
    }

    override fun onDetachedFromWindow() {
        removeCallbacks(revertStateRunnable)
        super.onDetachedFromWindow()
    }

    open fun setCurrentState(state: Int) {
        if (state == currentState) {
            return
        }

        currentState = state
        if (state == STATE_DONE) {
            isEnabled = false
            postDelayed(revertStateRunnable, RESET_STATE_DELAY_MILLIS)
            setInAnimation(context, R.anim.slide_in_done)
            setOutAnimation(context, R.anim.slide_out_send)
        } else if (state == STATE_SEND) {
            isEnabled = true
            setInAnimation(context, R.anim.slide_in_send)
            setOutAnimation(context, R.anim.slide_out_done)
        }
        showNext()
    }

    override fun onClick(v: View) {
        if (onSendClickListener != null) {
            onSendClickListener!!.onSendClickListener(this)
        }
    }

    open fun setOnSendClickListener(onSendClickListener: OnSendClickListener) {
        this.onSendClickListener = onSendClickListener
    }

    override fun setOnClickListener(l: View.OnClickListener?) {

    }

    interface OnSendClickListener {
        fun onSendClickListener(v: View)
    }

}