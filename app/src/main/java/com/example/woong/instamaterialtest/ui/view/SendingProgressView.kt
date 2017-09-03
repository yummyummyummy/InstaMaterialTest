package com.example.woong.instamaterialtest.ui.view

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.example.woong.instamaterialtest.R
import com.example.woong.instamaterialtest.ui.view.LoadingFeedItemView.OnLoadingFinishedListener

/**
 * Created by woong on 2017. 8. 31..
 */
class SendingProgressView: View {

    val STATE_NOT_STARTED = 0
    val STATE_PROGRESS_STARTED = 1
    val STATE_DONE_STARTED = 2
    val STATE_FINISHED = 3

    private val PROGRESS_STROKE_SIZE = 10
    private val INNER_CIRCLE_PADDING = 30
    private val MAX_DONE_BG_OFFSET = 800
    private val MAX_DONE_IMG_OFFSET = 400

    private var state = STATE_NOT_STARTED
    private var currentProgress = 0f
    private var currentDoneBgOffset = MAX_DONE_BG_OFFSET.toFloat()
    private var currentCheckmarkOffset = MAX_DONE_IMG_OFFSET.toFloat()

    private var progressPaint: Paint? = null
    private var doneBgPaint: Paint? = null
    private var maskPaint: Paint? = null

    private var progressBounds: RectF? = null

    private var checkmarkBitmap: Bitmap? = null
    private var innerCircleMaskBitmap: Bitmap? = null

    private var checkmarkXPosition = 0
    private var checkmarkYPosition = 0

    private var checkmarkPaint: Paint? = null
    private var tempBitmap: Bitmap? = null
    private var tempCanvas: Canvas? = null

    private var simulateProgressAnimator: ObjectAnimator? = null
    private var doneBgAnimator: ObjectAnimator? = null
    private var checkmarkAnimator: ObjectAnimator? = null

    private var onLoadingFinishedListener: OnLoadingFinishedListener? = null

