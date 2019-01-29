package com.shinetech.mvp.network.UDP.bean.item;

import com.shinetech.mvp.network.UDP.listener.BaseCarHistoricalRouteItem;

import java.io.Serializable;

/**
 * Created by ljn on 2017/2/10.
 */
public class CarHistoricalRouteItem implements Serializable, BaseCarHistoricalRouteItem {

    /**
     * 定点时间
     */
    private String locationTime;

    /**
     * 经度
     */
    private int lng;

    /**
     * 纬度
     */
    private  int lat;

    /**
     * GNSS 速度
     */
    private byte mGNSSSpeed;

    /**
     * 方向  单位：°，以正北为0°，顺时针。
     */
    private short orientation;

    public CarHistoricalRouteItem() {
    }

    public CarHistoricalRouteItem(String locationTime, int lng, int lat, byte mGNSSSpeed, short orientation) {
        this.locationTime = locationTime;
        this.lng = lng;
        this.lat = lat;
        this.mGNSSSpeed = mGNSSSpeed;
        this.orientation = orientation;
    }

    @Override
    public int getOperationCode() {
        return 0;
    }

    @Override
    public String getPlateNumber() {
        return null;
    }

    @Override
    public int getOrdinal() {
        return 0;
    }

    @Override
    public String getLocationTime() {
        return locationTime;
    }

    public void setLocationTime(String locationTime) {
        this.locationTime = locationTime;
    }

    @Override
    public int getLng() {
        return lng;
    }

    public void setLng(int lng) {
        this.lng = lng;
    }

    @Override
    public int getLat() {
        return lat;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }

    @Override
    public byte getmGNSSSpeed() {
        return mGNSSSpeed;
    }

    public void setmGNSSSpeed(byte mGNSSSpeed) {
        this.mGNSSSpeed = mGNSSSpeed;
    }

    @Override
    public short getOrientation() {
        return orientation;
    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public int getAlarmType() {
        return 0;
    }

    @Override
    public int getStopTimeInt() {
        return 0;
    }

    @Override
    public int getRelativelyMileage() {
        return 0;
    }

    public void setOrientation(short orientation) {
        this.orientation = orientation;
    }

    @Override
    public String toString() {
        return "CarHistoricalRouteItem{" +
                " 定位时间 locationTime='" + locationTime + '\'' +
                ", 经度 lng=" + lng +
                ", 纬度 lat=" + lat +
                ", GNSS速度 mGNSSSpeed=" + mGNSSSpeed +
                ", 方向 orientation=" + orientation +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CarHistoricalRouteItem that = (CarHistoricalRouteItem) o;

        if (getLng() != that.getLng()) return false;
        if (getLat() != that.getLat()) return false;
        if (getmGNSSSpeed() != that.getmGNSSSpeed()) return false;
        if (getOrientation() != that.getOrientation()) return false;
        return getLocationTime().equals(that.getLocationTime());

    }
}
