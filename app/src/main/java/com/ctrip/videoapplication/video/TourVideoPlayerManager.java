package com.ctrip.videoapplication.video;

/**
 * @author Zhenhua on 2017/12/20.
 * @email zhshan@ctrip.com ^.^
 * 视频播放器管理类
 */

public class TourVideoPlayerManager {
    private TourVideoPlayer mVideoPlayer;

    private TourVideoPlayerManager() {
    }

    private static TourVideoPlayerManager sInstance;

    public static synchronized TourVideoPlayerManager instance() {
        if (sInstance == null) {
            sInstance = new TourVideoPlayerManager();
        }
        return sInstance;
    }

    public TourVideoPlayer getCurrentVideoPlayer() {
        return mVideoPlayer;
    }

    public void setCurrentVideoPlayer(TourVideoPlayer videoPlayer) {
        if (mVideoPlayer != videoPlayer) {
            releaseVideoPlayer();
            mVideoPlayer = videoPlayer;
        }
    }

    public void suspendNiceVideoPlayer() {
        if (mVideoPlayer != null && ((mVideoPlayer.isPlaying() || mVideoPlayer.isBufferingPlaying()))) {
            mVideoPlayer.pause();
        }
    }

    public void resumeVideoPlayer() {
        if (mVideoPlayer != null && (mVideoPlayer.isPaused() || mVideoPlayer.isBufferingPaused())) {
            mVideoPlayer.restart();
        }
    }

    public void releaseVideoPlayer() {
        if (mVideoPlayer != null) {
            mVideoPlayer.release();
            mVideoPlayer = null;
        }
    }

    public boolean onBackPressed() {
        if (mVideoPlayer != null) {
            if (mVideoPlayer.isFullScreen()) {
                return mVideoPlayer.exitFullScreen();
            } else if (mVideoPlayer.isTinyWindow()) {
                return mVideoPlayer.exitTinyWindow();
            }
        }
        return false;
    }
}
