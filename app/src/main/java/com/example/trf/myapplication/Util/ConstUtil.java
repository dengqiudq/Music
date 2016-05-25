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
        //刷新UI
        public static final int REFRESHUI=7;


        public ConstUtil() {
            // TODO Auto-generated constructor stub
        }

}
