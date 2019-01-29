package com.easyder.carmonitor.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyder.carmonitor.R;

/**
 * Created by Lenovo on 2017-05-11.
 */
public class FaqGridViewAdapter extends BaseAdapter{

    private Context context;

    private String[] allFaqList;

    private int[] iconList;

    public FaqGridViewAdapter(Context context) {
        this.context = context;
        allFaqList = context.getResources().getStringArray(R.array.all_faq_list);
        iconList = new int[]{R.mipmap.login_faq, R.mipmap.device_faq, R.mipmap.location_faq, R.mipmap.alarm_faq, R.mipmap.track_faq, R.mipmap.order_faq};

    }



    @Override
    public int getCount() {
        if(allFaqList != null){
            return allFaqList.length;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(allFaqList != null && allFaqList.length>position){
            return allFaqList[position];
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder mViewHolder;

        if(convertView == null){
            convertView = View.inflate(context,R.layout.faq_gridview_item,null);
            mViewHolder = new ViewHolder();
            mViewHolder.faq_gridview_item_tv = (TextView) convertView.findViewById(R.id.faq_gridview_item_tv);
            mViewHolder.faq_gridview_icon = (ImageView) convertView.findViewById(R.id.faq_gridview_icon);
            convertView.setTag(mViewHolder);
        }else{
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.faq_gridview_item_tv.setText(allFaqList[position]);
        mViewHolder.faq_gridview_icon.setImageResource(iconList[position]);

        return convertView;
    }

    class ViewHolder{
        TextView faq_gridview_item_tv;
        ImageView faq_gridview_icon;
    }
}
