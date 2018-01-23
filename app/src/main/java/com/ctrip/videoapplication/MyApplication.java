package com.ctrip.videoapplication;

import android.app.Application;

/**
 * @author Zhenhua on 2018/1/23.
 * @email zhshan@ctrip.com ^.^
 */

public class MyApplication extends Application {
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApplication getInstance() {
        return instance;
    }
}
