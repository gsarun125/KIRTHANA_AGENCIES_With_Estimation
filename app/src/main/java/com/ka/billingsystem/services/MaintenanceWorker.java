package com.ka.billingsystem.services;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MaintenanceWorker extends Worker {
    public MaintenanceWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {

        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Intent intent=new Intent();
        intent.setAction("com.ka.billingsystem.Send");
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        getApplicationContext().sendBroadcast(intent);
        return Result.success();
    }


}
