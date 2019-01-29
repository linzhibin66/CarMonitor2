package com.shinetech.mvp.network.UDP.listener;

import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;

/**
 * Created by ljn on 2017/2/28.
 */
public interface CarListChangeListener {
    void changedAll();
    void changed(CarInfoBean mCarInfoBean);
}
