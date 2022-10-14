package com.service.saver.saverservice.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

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
import com.service.saver.saverservice.util.ClipDataListener;
import com.service.saver.saverservice.util.Files;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Pedro R on 4/03/2017.
 */

public class SaverService extends JobService {
    private static Runnable LINK_CAPTURE;
    private List<PostLink> listlinks = new ArrayList<>();
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private DownloadSerialQueue serialQueue;
    private AdminSQLiteOpenHelper db = null;
    public static ClipDataListener CLIPDATALISTENER;
    private SharedPreferences settings;
    public static Integer COUNTER = 0;
    public static Boolean IN_BACK = Boolean.FALSE;

    public static void setOnValidLinkCapture(Runnable linkCapture) {
        LINK_CAPTURE = linkCapture;
    }

    @Override
    public void onCreate() {
        mNotifyManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        final DownloadContext.QueueSet set = new DownloadContext.QueueSet();
        set.setParentPathFile(Files.getRunningDirByFile());
        set.setMinIntervalMillisCallbackProcess(200);
        set.commit();
        serialQueue = new DownloadSerialQueue(getListener());
        db = new AdminSQLiteOpenHelper(this.getBaseContext());
        if (SaverService.CLIPDATALISTENER == null) {
            setupClipListener();
        }
        settings = getBaseContext().getSharedPreferences("settings", 0);
    }

    private void setupClipListener() {
        if (SaverService.CLIPDATALISTENER == null) {
            CLIPDATALISTENER = new ClipDataListener(getBaseContext());
            CLIPDATALISTENER.onValidLinkCapture(LINK_CAPTURE);
        }
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
                System.out.println("ERROR" + cause);
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
                System.out.println("ERROR");

            }

            @Override
            public void fetchStart(@NonNull DownloadTask task, int blockIndex, long contentLength) {
                System.out.println("Start");

            }


            @Override
            public void fetchEnd(@NonNull DownloadTask task, int blockIndex, long contentLength) {
                File file = task.getFile();
                PostLink postLink = (PostLink) task.getTag(0);
                removeSafe(postLink);
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
                        .setContentIntent(PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT));

                mNotifyManager.notify(task.getId(), mBuilder.build());

            }

        };
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        mBuilder = getmBuilder();


        new com.service.saver.saverservice.util.Util.Companion.Task(() -> {

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
                                .build().addTag(0, postLink));
                       // removeSafe(postLink);
                    } catch (Exception e) {
                        removeSafe(postLink);
                        Log.e("ERROR", "E/RR", e);
                    }
                }
                COUNTER += 1;
                try {
                    Thread.currentThread().sleep(1000L);
                } catch (InterruptedException e) {
                    Log.e("ERROR", "E/RR", e);
                }
                setupClipListener();
                if(COUNTER == 60 && IN_BACK){
//                    System.exit(0);
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        this.jobFinished(params, true);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    @NonNull
    private NotificationCompat.Builder getmBuilder() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String chanel_id = "3002";
            CharSequence name = "Channel Name";
            String description = "Chanel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = null;
            mChannel = new NotificationChannel(chanel_id, name, importance);
            mChannel.setDescription(description);
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

    @Override
    public void onDestroy() {
        System.out.println("Destroy");
    }
}
