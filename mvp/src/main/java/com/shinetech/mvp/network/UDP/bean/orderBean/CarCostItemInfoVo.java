package com.shinetech.mvp.network.UDP.bean.orderBean;

import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.BaseVo;

/**
 * Created by ljn on 2017-12-27.
 */

public class CarCostItemInfoVo extends BaseVo {

    /**
     * 车牌
     */
    private String plateNumber;

    /**
     * 总条数
     */
    private int sumTotal;

    /**
     * 序号
     */
    private int order;

    /**
     * 收费项目
     */
    private String payItems;

    /**
     * 计费状态
     * 1 未审核 ; 2 营运中 ; 3 已报停 ; 4 报废
     */
    private byte costStatus;

    /**
     * 报停时间
     */
    private String quitTime;

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
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 计费月份
     */
    private String costMonth;

    /**
     * 欠费月份
     */
    private int arrearsMonth;

    /**
     * 历史欠费
     */
    private int historyArrears;

    /**
     * 应收月租
     */
    private int receivableMonthlyRent;

    /**
     * 本月减免
     */
    private int currentMonthDerate;

    /**
     * 本月赠送
     */
    private int currentMonthGift;

    /**
     * 本月实收月租
     */
    private int currentChargeMonthlyRent;

    /**
     * 本月实收欠费
     */
    private int currentChargeArrears;



    public CarCostItemInfoVo() {
        responseProtocolHead = Protocol.RESPONSE_PROTOCOL_CARCOST_INFO_BEAT;
    }

    @Override
    public Object[] getProperties() {
        return new Object[0];
    }

    @Override
    public void setProperties(Object[] properties) {
        this.plateNumber = parseString((byte[]) properties[0]);

        this.sumTotal = (int) properties[1];
        this.order = (int) properties[2];

        this.payItems = parseString((byte[]) properties[3]);
        this.costStatus = (byte) properties[4];
        this.quitTime = parseString((byte[]) properties[5]);

        this.monthlyRent = (int) properties[6];
        this.depositedamount = (int) properties[7];
        this.giftamount = (int) properties[8];

        this.startDate = parseString((byte[]) properties[9]);
        this.endDate = parseString((byte[]) properties[10]);
        this.remark = parseString((byte[]) properties[11]);
        this.costMonth = parseString((byte[]) properties[12]);

        this.arrearsMonth = (int) properties[13];
        this.historyArrears = (int) properties[14];
        this.receivableMonthlyRent = (int) properties[15];
        this.currentMonthDerate = (int) properties[16];
        this.currentMonthGift = (int) properties[17];
        this.currentChargeMonthlyRent = (int) properties[18];
        this.currentChargeArrears = (int) properties[19];

    }

