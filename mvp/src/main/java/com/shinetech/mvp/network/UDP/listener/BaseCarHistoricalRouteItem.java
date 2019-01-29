package com.shinetech.mvp.network.UDP.listener;

/**
 * Created by ljn on 2017-08-01.
 */
public interface BaseCarHistoricalRouteItem {

    int getOperationCode();

    String getPlateNumber();

    int getOrdinal();

    String getLocationTime();

    int getLng();

    int getLat();

    byte getmGNSSSpeed();

    short getOrientation();

    int getStatus();

    int getAlarmType();

    int getStopTimeInt();

    int getRelativelyMileage();

}
