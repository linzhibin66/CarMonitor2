package com.easyder.carmonitor.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.shinetech.mvp.network.UDP.InfoTool.CarStatusInfoTool;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2017-03-28.
 */
public class SearchAdapter extends BaseSearchAdapter {

    private List<CarInfoBean> data = new ArrayList<>();

    public SearchAdapter(List<CarInfoBean> data, Context context) {
        super(context);
        setData(data);
    }

    public void setData(List<CarInfoBean> dataList){
        data.clear();
        data.addAll(dataList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        if(data.size()>position){
            return data.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        if(data.size()>position){
            return position;
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public CarInfoBean getCarinfo(int position) {
        if(data.size()>position) {
            return data.get(position);
        }
        return null;
    }

    @Override
    public String getPlateNumber(int position) {
        if(data.size()>position) {
            CarInfoBean carInfoBean = data.get(position);
            return carInfoBean.getPlateNumber();
        }
        return null;
    }
}
