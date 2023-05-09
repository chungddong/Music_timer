package com.sophra.music_timer;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Debug;
import android.util.Log;

import java.util.Calendar;

public class RepeatAlarm extends BroadcastReceiver {

    SharedPreferences pref;
    long setTime;

    Calendar currentTime;

    @Override
    public void onReceive(Context context, Intent intent) {

        int notifyID = 3;

        pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
        setTime = pref.getLong("setTime", 0);

        Log.v("sophra_musictimer","반복 알람 호출");

        currentTime = Calendar.getInstance();


        long timeDiff = setTime - currentTime.getTimeInMillis();  //남은시간

        String remain = "";

        if (timeDiff > 0) {
            int seconds = (int) (timeDiff / 1000) % 60;
            int minutes = (int) ((timeDiff / (1000 * 60)) % 60);
            int hours = (int) ((timeDiff / (1000 * 60 * 60)) % 24);


            //minutes += 1;

            if(minutes != 0)
            {
                if(hours != 0)
                {
                    remain = hours + "시간 " + minutes + "분 " + "뒤 음악이 종료됩니다";
                }
                else
                {
                    remain = minutes + "분 " + "뒤 음악이 종료됩니다";
                }
            }
            else
            {
                remain = "음악이 곧 종료됩니다";
            }

        }




            // 노티 알림 코드
        if(android.os.Build.VERSION.SDK_INT > 25) {  //안드로이드 8부터

            //푸시를 클릭했을때 이동//
            Intent intents = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            //푸시를 클릭했을때 이동//

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel mChannel = new NotificationChannel("musictimer", "musict", NotificationManager.IMPORTANCE_LOW);
            mChannel.setDescription("");
            mChannel.enableLights(false);
            mChannel.setVibrationPattern(new long[]{0});
            mChannel.enableVibration(true);

            mNotificationManager.createNotificationChannel(mChannel);
            mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

            try {
                Notification notification = new Notification.Builder(context,"musictimer")
                        .setSmallIcon(R.mipmap.ic_music_timer)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setContentTitle(remain)
                        .setSubText("음악 타이머")
                        .setOngoing(false)  //안 지워지게 하는거
                        .build();
                mNotificationManager.notify(notifyID, notification);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        //오레오 미만 기기에서
        else{
            NotificationManager notificationManager;
            PendingIntent intent2 = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder builder = null;
            try {
                builder = new Notification.Builder(context)
                        .setSmallIcon(R.mipmap.ic_music_timer)
                        .setDefaults(Notification.BADGE_ICON_NONE)
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentTitle(remain)
                        .setSubText("음악 타이머")
                        .setContentIntent(intent2);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(notifyID, builder.build());
        }
    }
}
