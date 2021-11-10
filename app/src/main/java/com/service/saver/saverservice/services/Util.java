package com.service.saver.saverservice.services;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.PersistableBundle;

import com.service.saver.saverservice.twitter.TwitterActivity;

import org.jetbrains.annotations.NotNull;

public class Util {

    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, SaverService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent)
                .setMinimumLatency(1)
                .setOverrideDeadline(1);
        builder.setTriggerContentMaxDelay(0);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setPersisted(true);
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(context.JOB_SCHEDULER_SERVICE);
        int resultCode = jobScheduler.schedule(builder.build());
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            System.out.println("Success");
        }
    }


    public static void scheduleJob(@NotNull Context context, @NotNull Runnable callback) {
        ComponentName serviceComponent = new ComponentName(context, SaverService.class);

        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent)
                .setMinimumLatency(1)
                .setOverrideDeadline(1);

        builder.setTriggerContentMaxDelay(0);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setPersisted(true);
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(context.JOB_SCHEDULER_SERVICE);
        int resultCode = jobScheduler.schedule(builder.build());
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            System.out.println("Success");
            callback.run();
        }

    }
}