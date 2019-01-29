package com.shinetech.mvp.network.UDP.resopnsebean;

import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.BaseVo;

import java.io.Serializable;

/**
 * Created by Lenovo on 2017-07-26.
 */
public class ViolationLogItem extends BaseVo implements Serializable {

    /**
     * 操作码
     */
    private int operationCode;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 序号
     */
    private int ordinal;

    /**
     * 规则名
     */
    private String violationName;

    /**
     * 违规时间
     */
    private String violationTime;

    /**
     * 经度
     */
    private int lng;

    /**
     * 纬度
     */
    private int lat;

    /**
     * GNSS 速度
     */
    private byte gNSSSpeed;

    /**
     * 方向
     */
    private short orientation;

    /**
     * 状态
     */
    private int status;

    /**
     * 报警标志
     */
    private int alarmType;

    /**
     * 处理时间
     */
    private String disposTime;

    /**
     * 处理记录
     */
    private String disposeLog;

    /**
     * 处理员
     */
    private String disposePerson;

    private String adress;

    public ViolationLogItem() {
        responseProtocolHead = Protocol.RESPONSE_PROTOCOL_VIOLATIONLOG_BEAT_RESULT;
    }

    @Override
    public Object[] getProperties() {
        return new Object[0];
    }

    @Override
    public void setProperties(Object[] properties) {
        operationCode = (int) properties[0];
        plateNumber = parseString((byte[]) properties[1]);
        ordinal = (int) properties[2];
        violationName = parseString((byte[]) properties[3]);
        violationTime = parseString((byte[]) properties[4]);
        lng = (int) properties[5];
        lat = (int) properties[6];
        gNSSSpeed = (byte) properties[7];
        orientation = (short) properties[8];
        status = (int) properties[9];
        alarmType = (int) properties[10];
        disposTime = parseString((byte[]) properties[11]);
        disposeLog = parseString((byte[]) properties[12]);
        disposePerson = parseString((byte[]) properties[13]);

    }

    @Override
    public short[] getDataTypes() {
        return new short[]{INT, STRING, INT, STRING, STRING, INT, INT, BYTE, SHORT, INT, INT, STRING, STRING, STRING};
    }

    public int getOperationCode() {
        return operationCode;
    }

    public void setOperationCode(int operationCode) {
        this.operationCode = operationCode;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public String getViolationName() {
        return violationName;
    }

    public void setViolationName(String violationName) {
        this.violationName = violationName;
    }

    public String getViolationTime() {
        return violationTime;
    }

    public void setViolationTime(String violationTime) {
        this.violationTime = violationTime;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(int alarmType) {
        this.alarmType = alarmType;
    }

    public String getDisposTime() {
        return disposTime;
    }

    public void setDisposTime(String disposTime) {
        this.disposTime = disposTime;
    }

    public String getDisposeLog() {
        return disposeLog;
    }

    public void setDisposeLog(String disposeLog) {
        this.disposeLog = disposeLog;
    }

    public String getDisposePerson() {
        return disposePerson;
    }

    public void setDisposePerson(String disposePerson) {
        this.disposePerson = disposePerson;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ViolationLogItem that = (ViolationLogItem) o;

        if (getOperationCode() != that.getOperationCode()) return false;
        if (getOrdinal() != that.getOrdinal()) return false;
        if (getLng() != that.getLng()) return false;
        if (getLat() != that.getLat()) return false;
        if (getgNSSSpeed() != that.getgNSSSpeed()) return false;
        if (getOrientation() != that.getOrientation()) return false;
        if (getStatus() != that.getStatus()) return false;
        if (getAlarmType() != that.getAlarmType()) return false;
        if (getPlateNumber() != null ? !getPlateNumber().equals(that.getPlateNumber()) : that.getPlateNumber() != null)
            return false;
        if (getViolationName() != null ? !getViolationName().equals(that.getViolationName()) : that.getViolationName() != null)
            return false;
        if (getViolationTime() != null ? !getViolationTime().equals(that.getViolationTime()) : that.getViolationTime() != null)
            return false;
        if (getDisposTime() != null ? !getDisposTime().equals(that.getDisposTime()) : that.getDisposTime() != null)
            return false;
        if (getDisposeLog() != null ? !getDisposeLog().equals(that.getDisposeLog()) : that.getDisposeLog() != null)
            return false;
        return !(getDisposePerson() != null ? !getDisposePerson().equals(that.getDisposePerson()) : that.getDisposePerson() != null);

    }

    @Override
    public String toString() {
        return "ViolationLogItem{" +
                "operationCode=" + operationCode +
                ", plateNumber='" + plateNumber + '\'' +
                ", ordinal=" + ordinal +
                ", violationName='" + violationName + '\'' +
                ", violationTime='" + violationTime + '\'' +
                ", lng=" + lng +
                ", lat=" + lat +
                ", gNSSSpeed=" + gNSSSpeed +
                ", orientation=" + orientation +
                ", status=" + status +
                ", alarmType=" + alarmType +
                ", disposTime='" + disposTime + '\'' +
                ", disposeLog='" + disposeLog + '\'' +
                ", disposePerson='" + disposePerson + '\'' +
                '}';
    }
}
