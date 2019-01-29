package com.easyder.carmonitor.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.widget.CarAlarmItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-04-11.
 */
public class CarAlarmAdapter extends BaseAdapter{

    private List<CarAlarmItem> data = new ArrayList<>();

    private Context context;

    public CarAlarmAdapter(Context context,List<CarAlarmItem> mData) {
        this.context = context;
        data.clear();
        data.addAll(mData);
    }

    public void UpData(List<CarAlarmItem> mData){
        data.clear();
        data.addAll(mData);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        if(data.size()>position) {
            return data.get(position);
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
            mViewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.car_alarm_item,null);
            mViewHolder.alarm_icon = (ImageView) convertView.findViewById(R.id.alarm_icon);
            mViewHolder.alarm_values = (TextView) convertView.findViewById(R.id.alarm_values);
            convertView.setTag(mViewHolder);
        }else{
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        CarAlarmItem carAlarmItem = data.get(position);

        int type = carAlarmItem.getType();
        if(type == CarAlarmItem.ALARM) {
            mViewHolder.alarm_icon.setImageResource(R.mipmap.icon_warn);
        }else{
            mViewHolder.alarm_icon.setImageResource(R.mipmap.icon_abnormal);
        }

        mViewHolder.alarm_values.setText(carAlarmItem.getAttribute());

        return convertView;
    }


    class ViewHolder{
        ImageView alarm_icon;
        TextView alarm_values;
    }
}
