package com.service.saver.saverservice;

import android.app.Application;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
          DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(this)
                .build();
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .setMainDiskCacheConfig(diskCacheConfig)
                .build();
        Fresco.initialize(this, config);
    }
}
