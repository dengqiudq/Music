package com.example.trf.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trf.myapplication.Util.ConstUtil;
import com.example.trf.myapplication.Util.FileUtil;
import com.example.trf.myapplication.service.MyMusicPlayerService;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {

    public static TextView songName;
    private TextView currentTime;
    private TextView allTime;
    private ImageButton ibPlay;
    private ImageButton ibNext;
    private ImageButton ibPrev;
    private ImageButton ibcycle;
    public static ImageView imageView;
    public static SeekBar seekBar;
    public static boolean isPlaying;

//    private boolean isIsPlaying

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取控件
        initView();
        //设置监听
        setSeekBarOnClick();
        //注册广播
        setRegisterReceiver();

        //启动后台Service
        String change = getIntent().getStringExtra("change");
        if (TextUtils.isEmpty(change)) {
            setStartService();
        } else {
            sendBroadcastToService(ConstUtil.REFRESHUI);
            ibPlay.setImageResource(android.R.drawable.ic_media_pause);
        }

    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        if (!isPlaying){
//            setStartService(intent);
//        }
//    }

    //开启Service
    private void setStartService() {
        Intent intent = new Intent(this, MyMusicPlayerService.class);
        int position = getIntent().getIntExtra("position", 0);
        intent.putExtra("position", position);
        startService(intent);
        ibPlay.setImageResource(android.R.drawable.ic_media_pause);
    }


    private void initView() {
        songName = (TextView) findViewById(R.id.songName);
        currentTime = (TextView) findViewById(R.id.currentTime);
        allTime = (TextView) findViewById(R.id.allTime);
        ibPlay = (ImageButton) findViewById(R.id.start_button);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        ibNext = (ImageButton) findViewById(R.id.next_button);
        ibPrev = (ImageButton) findViewById(R.id.prev_button);
        ibcycle = (ImageButton) findViewById(R.id.cycle_button);
        imageView = (ImageView) findViewById(R.id.imageView);

    }

    private void setRegisterReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstUtil.SEEKBAR_MAX_ACTION);
        intentFilter.addAction(ConstUtil.SEEKBAR_ACTION);
        registerReceiver(receiver, intentFilter);
    }


    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.start_button:
                if (!isPlaying) {
                    ibPlay.setImageResource(android.R.drawable.ic_media_pause);
                    sendBroadcastToService(ConstUtil.STATE_PLAY);
                    isPlaying = true;
                } else {
                    ibPlay.setImageResource(android.R.drawable.ic_media_play);
                    sendBroadcastToService(ConstUtil.STATE_PAUSE);
                    isPlaying = false;
                }
                break;

            case R.id.next_button:
                ibPlay.setImageResource(android.R.drawable.ic_media_pause);
                sendBroadcastToService(ConstUtil.STATE_NEXT);
                isPlaying = true;
                break;

            case R.id.prev_button:
                ibPlay.setImageResource(android.R.drawable.ic_media_pause);
                sendBroadcastToService(ConstUtil.STATE_PREVIOUS);
                isPlaying = true;
                break;

            case R.id.cycle_button:
                if (MyMusicPlayerService.mediaPlayer != null) {
                    if (MyMusicPlayerService.mediaPlayer.isLooping()) {
                        MyMusicPlayerService.mediaPlayer.setLooping(false);
                        ibcycle.setImageResource(android.R.drawable.ic_menu_rotate);
                        Toast.makeText(MainActivity.this, "单曲循环关闭", Toast.LENGTH_SHORT).show();
                    } else {
                        MyMusicPlayerService.mediaPlayer.setLooping(true);
                        ibcycle.setImageResource(android.R.drawable.ic_popup_sync);
                        Toast.makeText(MainActivity.this, "单曲循环开启", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }

    }


    private void setSeekBarOnClick() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MyMusicPlayerService.isauto = true;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                MyMusicPlayerService.isauto = false;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //当拖动停止后，控制mediaPlayer播放指定位置的音乐
                if (!MyMusicPlayerService.isauto) {
                    MyMusicPlayerService.mediaPlayer.seekTo(seekBar.getProgress());
                }
            }
        });
    }

    /**
     * 向后台Service发送控制广播
     *
     * @param state int control 控制状态码
     */
    protected void sendBroadcastToService(int state) {
        intent = new Intent();
        intent.setAction(ConstUtil.MUSICSERVICE_ACTION);
        intent.putExtra("control", state);
        //向后台Service发送播放控制的广播
        sendBroadcast(intent);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String intentAction = intent.getAction();
            if ((ConstUtil.SEEKBAR_MAX_ACTION).equals(intentAction)) {
                int count = intent.getIntExtra("count", 0);
                seekBar.setMax(count);
                int max = count / 1000;
                int m = max / 60;
                int s = max % 60;
                allTime.setText(m + ":" + s);
            } else if ((ConstUtil.SEEKBAR_ACTION).equals(intentAction)) {
                int currtTime = intent.getIntExtra("currtTime", 0);
                int now = currtTime / 1000;
                int m = now / 60;
                int s = now % 60;
                if (s < 10) {
                    currentTime.setText(m + ":0" + s);
                } else {
                    currentTime.setText(m + ":" + s);

                }
                seekBar.setProgress(currtTime);
            }
        }
    };

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_HEADSETHOOK){
//            if(MyMusicPlayerService.mediaPlayer.isPlaying()){
//                sendBroadcastToService(ConstUtil.STATE_PAUSE);
//            }else{
//                sendBroadcastToService(ConstUtil.STATE_PLAY);
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}

