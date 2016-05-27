package com.example.trf.myapplication;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.trf.myapplication.Util.AudioUtils;
import com.example.trf.myapplication.Util.ConstUtil;
import com.example.trf.myapplication.adapter.MyAdapter;
import com.example.trf.myapplication.entity.Song;
import com.example.trf.myapplication.service.MyMusicPlayerService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 16-5-16.
 */
public class ListActivity extends Activity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private ImageView iv_back;
    public static ImageView iv_header;
    private ImageButton bt_menu,bt_playNext;
    public static ImageButton bt_play;
    public static TextView tv_musicname,tv_position,tv_duration;
    private ProgressBar seekBar2;
    private ArrayList<Song> files;
    Context context;
    MyAdapter myAdapter;
    Intent intent;
    public static boolean isPlaying = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initView();
        context = this;
        files = AudioUtils.getAllSongs(context);
        myAdapter = new MyAdapter(this,files);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(this);
        //注册广播
        setRegisterReceiver();
        if(isServiceRunning()){
            iv_back.setImageResource(android.R.drawable.ic_menu_send);
            bt_play.setImageResource(R.drawable.icon_pause_normal);
        }
    }



    @Override
    protected void onRestart() {
        super.onRestart();
        if(isServiceRunning()){
            iv_back.setImageResource(android.R.drawable.ic_menu_send);
        }
    }


    private void initView() {
        listView = (ListView) findViewById(R.id.list);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_header = (ImageView) findViewById(R.id.iv_header);
        bt_play = (ImageButton) findViewById(R.id.bt_play);
        bt_playNext = (ImageButton) findViewById(R.id.bt_playNext);
        tv_musicname = (TextView) findViewById(R.id.tv_musicname);
        tv_position = (TextView) findViewById(R.id.tv_position);
        tv_duration = (TextView) findViewById(R.id.tv_duration);
        seekBar2 = (ProgressBar) findViewById(R.id.seekBar2);
    }


    private void setRegisterReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstUtil.SEEKBAR_MAX_ACTION);
        intentFilter.addAction(ConstUtil.SEEKBAR_ACTION);
        registerReceiver(listReceiver, intentFilter);

    }

    //获取专辑图片、歌手和歌名
    private void getSong(){
        //专辑图片
        String albumArt =
                AudioUtils.getAlbumArt(files.get(MyMusicPlayerService.currIndex).getAlbumpic(),context);
        Bitmap bitmap = BitmapFactory.decodeFile(albumArt);
        BitmapDrawable drawable = new BitmapDrawable(bitmap);
        iv_header.setImageDrawable(drawable);
        //歌手、歌名
        Song s = files.get(MyMusicPlayerService.currIndex);
        tv_musicname.setText(s.getFileName().trim());
    }




    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_back:
                intent = new Intent(this, MainActivity.class);
                intent.putExtra("change", "change");
                startActivity(intent);
                break;
            case R.id.bt_play:
                if(isServiceRunning()) {
                    if (!isPlaying) {
                        sendBroadcastToService(ConstUtil.STATE_PLAY);
                        isPlaying = true;
                    } else {
                        sendBroadcastToService(ConstUtil.STATE_PAUSE);
                        isPlaying = false;
                    }
                }
                break;
            case R.id.bt_playNext:
                if(isServiceRunning()) {
                    sendBroadcastToService(ConstUtil.STATE_NEXT);
                    isPlaying = true;
                }
                break;
            case R.id.iv_header:
                if(isServiceRunning()) {
                    intent = new Intent(this, MainActivity.class);
                    intent.putExtra("change", "change");
                    startActivity(intent);
                }
                break;
        }
    }

    //判断service是否运行
    public boolean isServiceRunning() {
        boolean isRunning = false;
        ActivityManager myAM = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(ConstUtil.isMyServiceRunning)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
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


    //listView点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, MyMusicPlayerService.class);
        intent.putExtra("position",position);
        startService(intent);
        isPlaying = true;
        getSong();
    }

    private BroadcastReceiver listReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String intentAction = intent.getAction();
            if ((ConstUtil.SEEKBAR_MAX_ACTION).equals(intentAction)) {
                int count = intent.getIntExtra("count", 0);
                seekBar2.setMax(count);
                int max = count / 1000;
                int m = max / 60;
                int s = max % 60;
                tv_duration.setText(m + ":" + s);
            } else if ((ConstUtil.SEEKBAR_ACTION).equals(intentAction)) {
                int currtTime = intent.getIntExtra("currtTime", 0);
                int now = currtTime / 1000;
                int m = now / 60;
                int s = now % 60;
                if (s < 10) {
                    tv_position.setText(m + ":0" + s);
                } else {
                    tv_position.setText(m + ":" + s);

                }
                seekBar2.setProgress(currtTime);
            }
        }
    };

    //重写返回键，listActivity不被销毁
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(listReceiver);
    }
}
