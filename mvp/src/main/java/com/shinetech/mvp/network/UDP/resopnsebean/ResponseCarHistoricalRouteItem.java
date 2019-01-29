package com.shinetech.mvp.network.UDP.resopnsebean;

import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.listener.BaseCarHistoricalRouteItem;

import java.io.Serializable;

/**
 * Created by ljn on 2017-07-25.
 */
public class ResponseCarHistoricalRouteItem extends BaseVo implements BaseCarHistoricalRouteItem, Serializable {


    /**
     * 操作码
     */
    private int operationCode;

    /**
     * 返回车牌号
     */
    private String plateNumber;

    /**
     * 序号
     */
    private int ordinal;

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

    /**
     * 状态
     */
    private int status;

    /**
     * 报警
     */
    private int alarmType;

    /**
     * 停留时长
     */
    private int stopTime;

    /**
     * 相对里程
     */
    private int relativelyMileage;


    public ResponseCarHistoricalRouteItem() {
        responseProtocolHead = Protocol.RESPONSE_PROTOCOL_CARHISTORICALROUTE_BEAT_RESULT;
    }

    public ResponseCarHistoricalRouteItem(String locationTime, int lng, int lat, byte mGNSSSpeed, short orientation) {
        responseProtocolHead = Protocol.RESPONSE_PROTOCOL_CARHISTORICALROUTE_BEAT_RESULT;
        this.locationTime = locationTime;
        this.lng = lng;
        this.lat = lat;
        this.mGNSSSpeed = mGNSSSpeed;
        this.orientation = orientation;
    }

    public ResponseCarHistoricalRouteItem(int relativelyMileage, int stopTime, int alarmType, int status, short orientation, byte mGNSSSpeed, int lng, int lat, String locationTime, int ordinal, String plateNumber, int operationCode) {
        this.relativelyMileage = relativelyMileage;
        this.stopTime = stopTime;
        this.alarmType = alarmType;
        this.status = status;
        this.orientation = orientation;
        this.mGNSSSpeed = mGNSSSpeed;
        this.lng = lng;
        this.lat = lat;
        this.locationTime = locationTime;
        this.ordinal = ordinal;
        this.plateNumber = plateNumber;
        this.operationCode = operationCode;
    }

    @Override
    public Object[] getProperties() {
        return new Object[0];
    }

    @Override
    public void setProperties(Object[] properties) {
        this.operationCode = (int) properties[0];
        this.plateNumber = parseString((byte[]) properties[1]);
        this.ordinal = (int) properties[2];
        this.locationTime = parseString((byte[]) properties[3]);
        this.lng = (int) properties[4];
        this.lat = (int) properties[5];
        this.mGNSSSpeed = (byte) properties[6];
        this.orientation = (short) properties[7];
        this.status = (int) properties[8];
        this.alarmType = (int) properties[9];
        this.stopTime = (int) properties[10];
        this.relativelyMileage = (int) properties[11];
    }

    @Override
    public short[] getDataTypes() {
        return new short[]{INT, STRING, INT, STRING, INT, INT, BYTE, SHORT, INT, INT, INT, INT,};
    }

    @Override
    public String toString() {
        return "ResponseCarHistoricalRouteItem{" +
                "operationCode=" + operationCode +
                ", plateNumber='" + plateNumber + '\'' +
                ", ordinal=" + ordinal +
                ", locationTime='" + locationTime + '\'' +
                ", lng=" + lng +
                ", lat=" + lat +
                ", mGNSSSpeed=" + mGNSSSpeed +
                ", orientation=" + orientation +
                ", status=" + status +
                ", alarmType=" + alarmType +
                ", stopTime=" + stopTime +
                ", relativelyMileage=" + relativelyMileage +
                '}';
    }

    @Override
    public int getOperationCode() {
        return operationCode;
    }

    public void setOperationCode(int operationCode) {
        this.operationCode = operationCode;
    }

    @Override
    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    @Override
    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
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

    public void setOrientation(short orientation) {
        this.orientation = orientation;
    }

    @Override
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(int alarmType) {
        this.alarmType = alarmType;
    }

    @Override
    public int getStopTimeInt() {
        return stopTime;
    }

    public void setStopTime(int stopTime) {
        this.stopTime = stopTime;
    }

    @Override
    public int getRelativelyMileage() {
        return relativelyMileage;
    }

    public void setRelativelyMileage(int relativelyMileage) {
        this.relativelyMileage = relativelyMileage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResponseCarHistoricalRouteItem that = (ResponseCarHistoricalRouteItem) o;

        if (operationCode != that.operationCode) return false;
        if (ordinal != that.ordinal) return false;
        if (lng != that.lng) return false;
        if (lat != that.lat) return false;
        if (mGNSSSpeed != that.mGNSSSpeed) return false;
        if (orientation != that.orientation) return false;
        if (status != that.status) return false;
        if (alarmType != that.alarmType) return false;
        if (stopTime != that.stopTime) return false;
        if (relativelyMileage != that.relativelyMileage) return false;
        if (plateNumber != null ? !plateNumber.equals(that.plateNumber) : that.plateNumber != null)
            return false;
        return !(locationTime != null ? !locationTime.equals(that.locationTime) : that.locationTime != null);

    }
}
