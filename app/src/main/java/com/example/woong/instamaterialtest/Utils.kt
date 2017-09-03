package com.example.woong.instamaterialtest

/**
 * Created by woong on 2017. 8. 29..
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

open class Utils {
    companion object {
        var screenWidth: Int = 0
        var screenHeight: Int = 0

        fun dpToPx(dp: Int): Int {
            return (dp * Resources.getSystem().getDisplayMetrics().density).toInt()
        }

        fun getScreenHeight(c: Context): Int {
            if (screenHeight == 0) {
                val wm: WindowManager = c.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val display: Display = wm.getDefaultDisplay()
                var size: Point = Point()
                display.getSize(size)
                screenHeight = size.y
            }
            return screenHeight
        }

        fun getScreenWidth(c: Context): Int {
            if (screenWidth == 0) {
                val wm: WindowManager = c.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val display: Display = wm.getDefaultDisplay()
                var size: Point = Point()
                display.getSize(size)
                screenWidth = size.y
            }
            return screenWidth
        }

        fun isAndroid5(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        }
    }
}