package com.easyder.carmonitor.Scheme.OrderInfoScheme;

import android.support.v4.view.PagerAdapter;
import android.view.View;

import com.easyder.carmonitor.interfaces.LoadOrderDataListener;
import com.easyder.carmonitor.interfaces.OrderInfoShowContentInterfaces;
import com.easyder.carmonitor.interfaces.OrderManagerFiltrateListener;
import com.shinetech.mvp.DB.bean.BaseOrderInfoDB;

/**
 * Created by ljn on 2017-11-10.
 */

public abstract class BaseOrderInfoScheme {

    protected LoadOrderDataListener mLoadOrderDataListener;

    public BaseOrderInfoScheme(LoadOrderDataListener listener) {
        this.mLoadOrderDataListener = listener;
    }

//    protected abstract PagerAdapter getPagerAdapter();

    public abstract void loadData();

    public abstract void setOrderInterfaces(OrderInfoShowContentInterfaces orderInterfaces);

    public LoadOrderDataListener getListener(){
        return mLoadOrderDataListener;
    }

    public abstract View CreatFiltrateView(OrderManagerFiltrateListener listener);

    public abstract void update(int position);

    public abstract void initInfo();

    public abstract void deleteOrder(BaseOrderInfoDB baseOrderInfoDB);
}
