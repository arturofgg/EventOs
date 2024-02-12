package com.anarlu.eventos;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.viewpager.widget.ViewPager;

public class CustomViewPager extends ViewPager {

    private boolean enableSwipeLeft = true;
    private boolean enableSwipeRight = true;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Deshabilitar el deslizamiento a la izquierda o a la derecha
        if (this.getCurrentItem() == 1 && event.getAction() == MotionEvent.ACTION_MOVE) {
            if (!enableSwipeRight && event.getX() > event.getHistoricalX(0)) {
                return false;
            }
            if (!enableSwipeLeft && event.getX() < event.getHistoricalX(0)) {
                return false;
            }
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Deshabilitar el deslizamiento a la izquierda o a la derecha
        if (this.getCurrentItem() == 1 && event.getAction() == MotionEvent.ACTION_MOVE) {
            if (!enableSwipeRight && event.getX() > event.getHistoricalX(0)) {
                return false;
            }
            if (!enableSwipeLeft && event.getX() < event.getHistoricalX(0)) {
                return false;
            }
        }
        return super.onTouchEvent(event);
    }

    public void setEnableSwipeLeft(boolean enableSwipeLeft) {
        this.enableSwipeLeft = enableSwipeLeft;
    }

    public void setEnableSwipeRight(boolean enableSwipeRight) {
        this.enableSwipeRight = enableSwipeRight;
    }
}

