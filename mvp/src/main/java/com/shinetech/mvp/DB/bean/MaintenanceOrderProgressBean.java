package com.shinetech.mvp.DB.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by ljn on 2017-12-21.
 */

@Entity(indexes = {@Index(value = "time DESC", unique = true)})
public class MaintenanceOrderProgressBean {

    /**
     * 工单号
     */
    @NotNull
    private String orderNumber;

    /**
     * 进度状态
     * 1 订单已提交
     * 2 客服派单（客服处理中）
     * 3 师傅接单
     * 4 师傅维修
     * 5 回访评价
     */
    private byte progressStatus;

    /**
     * 进度时间
     */
    private String time;


    public MaintenanceOrderProgressBean() {
    }

    @Generated(hash = 401157856)
    public MaintenanceOrderProgressBean(@NotNull String orderNumber,
            byte progressStatus, String time) {
        this.orderNumber = orderNumber;
        this.progressStatus = progressStatus;
        this.time = time;
    }

    public String getOrderNumber() {
        return this.orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public byte getProgressStatus() {
        return this.progressStatus;
    }

    public void setProgressStatus(byte progressStatus) {
        this.progressStatus = progressStatus;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
