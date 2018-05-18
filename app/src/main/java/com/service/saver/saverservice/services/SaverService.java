package com.service.saver.saverservice.services;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnProgressListener;
import com.downloader.PRDownloader;
import com.downloader.Priority;
import com.downloader.Progress;
import com.downloader.request.DownloadRequest;
import com.service.saver.saverservice.MyApp;
import com.service.saver.saverservice.R;
import com.service.saver.saverservice.tumblr.model.PostModel;
import com.service.saver.saverservice.util.Files;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import needle.Needle;

/**
 * Created by Pedro R on 4/03/2017.
 */

public class SaverService extends IntentService {
    public static final int ID = 23;
    private List<String> listlinks = new ArrayList<>();
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    public SaverService() {
        super("SaverService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotifyManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        mBuilder = getmBuilder();


        Needle.onBackgroundThread().execute(() -> {
            while (true) {
                listlinks = MyApp.getFiles();
                if (!listlinks.isEmpty()) {
                    String link = listlinks.get(0);
                    try {
                        String[] split = link.split("/");
                        String[] namelink = link.split(PostModel.NAMESPACE);
                        Uri destinationUri = Uri.parse(Files.getRunningDir() + "/" + (namelink.length > 1 ? namelink[0] : split[split.length - 1]));
                        File f = new File(destinationUri.getPath());
                        if (!f.exists()) {
                            getDownloadRequest((namelink.length > 1 ? namelink[1] : link), Files.getRunningDir() + "/", (namelink.length > 1 ? namelink[0] : split[split.length - 1]));
                        }
                        removeSafe(link);
                    } catch (Exception e) {
                        removeSafe(link);
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.currentThread().sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @NonNull
    private NotificationCompat.Builder getmBuilder() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String chanel_id = "3000";
            CharSequence name = "Channel Name";
            String description = "Chanel Description";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = null;
            mChannel = new NotificationChannel(chanel_id, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.BLUE);
            mNotifyManager.createNotificationChannel(mChannel);
            return new NotificationCompat.Builder(this, chanel_id);
        } else {
            return new NotificationCompat.Builder(this);

        }


    }

    private int getDownloadRequest(String link, String dirPath, String fileName) {
        DownloadRequest build = PRDownloader.download(link, dirPath, fileName)
                .setPriority(Priority.HIGH)
                .build();
        int downloadId = build.getDownloadId();

        return build
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        mBuilder.setContentTitle("Download")
                                .setContentText("Downloading")
                                .setSubText((progress.currentBytes * 100) / progress.totalBytes + " % ")
                                .setSmallIcon(R.drawable.androidicon)
                                .setProgress(100, 100, true);
                        mNotifyManager.notify(downloadId, mBuilder.build());
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        removeSafe(link);
                        mBuilder.setContentTitle("Download")
                                .setContentText("Downloaded")
                                .setSubText("")
                                .setSmallIcon(R.drawable.androidicon)
                                .setProgress(100, 100, false);
                        mNotifyManager.notify(downloadId, mBuilder.build());
                    }

                    @Override
                    public void onError(Error error) {
                        mBuilder.setContentTitle("Download")
                                .setContentText("Downloading error - " + (error.isConnectionError() ? "Conexion error" : "Server Error"))
                                .setSmallIcon(R.drawable.androidicon)
                                .setProgress(0, 0, false);
                        mNotifyManager.notify(downloadId, mBuilder.build());
                        if (error.isConnectionError())
                            MyApp.add(link);
                    }
                });

    }

    private void removeSafe(String string) {
        listlinks.remove(string);
        MyApp.remove(string);
        listlinks = MyApp.getFiles();
    }


}
