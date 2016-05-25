package com.example.trf.myapplication;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import com.example.trf.myapplication.Util.AudioUtils;
import com.example.trf.myapplication.adapter.MyAdapter;
import com.example.trf.myapplication.entity.Song;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 16-5-16.
 */
public class ListActivity extends Activity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private ImageView imageView;
    private ArrayList<Song> files;
    Context context;
    MyAdapter myAdapter;
    private String isMyServiceRunning = "com.example.trf.myapplication.service.MyMusicPlayerService";


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
        if(isServiceRunning()){
            imageView.setImageResource(android.R.drawable.ic_menu_send);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(isServiceRunning()){
            imageView.setImageResource(android.R.drawable.ic_menu_send);
        }
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.list);
        imageView = (ImageView) findViewById(R.id.imageView);
    }


    public void onClick(View view){
        switch (view.getId()){
            case R.id.imageView:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("change", "change");
                startActivity(intent);
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
            if (mName.equals(isMyServiceRunning)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("position",position);
        startActivity(intent);
    }
}