    constructor (context: Context) : super(context) { init() }
    constructor (context: Context, attrs: AttributeSet) : super(context, attrs) { init() }
    constructor (context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { init() }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor (context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) { init() }

    private fun init() {
        setupProgressPaint()
        setupDonePaints()
        setupSimulateProgressAnimator()
        setupDoneAnimators()
    }

    private fun setupProgressPaint() {
        progressPaint = Paint()
        progressPaint?.setAntiAlias(true)
        progressPaint?.setStyle(Paint.Style.STROKE)
        progressPaint?.setColor(0xffffffff.toInt())
        progressPaint?.setStrokeWidth(PROGRESS_STROKE_SIZE.toFloat())
    }

    private fun setupSimulateProgressAnimator() {
        simulateProgressAnimator = ObjectAnimator.ofFloat(this, "currentProgress", 0F, 100F).setDuration(2000)
        simulateProgressAnimator?.setInterpolator(AccelerateInterpolator())
        simulateProgressAnimator?.addListener (object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                changeState(STATE_DONE_STARTED)
            }
        })
    }

    private fun setupDonePaints() {
        doneBgPaint = Paint()
        doneBgPaint?.setAntiAlias(true)
        doneBgPaint?.setStyle(Paint.Style.FILL)
        doneBgPaint?.setColor(0xff39cb72.toInt())

        checkmarkPaint = Paint()

        maskPaint = Paint()
        maskPaint?.setXfermode(PorterDuffXfermode(PorterDuff.Mode.DST_IN))
    }

    private fun setupDoneAnimators() {
        doneBgAnimator = ObjectAnimator.ofFloat(this, "currentDoneBgOffset", MAX_DONE_BG_OFFSET.toFloat(), 0F)
        doneBgAnimator?.setInterpolator(DecelerateInterpolator())

        checkmarkAnimator = ObjectAnimator.ofFloat(this, "currentCheckmarkOffset", MAX_DONE_IMG_OFFSET.toFloat(), 0F).setDuration(300)
        checkmarkAnimator?.setInterpolator(OvershootInterpolator())
        checkmarkAnimator?.addListener (object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                changeState(STATE_FINISHED)
            }
        })
    }

    protected override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateProgressBounds()
        setupCheckmarkBitmap()
        setupDoneMaskBitmap()
        resetTempCanvas()
    }

    private fun updateProgressBounds() {
        progressBounds = RectF(
                PROGRESS_STROKE_SIZE.toFloat(), PROGRESS_STROKE_SIZE.toFloat(),
                getWidth() - PROGRESS_STROKE_SIZE.toFloat(), getWidth() - PROGRESS_STROKE_SIZE.toFloat()
        )
    }

    private fun setupCheckmarkBitmap() {
        checkmarkBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_48dp)
        checkmarkXPosition = (getWidth() / 2).minus(checkmarkBitmap!!.getWidth().div(2))
        checkmarkYPosition = (getWidth() / 2).minus(checkmarkBitmap!!.getHeight().div(2))
    }

    private fun setupDoneMaskBitmap() {
        innerCircleMaskBitmap = Bitmap.createBitmap(getWidth(), getWidth(), Bitmap.Config.ARGB_8888)
        var srcCanvas: Canvas = Canvas(innerCircleMaskBitmap)
        srcCanvas.drawCircle(getWidth() / 2f, getWidth() / 2f, getWidth() / 2f - INNER_CIRCLE_PADDING, Paint())
    }

    private fun resetTempCanvas() {
        tempBitmap = Bitmap.createBitmap(getWidth(), getWidth(), Bitmap.Config.ARGB_8888)
        tempCanvas = Canvas(tempBitmap)
    }

    protected override fun onDraw(canvas: Canvas?) {
        if (state == STATE_PROGRESS_STARTED) {
            drawArcForCurrentProgress()
        } else if (state == STATE_DONE_STARTED) {
            drawFrameForDoneAnimation()
            postInvalidate()
        } else if (state == STATE_FINISHED) {
            drawFinishedState()
        }
        canvas?.drawBitmap(tempBitmap, 0F, 0F, null)
    }

    private fun drawArcForCurrentProgress() {
        tempCanvas?.drawArc(progressBounds, -90f, 360 * currentProgress / 100, false, progressPaint)
    }

    private fun drawFrameForDoneAnimation() {
        tempCanvas?.drawCircle(getWidth() / 2f, getWidth() / 2 + currentDoneBgOffset, getWidth() / 2f - INNER_CIRCLE_PADDING, doneBgPaint)
        tempCanvas?.drawBitmap(checkmarkBitmap, checkmarkXPosition.toFloat(), checkmarkYPosition + currentCheckmarkOffset, checkmarkPaint)
        tempCanvas?.drawBitmap(innerCircleMaskBitmap, 0f, 0f, maskPaint)
        tempCanvas?.drawArc(progressBounds, 0f, 360f, false, progressPaint)
    }

    private fun drawFinishedState() {
        tempCanvas?.drawCircle(getWidth() / 2f, getWidth() / 2f, getWidth() / 2f - INNER_CIRCLE_PADDING, doneBgPaint)
        tempCanvas?.drawBitmap(checkmarkBitmap, checkmarkXPosition.toFloat(), checkmarkYPosition.toFloat(), checkmarkPaint)
        tempCanvas?.drawArc(progressBounds, 0f, 360f, false, progressPaint)
    }

    private fun changeState(state: Int) {
        if (this.state == state) {
            return;
        }

        tempBitmap?.recycle()
        resetTempCanvas()

        this.state = state
        if (state == STATE_PROGRESS_STARTED) {
            setCurrentProgress(0f)
            simulateProgressAnimator?.start()
        } else if (state == STATE_DONE_STARTED) {
            setCurrentDoneBgOffset(MAX_DONE_BG_OFFSET.toFloat())
            setCurrentCheckmarkOffset(MAX_DONE_IMG_OFFSET.toFloat())
            var animatorSet: AnimatorSet = AnimatorSet()
            animatorSet.playSequentially(doneBgAnimator, checkmarkAnimator)
            animatorSet.start()
        } else if (state == STATE_FINISHED) {
            if (onLoadingFinishedListener != null)
                onLoadingFinishedListener?.onLoadingFinished()
        }
    }

    fun simulateProgress() { changeState(STATE_PROGRESS_STARTED)}

    fun setCurrentProgress(currentProgress: Float) {
        this.currentProgress = currentProgress
        postInvalidate()
    }

    fun setCurrentDoneBgOffset(currentDoneBgOffset: Float) {
        this.currentDoneBgOffset = currentDoneBgOffset
        postInvalidate()
    }

    fun setCurrentCheckmarkOffset(currentCheckmarkOffset: Float) {
        this.currentCheckmarkOffset = currentCheckmarkOffset
        postInvalidate()
    }

    fun setOnLoadingFinishedListener(onLoadingFinishedListener: OnLoadingFinishedListener) {
        this.onLoadingFinishedListener = onLoadingFinishedListener
    }

    interface OnLoadingFinishedListener {
        fun onLoadingFinished()
    }
}