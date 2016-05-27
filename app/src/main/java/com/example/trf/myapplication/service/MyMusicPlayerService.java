package com.example.trf.myapplication.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.trf.myapplication.ListActivity;
import com.example.trf.myapplication.MainActivity;
import com.example.trf.myapplication.R;
import com.example.trf.myapplication.Util.AudioUtils;
import com.example.trf.myapplication.Util.ConstUtil;
import com.example.trf.myapplication.entity.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 16-5-18.
 */
public class MyMusicPlayerService extends Service implements MediaPlayer.OnCompletionListener{
    File path;
    int count;
    //创建一个媒体播放器的对象
    public static MediaPlayer mediaPlayer;
    //存放音乐名的数组
    private ArrayList<Song> files;
    //当前的播放的音乐
    public static int currIndex=0;
    //拖动seekBar定义boolean值
    public static boolean isauto = false;
    //发送广播定义Intent
    Intent intent;

    int state = ConstUtil.STATE_NON;

    Context context;



    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ConstUtil.UPDATE_PROGRESS:
                    intent = new Intent();
                    int currtTime = mediaPlayer.getCurrentPosition();
                    intent.putExtra("currtTime",mediaPlayer.getCurrentPosition());
                    intent.setAction(ConstUtil.SEEKBAR_ACTION);
                    sendBroadcast(intent);
                    handler.sendEmptyMessageDelayed(ConstUtil.UPDATE_PROGRESS,1000);
                    break;

                case ConstUtil.SET_SEEKBAR_MAX:
                    intent = new Intent();
                    int count = mediaPlayer.getDuration();
                    intent.putExtra("count",count);
                    intent.setAction(ConstUtil.SEEKBAR_MAX_ACTION);
                    sendBroadcast(intent);
                    break;
            }


        }
    };

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        //注册广播
        setRegisterReceiver();
        mediaPlayer = new MediaPlayer();
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            path = Environment.getExternalStorageDirectory();
//        } else {
//            Toast.makeText(this, "没有sdcard", Toast.LENGTH_SHORT).show();
//            return;
//        }

