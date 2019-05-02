package com.fourweather.learn.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.fourweather.learn.R;
import com.fourweather.learn.View.APP;
import com.fourweather.learn.View.ScheduleActivity;
import com.fourweather.learn.View.ScheduleManageActivity;
import com.fourweather.learn.View.WeatherActivity1;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

public class AlarmReceiver extends BroadcastReceiver {

    public static final int TRIP_NOTIFY_REQUEST_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.fourweather.learn.TRIP_ADVANCE_NOTIFY")) {

            // 点击通知后，进入行程管理Ac
            Intent i = new Intent(APP.getContext(), ScheduleManageActivity.class);
            PendingIntent pi = PendingIntent.getActivity(APP.getContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);


            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification tripNotify = new NotificationCompat.Builder(context, ScheduleActivity.CHANNEL_TRIP_ADVANCE_NOTIFY)
                    .setContentTitle("主人，别忘了您的行程安排哟")
                    .setContentText("逃跑计划")
                    .setContentIntent(pi)
                    .setWhen(System.currentTimeMillis())
                    .setTicker("行程进行中")
                    .setSmallIcon(R.drawable.ic_trip_notify_small)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_trip_notify_large))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setColor(Color.parseColor("#4b9afc"))
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .build();
            notificationManager.notify(TRIP_NOTIFY_REQUEST_ID, tripNotify);
        }
    }
}