    @Override
    public short[] getDataTypes() {
        return new short[]{STRING, INT, INT, STRING, BYTE, STRING, INT, INT, INT, STRING, STRING, STRING, STRING, INT, INT, INT, INT, INT, INT, INT};
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public int getSumTotal() {
        return sumTotal;
    }

    public void setSumTotal(int sumTotal) {
        this.sumTotal = sumTotal;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getPayItems() {
        return payItems;
    }

    public void setPayItems(String payItems) {
        this.payItems = payItems;
    }

    public byte getCostStatus() {
        return costStatus;
    }

    public void setCostStatus(byte costStatus) {
        this.costStatus = costStatus;
    }

    public String getQuitTime() {
        return quitTime;
    }

    public void setQuitTime(String quitTime) {
        this.quitTime = quitTime;
    }

    public int getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(int monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public int getDepositedamount() {
        return depositedamount;
    }

    public void setDepositedamount(int depositedamount) {
        this.depositedamount = depositedamount;
    }

    public int getGiftamount() {
        return giftamount;
    }

    public void setGiftamount(int giftamount) {
        this.giftamount = giftamount;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCostMonth() {
        return costMonth;
    }

    public void setCostMonth(String costMonth) {
        this.costMonth = costMonth;
    }

    public int getArrearsMonth() {
        return arrearsMonth;
    }

    public void setArrearsMonth(int arrearsMonth) {
        this.arrearsMonth = arrearsMonth;
    }

    public int getHistoryArrears() {
        return historyArrears;
    }

    public void setHistoryArrears(int historyArrears) {
        this.historyArrears = historyArrears;
    }

    public int getReceivableMonthlyRent() {
        return receivableMonthlyRent;
    }

    public void setReceivableMonthlyRent(int receivableMonthlyRent) {
        this.receivableMonthlyRent = receivableMonthlyRent;
    }

    public int getCurrentMonthDerate() {
        return currentMonthDerate;
    }

    public void setCurrentMonthDerate(int currentMonthDerate) {
        this.currentMonthDerate = currentMonthDerate;
    }

    public int getCurrentMonthGift() {
        return currentMonthGift;
    }

    public void setCurrentMonthGift(int currentMonthGift) {
        this.currentMonthGift = currentMonthGift;
    }

    public int getCurrentChargeMonthlyRent() {
        return currentChargeMonthlyRent;
    }

    public void setCurrentChargeMonthlyRent(int currentChargeMonthlyRent) {
        this.currentChargeMonthlyRent = currentChargeMonthlyRent;
    }

    public int getCurrentChargeArrears() {
        return currentChargeArrears;
    }

    public void setCurrentChargeArrears(int currentChargeArrears) {
        this.currentChargeArrears = currentChargeArrears;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CarCostItemInfoVo that = (CarCostItemInfoVo) o;

        if (getSumTotal() != that.getSumTotal()) return false;
        if (getOrder() != that.getOrder()) return false;
        if (getCostStatus() != that.getCostStatus()) return false;
        if (getMonthlyRent() != that.getMonthlyRent()) return false;
        if (getDepositedamount() != that.getDepositedamount()) return false;
        if (getGiftamount() != that.getGiftamount()) return false;
        if (getArrearsMonth() != that.getArrearsMonth()) return false;
        if (getHistoryArrears() != that.getHistoryArrears()) return false;
        if (getReceivableMonthlyRent() != that.getReceivableMonthlyRent()) return false;
        if (getCurrentMonthDerate() != that.getCurrentMonthDerate()) return false;
        if (getCurrentMonthGift() != that.getCurrentMonthGift()) return false;
        if (getCurrentChargeMonthlyRent() != that.getCurrentChargeMonthlyRent()) return false;
        if (getCurrentChargeArrears() != that.getCurrentChargeArrears()) return false;
        if (getPlateNumber() != null ? !getPlateNumber().equals(that.getPlateNumber()) : that.getPlateNumber() != null)
            return false;
        if (getPayItems() != null ? !getPayItems().equals(that.getPayItems()) : that.getPayItems() != null)
            return false;
        if (getQuitTime() != null ? !getQuitTime().equals(that.getQuitTime()) : that.getQuitTime() != null)
            return false;
        if (getStartDate() != null ? !getStartDate().equals(that.getStartDate()) : that.getStartDate() != null)
            return false;
        if (getEndDate() != null ? !getEndDate().equals(that.getEndDate()) : that.getEndDate() != null)
            return false;
        if (getRemark() != null ? !getRemark().equals(that.getRemark()) : that.getRemark() != null)
            return false;
        return getCostMonth() != null ? getCostMonth().equals(that.getCostMonth()) : that.getCostMonth() == null;

    }

    @Override
    public int hashCode() {
        int result = getPlateNumber() != null ? getPlateNumber().hashCode() : 0;
        result = 31 * result + getSumTotal();
        result = 31 * result + getOrder();
        result = 31 * result + (getPayItems() != null ? getPayItems().hashCode() : 0);
        result = 31 * result + (int) getCostStatus();
        result = 31 * result + (getQuitTime() != null ? getQuitTime().hashCode() : 0);
        result = 31 * result + getMonthlyRent();
        result = 31 * result + getDepositedamount();
        result = 31 * result + getGiftamount();
        result = 31 * result + (getStartDate() != null ? getStartDate().hashCode() : 0);
        result = 31 * result + (getEndDate() != null ? getEndDate().hashCode() : 0);
        result = 31 * result + (getRemark() != null ? getRemark().hashCode() : 0);
        result = 31 * result + (getCostMonth() != null ? getCostMonth().hashCode() : 0);
        result = 31 * result + getArrearsMonth();
        result = 31 * result + getHistoryArrears();
        result = 31 * result + getReceivableMonthlyRent();
        result = 31 * result + getCurrentMonthDerate();
        result = 31 * result + getCurrentMonthGift();
        result = 31 * result + getCurrentChargeMonthlyRent();
        result = 31 * result + getCurrentChargeArrears();
        return result;
    }
}
