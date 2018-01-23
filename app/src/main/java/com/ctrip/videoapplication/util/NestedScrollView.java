package com.ctrip.videoapplication.util;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * @author Zhenhua on 2017/6/2 09:46.
 * @email zhshan@ctrip.com
 */

public class NestedScrollView extends ScrollView {
    private static final String TAG = "nestedscrollview_szh";
    private Runnable scrollerTask;
    private int initialPosition;
    private int newCheck = 100;
    private View fixView;
    private OnFixListener listener;
    private boolean isTop;
    float lastY = 0;
    private boolean rvDisable;
    private boolean displayPush;
    private OnScrollStoppedListener onScrollStoppedListener;
    private OnScrollChangeListener changeListener;


    public NestedScrollView(Context context) {
        super(context);
    }

    public NestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        scrollerTask = new Runnable() {
            @Override
            public void run() {
                int newPosition = getScrollY();
                if (initialPosition - newPosition == 0) {
                    //=0，means stop
                    if (onScrollStoppedListener != null) {
                        onScrollStoppedListener.onScrollStopped();
                    }

                } else {
                    initialPosition = getScrollY();
                    NestedScrollView.this.postDelayed(scrollerTask, newCheck);
                }

            }
        };

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (changeListener != null) {
            changeListener.onScrollChanged(getScrollY() * 0.65f);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            lastY = ev.getY();
        }
        if (action == MotionEvent.ACTION_MOVE) {
            if (isTop && !rvDisable) {
                return false;
            } else {
                return super.onInterceptTouchEvent(ev);
            }
        }

        return super.onInterceptTouchEvent(ev);
    }

    public interface OnScrollStoppedListener {
        void onScrollStopped();
    }

    public void setOnScrollStoppedListener(OnScrollStoppedListener listener) {
        onScrollStoppedListener = listener;
    }

    public void startScrollerTask() {
        initialPosition = getScrollY();
        NestedScrollView.this.postDelayed(scrollerTask, newCheck);
    }

    public void setFixListener(OnFixListener listener) {
        this.listener = listener;
    }

    private void fix() {
        if (listener != null) {
            listener.onFix();
        }
    }

    private void dismiss() {
        if (listener != null) {
            listener.onDismiss();
        }
    }

    public interface OnFixListener {
        void onFix();

        void onDismiss();
    }

    public void setDisplayPush(boolean displayPush) {
        this.displayPush = displayPush;
    }

    public void setRvDisable(boolean rvDisable) {
        this.rvDisable = rvDisable;
    }

    public void setOnScrollListener(OnScrollChangeListener listener) {
        this.changeListener = listener;
    }

    public interface OnScrollChangeListener {
        void onScrollChanged(float dy);
    }

}
