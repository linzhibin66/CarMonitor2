package com.easyder.carmonitor.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2016/11/14.
 */
public class OfflinePagerAdapter extends PagerAdapter {

    private List<View> viewsList = new ArrayList<View>();

    public void setData(List<View> data){

        if(data!=null && data.size()>0){
            viewsList.clear();
            viewsList.addAll(data);
        }

        notifyDataSetChanged();

    }
    @Override
    public int getCount() {
        return viewsList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        if((viewsList.size()-1)<position){
            return;
        }

        container.removeView(viewsList.get(position));
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = viewsList.get(position);


        container.addView(view);
        return view;
    }
}
