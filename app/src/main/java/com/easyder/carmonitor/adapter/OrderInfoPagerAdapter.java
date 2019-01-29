package com.easyder.carmonitor.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.easyder.carmonitor.Scheme.OrderInfoScheme.BaseOrderInfoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2017-11-10.
 */

public class OrderInfoPagerAdapter extends PagerAdapter {

    private List<BaseOrderInfoItem> datas = new ArrayList<>();

    public void initData(List<BaseOrderInfoItem> items){
        datas.clear();
        datas.addAll(items);
        notifyDataSetChanged();

    }

    public BaseOrderInfoItem getPositionItem(int position){
        return datas.get(position);
    }

    // 获取要滑动的控件的数量，在这里我们以滑动的广告栏为例，那么这里就应该是展示的广告图片的ImageView数量
    @Override
    public int getCount() {
        return datas.size();
    }

    // 来判断显示的是否是同一张图片，这里我们将两个参数相比较返回即可
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    // PagerAdapter只缓存三张要显示的图片，如果滑动的图片超出了缓存的范围，就会调用这个方法，将图片销毁
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(datas.get(position).getView());
    }

    // 当要显示的图片可以进行缓存的时候，会调用这个方法进行显示图片的初始化，我们将要显示的ImageView加入到ViewGroup中，然后作为返回值返回即可
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = datas.get(position).getView();
        container.addView(view);
        return view;
    }
}
