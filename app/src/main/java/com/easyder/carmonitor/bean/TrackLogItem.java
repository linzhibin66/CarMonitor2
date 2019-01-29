package com.easyder.carmonitor.bean;

import com.shinetech.mvp.network.UDP.listener.BaseCarHistoricalRouteItem;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarHistoricalRouteItem;

import java.io.Serializable;

/**
 * Created by ljn on 2017-05-05.
 */
public class TrackLogItem extends ResponseCarHistoricalRouteItem {

    /**
     * 是否为停留状态
     */
    private boolean isStop = false;

    private String adress;

    private boolean isSplitLine = false;

    /**
     * 车辆停留的时间（停止状态下统计的数据）
     */
    private String stopTime;

    public TrackLogItem(String locationTime, int lng, int lat, byte mGNSSSpeed, short orientation) {
        super(locationTime, lng, lat, mGNSSSpeed, orientation);
    }

    public TrackLogItem(BaseCarHistoricalRouteItem mCarHistoricalRouteItem){
        super(mCarHistoricalRouteItem.getRelativelyMileage(), mCarHistoricalRouteItem.getStopTimeInt(), mCarHistoricalRouteItem.getAlarmType() ,mCarHistoricalRouteItem.getStatus(),
                mCarHistoricalRouteItem.getOrientation(), mCarHistoricalRouteItem.getmGNSSSpeed(), mCarHistoricalRouteItem.getLng(), mCarHistoricalRouteItem.getLat(),
                mCarHistoricalRouteItem.getLocationTime(), mCarHistoricalRouteItem.getOrdinal(), mCarHistoricalRouteItem.getPlateNumber(), mCarHistoricalRouteItem.getOperationCode());
    }

    public TrackLogItem(boolean isSplitLine) {
        this.isSplitLine = isSplitLine;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }


    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public boolean isStop() {
        return isStop;
    }

    public void setIsStop(boolean isStop) {
        this.isStop = isStop;
    }

    public boolean isSplitLine() {
        return isSplitLine;
    }

    public void setIsSplitLine(boolean isSplitLine) {
        this.isSplitLine = isSplitLine;
    }

    public static TrackLogItem creatItem(BaseCarHistoricalRouteItem mCarHistoricalRouteItem){
        return new TrackLogItem(mCarHistoricalRouteItem);
    }
}
