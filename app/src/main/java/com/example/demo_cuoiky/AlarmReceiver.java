package com.example.demo_cuoiky;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String taskName = intent.getStringExtra("task_name");
        int taskId = intent.getIntExtra("task_id", -1);

        if (taskName != null && taskId != -1) {
            Log.d("AlarmReceiver", "Alarm received for task: " + taskName + ", ID: " + taskId);

            // Hiển thị thông báo
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Kênh thông báo cho Android 8.0+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        "task_channel",
                        "Task Notifications",
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationManager.createNotificationChannel(channel);
            }

            // Tạo thông báo
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "task_channel")
                    .setSmallIcon(R.drawable.ic_notification)  // Icon thông báo
                    .setContentTitle("Task Reminder")
                    .setContentText("Don't forget: " + taskName)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            notificationManager.notify(taskId, builder.build());
        } else {
            Log.e("AlarmReceiver", "Invalid data in Intent");
        }
    }
}
