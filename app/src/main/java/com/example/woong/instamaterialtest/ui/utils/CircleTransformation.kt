package com.example.woong.instamaterialtest.ui.utils

/**
 * Created by woong on 2017. 8. 29..
 */
import android.graphics.*

import com.squareup.picasso.Transformation

open class CircleTransformation: Transformation {

    val STROKE_WIDTH: Int = 6
    override fun transform(source: Bitmap): Bitmap {
        val size: Int = Math.min(source.getWidth(), source.getHeight())
        val x: Int = (source.getWidth() - size) / 2
        val y: Int = (source.getHeight() - size) / 2

        var squaredBitmap: Bitmap = Bitmap.createBitmap(source, x, y, size, size)
        if (squaredBitmap != source) {
            source.recycle()
        }

        var bitmap: Bitmap = Bitmap.createBitmap(size, size, source.getConfig())

        var canvas: Canvas = Canvas(bitmap)

        var avatarPaint: Paint = Paint()
        var shader:BitmapShader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        avatarPaint.setShader(shader)

        var outlinePaint: Paint = Paint()
        outlinePaint.setColor(Color.WHITE)
        outlinePaint.setStyle(Paint.Style.STROKE)
        outlinePaint.setStrokeWidth(STROKE_WIDTH as Float)
        outlinePaint.setAntiAlias(true)

        val r:Float = size / 2f
        canvas.drawCircle(r, r, r, avatarPaint)
        canvas.drawCircle(r, r, r - (STROKE_WIDTH / 2f), outlinePaint)

        squaredBitmap.recycle()
        return bitmap
    }

    open override fun key(): String {
        return "circleTransformation()"
    }
}