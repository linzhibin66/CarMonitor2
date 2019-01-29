package com.shinetech.mvp.network.UDP.bean;

import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;

/**
 * Created by ljn on 2017/2/9.
 * 查询车辆最新状态
 */
public class CarLatestStatus extends BaseVo{

    /**
     * 请求的车牌号
     */
    private String platenumber;

    /**************************************** result info start ******************************************************/

    /**
     * 返回的车牌号
     */
    private String resultPlateNumber;

    /**
     * 定位时间
     */
    private String locationTime;

    /**
     * 经度
     */
    private int resultlng;

    /**
     * 纬度
     */
    private int resultlat;

    /**
     * 原车速度
     */
    private byte resultSpeed;

    /**
     * GNSS 速度
     */
    private byte resultGNSSSpeed;

    /**
     * 方向
     */
    private short resultOrientation;

    /**
     * 高度
     */
    private short resultAltitude;

    /**
     * 里程
     */
    private int resultMileage;

    /**
     * 油量
     */
    private short resultOilMass;

    /**
     * 状态
     */
    private int resultStatus;

    /**
     * 报警标志
     */
    private int resultAlarmType;


    /**************************************** result info end ******************************************************/

    public CarLatestStatus(String resultPlateNumber) {
        this.platenumber = resultPlateNumber;
        requestProtocolHead = Protocol.PROTOCOL_CARSTATUS_BEAT;
        responseProtocolHead = Protocol.PROTOCOL_CARSTATUS_BEAT_RESULT;

    }


    @Override
    public Object[] getProperties() {
        return new Object[]{platenumber};
    }

    @Override
    public void setProperties(Object[] properties) {
        this.resultPlateNumber = parseString((byte[]) properties[0]);
        this.locationTime = parseString((byte[]) properties[1]);
        this.resultlng = (int) properties[2];
        this.resultlat = (int) properties[3];
        this.resultSpeed = (byte) properties[4];
        this.resultGNSSSpeed = (byte) properties[5];
        this.resultOrientation = (short) properties[6];
        this.resultAltitude = (short) properties[7];
        this.resultMileage = (int) properties[8];
        this.resultOilMass = (short) properties[9];
        this.resultStatus = (int) properties[10];
        this.resultAlarmType = (int) properties[11];

    }

    public CarInfoBean toCarInfoBean(){
        CarInfoBean carInfoBean = new CarInfoBean();
        carInfoBean.setPlateNumber(resultPlateNumber);
        carInfoBean.setLocationTime(locationTime);
        carInfoBean.setLng(resultlng);
        carInfoBean.setLat(resultlat);
        carInfoBean.setSpeed(resultSpeed);
        carInfoBean.setgNSSSpeed(resultGNSSSpeed);
        carInfoBean.setOrientation(resultOrientation);
        carInfoBean.setAltitude(resultAltitude);
        carInfoBean.setMileage(resultMileage);
        carInfoBean.setOilMass(resultOilMass);
        carInfoBean.setStatus(resultStatus);
        carInfoBean.setAlarmType(resultAlarmType);

        return carInfoBean;

    }

    @Override
    public short[] getDataTypes() {
        return new short[]{STRING, STRING, INT, INT, BYTE, BYTE, SHORT, SHORT, INT, SHORT, INT, INT};
    }

    public String getResultPlateNumber() {
        return resultPlateNumber;
    }

    public void setResultPlateNumber(String resultPlateNumber) {
        this.resultPlateNumber = resultPlateNumber;
    }

    public String getLocationTime() {
        return locationTime;
    }

    public void setLocationTime(String locationTime) {
        this.locationTime = locationTime;
    }

    public int getResultlng() {
        return resultlng;
    }

    public void setResultlng(int resultlng) {
        this.resultlng = resultlng;
    }

    public int getResultlat() {
        return resultlat;
    }

    public void setResultlat(int resultlat) {
        this.resultlat = resultlat;
    }

    public byte getResultSpeed() {
        return resultSpeed;
    }

    public void setResultSpeed(byte resultSpeed) {
        this.resultSpeed = resultSpeed;
    }

    public byte getResultGNSSSpeed() {
        return resultGNSSSpeed;
    }

    public void setResultGNSSSpeed(byte resultGNSSSpeed) {
        this.resultGNSSSpeed = resultGNSSSpeed;
    }

    public short getResultOrientation() {
        return resultOrientation;
    }

    public void setResultOrientation(short resultOrientation) {
        this.resultOrientation = resultOrientation;
    }

    public short getResultAltitude() {
        return resultAltitude;
    }

    public void setResultAltitude(short resultAltitude) {
        this.resultAltitude = resultAltitude;
    }

    public int getResultMileage() {
        return resultMileage;
    }

    public void setResultMileage(int resultMileage) {
        this.resultMileage = resultMileage;
    }

    public short getResultOilMass() {
        return resultOilMass;
    }

    public void setResultOilMass(short resultOilMass) {
        this.resultOilMass = resultOilMass;
    }

    public int getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(int resultStatus) {
        this.resultStatus = resultStatus;
    }

    public int getResultAlarmType() {
        return resultAlarmType;
    }

    public void setResultAlarmType(int resultAlarmType) {
        this.resultAlarmType = resultAlarmType;
    }

    @Override
    public String toString() {
        return "CarLatestStatus{" +
                " 车牌号 resultPlateNumber='" + resultPlateNumber + '\'' +
                ", 定位时间  locationTime='" + locationTime + '\'' +
                ", 经度 resultlng=" + resultlng +
                ", 纬度 resultlat=" + resultlat +
                ", 原车速度 resultSpeed=" + resultSpeed +
                ", GNSS 速度 resultGNSSSpeed=" + resultGNSSSpeed +
                ", 方向 resultOrientation=" + resultOrientation +
                ", 高度 resultAltitude=" + resultAltitude +
                ", 里程 resultMileage=" + resultMileage +
                ", 油量 resultOilMass=" + resultOilMass +
                ", 状态 resultStatus=" + resultStatus +
                ", 报警标志 resultAlarmType=" + resultAlarmType +
                '}';
    }
}
