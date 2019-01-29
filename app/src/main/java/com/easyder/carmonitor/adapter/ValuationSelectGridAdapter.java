package com.easyder.carmonitor.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyder.carmonitor.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-11-17.
 */

public class ValuationSelectGridAdapter extends BaseAdapter {

    private List<String> data = new ArrayList<>();

    private Context context;

    public ValuationSelectGridAdapter(Context context) {
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
            convertView = View.inflate(context, R.layout.valuation_select_textview,null);
            mImageViewHodler.textView = (TextView) convertView.findViewById(R.id.maintenance_order_number_value);
            convertView.setTag(mImageViewHodler);
        }else{
            mImageViewHodler = (ImageViewHodler) convertView.getTag();
        }

        mImageViewHodler.textView.setText(data.get(position));

        return convertView;
    }

    class ImageViewHodler{
        public TextView textView;
    }
}
