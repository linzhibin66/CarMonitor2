package com.easyder.carmonitor.interfaces;

import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;

/**
 * Created by ljn on 2017-03-29.
 */
public abstract class SearchDialogClickListeren {

    public void OnClickBefore(){};

    public abstract void OnClick(CarInfoBean mCarInfoBean);

    public void OnClickAfter(){};

}
