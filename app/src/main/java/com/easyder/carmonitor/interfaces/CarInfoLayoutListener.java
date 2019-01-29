package com.easyder.carmonitor.interfaces;

import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;

/**
 * Created by ljn on 2017-04-10.
 */
public interface CarInfoLayoutListener extends LayoutBackListener{

    void OnClickMoreStatus(CarInfoBean carInfoBean);

    void OnClickAlarm(CarInfoBean carInfoBean);

    void OnClickCarcost(CarInfoBean carInfoBean);
}
