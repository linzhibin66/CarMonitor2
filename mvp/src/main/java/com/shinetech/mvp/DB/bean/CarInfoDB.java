package com.shinetech.mvp.DB.bean;

import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarStatusInfoBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-10-31.
 */

@Entity(indexes = {@Index(value = "userName,plateNumber DESC", unique = true)})
public class CarInfoDB {

    @NotNull
    private String userName;

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
    @Property(nameInDb = "GNSS_SPEED")
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
    @Property(nameInDb = "OILMASS")
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
    private String violationList;

    public CarInfoDB() {
    }

    @Generated(hash = 47920277)
    public CarInfoDB(@NotNull String userName, String plateNumber,
            String locationTime, int lng, int lat, byte speed, byte gNSSSpeed,
            short orientation, short altitude, int mileage, short oilMass,
            int status, int alarmType, byte violationCount, String violationList) {
        this.userName = userName;
        this.plateNumber = plateNumber;
        this.locationTime = locationTime;
        this.lng = lng;
        this.lat = lat;
        this.speed = speed;
        this.gNSSSpeed = gNSSSpeed;
        this.orientation = orientation;
        this.altitude = altitude;
        this.mileage = mileage;
        this.oilMass = oilMass;
        this.status = status;
        this.alarmType = alarmType;
        this.violationCount = violationCount;
        this.violationList = violationList;
    }

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

    public String getViolationList() {
        return violationList;
    }

    public void setViolationList(String violationList) {
        this.violationList = violationList;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public byte getGNSSSpeed() {
        return this.gNSSSpeed;
    }

    public void setGNSSSpeed(byte gNSSSpeed) {
        this.gNSSSpeed = gNSSSpeed;
    }

    public String getViolationSeparator(){
        return "~@~";
    }

    public CarInfoBean toCarInfoBean(){


        CarInfoBean mCarInfoBean = new CarInfoBean();

        mCarInfoBean.setPlateNumber(plateNumber);
        mCarInfoBean.setLocationTime(locationTime);
        mCarInfoBean.setLng(lng);
        mCarInfoBean.setLat(lat);
        mCarInfoBean.setSpeed(speed);
        mCarInfoBean.setgNSSSpeed(gNSSSpeed);
        mCarInfoBean.setOrientation(orientation);
        mCarInfoBean.setAltitude(altitude);
        mCarInfoBean.setMileage(mileage);
        mCarInfoBean.setOilMass(oilMass);
        mCarInfoBean.setStatus(status);
        mCarInfoBean.setAlarmType(alarmType);
        mCarInfoBean.setViolationCount(violationCount);

        String[] split = violationList.split(getViolationSeparator());
        List<String> mViolationList = new ArrayList<String>();
        if(split.length>0){

                for(String str : split){
                    mViolationList.add(str);
                }
        }

        mCarInfoBean.setViolationList(mViolationList);
        return mCarInfoBean;

    }


}
