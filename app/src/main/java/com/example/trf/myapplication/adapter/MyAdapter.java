package com.example.trf.myapplication.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trf.myapplication.MainActivity;
import com.example.trf.myapplication.R;
import com.example.trf.myapplication.Util.AudioUtils;
import com.example.trf.myapplication.entity.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 16-5-23.
 */
public class MyAdapter extends BaseAdapter{

    private LayoutInflater layoutInflater;
    private ArrayList<Song> list;
    private Context context;

    public MyAdapter(Context context,ArrayList<Song>list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        Song s = list.get(position);
        if(convertView == null){
            convertView = View.inflate(context,R.layout.item,null);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.tvSinger = (TextView) convertView.findViewById(R.id.tvSinger);
            viewHolder.tvAlbum = (TextView) convertView.findViewById(R.id.tvAlbum);
            viewHolder.tvDuration = (TextView) convertView.findViewById(R.id.tvDuration);
            viewHolder.ivPic = (ImageView) convertView.findViewById(R.id.ivPic);

            convertView.setTag(viewHolder);
        }

        int count =s.getDuration();
        int max = count/1000;
        int minutes = max/60;
        int seconds = max%60;
        String allTime = minutes+":"+seconds;
        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.tvName.setText(s.getFileName().trim());
        viewHolder.tvSinger.setText(s.getSinger().trim());
        viewHolder.tvAlbum.setText(s.getAlbum().trim());
        viewHolder.tvDuration.setText(allTime);

        String albumArt = AudioUtils.getAlbumArt(s.getAlbumpic(), context);
        Bitmap bm = BitmapFactory.decodeFile(albumArt);
        BitmapDrawable drawable = new BitmapDrawable();

        if(albumArt == null){
            viewHolder.ivPic.setImageResource(R.drawable.app_music);
        }else {
            BitmapDrawable bmpDraw = new BitmapDrawable(bm);
            viewHolder.ivPic.setImageDrawable(bmpDraw);
        }

        return convertView;
    }

    private class ViewHolder{
        protected TextView tvName;
        protected TextView tvSinger;
        protected TextView tvAlbum;
        protected TextView tvDuration;
        protected ImageView ivPic;
    }

}
