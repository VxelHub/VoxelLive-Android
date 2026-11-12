package com.voxellive.android;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.File;

public class VoxelLiveService extends Service {
    private static final String CHANNEL = "voxellive_runtime";
    private PowerManager.WakeLock wakeLock;

    @Override public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            getSystemService(NotificationManager.class).createNotificationChannel(
                new NotificationChannel(CHANNEL, "VoxelLive runtime", NotificationManager.IMPORTANCE_LOW)
            );
        }
        Notification.Builder builder = Build.VERSION.SDK_INT >= 26
            ? new Notification.Builder(this, CHANNEL)
            : new Notification.Builder(this);
        startForeground(2001, builder
            .setContentTitle("VoxelLive aktif")
            .setContentText("Menjaga koneksi TikTok LIVE dan Minecraft")
            .setSmallIcon(android.R.drawable.stat_notify_sync_noanim)
            .setOngoing(true)
            .build());

        try {
            PowerManager power = (PowerManager) getSystemService(POWER_SERVICE);
            if (power != null) {
                wakeLock = power.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "VoxelLive:LiveRuntime");
                wakeLock.setReferenceCounted(false);
                wakeLock.acquire(8L * 60L * 60L * 1000L); // Safety timeout: 8 hours.
            }
        } catch (Exception e) {
            Log.w("VoxelLive", "Tidak dapat mengaktifkan wakelock", e);
        }

        new Thread(() -> {
            try {
                if (!Python.isStarted()) Python.start(new AndroidPlatform(this));
                PyObject server = Python.getInstance().getModule("server");
                File data = new File(getFilesDir(), "voxellive-data");
                if (!data.exists() && !data.mkdirs()) throw new IllegalStateException("Folder data tidak bisa dibuat");
                server.callAttr("start_background", data.getAbsolutePath());
            } catch (Throwable e) {
                Log.e("VoxelLive", "Backend gagal dimulai", e);
            }
        }, "VoxelLive-Python-Startup").start();
    }

    @Override public int onStartCommand(Intent intent, int flags, int id) {
        return START_STICKY;
    }

    @Override public void onDestroy() {
        try {
            if (wakeLock != null && wakeLock.isHeld()) wakeLock.release();
        } catch (Exception e) {
            Log.w("VoxelLive", "Gagal melepas wakelock", e);
        }
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}
