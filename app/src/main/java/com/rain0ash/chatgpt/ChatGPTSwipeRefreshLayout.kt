package com.rain0ash.chatgpt

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class ChatGPTSwipeRefreshLayout(context: Context, attrs: AttributeSet) : SwipeRefreshLayout(context, attrs) {

    private var startY = 0f
    private val thresholdY = 50  // Вы можете установить нужное вам значение в dp

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startY = event.y
            }
        }
        return super.onInterceptTouchEvent(event)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_MOVE && ev.y - startY > thresholdY) {
            // Если свайп начался ниже определенного порога, предотвращаем обработку события
            return false
        }
        return super.onTouchEvent(ev)
    }
}