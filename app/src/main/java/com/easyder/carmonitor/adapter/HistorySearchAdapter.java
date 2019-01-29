package com.easyder.carmonitor.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyder.carmonitor.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017/3/21.
 */
public class HistorySearchAdapter extends BaseAdapter{

    private List<String> historyList = new ArrayList<>();

    private Context context;

    public HistorySearchAdapter(Context context, List<String> historyList) {
        this.context = context;
        this.historyList.addAll(historyList);
    }

    public void update(List<String> data){
        historyList.clear();
        historyList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(historyList!=null)
            return historyList.size();

        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(historyList!=null && historyList.size()>position)
            return historyList.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if(convertView!=null) {
            holder = (ViewHolder) convertView.getTag();
        }else{
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.history_listview_item, null);
            holder.item_car_platenumber = (TextView) convertView.findViewById(R.id.item_car_platenumber);
            convertView.setTag(holder);
        }

        holder.item_car_platenumber.setText(historyList.get(position));

        return convertView;
    }

    class ViewHolder{
        TextView item_car_platenumber;
        ImageView item_car_icon;

    }
}
