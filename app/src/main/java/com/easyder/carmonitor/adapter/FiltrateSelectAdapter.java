package com.easyder.carmonitor.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.easyder.carmonitor.R;

import java.util.List;

/**
 * Created by ljn on 2017-09-05.
 */

public class FiltrateSelectAdapter extends BaseAdapter {

    private String[] mData;

    private List<String> selectList;
    private Context context;

    public FiltrateSelectAdapter(Context context, String[] mOrderStatusList, List<String> mSelectViolationList) {
        this.context = context;
        mData = mOrderStatusList;
        selectList = mSelectViolationList;
    }

    public void updata(List<String> mSelectViolationList){
        selectList = mSelectViolationList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(mData != null && mData.length>0) {
            return mData.length;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(mData != null && mData.length>position){
            return mData[position];
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
            convertView = View.inflate(context, R.layout.violation_selectlist_item, null);
            mViewHolder = new ViewHolder();
            mViewHolder.item_violation_name = (TextView) convertView.findViewById(R.id.item_violation_name);
            mViewHolder.check_item = (CheckBox) convertView.findViewById(R.id.check_item);
            convertView.setTag(mViewHolder);
        }else{
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        String violationName = mData[position];
        mViewHolder.item_violation_name.setText(violationName);

        if(selectList!= null && selectList.contains(violationName)){
            mViewHolder.check_item.setChecked(true);
        }else{
            mViewHolder.check_item.setChecked(false);
        }

        return convertView;
    }

    class ViewHolder{
        TextView item_violation_name;
        CheckBox check_item;
    }
}
