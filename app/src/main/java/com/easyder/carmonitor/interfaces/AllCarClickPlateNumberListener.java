package com.easyder.carmonitor.interfaces;

import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;

/**
 * Created by ljn on 2017-06-15.
 */
public interface AllCarClickPlateNumberListener {

    void onClickPlateNumber(CarInfoBean mCarInfoBean);
}
