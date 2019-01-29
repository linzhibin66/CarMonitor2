package com.shinetech.mvp.DB.bean;

import android.text.TextUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by ljn on 2017-11-30.
 */

@Entity(indexes = {@Index(value = "id DESC", unique = true)})
public class InstallTerminalnfo {

    @Id
    @Unique
    private Long id;

    /**
     * 工单号
     */
    @NotNull
    private String orderNumber;

    /**
     * 车架号
     */
    private String vin;

    /**
     * 车牌
     */
    private String plateNumber;

    /**
     * 终端型号
     */
    private String terminalType;

    /**
     * 终端ID
     */
    private String terminalID;

    /**
     * SIM 卡
     */
    private String simCard;

    /**
     * 安装日期
     */
    private String installTime;

    /**
     * 设备运行情况
     */
    private  String deviceStatus;

    /**
     * 安装人员
     */
    private String installationPersonnel;

    /**
     * 附件图片
     */
    private String pathjsonStr;

    public InstallTerminalnfo() {
    }

    @Generated(hash = 1846701701)
    public InstallTerminalnfo(Long id, @NotNull String orderNumber, String vin, String plateNumber, String terminalType, String terminalID, String simCard,
            String installTime, String deviceStatus, String installationPersonnel, String pathjsonStr) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.vin = vin;
        this.plateNumber = plateNumber;
        this.terminalType = terminalType;
        this.terminalID = terminalID;
        this.simCard = simCard;
        this.installTime = installTime;
        this.deviceStatus = deviceStatus;
        this.installationPersonnel = installationPersonnel;
        this.pathjsonStr = pathjsonStr;
    }

    public String getOrderNumber() {
        return this.orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getVin() {
        return this.vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getPlateNumber() {
        return this.plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getTerminalType() {
        return this.terminalType;
    }

    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType;
    }

    public String getTerminalID() {
        return this.terminalID;
    }

    public void setTerminalID(String terminalID) {
        this.terminalID = terminalID;
    }

    public String getSimCard() {
        return this.simCard;
    }

    public void setSimCard(String simCard) {
        this.simCard = simCard;
    }

    public String getInstallationPersonnel() {
        return this.installationPersonnel;
    }

    public void setInstallationPersonnel(String installationPersonnel) {
        this.installationPersonnel = installationPersonnel;
    }

    public String getInstallTime() {
        return this.installTime;
    }

    public void setInstallTime(String installTime) {
        this.installTime = installTime;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getPathjsonStr() {
        return pathjsonStr;
    }

    public void setPathjsonStr(String pathjsonStr) {
        this.pathjsonStr = pathjsonStr;
    }

    @Override
    public String toString() {
        return "InstallTerminalnfo{" +
                "orderNumber='" + orderNumber + '\'' +
                ", vin='" + vin + '\'' +
                ", plateNumber='" + plateNumber + '\'' +
                ", terminalType='" + terminalType + '\'' +
                ", terminalID='" + terminalID + '\'' +
                ", simCard='" + simCard + '\'' +
                ", installationPersonnel='" + installationPersonnel + '\'' +
                ", installTime='" + installTime + '\'' +
                '}';
    }

    public boolean isIntegrityInfo(){
        return !(TextUtils.isEmpty(terminalType) || TextUtils.isEmpty(simCard) || TextUtils.isEmpty(installationPersonnel) || TextUtils.isEmpty(installTime));
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
