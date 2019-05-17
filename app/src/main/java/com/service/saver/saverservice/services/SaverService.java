package com.service.saver.saverservice.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;

import com.liulishuo.okdownload.DownloadContext;
import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadSerialQueue;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.SpeedCalculator;
import com.liulishuo.okdownload.core.breakpoint.BlockInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.listener.DownloadListener4WithSpeed;
import com.liulishuo.okdownload.core.listener.assist.Listener4SpeedAssistExtend;
import com.service.saver.saverservice.BuildConfig;
import com.service.saver.saverservice.R;
import com.service.saver.saverservice.domain.PostLink;
import com.service.saver.saverservice.sqllite.AdminSQLiteOpenHelper;
import com.service.saver.saverservice.util.Files;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import needle.Needle;

/**
 * Created by Pedro R on 4/03/2017.
 */

public class SaverService extends IntentService {
    private List<PostLink> listlinks = new ArrayList<>();
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private DownloadSerialQueue serialQueue;
    private AdminSQLiteOpenHelper db = null;

    public SaverService() {
        super("SaverService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotifyManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        final DownloadContext.QueueSet set = new DownloadContext.QueueSet();
        set.setParentPathFile(Files.getRunningDirByFile());
        set.setMinIntervalMillisCallbackProcess(200);
        set.commit();
        serialQueue = new DownloadSerialQueue(getListener());
        db = new AdminSQLiteOpenHelper(this.getBaseContext());

    }

    @NonNull
    private DownloadListener getListener() {
        return new DownloadListener4WithSpeed() {
            @Override
            public void infoReady(@NonNull DownloadTask task, @NonNull BreakpointInfo info, boolean fromBreakpoint, @NonNull Listener4SpeedAssistExtend.Listener4SpeedModel model) {

            }

            @Override
            public void progressBlock(@NonNull DownloadTask task, int blockIndex, long currentBlockOffset, @NonNull SpeedCalculator blockSpeed) {

            }

            @Override
            public void progress(@NonNull DownloadTask task, long currentOffset, @NonNull SpeedCalculator taskSpeed) {
            }

            @Override
            public void blockEnd(@NonNull DownloadTask task, int blockIndex, BlockInfo info, @NonNull SpeedCalculator blockSpeed) {

            }

            @Override
            public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull SpeedCalculator taskSpeed) {
                System.out.println("ERROR");
            }

            @Override
            public void taskStart(@NonNull DownloadTask task) {

            }

            @Override
            public void connectTrialStart(@NonNull DownloadTask task, @NonNull Map<String, List<String>> requestHeaderFields) {

            }

            @Override
            public void connectTrialEnd(@NonNull DownloadTask task, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {

            }


            @Override
            public void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull Map<String, List<String>> requestHeaderFields) {

            }

            @Override
            public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {

            }

            @Override
            public void fetchStart(@NonNull DownloadTask task, int blockIndex, long contentLength) {

            }


            @Override
            public void fetchEnd(@NonNull DownloadTask task, int blockIndex, long contentLength) {
                File file = task.getFile();
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(getBaseContext(), BuildConfig.APPLICATION_ID + ".provider", file);
                } else {
                    uri = Uri.fromFile(file);
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "image/* video/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                mBuilder.setContentTitle("Download")
                        .setContentText("Downloaded " + task.getFilename())
                        .setSmallIcon(R.drawable.ic_cloud_download)
                        .setProgress(100, 100, false)
                        .setContentIntent(PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT));

                mNotifyManager.notify(task.getId(), mBuilder.build());
            }

        };
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        mBuilder = getmBuilder();


        Needle.onBackgroundThread().execute(() -> {
            while (true) {
                listlinks = db.getAllUnSavePostLinks();
                if (!listlinks.isEmpty()) {
                    PostLink postLink = listlinks.get(0);
                    try {
                        String linkUrl = postLink.getUrl();
                        String[] split = linkUrl.split("/");
                        String[] namelink = linkUrl.split(":NAME:");
                        serialQueue.enqueue(new DownloadTask.Builder((namelink.length > 1 ? namelink[1] : linkUrl), Files.getRunningDirByFile(postLink.getUsername().replace("#", "")))
                                .setFilename((namelink.length > 1 ? namelink[0] : split[split.length - 1]))
                                .setPassIfAlreadyCompleted(true)
                                .build());
                        removeSafe(postLink);
                    } catch (Exception e) {
                        removeSafe(postLink);
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
            int importance = NotificationManager.IMPORTANCE_NONE;
            NotificationChannel mChannel = null;
            mChannel = new NotificationChannel(chanel_id, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.BLUE);
            mNotifyManager.createNotificationChannel(mChannel);
            return new NotificationCompat.Builder(this, chanel_id).setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND).setVibrate(new long[]{0L});
        } else {
            return new NotificationCompat.Builder(this).setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND).setVibrate(new long[]{0L});

        }


    }

    private void removeSafe(PostLink postLink) {
        postLink.setSave(true);
        db.updatePostLink(postLink);
    }


}
