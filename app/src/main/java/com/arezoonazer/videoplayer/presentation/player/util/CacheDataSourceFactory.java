package com.arezoonazer.videoplayer.presentation.player.util;

import android.content.Context;
import android.util.Log;

import com.arezoonazer.videoplayer.R;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

public class CacheDataSourceFactory implements DataSource.Factory {
    private static final String TAG = "CacheDataSourceFactory";
    private final Context context;
    private final DataSource.Factory defaultDataSourceFactory;
    private final long maxFileSize, maxCacheSize;

    private static SimpleCache simpleCache;

    public CacheDataSourceFactory(Context context, long maxCacheSize, long maxFileSize) {
        super();
        this.context = context;
        this.maxCacheSize = maxCacheSize;
        this.maxFileSize = maxFileSize;
        String userAgent = Util.getUserAgent(context, context.getString(R.string.app_name));

        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        defaultDataSourceFactory = new DefaultDataSourceFactory(this.context,
                bandwidthMeter,
                new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter));
    }

    @Override
    public DataSource createDataSource() {
        LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(maxCacheSize);
        simpleCache =getInstance(evictor);

        Log.d(TAG, "createDataSource() called" + context.getCacheDir());

        return new CacheDataSource(simpleCache, defaultDataSourceFactory.createDataSource(),
                new FileDataSource(), new CacheDataSink(simpleCache, maxFileSize),
                CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR, null);
    }

    public SimpleCache getInstance(LeastRecentlyUsedCacheEvictor evictor) {
        if (simpleCache == null)
            simpleCache = new SimpleCache(new File(context.getCacheDir(), "media"), evictor);
        return simpleCache;
    }
}
