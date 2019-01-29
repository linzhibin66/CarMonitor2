package com.shinetech.mvp.network.UDP.bean.item;

/**
 * Created by ljn on 2017/2/14.
 */
public class TrafficStatusItem {

    /**
     * 路段ID
     */
    private short roadSectionID;

    /**
     * 起点经度
     */
    private int startLng;

    /**
     * 起点纬度
     */
    private int startLat;

    /**
     * 终点经度
     */
    private int endLng;

    /**
     * 终点纬度
     */
    private int endLat;

    /**
     * 平均速度
     */
    private byte averageSpeed;

    /**
     * 事件消息
     */
    private String eventMessage;

    public short getRoadSectionID() {
        return roadSectionID;
    }

    public void setRoadSectionID(short roadSectionID) {
        this.roadSectionID = roadSectionID;
    }

    public int getStartLng() {
        return startLng;
    }

    public void setStartLng(int startLng) {
        this.startLng = startLng;
    }

    public int getStartLat() {
        return startLat;
    }

    public void setStartLat(int startLat) {
        this.startLat = startLat;
    }

    public int getEndLng() {
        return endLng;
    }

    public void setEndLng(int endLng) {
        this.endLng = endLng;
    }

    public int getEndLat() {
        return endLat;
    }

    public void setEndLat(int endLat) {
        this.endLat = endLat;
    }

    public byte getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(byte averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public String getEventMessage() {
        return eventMessage;
    }

    public void setEventMessage(String eventMessage) {
        this.eventMessage = eventMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrafficStatusItem that = (TrafficStatusItem) o;

        if (getRoadSectionID() != that.getRoadSectionID()) return false;
        if (getStartLng() != that.getStartLng()) return false;
        if (getStartLat() != that.getStartLat()) return false;
        if (getEndLng() != that.getEndLng()) return false;
        if (getEndLat() != that.getEndLat()) return false;
        if (getAverageSpeed() != that.getAverageSpeed()) return false;
        return getEventMessage().equals(that.getEventMessage());

    }
}
