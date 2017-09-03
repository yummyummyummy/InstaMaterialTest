package com.example.woong.instamaterialtest.ui.view

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by woong on 2017. 9. 1..
 */
class SquaredImageView: ImageView {
    constructor (context: Context): super(context)
    constructor (context: Context, attrs: AttributeSet): super(context, attrs)
    constructor (context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr)
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor (context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width:Int = getMeasuredWidth()
        setMeasuredDimension(width, width)
    }
}