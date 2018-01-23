package com.ctrip.videoapplication.video;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Zhenhua on 2017/12/20.
 * @email zhshan@ctrip.com ^.^
 */

public abstract class IVideoController extends FrameLayout implements View.OnTouchListener {
    private Context mContext;
    protected IVideoPlayer mVideoPlayer;

    private Timer mUpdateProgressTimer;
    private TimerTask mUpdateProgressTimerTask;

    private float mDownX;
    private boolean mNeedChangePosition;
    private static final int THRESHOLD = 80;
    private int mGestureDownPosition;
    private int mNewPosition;

    public IVideoController(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public void setVideoPlayer(IVideoPlayer videoPlayer) {
        mVideoPlayer = videoPlayer;
    }

    public abstract void setUrl(String videoUrl);

    //设置视频底图
    public abstract void setImage(String url);

    //得到视频底图ImageView
    public abstract ImageView imageView();

    /**
     * 播放器的播放状态发生改变
     *
     * @param: STATE_IDLE
     * STATE_PREPARING
     * STATE_PREPARED
     * STATE_PLAYING
     * STATE_PAUSED
     * STATE_BUFFERING_PLAYING
     * STATE_BUFFERING_PAUSED
     * STATE_ERROR
     * STATE_COMPLETED
     */
    protected abstract void onPlayStateChanged(int playState);

    /**
     * 播放器的播放模式发生改变
     *
     * @param: MODE_NORMAL MODE_FULL_SCREEN
     * MODE_TINY_WINDOW
     */
    protected abstract void onPlayModeChanged(int playMode);

    //重置播放器
    protected abstract void reset();

    //开启更新进度计时器
    protected void startUpdateProgressTimer() {
        cancelUpdateProgressTimer();
        if (mUpdateProgressTimer == null) {
            mUpdateProgressTimer = new Timer();
        }
        if (mUpdateProgressTimerTask == null) {
            mUpdateProgressTimerTask = new TimerTask() {
                @Override
                public void run() {
                    IVideoController.this.post(new Runnable() {
                        @Override
                        public void run() {
                            updateProgress();
                        }
                    });
                }
            };
        }
        mUpdateProgressTimer.schedule(mUpdateProgressTimerTask, 0, 1000);
    }

    //取消更新进度的计时器
    protected void cancelUpdateProgressTimer() {
        if (mUpdateProgressTimer != null) {
            mUpdateProgressTimer.cancel();
            mUpdateProgressTimer = null;
        }
        if (mUpdateProgressTimerTask != null) {
            mUpdateProgressTimerTask.cancel();
            mUpdateProgressTimerTask = null;
        }
    }

    //更新进度
    protected abstract void updateProgress();

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 只有全屏的时候才能拖动位置、亮度、声音
        if (!mVideoPlayer.isFullScreen()) {
            return false;
        }
        // 只有在播放、暂停、缓冲的时候能够拖动改变位置、亮度和声音
        if (mVideoPlayer.isIdle()
                || mVideoPlayer.isError()
                || mVideoPlayer.isPreparing()
                || mVideoPlayer.isPrepared()
                || mVideoPlayer.isCompleted()) {
            hideChangePosition();
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = x;
                mNeedChangePosition = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = x - mDownX;
                float absDeltaX = Math.abs(deltaX);
                if (!mNeedChangePosition) {
                    // 只有在播放、暂停、缓冲的时候能够拖动改变位置
                    if (absDeltaX >= THRESHOLD) {
                        cancelUpdateProgressTimer();
                        mNeedChangePosition = true;
                        mGestureDownPosition = mVideoPlayer.getCurrentPosition();
                    }
                }
                if (mNeedChangePosition) {
                    int duration = mVideoPlayer.getDuration();
                    int toPosition = (int) (mGestureDownPosition + duration * deltaX / getWidth());
                    mNewPosition = Math.max(0, Math.min(duration, toPosition));
                    int newPositionProgress = (int) (100f * mNewPosition / duration);
                    showChangePosition(duration, newPositionProgress);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mNeedChangePosition) {
                    mVideoPlayer.seekTo(mNewPosition);
                    hideChangePosition();
                    startUpdateProgressTimer();
                    return true;
                }
                break;
        }
        return false;
    }

    //左右滑动改变播放位置时，显示控制器中间的播放位置变化视图
    //在ACTION_MOVE时，会不断调用此方法
    protected abstract void showChangePosition(long duration, int newPositionProgress);

    //左右滑动改变播放位置时，up或cancel后，隐藏中间的播放位置变化视图
    protected abstract void hideChangePosition();

}
