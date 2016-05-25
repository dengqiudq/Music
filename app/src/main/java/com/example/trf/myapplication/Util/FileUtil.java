package com.example.trf.myapplication.Util;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by user on 16-5-16.
 */
public class FileUtil {
    //获取音乐文件
    public static List<String> getFiles(File path){
        List<String> files = new ArrayList<>();
        if(path.exists()) {
            File musics[] = path.listFiles();
            if (musics != null) {
                for (int i = 0; i < musics.length; i++) {
                    if (musics[i].getName().indexOf(".") >= 0) {
                        String file = musics[i].getName().substring(musics[i].getName().indexOf("."));
                        if (file.toLowerCase().equals(".mp3")
                                || file.toLowerCase().equals(".amr")
                                || file.toLowerCase().equals("wma")) {
                            files.add(musics[i].getName());
                        }
                    }
                }
            }
        }
        return files;
    }
    //获取音乐歌词文件
    public static Set<String> getLrcs(File path){
        Set<String> files = new HashSet<>();
        File musics[] = path.listFiles();
        if(musics != null){
            for(int i=0;i<musics.length;i++){
                if(musics[i].getName().indexOf(".")>=0){
                    String file = musics[i].getName().substring(musics[i].getName().indexOf("."));
                    if(file.toLowerCase().equals(".lrc")){
                        files.add(musics[i].getName());
                    }
                }
            }
        }
        return files;
    }

}
