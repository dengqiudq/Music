package com.example.trf.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trf.myapplication.Util.AudioUtils;
import com.example.trf.myapplication.Util.ConstUtil;
import com.example.trf.myapplication.Util.FileUtil;
import com.example.trf.myapplication.adapter.MyAdapter;
import com.example.trf.myapplication.entity.Song;
import com.example.trf.myapplication.service.MyMusicPlayerService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {

    public static TextView songName;
    private TextView currentTime;
    private TextView allTime;
    public static ImageButton ibPlay;
    private ImageButton ibNext;
    private ImageButton ibPrev;
    private ImageButton ibcycle;
    private ImageButton ibmenu;
    public static ImageView imageView;
    public static SeekBar seekBar;
    private ArrayList<Song> files;
    Intent intent;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
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
            sendBroadcastToService(ConstUtil.REFRESHMAIN);
        }

    }


    //开启Service
    private void setStartService() {
        Intent intent = new Intent(this, MyMusicPlayerService.class);
        startService(intent);
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
        ibmenu = (ImageButton) findViewById(R.id.menu_button);

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
                if (!ListActivity.isPlaying) {
                    sendBroadcastToService(ConstUtil.STATE_PLAY);
                    ListActivity.isPlaying = true;
                    ibPlay.setImageResource(R.drawable.player_pause);
                } else {
                    sendBroadcastToService(ConstUtil.STATE_PAUSE);
                    ListActivity.isPlaying = false;
                    ibPlay.setImageResource(R.drawable.player_play);
                }
                break;

            case R.id.next_button:
                sendBroadcastToService(ConstUtil.STATE_MAINNEXT);
                ListActivity.isPlaying = true;
                ibPlay.setImageResource(R.drawable.player_pause);
                break;

            case R.id.prev_button:
                sendBroadcastToService(ConstUtil.STATE_PREVIOUS);
                ListActivity.isPlaying = true;
                ibPlay.setImageResource(R.drawable.player_pause);
                break;

            case R.id.cycle_button:
                if (MyMusicPlayerService.mediaPlayer != null) {
                    if (MyMusicPlayerService.mediaPlayer.isLooping()) {
                        MyMusicPlayerService.mediaPlayer.setLooping(false);
                        ibcycle.setImageResource(R.drawable.icon_playing_mode_repeat_all);
                        Toast.makeText(MainActivity.this, "列表循环", Toast.LENGTH_SHORT).show();
                    } else {
                        MyMusicPlayerService.mediaPlayer.setLooping(true);
                        ibcycle.setImageResource(R.drawable.icon_playing_mode_repeat_cur);
                        Toast.makeText(MainActivity.this, "单曲循环", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.menu_button:

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




    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}

