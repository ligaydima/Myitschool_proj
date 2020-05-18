package com.example.myitschool_proj;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.channel_reminder_name))
                .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                .setContentTitle(intent.getStringExtra("title"))
                .setContentText(intent.getStringExtra("text"))
                .setSubText(intent.getStringExtra("description"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_ALARM);
        NotificationManagerCompat notif_manager = NotificationManagerCompat.from(context);
        notif_manager.notify(1, builder.build());
    }
}
