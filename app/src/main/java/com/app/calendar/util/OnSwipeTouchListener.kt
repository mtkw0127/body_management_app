package com.app.calendar.util

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.max

abstract class OnSwipeTouchListener(context: Context) : View.OnTouchListener {

    private val gestureDetector: GestureDetector

    init {
        gestureDetector = GestureDetector(context, GestureListener())
    }
 
    sealed class SwipeDirection {
        object UP : SwipeDirection()
        object DOWN : SwipeDirection()
        object LEFT : SwipeDirection()
        object RIGHT : SwipeDirection()
        object NONE : SwipeDirection()
        companion object {
            private const val SWIPE_THRESHOLD = 100
            private const val SWIPE_VELOCITY_THRESHOLD = 100
            fun newInstance(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): SwipeDirection {
                // 移動量
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                val absDiffY = abs(diffY)
                val absDiffX = abs(diffX)
                val sideSwipe = absDiffX > absDiffY
                val diff = max(absDiffX, absDiffY)
                // 移動量が閾値未満は非スワイプ
                if (diff < SWIPE_THRESHOLD) return NONE
                if (sideSwipe && abs(velocityX) < SWIPE_VELOCITY_THRESHOLD) return NONE
                if (!sideSwipe && abs(velocityY) < SWIPE_VELOCITY_THRESHOLD) return NONE
                // 上下左右判定
                return if (sideSwipe) {
                    if (diffX > 0) RIGHT
                    else LEFT
                } else {
                    if (diffY > 0) DOWN
                    else UP
                }
            }
        }
    }

    private inner class GestureListener : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent) = true
        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1 == null || e2 == null) return false
            val swipeDirection = SwipeDirection.newInstance(e1, e2, velocityX, velocityY)
            val result = swipeDirection !is SwipeDirection.NONE
            when (swipeDirection) {
                is SwipeDirection.UP -> up()
                is SwipeDirection.DOWN -> down()
                is SwipeDirection.RIGHT -> right()
                is SwipeDirection.LEFT -> left()
                else -> {}
            }
            return result
        }
    }

    abstract fun up()
    abstract fun down()
    abstract fun right()
    abstract fun left()

    override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }
}