package com.sophra.music_timer;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.Toast;

public class Alarm extends BroadcastReceiver {

    MediaPlayer mediaPlayer;
    SharedPreferences pref;


    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "노래를 중지합니다.", Toast.LENGTH_SHORT).show();

        //SharedPreferences.Editor editor = pref.edit();
        //editor.putLong("setTime", 0);  //설정한 시간 초기화해서 저장
        //editor.commit(); //데이터 저장

        NotificationManager notificancel = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificancel.cancelAll();

        int notifyID = 2;

        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //오디오 매니저

        AudioManager.OnAudioFocusChangeListener focusChangeListener =
                new AudioManager.OnAudioFocusChangeListener() {
                    public void onAudioFocusChange(int focusChange) {
                        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        switch (focusChange) {

                            case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
                                // Lower the volume while ducking.
                                mediaPlayer.setVolume(0.2f, 0.2f);
                                break;
                            case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):
                                break;

                            case (AudioManager.AUDIOFOCUS_LOSS):
                                break;

                            case (AudioManager.AUDIOFOCUS_GAIN):
                                // Return the volume to normal and resume if paused.
                                mediaPlayer.setVolume(1f, 1f);
                                mediaPlayer.start();
                                break;
                            default:
                                break;
                        }
                    }
                };

        int result = am.requestAudioFocus(focusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if(android.os.Build.VERSION.SDK_INT > 25) {  //안드로이드 8부터

            //푸시를 클릭했을때 이동//
            Intent intents = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 , intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
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
                        .setContentTitle("음악이 종료되었습니다")
                        .setSubText("종료")
                        .setOngoing(false)
                        .build();
                mNotificationManager.notify(notifyID, notification);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        else{
            NotificationManager notificationManager;
            PendingIntent intent2 = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder builder = null;
            try {
                builder = new Notification.Builder(context)
                        .setSmallIcon(R.mipmap.ic_music_timer)
                        .setDefaults(Notification.BADGE_ICON_NONE)
                        .setAutoCancel(true)
                        .setContentTitle("음악이 종료되었습니다")
                        .setSubText("종료")
                        .setPriority(Notification.PRIORITY_HIGH)
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

    //노티 관련
