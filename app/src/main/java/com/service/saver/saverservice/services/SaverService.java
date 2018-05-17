package com.service.saver.saverservice.services;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import com.service.saver.saverservice.MainTabActivity;
import com.service.saver.saverservice.MyApp;
import com.service.saver.saverservice.R;
import com.service.saver.saverservice.util.Files;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
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
    private ThinDownloadManager downloadManager;

    public SaverService() {
        super("SaverService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotifyManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        downloadManager = new ThinDownloadManager();

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        mBuilder = getmBuilder();


        Needle.onBackgroundThread().execute(() -> {
            while (true) {
                listlinks = MyApp.getFiles();
                try {
                    if (!listlinks.isEmpty()) {
                        String link = listlinks.get(0);
                        URL url;
                        String[] split = link.split("/");
                        Uri downloadUri = Uri.parse(link);
                        Uri destinationUri = Uri.parse(Files.getRunningDir() + "/" + split[split.length - 1] + ".mp4");
                        DownloadRequest downloadRequest = getDownloadRequest(link, downloadUri, destinationUri);
                        int downloadId = downloadManager.add(downloadRequest);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
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

    private DownloadRequest getDownloadRequest(String link, Uri downloadUri, Uri destinationUri) {
        return new DownloadRequest(downloadUri)
                .setRetryPolicy(new DefaultRetryPolicy())
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                .setStatusListener(new DownloadStatusListenerV1() {
                    @Override
                    public void onDownloadComplete(DownloadRequest downloadRequest) {
                        removeSafe(link);
                        mBuilder.setContentTitle("Download")
                                .setContentText("Downloaded")
                                .setSmallIcon(R.drawable.androidicon)
                                .setProgress(100, 100, true);
                        mNotifyManager.notify(ID, mBuilder.build());
                    }

                    @Override
                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {

                    }

                    @Override
                    public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
                        mBuilder.setContentTitle("Download")
                                .setContentText("Downloading")
                                .setSubText(progress + " % ")
                                .setSmallIcon(R.drawable.androidicon)
                                .setProgress(100, 100, true);
                        mNotifyManager.notify(ID, mBuilder.build());
                    }
                });
    }

    private void removeSafe(String string) {
        listlinks.remove(string);
        MyApp.remove(string);
        listlinks = MyApp.getFiles();
    }


}
