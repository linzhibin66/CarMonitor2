package com.shinetech.mvp.DB.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by ljn on 2018-01-10.
 */

@Entity(indexes = {@Index(value = "orderNumber DESC", unique = true)})
public class CreateMaintenanceInfoDB {

    @Id
    private Long id;

    /**
     * 工单号
     */
    @Unique
    private String orderNumber;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 报修人
     */
    private String proposer;

    /**
     * 联系电话
     */
    private String contactNumber;

    /**
     * 报修时间
     */
    private String repairTime;

    /**
     * 预约时间
     */
    private String appointmentTime;

    /**
     * 预约地点
     */
    private String appointmentLocation;

    /**
     * 故障描述
     */
    private String problemDescription;

    /**
     * 附件图片
     */
    private String pathjsonStr;

    public CreateMaintenanceInfoDB(String orderNumber, String plateNumber, String proposer, String contactNumber, String appointmentTime, String appointmentLocation, String problemDescription) {
        this.orderNumber = orderNumber;
        this.plateNumber = plateNumber;
        this.proposer = proposer;
        this.contactNumber = contactNumber;
        this.appointmentTime = appointmentTime;
        this.appointmentLocation = appointmentLocation;
        this.problemDescription = problemDescription;
    }

    public CreateMaintenanceInfoDB() {
    }

    @Generated(hash = 195783896)
    public CreateMaintenanceInfoDB(Long id, String orderNumber, String plateNumber, String proposer, String contactNumber, String repairTime, String appointmentTime, String appointmentLocation,
            String problemDescription, String pathjsonStr) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.plateNumber = plateNumber;
        this.proposer = proposer;
        this.contactNumber = contactNumber;
        this.repairTime = repairTime;
        this.appointmentTime = appointmentTime;
        this.appointmentLocation = appointmentLocation;
        this.problemDescription = problemDescription;
        this.pathjsonStr = pathjsonStr;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getProposer() {
        return proposer;
    }

    public void setProposer(String proposer) {
        this.proposer = proposer;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getAppointmentLocation() {
        return appointmentLocation;
    }

    public void setAppointmentLocation(String appointmentLocation) {
        this.appointmentLocation = appointmentLocation;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public String getRepairTime() {
        return repairTime;
    }

    public void setRepairTime(String repairTime) {
        this.repairTime = repairTime;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPathjsonStr() {
        return pathjsonStr;
    }

    public void setPathjsonStr(String pathjsonStr) {
        this.pathjsonStr = pathjsonStr;
    }
}
