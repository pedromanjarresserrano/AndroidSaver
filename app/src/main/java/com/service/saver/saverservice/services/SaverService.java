package com.service.saver.saverservice.services;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.service.saver.saverservice.MainTabActivity;
import com.service.saver.saverservice.MyApp;
import com.service.saver.saverservice.util.Files;
import com.shashank.sony.fancytoastlib.FancyToast;

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
    private List<String> listlinks = new ArrayList<>();
   // private NotificationManager mNotifyManager;
  //  private NotificationCompat.Builder mBuilder;

    public SaverService() {
        super("SaverService");
     //   mBuilder = new NotificationCompat.Builder((Context) MainTabActivity.activity.getBaseContext(), "SSSAVER");
    //    mBuilder.setContentTitle("Tweet Download")
   //             .setContentText("Downloading")
   //             .setSmallIcon(R.drawable.ic_launcher_foreground);
    }


    @SuppressLint("NewApi")
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Needle.onBackgroundThread().execute(() -> {
            while (true) {
                listlinks = MyApp.getFiles();
                try {
                    if (!listlinks.isEmpty()) {
                        String string = listlinks.get(0);
                        try {
                            URL url;
                            String[] split = string.split("/");
                            File file = new File(Files.getRunningDir() + "/" + split[split.length - 1]);
                            int count;
                            long progress = 0;
                            try {
                                url = new URL(string);
                                URLConnection conection = url.openConnection();
                                conection.connect();
                                long lenghtOfFile = conection.getContentLength();
                                if (file.exists() && file.length() == lenghtOfFile) {
                                    FancyToast.makeText(MainTabActivity.activity.getBaseContext(), "File Foubnd!", Toast.LENGTH_SHORT, FancyToast.INFO, true).show();
                                    return;
                                } else {
                                    InputStream input = new BufferedInputStream(url.openStream(), 12192);
                                    OutputStream output = new FileOutputStream(file);
                                    byte data[] = new byte[1024];
                                    long total = 0;
                               //     mBuilder.setContentText("Downloading");
                              //      mBuilder.setProgress(0, 0, true)
                               //             .setContentInfo(file.getName());
                               //     mNotifyManager.notify(1111111, mBuilder.build());
                                    while ((count = input.read(data)) != -1) {
                                        total += count;
                                        output.write(data, 0, count);
                                    }
                                    FancyToast.makeText(MainTabActivity.activity.getBaseContext(), "Saved!", Toast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                                    output.flush();
                                    output.close();
                                    input.close();
                                }

                            } catch (Exception e) {

                            } finally {
                            //    mBuilder.setProgress(0, 0, false).setContentText("Download completed");
                            //    mNotifyManager.notify(1111111, mBuilder.build());
                            }
                           // mBuilder.setProgress(0, 0, false).setContentText("Download completed");
                           // mNotifyManager.notify(1111111, mBuilder.build());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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

    private void removeSafe(String string) {
        listlinks.remove(string);
        MyApp.remove(string);
        listlinks = MyApp.getFiles();
    }


}
