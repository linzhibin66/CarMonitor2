package com.shinetech.mvp.network.UDP.bean.orderBean;

import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.BaseVo;

import java.util.Arrays;

/**
 * Created by ljn on 2018-01-02.
 */

public class SelectOrderByNumberVo extends BaseVo {

    /**
     * 查询方式
     * 0 按工单号查询
     */
    private byte wayToQuery = 0;

    /**
     * 查询条件
     */
    private String queryCondition;

    /**
     * 工单名称
     * 为空时查所有工单
     */
    private String orderName;

    /**
     * 工单类型
     * 0 进行 1 结束，按单号查时无用
     */
    private  byte orderType = 0;

    /**************************************** result info start ******************************************************/

    /**
     * 工单名称
     */
    private String resultOrderName;

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
     * 车辆类型
     */
    private String carType;

    /**
     * 创建人
     */
    private String founder;

    /**
     * 内容列表
     */
    private byte[] contentList;

    /**
     * 附件列表
     */
    private byte[] attachmentList;

    /**
     * 流程列表
     */
    private byte[] processList;

    //工单操作结果
    /**
     * 操作结果
     */
    private byte orderCtrlResult;

    /**
     * 原因
     */
    private String reason;

    /**************************************** result info end ******************************************************/

    public SelectOrderByNumberVo(String queryCondition, String orderName) {
        requestProtocolHead = Protocol.RESPONSE_PROTOCOL_SELECT_ORDER_BEAT;
        responseProtocolHead = Protocol.RESPONSE_PROTOCOL_SELECT_ORDER_INFO_BEAT_RESULT;
        this.queryCondition = queryCondition;
        this.orderName = orderName;
        //需处理工单操作结果包
        isHasOrderCtrlResult = true;
        setReceiveOtherData(false);
    }

    @Override
    public Object[] getProperties() {
        return new Object[]{wayToQuery, queryCondition, orderName, orderType};
    }

    @Override
    public void setProperties(Object[] properties) {
        this.resultOrderName = parseString((byte[]) properties[0]);
        this.orderNumber = parseString((byte[]) properties[1]);
        this.orderStatus = (byte) properties[2];
        this.clienteleName = parseString((byte[]) properties[3]);
        this.clientelePhone = parseString((byte[]) properties[4]);
        this.carType = parseString((byte[]) properties[5]);
        this.founder = parseString((byte[]) properties[6]);
        this.contentList = (byte[]) properties[7];
        this.attachmentList = (byte[]) properties[8];
        this.processList = (byte[]) properties[9];
    }

    @Override
    public short[] getDataTypes() {
        return new short[]{STRING, STRING, BYTE, STRING, STRING, STRING, STRING, DATA, DATA, DATA, };
    }

    public String getResultOrderName() {
        return resultOrderName;
    }

    public void setResultOrderName(String resultOrderName) {
        this.resultOrderName = resultOrderName;
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

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getFounder() {
        return founder;
    }

    public void setFounder(String founder) {
        this.founder = founder;
    }

    public byte[] getContentList() {
        return contentList;
    }

    public void setContentList(byte[] contentList) {
        this.contentList = contentList;
    }

    public byte[] getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(byte[] attachmentList) {
        this.attachmentList = attachmentList;
    }

    public byte[] getProcessList() {
        return processList;
    }

    public void setProcessList(byte[] processList) {
        this.processList = processList;
    }

    public byte getOrderCtrlResult() {
        return orderCtrlResult;
    }

    public void setOrderCtrlResult(byte orderCtrlResult) {
        this.orderCtrlResult = orderCtrlResult;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SelectOrderByNumberVo that = (SelectOrderByNumberVo) o;

        if (wayToQuery != that.wayToQuery) return false;
        if (orderType != that.orderType) return false;
        if (getOrderStatus() != that.getOrderStatus()) return false;
        if (getCarType() != that.getCarType()) return false;
        if (queryCondition != null ? !queryCondition.equals(that.queryCondition) : that.queryCondition != null)
            return false;
        if (orderName != null ? !orderName.equals(that.orderName) : that.orderName != null)
            return false;
        if (getResultOrderName() != null ? !getResultOrderName().equals(that.getResultOrderName()) : that.getResultOrderName() != null)
            return false;
        if (getOrderNumber() != null ? !getOrderNumber().equals(that.getOrderNumber()) : that.getOrderNumber() != null)
            return false;
        if (getClienteleName() != null ? !getClienteleName().equals(that.getClienteleName()) : that.getClienteleName() != null)
            return false;
        if (getClientelePhone() != null ? !getClientelePhone().equals(that.getClientelePhone()) : that.getClientelePhone() != null)
            return false;
        if (getFounder() != null ? !getFounder().equals(that.getFounder()) : that.getFounder() != null)
            return false;
        if (!Arrays.equals(getContentList(), that.getContentList())) return false;
        if (!Arrays.equals(getAttachmentList(), that.getAttachmentList())) return false;
        return Arrays.equals(getProcessList(), that.getProcessList());

    }

    @Override
    public int hashCode() {
        int result = (int) wayToQuery;
        result = 31 * result + (queryCondition != null ? queryCondition.hashCode() : 0);
        result = 31 * result + (orderName != null ? orderName.hashCode() : 0);
        result = 31 * result + (int) orderType;
        result = 31 * result + (getResultOrderName() != null ? getResultOrderName().hashCode() : 0);
        result = 31 * result + (getOrderNumber() != null ? getOrderNumber().hashCode() : 0);
        result = 31 * result + (int) getOrderStatus();
        result = 31 * result + (getClienteleName() != null ? getClienteleName().hashCode() : 0);
        result = 31 * result + (getClientelePhone() != null ? getClientelePhone().hashCode() : 0);
        result = 31 * result + (getCarType() != null ? getCarType().hashCode() : 0);
        result = 31 * result + (getFounder() != null ? getFounder().hashCode() : 0);
        result = 31 * result + Arrays.hashCode(getContentList());
        result = 31 * result + Arrays.hashCode(getAttachmentList());
        result = 31 * result + Arrays.hashCode(getProcessList());
        return result;
    }

    @Override
    public boolean disposeOrderCtrlResult(BaseVo mBaseVo) {
        if(mBaseVo instanceof OrderCtrlResultVo){
            OrderCtrlResultVo mOrderCtrlResultVo = (OrderCtrlResultVo) mBaseVo;
            byte ctrlType = mOrderCtrlResultVo.getCtrlType();
            String orderNumber = mOrderCtrlResultVo.getOrderNumber();
            if(ctrlType == OrderCtrlResultVo.ORDER_SELECT/* && orderNumber.equals(queryCondition)*/){
                orderCtrlResult = mOrderCtrlResultVo.getResult();
                reason = mOrderCtrlResultVo.getReason();
                setDisposeOrderCtrlResult(true);
                return true;
            }
        }

        return super.disposeOrderCtrlResult(mBaseVo);
    }
}
