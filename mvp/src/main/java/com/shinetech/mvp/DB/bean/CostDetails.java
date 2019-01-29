package com.shinetech.mvp.DB.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by ljn on 2017-12-12.
 */

//@Entity(indexes = {@Index(value = "vin DESC", unique = true)})
public class CostDetails {

    /**
     * 车架号
     */
    private String vin;

    /**
     * 收费项目
     */
    private String payItems;

    /**
     * 月租
     */
    private int monthlyRent;

    /**
     * 预存金额
     */
    private int depositedamount;

    /**
     * 赠送金额
     */
    private int giftamount;

    /**
     * 本月减免
     */
    private int currentMonthDerate;

    /**
     * 累计历史欠费
     */
    private int historyArrears;

    /**
     * 累计欠费月份
     */
    private int arrearageMonth;

    /**
     * 本月抵扣预存额
     */
    private int deductionDepositedAmount;

    /**
     * 本月抵扣赠送额
     */
    private int deductionGiftAmount;

    /**
     * 本月收历史欠费
     */
    private int chargeHistoryArrears;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

//    @Generated(hash = 1769120820)
    public CostDetails(String vin, String payItems, int monthlyRent,
            int depositedamount, int giftamount, int currentMonthDerate,
            int historyArrears, int arrearageMonth, int deductionDepositedAmount,
            int deductionGiftAmount, int chargeHistoryArrears, String startDate,
            String endDate) {
        this.vin = vin;
        this.payItems = payItems;
        this.monthlyRent = monthlyRent;
        this.depositedamount = depositedamount;
        this.giftamount = giftamount;
        this.currentMonthDerate = currentMonthDerate;
        this.historyArrears = historyArrears;
        this.arrearageMonth = arrearageMonth;
        this.deductionDepositedAmount = deductionDepositedAmount;
        this.deductionGiftAmount = deductionGiftAmount;
        this.chargeHistoryArrears = chargeHistoryArrears;
        this.startDate = startDate;
        this.endDate = endDate;
    }

//    @Generated(hash = 2122690616)
    public CostDetails() {
    }

    public String getVin() {
        return this.vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getPayItems() {
        return this.payItems;
    }

    public void setPayItems(String payItems) {
        this.payItems = payItems;
    }

    public int getMonthlyRent() {
        return this.monthlyRent;
    }

    public void setMonthlyRent(int monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public int getDepositedamount() {
        return this.depositedamount;
    }

    public void setDepositedamount(int depositedamount) {
        this.depositedamount = depositedamount;
    }

    public int getGiftamount() {
        return this.giftamount;
    }

    public void setGiftamount(int giftamount) {
        this.giftamount = giftamount;
    }

    public int getCurrentMonthDerate() {
        return this.currentMonthDerate;
    }

    public void setCurrentMonthDerate(int currentMonthDerate) {
        this.currentMonthDerate = currentMonthDerate;
    }

    public int getHistoryArrears() {
        return this.historyArrears;
    }

    public void setHistoryArrears(int historyArrears) {
        this.historyArrears = historyArrears;
    }

    public int getArrearageMonth() {
        return this.arrearageMonth;
    }

    public void setArrearageMonth(int arrearageMonth) {
        this.arrearageMonth = arrearageMonth;
    }

    public int getDeductionDepositedAmount() {
        return this.deductionDepositedAmount;
    }

    public void setDeductionDepositedAmount(int deductionDepositedAmount) {
        this.deductionDepositedAmount = deductionDepositedAmount;
    }

    public int getDeductionGiftAmount() {
        return this.deductionGiftAmount;
    }

    public void setDeductionGiftAmount(int deductionGiftAmount) {
        this.deductionGiftAmount = deductionGiftAmount;
    }

    public int getChargeHistoryArrears() {
        return this.chargeHistoryArrears;
    }

    public void setChargeHistoryArrears(int chargeHistoryArrears) {
        this.chargeHistoryArrears = chargeHistoryArrears;
    }

    public String getStartDate() {
        return this.startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

}
