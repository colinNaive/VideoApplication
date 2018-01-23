package com.ctrip.videoapplication.video;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ctrip.videoapplication.R;
import com.ctrip.videoapplication.util.CTTourIconFont;

/**
 * @author Zhenhua on 2017/12/20.
 * @email zhshan@ctrip.com ^.^
 */

public class TourVideoController extends IVideoController implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private Context mContext;
    private ImageView mImage;
    private CTTourIconFont mCenterStart;

    private LinearLayout mTop;
    private CTTourIconFont mBack;
    private String mVideoUrl;

    private LinearLayout mBottom;
    private CTTourIconFont mRestartPause;
    private TextView mPosition;
    private TextView mDuration;
    private SeekBar mSeek;
    private CTTourIconFont mFullScreen;

    private LinearLayout mLoading;
    private TextView mLoadText;

    private LinearLayout mChangePositon;
    private TextView mChangePositionCurrent;
    private ProgressBar mChangePositionProgress;

    private LinearLayout mError;
    private TextView mRetry;

    private LinearLayout mNote4G;
    private TextView mContinue;

    private LinearLayout mDisconnect;
    private TextView mRefresh;

    private LinearLayout mCompleted;
    private TextView mReplay;

    private boolean topBottomVisible;
    private CountDownTimer mDismissTopBottomCountDownTimer;
    private OnPlayListener mOnPlayListener;

    public TourVideoController(@NonNull Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.video_player_controller, this, true);

        mCenterStart = (CTTourIconFont) findViewById(R.id.center_start);
        mImage = (ImageView) findViewById(R.id.image);

        mTop = (LinearLayout) findViewById(R.id.top);
        mBack = (CTTourIconFont) findViewById(R.id.back);

        mBottom = (LinearLayout) findViewById(R.id.bottom);
        mRestartPause = (CTTourIconFont) findViewById(R.id.restart_or_pause);
        mPosition = (TextView) findViewById(R.id.position);
        mDuration = (TextView) findViewById(R.id.duration);
        mSeek = (SeekBar) findViewById(R.id.seek);
        mFullScreen = (CTTourIconFont) findViewById(R.id.full_screen);

        mLoading = (LinearLayout) findViewById(R.id.ll_loading);
        mLoadText = (TextView) findViewById(R.id.load_text);

        mChangePositon = (LinearLayout) findViewById(R.id.change_position);
        mChangePositionCurrent = (TextView) findViewById(R.id.change_position_current);
        mChangePositionProgress = (ProgressBar) findViewById(R.id.change_position_progress);

        mError = (LinearLayout) findViewById(R.id.error);
        mRetry = (TextView) findViewById(R.id.retry);

        mNote4G = (LinearLayout) findViewById(R.id.note_4g);
        mContinue = (TextView) findViewById(R.id.continue_play);

        mDisconnect = (LinearLayout) findViewById(R.id.disconnect);
        mRefresh = (TextView) findViewById(R.id.refresh);

        mCompleted = (LinearLayout) findViewById(R.id.completed);
        mReplay = (TextView) findViewById(R.id.replay);

        mCenterStart.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mRestartPause.setOnClickListener(this);
        mFullScreen.setOnClickListener(this);
        mRetry.setOnClickListener(this);
        mReplay.setOnClickListener(this);
        mSeek.setOnSeekBarChangeListener(this);
        mContinue.setOnClickListener(this);
        mRefresh.setOnClickListener(this);
        this.setOnClickListener(this);
    }

    @Override
    public void setImage(String url) {
//        mImage.setImageResource(R.drawable.default_pic_material);
    }

    @Override
    public ImageView imageView() {
        return mImage;
    }

    @Override
    public void setVideoPlayer(IVideoPlayer videoPlayer) {
        super.setVideoPlayer(videoPlayer);
        mVideoPlayer.setUp(mVideoUrl, null);
    }

    @Override
    public void setUrl(String videoUrl) {
        this.mVideoUrl = videoUrl;
    }

    @Override
    protected void onPlayStateChanged(int playState) {
        switch (playState) {
            case TourVideoPlayer.STATE_IDLE:
                break;
            case TourVideoPlayer.STATE_PREPARING:
                mNote4G.setVisibility(View.GONE);
                mDisconnect.setVisibility(View.GONE);
                mError.setVisibility(View.GONE);
                mCompleted.setVisibility(View.GONE);

                mLoading.setVisibility(View.VISIBLE);
                mImage.setVisibility(View.VISIBLE);
                mLoadText.setText("正在准备...");
                mTop.setVisibility(View.GONE);
                mBottom.setVisibility(View.GONE);
                mCenterStart.setVisibility(View.GONE);
                setIsPlayingFlag();
                break;
            case TourVideoPlayer.STATE_PREPARED:
                startUpdateProgressTimer();
                setIsPlayingFlag();
                break;
            case TourVideoPlayer.STATE_PLAYING:
                mLoading.setVisibility(GONE);
                mError.setVisibility(GONE);
                mNote4G.setVisibility(View.GONE);
                mDisconnect.setVisibility(View.GONE);
                mLoading.setVisibility(View.GONE);

                mImage.setVisibility(View.GONE);
                mRestartPause.setText("\ue546");//暂停
                mCenterStart.setText("\ue546");//暂停
                mCenterStart.setVisibility(VISIBLE);
                setTopBottomVisible(true);
                startDismissTopCenterBottomTimer();
                setIsPlayingFlag();
                break;
            case TourVideoPlayer.STATE_PAUSED:
                mError.setVisibility(GONE);
                mCompleted.setVisibility(GONE);
                mNote4G.setVisibility(GONE);
                mDisconnect.setVisibility(GONE);
                mLoading.setVisibility(View.GONE);

                mRestartPause.setText("\ue545");//播放
                mCenterStart.setText("\ue545");//播放
                setTopBottomVisible(false);
                cancelDismissTopCenterBottomTimer();
                mCenterStart.setVisibility(VISIBLE);
                setIsPauseFlag();
                if (mVideoPlayer.isFullScreen()) {
                    mTop.setVisibility(VISIBLE);
                }
                break;
            case TourVideoPlayer.STATE_BUFFERING_PLAYING:
                mError.setVisibility(GONE);
                mCompleted.setVisibility(GONE);
                mNote4G.setVisibility(GONE);
                mDisconnect.setVisibility(GONE);

                mRestartPause.setText("\ue546");//暂停
                mCenterStart.setText("\ue546");//暂停
                mCenterStart.setVisibility(VISIBLE);
                mLoading.setVisibility(View.VISIBLE);
                mLoadText.setText("正在缓冲...");
                setTopBottomVisible(true);
                startDismissTopCenterBottomTimer();
                setIsPlayingFlag();
                break;
            case TourVideoPlayer.STATE_BUFFERING_PAUSED:
                mError.setVisibility(GONE);
                mCompleted.setVisibility(GONE);
                mDisconnect.setVisibility(GONE);
                mNote4G.setVisibility(GONE);

                mLoading.setVisibility(View.VISIBLE);
                mLoadText.setText("正在缓冲...");
                mRestartPause.setText("\ue545");//播放
                mCenterStart.setText("\ue545");//播放
                mCenterStart.setVisibility(VISIBLE);
                cancelDismissTopCenterBottomTimer();
                setTopBottomVisible(false);
                setIsPauseFlag();
                if (mVideoPlayer.isFullScreen()) {
                    mTop.setVisibility(VISIBLE);
                }
                break;
            case TourVideoPlayer.STATE_ERROR:
                mCompleted.setVisibility(GONE);
                mDisconnect.setVisibility(GONE);
                mLoading.setVisibility(GONE);
                mNote4G.setVisibility(GONE);

                cancelUpdateProgressTimer();
                setTopBottomVisible(false);
                mCenterStart.setVisibility(GONE);
                mTop.setVisibility(View.VISIBLE);
                mError.setVisibility(View.VISIBLE);
                if (mVideoPlayer.isFullScreen()) {
                    mTop.setVisibility(VISIBLE);
                    mBack.setVisibility(View.VISIBLE);
                }
                break;
            case TourVideoPlayer.STATE_COMPLETED:
                mError.setVisibility(GONE);
                mCompleted.setVisibility(GONE);
                mDisconnect.setVisibility(GONE);
                mLoading.setVisibility(GONE);
                mNote4G.setVisibility(GONE);

                cancelUpdateProgressTimer();
                cancelDismissTopCenterBottomTimer();
                setTopBottomVisible(false);
                mCenterStart.setVisibility(GONE);
                mImage.setVisibility(View.VISIBLE);
                mCompleted.setVisibility(View.VISIBLE);
                if (mVideoPlayer.isFullScreen()) {
                    mTop.setVisibility(VISIBLE);
                    mBack.setVisibility(View.VISIBLE);
                }
                break;
            case TourVideoPlayer.STATE_NOTE_4G:
                mError.setVisibility(GONE);
                mCompleted.setVisibility(GONE);
                mDisconnect.setVisibility(GONE);
                mLoading.setVisibility(View.GONE);

                mNote4G.setVisibility(VISIBLE);
                mImage.setVisibility(View.VISIBLE);
                setTopBottomVisible(false);
                mCenterStart.setVisibility(GONE);
                if (mVideoPlayer.isFullScreen()) {
                    mTop.setVisibility(VISIBLE);
                    mBack.setVisibility(View.VISIBLE);
                }
                break;
            case TourVideoPlayer.STATE_NOTE_DISCONNECT:
                mError.setVisibility(GONE);
                mCompleted.setVisibility(GONE);
                mNote4G.setVisibility(GONE);
                mLoading.setVisibility(GONE);

                mDisconnect.setVisibility(VISIBLE);
                mImage.setVisibility(VISIBLE);
                setTopBottomVisible(false);
                mCenterStart.setVisibility(GONE);
                if (mVideoPlayer.isFullScreen()) {
                    mTop.setVisibility(VISIBLE);
                    mBack.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    protected void onPlayModeChanged(int playMode) {
        switch (playMode) {
            case TourVideoPlayer.MODE_NORMAL:
                mBack.setVisibility(View.GONE);
                mFullScreen.setText("\ue579");//全屏
                mFullScreen.setVisibility(View.VISIBLE);
                break;
            case TourVideoPlayer.MODE_FULL_SCREEN:
                mBack.setVisibility(View.VISIBLE);
                mFullScreen.setText("\ue57a");//缩小
                break;
            case TourVideoPlayer.MODE_TINY_WINDOW:
                mBack.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void reset() {
        topBottomVisible = false;
        cancelUpdateProgressTimer();
        cancelDismissTopCenterBottomTimer();
        mSeek.setProgress(0);
        mSeek.setSecondaryProgress(0);

        mCenterStart.setVisibility(View.VISIBLE);
        mImage.setVisibility(View.VISIBLE);

        mBottom.setVisibility(View.GONE);
        mFullScreen.setText("\ue579");//全屏

        mTop.setVisibility(View.VISIBLE);
        mBack.setVisibility(View.GONE);

        mLoading.setVisibility(View.GONE);
        mError.setVisibility(View.GONE);
        mCompleted.setVisibility(View.GONE);
    }

    public void autoStart() {
        mCenterStart.performClick();
    }

    @Override
    public void onClick(View v) {
        if (v == mCenterStart) {
            mRestartPause.performClick();
        } else if (v == mBack) {
            if (mVideoPlayer.isFullScreen()) {
                mVideoPlayer.exitFullScreen();
            } else if (mVideoPlayer.isTinyWindow()) {
                mVideoPlayer.exitTinyWindow();
            }
        } else if (v == mRestartPause) {
            if (NetworkChangeReceiver.isDisconnect()) {
                onPlayStateChanged(TourVideoPlayer.STATE_NOTE_DISCONNECT);
            } else if (NetworkChangeReceiver.is4G() && !TourVideoPlayer.allow4GFlag) {
                onPlayStateChanged(TourVideoPlayer.STATE_NOTE_4G);
            } else if (mVideoPlayer.isIdle()) {
                mVideoPlayer.start();
            } else if (mVideoPlayer.isPlaying() || mVideoPlayer.isBufferingPlaying()) {
                mVideoPlayer.pause();
            } else if (mVideoPlayer.isPaused() || mVideoPlayer.isBufferingPaused()) {
                mVideoPlayer.restart();
            }
        } else if (v == mFullScreen) {
            if (mVideoPlayer.isNormal() || mVideoPlayer.isTinyWindow()) {
                mVideoPlayer.enterFullScreen();
            } else if (mVideoPlayer.isFullScreen()) {
                mVideoPlayer.exitFullScreen();
            }
        } else if (v == mRetry) {
            mVideoPlayer.restart();
        } else if (v == mReplay) {
            mRetry.performClick();
        } else if (v == this) {
            if (mVideoPlayer.isPlaying()
                    || mVideoPlayer.isBufferingPlaying()) {
                setTopBottomVisible(!topBottomVisible);
                if (topBottomVisible) {
                    startDismissTopCenterBottomTimer();
                }
                mCenterStart.setVisibility(topBottomVisible ? VISIBLE : GONE);
            }
        } else if (v == mContinue) {
            TourVideoPlayer.allow4GFlag = true;
            mVideoPlayer.restart();
        } else if (v == mRefresh) {
            mVideoPlayer.restart();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mVideoPlayer.isBufferingPaused() || mVideoPlayer.isPaused()) {
            mVideoPlayer.restart();
        }
        int position = (int) (mVideoPlayer.getDuration() * seekBar.getProgress() / 100f);
        mVideoPlayer.seekTo(position);
        startDismissTopCenterBottomTimer();
    }

    private void setTopBottomVisible(boolean visible) {
        mTop.setVisibility(visible ? VISIBLE : GONE);
        mBottom.setVisibility(visible ? VISIBLE : GONE);
        topBottomVisible = visible;
        if (visible) {
            if (!mVideoPlayer.isPaused() && !mVideoPlayer.isBufferingPaused()) {
                startUpdateProgressTimer();
            }
        }
    }

    private void startDismissTopCenterBottomTimer() {
        cancelDismissTopCenterBottomTimer();
        if (mDismissTopBottomCountDownTimer == null) {
            mDismissTopBottomCountDownTimer = new CountDownTimer(3000, 3000) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    setTopBottomVisible(false);
                    mCenterStart.setVisibility(GONE);
                }
            };
        }
        mDismissTopBottomCountDownTimer.start();
    }

    private void cancelDismissTopCenterBottomTimer() {
        if (mDismissTopBottomCountDownTimer != null) {
            mDismissTopBottomCountDownTimer.cancel();
        }
    }

    @Override
    protected void updateProgress() {
        long position = mVideoPlayer.getCurrentPosition();
        long duration = mVideoPlayer.getDuration();
        int bufferPercentage = mVideoPlayer.getBufferPercentage();
        mSeek.setSecondaryProgress(bufferPercentage);
        int progress = (int) (100f * position / duration);
        mSeek.setProgress(progress);
        mPosition.setText(TourVideoUtil.formatTime(position));
        mDuration.setText(TourVideoUtil.formatTime(duration));
    }

    @Override
    protected void showChangePosition(long duration, int newPositionProgress) {
        mChangePositon.setVisibility(View.VISIBLE);
        long newPosition = (long) (duration * newPositionProgress / 100f);
        mChangePositionCurrent.setText(TourVideoUtil.formatTime(newPosition));
        mChangePositionProgress.setProgress(newPositionProgress);
        mSeek.setProgress(newPositionProgress);
        mPosition.setText(TourVideoUtil.formatTime(newPosition));
    }

    @Override
    protected void hideChangePosition() {
        mChangePositon.setVisibility(View.GONE);
    }

    public interface OnPlayListener {
        void play();

        void pause();
    }

    private void setIsPauseFlag() {
        if (mOnPlayListener != null) {
            mOnPlayListener.pause();
        }
    }

    private void setIsPlayingFlag() {
        if (mOnPlayListener != null) {
            mOnPlayListener.play();
        }
    }

    public void setOnPlayListener(OnPlayListener onPlayListener) {
        this.mOnPlayListener = onPlayListener;
    }

}
