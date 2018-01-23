package com.ctrip.videoapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ScrollView;

import com.ctrip.videoapplication.util.CommonUtil;
import com.ctrip.videoapplication.util.NestedScrollView;
import com.ctrip.videoapplication.video.TourVideoController;
import com.ctrip.videoapplication.video.TourVideoPlayer;

/**
 * @author Zhenhua on 2018/1/22.
 * @email zhshan@ctrip.com ^.^
 */

public class MainActivity extends Activity {
    private TourVideoPlayer mVideoPlayer;
    private NestedScrollView scrollView;
    private boolean flag = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scrollView = (NestedScrollView) findViewById(R.id.scrollView);
        mVideoPlayer = (TourVideoPlayer) findViewById(R.id.video_player);
        TourVideoController controller = new TourVideoController(this);
        String videoUrl = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
        controller.setUrl(videoUrl);
        mVideoPlayer.setController(controller);

        scrollView.setOnScrollListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChanged(float dy) {
                if (dy > CommonUtil.dp2px(MainActivity.this, 180)) {
                    if (!flag) {
                        flag = true;
                        mVideoPlayer.enterTinyWindow();
                    }
                } else {
                    if (flag) {
                        flag = false;
                        mVideoPlayer.exitTinyWindow();
                    }
                }

            }
        });
    }
}
