package com.easyder.carmonitor.interfaces;

import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;

/**
 * Created by ljn on 2017/2/23.
 */
public interface MarkerClickListener {

    void clickWarning(CarInfoBean mCarInfoBean);
    void clickTrack(CarInfoBean mCarInfoBean);
    void clickOperation(CarInfoBean mCarInfoBean);
    void onLayoutClick(CarInfoBean mCarInfoBean);
}
