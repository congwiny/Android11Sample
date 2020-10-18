package com.congwiny.android11;

import android.app.Application;

import com.liulishuo.filedownloader.FileDownloader;

public class Android11SampleApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FileDownloader.setupOnApplicationOnCreate(this);
    }
}
