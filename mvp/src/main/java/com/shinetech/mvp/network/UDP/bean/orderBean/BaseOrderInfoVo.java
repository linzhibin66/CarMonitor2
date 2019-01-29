package com.shinetech.mvp.network.UDP.bean.orderBean;

import android.text.TextUtils;

import com.shinetech.mvp.DB.DBManager;
import com.shinetech.mvp.DB.bean.BaseOrderInfoDB;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.BaseVo;

/**
 * Created by ljn on 2018-01-02.
 */

public class BaseOrderInfoVo extends BaseVo{

    /**
     * 总数
     */
    private int sumTotal;

    /**
     * 序号
     */
    private int listID;

    /**
     * 工单名称
     */
    private String orderName;

    /**
     * 工单号
     */
    private String orderNumber;

    /**
     * 状态
     */
    private byte orderStatus;

    /**
     * 客户名称
     */
    private String clienteleName;

    /**
     * 客户电话
     */
    private String clientelePhone;

    /**
     * 创建人
     */
    private String founder;

    public BaseOrderInfoVo() {
        responseProtocolHead = Protocol.RESPONSE_PROTOCOL_SELECT_ORDER_BEAT_RESULT;
    }

    @Override
    public Object[] getProperties() {
        return new Object[]{};
    }

    @Override
    public void setProperties(Object[] properties) {
        this.sumTotal = (int) properties[0];
        this.listID = (int) properties[1];
        this.orderName = parseString((byte[]) properties[2]);
        this.orderNumber = parseString((byte[]) properties[3]);
        this.orderStatus = (byte) properties[4];
        this.clienteleName = parseString((byte[]) properties[5]);
        this.clientelePhone = parseString((byte[]) properties[6]);
        this.founder = parseString((byte[]) properties[7]);

    }

    @Override
    public short[] getDataTypes() {
        return new short[]{INT, INT, STRING, STRING, BYTE, STRING, STRING, STRING};
    }

    public int getSumTotal() {
        return sumTotal;
    }

    public void setSumTotal(int sumTotal) {
        this.sumTotal = sumTotal;
    }

    public int getListID() {
        return listID;
    }

    public void setListID(int listID) {
        this.listID = listID;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public byte getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(byte orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getClienteleName() {
        return clienteleName;
    }

    public void setClienteleName(String clienteleName) {
        this.clienteleName = clienteleName;
    }

    public String getClientelePhone() {
        return clientelePhone;
    }

    public void setClientelePhone(String clientelePhone) {
        this.clientelePhone = clientelePhone;
    }

    public String getFounder() {
        return founder;
    }

    public void setFounder(String founder) {
        this.founder = founder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseOrderInfoVo that = (BaseOrderInfoVo) o;

        if (getSumTotal() != that.getSumTotal()) return false;
        if (getListID() != that.getListID()) return false;
        if (getOrderStatus() != that.getOrderStatus()) return false;
        if (getOrderName() != null ? !getOrderName().equals(that.getOrderName()) : that.getOrderName() != null)
            return false;
        if (getOrderNumber() != null ? !getOrderNumber().equals(that.getOrderNumber()) : that.getOrderNumber() != null)
            return false;
        if (getClienteleName() != null ? !getClienteleName().equals(that.getClienteleName()) : that.getClienteleName() != null)
            return false;
        if (getClientelePhone() != null ? !getClientelePhone().equals(that.getClientelePhone()) : that.getClientelePhone() != null)
            return false;
        return getFounder() != null ? getFounder().equals(that.getFounder()) : that.getFounder() == null;

    }

    @Override
    public int hashCode() {
        int result = getSumTotal();
        result = 31 * result + getListID();
        result = 31 * result + (getOrderName() != null ? getOrderName().hashCode() : 0);
        result = 31 * result + (getOrderNumber() != null ? getOrderNumber().hashCode() : 0);
        result = 31 * result + (int) getOrderStatus();
        result = 31 * result + (getClienteleName() != null ? getClienteleName().hashCode() : 0);
        result = 31 * result + (getClientelePhone() != null ? getClientelePhone().hashCode() : 0);
        result = 31 * result + (getFounder() != null ? getFounder().hashCode() : 0);
        return result;
    }

    public void saveDB(){

        BaseOrderInfoDB baseOrderInfoDB = DBManager.querySelectBaseOrderInfo(UserInfo.getInstance().getUserName(), orderNumber);
        if(baseOrderInfoDB == null || (!TextUtils.isEmpty(baseOrderInfoDB.getOrderName()) && !baseOrderInfoDB.getOrderName().equals(orderName))) {
            baseOrderInfoDB = new BaseOrderInfoDB(UserInfo.getInstance().getUserName(), orderName, orderNumber, orderStatus, clienteleName, clientelePhone, founder);
            DBManager.insertBaseOrderInfo(baseOrderInfoDB);
        }else{
            baseOrderInfoDB.setOrderStatus(orderStatus);
            baseOrderInfoDB.setClienteleName(clienteleName);
            baseOrderInfoDB.setClientelePhone(clientelePhone);
            baseOrderInfoDB.setFounder(founder);
            DBManager.updataBaseOrderInfo(baseOrderInfoDB);
        }

    }

    @Override
    public String toString() {
        return "BaseOrderInfoVo{" +
                "sumTotal=" + sumTotal +
                ", listID=" + listID +
                ", orderName='" + orderName + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", orderStatus=" + orderStatus +
                ", clienteleName='" + clienteleName + '\'' +
                ", clientelePhone='" + clientelePhone + '\'' +
                ", founder='" + founder + '\'' +
                '}';
    }
}
