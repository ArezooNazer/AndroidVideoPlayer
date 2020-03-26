package com.arezoonazer.videoplayer.app;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class PlayerApplication extends Application {

    private RefWatcher refWatcher;
    @Override
    public void onCreate() {
        super.onCreate();

        // initialize stetho
        Stetho.initializeWithDefaults(this);

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        refWatcher = LeakCanary.install(this);
        // Normal app init code...
    }

    public static RefWatcher getRefWatcher(Context context) {
        PlayerApplication application = (PlayerApplication) context.getApplicationContext();
        return application.refWatcher;
    }


}
