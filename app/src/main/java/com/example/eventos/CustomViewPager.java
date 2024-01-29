package com.example.eventos;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.viewpager.widget.ViewPager;

    public class CustomViewPager extends ViewPager {

        private boolean canSwipe;

        public CustomViewPager(Context context) {
            super(context);
            this.canSwipe = true;
        }

        public CustomViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.canSwipe = true;
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent event) {
            return this.canSwipe && super.onInterceptTouchEvent(event);
        }

        public void setSwipeEnabled(boolean canSwipe) {
            this.canSwipe = canSwipe;
        }
    }

