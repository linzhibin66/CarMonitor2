package com.easyder.carmonitor.interfaces;

import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;

/**
 * Created by ljn on 2017-04-17.
 */
public interface CompactMarkerListener {

    void onLayoutClick(CarInfoBean mCarInfoBean, String mAdress);

    void onAlarmCommit(CarInfoBean mCarInfoBean, String startTime, String endTime, String type);

    void onTrackCommit(CarInfoBean mCarInfoBean, String startTime, String endTime);

    void onOperationClick(String message, String successHint, String errorHint, String plateNumber, byte instruct);
}
