package com.example.trf.myapplication.Util;

/**
 * Created by user on 16-5-18.
 */
public class ConstUtil {
        //SeekBar最大值
        public static final String SEEKBAR_MAX_ACTION="com.trf.music.SeekBar_Max_ACTION";
        //MusicService接收器所能响应的Action
        public static final String MUSICSERVICE_ACTION="com.trf.music.MusicsService_ACTION";
        //SeekBar实时更新进度
        public static final String SEEKBAR_ACTION="com.trf.music.SeekBar_ACTION";
        //MusicService接收器所能响应的Action
        public static final String LISTACTIVITY_ACTION="com.trf.music.ListActivity_ACTION";
        //耳机长按广播
        public static final String LONGPRESS_ACTION = "com.trf.mediabutton.longpress";
        //耳机单机广播
        public static final String SHORTPRESS_ACTION = "com.trf.mediabutton.shortpress";

        //service包名
        public final static String isMyServiceRunning = "com.example.trf.myapplication.service.MyMusicPlayerService";

        //mainActivity包名
        public final static String isMainActivityRunning = "com.example.trf.myapplication.service.MainActivity";

        //初始化flag
        public static final int STATE_NON=1;
        //播放的flag
        public static final int STATE_PLAY=2;
        //暂停的flag
        public static final int STATE_PAUSE=3;
        //停止放的flag
        public static final int STATE_STOP=4;
        //播放上一首的flag
        public static final int STATE_PREVIOUS=5;
        //播放下一首的flag
        public static final int STATE_NEXT=6;
        //刷新ListUi
        public static final int REFRESHLIST=7;
        //刷新MainUi
        public static final int REFRESHMAIN=8;
        //mainActivity下一曲（listActivity和mainActivity实现功能不一样）
        public static final int STATE_MAINNEXT=9;
        // 刷新SeekBar进度
        public static final int UPDATE_PROGRESS = 10;
        // 设置Seebar最大进度
        public static final int SET_SEEKBAR_MAX = 11;


        public ConstUtil() {
            // TODO Auto-generated constructor stub
        }

}
