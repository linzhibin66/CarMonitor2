package com.easyder.carmonitor.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.easyder.carmonitor.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2017-11-17.
 */

public class FaultDescriptionImgGridAdapter extends BaseAdapter {

    private List<String> data = new ArrayList<>();

    private Context context;

    public FaultDescriptionImgGridAdapter(Context context) {
        this.context = context;
    }

    public void initData(List<String> data){
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        if((data.size()-1)<position){
            return null;
        }
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        if((data.size()-1)<position){
            return 0;
        }
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageViewHodler mImageViewHodler;
        if(convertView == null){
            mImageViewHodler = new ImageViewHodler();
            convertView = View.inflate(context, R.layout.imageview,null);
            mImageViewHodler.img = (ImageView) convertView.findViewById(R.id.img_view);
            convertView.setTag(mImageViewHodler);
        }else{
            mImageViewHodler = (ImageViewHodler) convertView.getTag();
        }

        ImageLoader.getInstance().displayImage((Uri.fromFile(new File(data.get(position)))).toString(), mImageViewHodler.img);

        return convertView;
    }

    class ImageViewHodler{
        public ImageView img;
    }
}
