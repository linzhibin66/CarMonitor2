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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2017-11-17.
 */

public class AddPicturesImgGridAdapter extends BaseAdapter {

    private List<String> data = new ArrayList<>();

    private Context context;

    public AddPicturesImgGridAdapter(Context context) {
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

        String path = data.get(position);
        if(path.equals("photo")){
            mImageViewHodler.img.setImageResource(R.mipmap.add_pictures);
            mImageViewHodler.img.setTag(null);
        }else {
//            mImageViewHodler.img.setImageBitmap(BitmapFactory.decodeFile(data.get(position)));
//            String uriPath = (Uri.fromFile(new File(data.get(position)))).toString();

            String filepath = data.get(position);
            File file = new File(filepath);
            if(!file.exists()){

                mImageViewHodler.img.setImageResource(R.mipmap.file_noexist);

            }else {

                String uriPath = "file://" + data.get(position);
                mImageViewHodler.img.setTag(uriPath);

//            ImageLoader.getInstance().displayImage(uriPath, mImageViewHodler.img , options);
                ImageLoader.getInstance().displayImage(uriPath, mImageViewHodler.img, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {}

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        Object tag = view.getTag();

                        if (tag != null && tag.toString().equals(imageUri)) {
                            ((ImageView) view).setImageBitmap(loadedImage);
                        } else {
                            ((ImageView) view).setImageResource(R.mipmap.add_pictures);
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                    }
                });
            }
//            mImageViewHodler.img.setTag(data.get(position));
//            ImageLoader.getInstance().displayImage((Uri.fromFile(new File(data.get(position)))).toString(), mImageViewHodler.img);
    }

        return convertView;
    }

    class ImageViewHodler{
        public ImageView img;
    }
}