//        //获取音乐放入集合
//        files = FileUtil.getFiles(path);
        //自动播放下一首
        mediaPlayer.setOnCompletionListener(this);

    }

    private void setRegisterReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstUtil.MUSICSERVICE_ACTION);
        registerReceiver(musicServiceReceiver, intentFilter);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //获取listActivity点击的歌曲
        int position = intent.getIntExtra("position",0);
        //获取音乐放入集合
        files = AudioUtils.getAllSongs(context);
        currIndex = position;
        play();
        ListActivity.isPlaying = true;
        ListActivity.bt_play.setImageResource(R.drawable.icon_pause_normal);
        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public void onCompletion(MediaPlayer mp) {
        if(files != null && files.size()>0){
            next();
        }else{
            Toast.makeText(MyMusicPlayerService.this,"播放列表为空",Toast.LENGTH_LONG).show();
        }
    }


    //上一首
    private void prev(){
        try{
            currIndex--;
            if(currIndex < 0){
                currIndex = files.size()-1;
            }
            play();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //下一首
    public void next() {
        try{
            currIndex++;
            if(currIndex == files.size()){
                currIndex = 0;
            }
            play();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //停止音乐
    private void stop(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
    }



    protected void play() {

        try {
            mediaPlayer.reset();
            if(files != null && files.size()>0) {
                mediaPlayer.setDataSource(files.get(currIndex).getFileUrl());
            }
//            mediaPlayer.setDataSource(path+"/"+filename);//设置路径
            mediaPlayer.prepare();//准备
            mediaPlayer.start();
            //发送广播停止前台ListActivity更新界面
            refreshList(context);
            isauto = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //设置图片大小
    private void getBitmapDrawable(BitmapDrawable bmpDraw) {
        WindowManager windowManager =
                (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics metric = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metric);
//            int width = windowManager.getDefaultDisplay().getWidth();
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) MainActivity.imageView.getLayoutParams();
        params.height = metric.widthPixels;
        params.width = metric.widthPixels;
        MainActivity.imageView.setLayoutParams(params);
        MainActivity.imageView.setImageDrawable(bmpDraw);
    }


    private BroadcastReceiver musicServiceReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
                int control = intent.getIntExtra("control", 0);
                switch (control) {
                    case ConstUtil.STATE_PLAY://播放音乐
                        if (state == ConstUtil.STATE_PAUSE) {//如果原来状态是暂停
                            mediaPlayer.start();
                        }else if(state != ConstUtil.STATE_PLAY){
                            play();
                        }
                        state = ConstUtil.STATE_PLAY;
                        ListActivity.bt_play.setImageResource(R.drawable.icon_pause_normal);
                        break;
                    case ConstUtil.STATE_PAUSE://暂停播放
                            mediaPlayer.pause();
                            state = ConstUtil.STATE_PAUSE;
                        ListActivity.bt_play.setImageResource(R.drawable.icon_play_normal);
                        break;
                    case ConstUtil.STATE_STOP://停止播放
                        if (control == ConstUtil.STATE_PLAY || control == ConstUtil.STATE_PAUSE) {
                            mediaPlayer.stop();
                            state = ConstUtil.STATE_STOP;
                        }
                        break;
                    case ConstUtil.STATE_PREVIOUS://上一首
                        prev();
                        state = ConstUtil.STATE_PLAY;
                        refreshMain(context);
                        break;
                    case ConstUtil.STATE_NEXT://下一首
                        next();
                        state = ConstUtil.STATE_PLAY;
                        break;
                    case ConstUtil.STATE_MAINNEXT:
                        next();
                        refreshMain(context);
                        state = ConstUtil.STATE_PLAY;
                        break;
                    case ConstUtil.REFRESHLIST:
                        refreshList(context);
                        break;
                    case ConstUtil.REFRESHMAIN:
                        refreshMain(context);
                        MainActivity.ibPlay.setImageResource(R.drawable.player_pause);
                    default:
                        break;
                }
            }

    };

    //刷新ListActivity
    private void refreshList(Context context) {
        //获取专辑图片
        String albumArt = AudioUtils.getAlbumArt(files.get(currIndex).getAlbumpic(), context);
        Bitmap bm = BitmapFactory.decodeFile(albumArt);
        BitmapDrawable bmpDraw = new BitmapDrawable(bm);
        ListActivity.iv_header.setImageDrawable(bmpDraw);
        //获取歌手和歌曲
        Song s = files.get(currIndex);
        ListActivity.tv_musicname.setText(s.getFileName().trim());
        handler.sendEmptyMessage(ConstUtil.SET_SEEKBAR_MAX);
        handler.sendEmptyMessage(ConstUtil.UPDATE_PROGRESS);
    }

    //刷新MainActivity
    private void refreshMain(Context context){
        //获取专辑图片
        files = AudioUtils.getAllSongs(context);
        String albumArt =
                AudioUtils.getAlbumArt(files.get(MyMusicPlayerService.currIndex).getAlbumpic(), context);
        Bitmap bm = BitmapFactory.decodeFile(albumArt);
        BitmapDrawable bmpDraw = new BitmapDrawable(bm);
        if(albumArt == null){
            MainActivity.imageView.setImageResource(R.drawable.bg);
        }else {
            getBitmapDrawable(bmpDraw);
        }
        //获取歌曲名
        String songName = files.get(MyMusicPlayerService.currIndex).getFileName();
        MainActivity.songName.setText(songName);
        //发消息更新seekBar
        handler.sendEmptyMessage(ConstUtil.SET_SEEKBAR_MAX);
        handler.sendEmptyMessage(ConstUtil.UPDATE_PROGRESS);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null != mediaPlayer){
            mediaPlayer.release();
            mediaPlayer = null;
            unregisterReceiver(musicServiceReceiver);
        }

    }
}

