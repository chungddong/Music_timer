package com.sophra.music_timer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Button btn_start;
    Boolean isStart = false;

    EditText et_hour;
    EditText et_min;

    TextView tv_semi;

    MediaPlayer mediaPlayer;

    int hour;
    int min;
    long setTime;
    long beforeTime;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private Handler mHandler = new Handler();
    Timer timer = new Timer();

    Calendar currentTime;
    Animation anim;



    String noti_id = "sophra_music_timer";


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        currentTime = Calendar.getInstance();

        anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink); //깜박이는 애니메이션 지정
        hour = pref.getInt("hour", 0);
        min = pref.getInt("min", 0);
        setTime = pref.getLong("setTime", 0);

        et_hour = findViewById(R.id.et_hour);
        et_min = findViewById(R.id.et_min);

        tv_semi = findViewById(R.id.tv_semi); //세미콜론
        btn_start = findViewById(R.id.btn_start);

        if (currentTime.getTimeInMillis() < setTime)  //현재시간이 설정한 시간보다 작으면 - > 아직 끝나는 시간이 아니라면
        {
            tv_semi.startAnimation(anim);
            btn_start.setText("정지");
            isStart = true;
            btn_start.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C14F4F")));

            timer.schedule(new TimerTask() {
                Runnable updateRemainingTimeRunnable = new Runnable() {
                    @Override
                    public void run() {
                        synchronized (mHandler) {
                            long currentTime = System.currentTimeMillis();
                            updateTimeRemaining(currentTime);
                        }
                    }
                };

                @Override
                public void run() {
                    mHandler.post(updateRemainingTimeRunnable);
                }
            }, 1000, 1000);
        }
        else
        {
            if (pref != null) {
                hour = pref.getInt("hour", 0);
                min = pref.getInt("min", 0);
                setTime = pref.getLong("setTime", 0);

                if (hour < 10) //10 이하일시
                {
                    et_hour.setText("0" + Integer.toString(hour));
                } else {
                    et_hour.setText(Integer.toString(hour));
                } //아닐시

                if (min < 10) {
                    et_min.setText("0" + Integer.toString(min));
                } else {
                    et_min.setText(Integer.toString(min));
                }


                Log.v("sophra_musictimer", "Saved time 시간 : " + hour + " 분 : " + min);
            }
        }

        et_hour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (Integer.parseInt(et_hour.getText().toString()) >= 24) {
                    et_hour.setText("23");
                    et_hour.setSelection(et_hour.length());
                }
            }
        });

        et_min.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (Integer.parseInt(et_min.getText().toString()) >= 60) {
                    et_min.setText("59");
                    et_min.setSelection(et_hour.length());
                }
            }
        });


        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int beforeColor;
                int afterColor;


                //btn_start.setAnimation(anim);
                //hour = Integer.parseInt(et_hour.getText().toString());  //입력된 시간 값 int 로 가져오는 거
                //min = Integer.parseInt(et_min.getText().toString());

                //Log.v("sophra_musictimer", "메시지" + Integer.parseInt(et_hour.getText().toString()) );
                //Log.v("sophra_musictimer", "메시지" + Integer.parseInt(et_hour.getText().toString()) );

                if (Integer.parseInt(et_hour.getText().toString()) != 0 || Integer.parseInt(et_min.getText().toString()) != 0)  // 시간과 분이 0이 아닐시 실행 hour != 0 || min != 0
                {
                    if (isStart != true)  //시작 눌렀을 때
                    {
                        tv_semi.startAnimation(anim);
                        beforeColor = Color.parseColor("#4F61C1");
                        afterColor = Color.parseColor("#C14F4F");

                        btn_start.setText("정지");
                        isStart = true;

                        hour = Integer.parseInt(et_hour.getText().toString());
                        min = Integer.parseInt(et_min.getText().toString());

                        if(hour !=0)
                        {
                            Toast.makeText(getApplicationContext(), hour + "시간" + " 후에 음악이 종료됩니다", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), min +"분" + " 후에 음악이 종료됩니다", Toast.LENGTH_SHORT).show();
                        }


                        et_hour.setFocusableInTouchMode(false);  //에딧 텍스트 수정 막는 코드
                        et_min.setFocusableInTouchMode(false);


                        editor.putInt("hour", hour);  // 저장하는 거
                        editor.putInt("min", min);


                        Calendar calendar = Calendar.getInstance();

                        //1초가 1000ms  1분은 60000ms 1시간은 60000ms * 60

                        long currentms = calendar.getTimeInMillis();
                        long timems = (hour * 60000 * 60) + (min * 60000);

                        timems += currentms; // 음악종료할 시간

                        calendar.setTimeInMillis(timems);
                        setTime = calendar.getTimeInMillis();
                        editor.putLong("setTime", calendar.getTimeInMillis());  //설정한 시간 저장
                        editor.putLong("beforeTime", currentms);  //설정했던 시간 저장
                        Log.v("sophra_musictimer", "종료 시간 : " + calendar.getTime());

                        regist(view, calendar, System.currentTimeMillis());

                        editor.commit(); //데이터 저장

                        timer = new Timer();

                        timer.schedule(new TimerTask() {
                            Runnable updateRemainingTimeRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    synchronized (mHandler) {
                                        long currentTime = System.currentTimeMillis();
                                        updateTimeRemaining(currentTime);
                                    }
                                }
                            };

                            @Override
                            public void run() {
                                mHandler.post(updateRemainingTimeRunnable);
                            }
                        }, 1000, 1000);


                    } else  //정지 눌렀을 때
                    {
                        et_hour.setFocusableInTouchMode(true);  //에딧 텍스트 수정 허용 코드
                        et_min.setFocusableInTouchMode(true);
                        tv_semi.clearAnimation();
                        beforeColor = Color.parseColor("#C14F4F");
                        afterColor = Color.parseColor("#4F61C1");
                        btn_start.setText("시작");
                        isStart = false;
                        setTime = 0;
                        editor.putLong("setTime", 0);  //설정한 시간 초기화해서 저장
                        editor.commit(); //데이터 저장
                        unregist(view);
                        if (hour < 10) //10 이하일시
                        {
                            et_hour.setText("0" + Integer.toString(hour));
                        } else {
                            et_hour.setText(Integer.toString(hour));
                        } //아닐시

                        if (min < 10) {
                            et_min.setText("0" + Integer.toString(min));
                        } else {
                            et_min.setText(Integer.toString(min));
                        }

                        timer.cancel();  //타이머 종료

                    }

                    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), beforeColor, afterColor);
                    colorAnimation.setDuration(500);
                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            btn_start.setBackgroundTintList(ColorStateList.valueOf((int) valueAnimator.getAnimatedValue()));
                        }
                    });
                    colorAnimation.start();


                } else  //둘다 0일시
                {
                    Log.v("sophra_musictimer", "시간 설정 안함");
                    Toast.makeText(getApplicationContext(), "시간을 설정해주세요!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    public void regist(View view, Calendar calendar, long realtime) {
        AlarmManager alarmManager;
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, Alarm.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);


        //종료하면 나올 알림
        alarmManager.setExact(AlarmManager.RTC, calendar.getTimeInMillis(), pIntent);

        //반복 알람
        Calendar current = Calendar.getInstance();
        Intent repeatintent = new Intent(this, RepeatAlarm.class);
        PendingIntent repeatIntent = PendingIntent.getBroadcast(MainActivity.this, 1, repeatintent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC, realtime, 30000, repeatIntent);
        //alarmManager.setInexactRepeating(AlarmManager.RTC, currentTime.getTimeInMillis(), 60000, repeatIntent);
    }

    public void unregist(View view) {
        AlarmManager alarmManager;
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //종료시 알람
        Intent intent = new Intent(this, Alarm.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        //반복 알람
        Intent repeatintent = new Intent(this, RepeatAlarm.class);
        PendingIntent repeatIntent = PendingIntent.getBroadcast(MainActivity.this, 1, repeatintent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        //알림 취소
        alarmManager.cancel(pIntent);
        alarmManager.cancel(repeatIntent);

    }


    //남은 시간 계산 코드
    public void updateTimeRemaining(long currentTime) {
        long timeDiff = setTime - currentTime;  //남은시간

        //Log.v("sophra_musictimer", "timediff" + timeDiff);
        if (timeDiff > 0) {
            int seconds = (int) (timeDiff / 1000) % 60;
            int minutes = (int) ((timeDiff / (1000 * 60)) % 60);
            int hours = (int) ((timeDiff / (1000 * 60 * 60)) % 24);

            Log.v("sophra_musictimer", "남은시간 : " + hours + "시간 " + minutes + "분 " + seconds + "초");

            if(hours < 10)
            {
                et_hour.setText("0" + Integer.toString(hours));
            }
            else
            {
                et_hour.setText(Integer.toString(hours));
            }

            if(minutes < 10)
            {
                et_min.setText("0" + Integer.toString(minutes + 1));
            }
            else
            {
                et_min.setText(Integer.toString(minutes + 1));
            }

        } else {

            //종료되었을 때
            timer.cancel();
            Log.v("sophra_musictimer", "끝남");
            //시간 초기화 하기

            if (hour < 10) //10 이하일시
            {
                et_hour.setText("0" + Integer.toString(hour));
            } else {
                et_hour.setText(Integer.toString(hour));
            } //아닐시

            if (min < 10) {
                et_min.setText("0" + Integer.toString(min));
            } else {
                et_min.setText(Integer.toString(min));
            }

            et_hour.setFocusableInTouchMode(true);  //에딧 텍스트 수정 허용 코드
            et_min.setFocusableInTouchMode(true);
            tv_semi.clearAnimation();
            btn_start.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4F61C1")));
            btn_start.setText("시작");
            isStart = false;
            setTime = 0;
            editor.putLong("setTime", 0);  //설정한 시간 초기화해서 저장
            editor.commit(); //데이터 저장
        }
    }



    //Edit Text 이외 터치시 포거스 나가게 하는 코드

    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                }
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

}