package com.shinetech.mvp.network.UDP.bean.item;

import com.shinetech.mvp.network.UDP.bean.BaseVo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ljn on 2017/2/14.
 */
public class CarInfoBean implements Serializable{

    /**
     * 返回的车牌号
     */
    private String plateNumber;

    /**
     * 定位时间
     */
    private String locationTime;

    /**
     * 经度
     */
    private int lng;

    /**
     * 纬度
     */
    private int lat;

    /**
     * 原车速度
     */
    private byte speed;

    /**
     * GNSS 速度
     */
    private byte gNSSSpeed;

    /**
     * 方向
     */
    private short orientation;

    /**
     * 高度
     */
    private short altitude;

    /**
     * 里程
     */
    private int mileage;

    /**
     * 油量
     */
    private short oilMass;

    /**
     * 状态
     */
    private int status;

    /**
     * 报警标志
     */
    private int alarmType;

    /**
     * 违规数量
     */
    private byte violationCount = 0;

    /**
     * 违规列表
     */
    private List<String> violationList;


    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getLocationTime() {
        return locationTime;
    }

    public void setLocationTime(String locationTime) {
        this.locationTime = locationTime;
    }

    public int getLng() {
        return lng;
    }

    public void setLng(int lng) {
        this.lng = lng;
    }

    public int getLat() {
        return lat;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }

    public byte getSpeed() {
        return speed;
    }

    public void setSpeed(byte speed) {
        this.speed = speed;
    }

    public byte getgNSSSpeed() {
        return gNSSSpeed;
    }

    public void setgNSSSpeed(byte gNSSSpeed) {
        this.gNSSSpeed = gNSSSpeed;
    }

    public short getOrientation() {
        return orientation;
    }

    public void setOrientation(short orientation) {
        this.orientation = orientation;
    }

    public int getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(int alarmType) {
        this.alarmType = alarmType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public short getOilMass() {
        return oilMass;
    }

    public void setOilMass(short oilMass) {
        this.oilMass = oilMass;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public short getAltitude() {
        return altitude;
    }

    public void setAltitude(short altitude) {
        this.altitude = altitude;
    }

    public byte getViolationCount() {
        return violationCount;
    }

    public void setViolationCount(byte violationCount) {
        this.violationCount = violationCount;
    }

    public List<String> getViolationList() {
        return violationList;
    }

    public void setViolationList(List<String> violationList) {
        this.violationList = violationList;
    }

    public static short[] getDataTypes() {
        return new short[]{BaseVo.STRING, BaseVo.STRING, BaseVo.INT, BaseVo.INT, BaseVo.BYTE, BaseVo.BYTE, BaseVo.SHORT, BaseVo.SHORT, BaseVo.INT, BaseVo.SHORT, BaseVo.INT, BaseVo.INT};
    }

    public void setProperties(Object[] properties) {
        this.plateNumber = BaseVo.parseString((byte[]) properties[0]);
        this.locationTime = BaseVo.parseString((byte[]) properties[1]);
        this.lng = (int) properties[2];
        this.lat = (int) properties[3];
        this.speed = (byte) properties[4];
        this.gNSSSpeed = (byte) properties[5];
        this.orientation = (short) properties[6];
        this.altitude = (short) properties[7];
        this.mileage = (int) properties[8];
        this.oilMass = (short) properties[9];
        this.status = (int) properties[10];
        this.alarmType = (int) properties[11];

    }

    @Override
    public String toString() {
        return "CarInfoBean{" +
                " 车牌号 plateNumber='" + plateNumber + '\'' +
                ", 定位时间  locationTime='" + locationTime + '\'' +
                ", 经度 lng=" + lng +
                ", 纬度 lat=" + lat +
                ", 原车速度 speed=" + speed +
                ", GNSS 速度 gNSSSpeed=" + gNSSSpeed +
                ", 方向 orientation=" + orientation +
                ", 高度 altitude=" + altitude +
                ", 里程 mileage=" +mileage +
                ", 油量 oilMass=" +oilMass +
                ", 状态 status=" + status +
                ", 报警标志 alarmType=" + alarmType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CarInfoBean that = (CarInfoBean) o;

        if (getLng() != that.getLng()) return false;
        if (getLat() != that.getLat()) return false;
        if (getSpeed() != that.getSpeed()) return false;
        if (getgNSSSpeed() != that.getgNSSSpeed()) return false;
        if (getOrientation() != that.getOrientation()) return false;
        if (getAltitude() != that.getAltitude()) return false;
        if (getMileage() != that.getMileage()) return false;
        if (getOilMass() != that.getOilMass()) return false;
        if (getStatus() != that.getStatus()) return false;
        if (getAlarmType() != that.getAlarmType()) return false;
        if (!getPlateNumber().equals(that.getPlateNumber())) return false;
        return getLocationTime().equals(that.getLocationTime());

    }
}
