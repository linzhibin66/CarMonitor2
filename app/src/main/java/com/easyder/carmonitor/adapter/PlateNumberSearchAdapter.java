package com.easyder.carmonitor.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-07-27.
 */
public class PlateNumberSearchAdapter extends BaseSearchAdapter{

    private List<String> plateNumberList = new ArrayList<>();

    public PlateNumberSearchAdapter(List<String> plateNumberList, Context context) {
        super(context);
        setData(plateNumberList);
    }

    public void setData(List<String> dataList){
        plateNumberList.clear();
        plateNumberList.addAll(dataList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return plateNumberList.size();
    }

    @Override
    public Object getItem(int position) {
        if(plateNumberList.size()>position){
            return plateNumberList.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        if(plateNumberList.size()>position){
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
        String plateNumber = getPlateNumber(position);
        CarInfoBean newestCarInfo = UserInfo.getInstance().getNewestCarInfo(plateNumber);
        return newestCarInfo;
    }

    @Override
    public String getPlateNumber(int position) {
        if(plateNumberList.size()>position) {
            String mPlateNumber = plateNumberList.get(position);
            return mPlateNumber;
        }
        return null;
    }
}
