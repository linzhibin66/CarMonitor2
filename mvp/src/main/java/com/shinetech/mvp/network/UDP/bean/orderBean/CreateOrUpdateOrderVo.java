package com.shinetech.mvp.network.UDP.bean.orderBean;

import android.text.TextUtils;

import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.BaseVo;

import java.util.Arrays;

/**
 * Created by Lenovo on 2018-01-05.
 */

public class CreateOrUpdateOrderVo extends OrderCtrlVo {

    /**************************************** result info start ******************************************************/

    /**
     * 工单名称
     */
    private String resultOrderName;

    /**
     * 工单号
     */
    private String resultOrderNumber;

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

    /**
     * @param orderName   工单名称
     * @param orderNumber 工单号
     * @param fileType    文件类型 （附件操作用）
     * @param fileName    文件名 （附件操作用）
     * @param fileData    文件数据
     */
    public CreateOrUpdateOrderVo(String orderName, String orderNumber, String fileType, String fileName, byte[] fileData) {
        super((byte)0, orderName, orderNumber, fileType, fileName, fileData);
        responseProtocolHead = Protocol.RESPONSE_PROTOCOL_SELECT_ORDER_INFO_BEAT_RESULT;
        //需处理工单操作结果包
        isHasOrderCtrlResult = true;
    }

    @Override
    public void setProperties(Object[] properties) {
        this.resultOrderName = parseString((byte[]) properties[0]);
        this.resultOrderNumber = parseString((byte[]) properties[1]);
        this.orderStatus = (byte) properties[2];
        this.clienteleName = parseString((byte[]) properties[3]);
        this.clientelePhone = parseString((byte[]) properties[4]);
        this.carType = parseString((byte[]) properties[5]);
        this.founder = parseString((byte[]) properties[6]);
        this.contentList = (byte[]) properties[7];
        this.attachmentList = (byte[]) properties[8];
        this.processList = (byte[]) properties[9];

        System.out.println(toString());
    }

    @Override
    public short[] getDataTypes() {
        return new short[]{STRING, STRING, BYTE, STRING, STRING, STRING, STRING, DATA, DATA, DATA};
    }

    public String getResultOrderName() {
        return resultOrderName;
    }

    public void setResultOrderName(String resultOrderName) {
        this.resultOrderName = resultOrderName;
    }

    public String getResultOrderNumber() {
        return resultOrderNumber;
    }

    public void setResultOrderNumber(String resultOrderNumber) {
        this.resultOrderNumber = resultOrderNumber;
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
    public boolean isDisposeResult(OrderCtrlResultVo mOrderCtrlResultVo) {
        if(mOrderCtrlResultVo.getCtrlType() == ctrlType && ((!TextUtils.isEmpty(resultOrderNumber) && resultOrderNumber.equals(mOrderCtrlResultVo.getOrderNumber()))
                || (TextUtils.isEmpty(resultOrderNumber) && TextUtils.isEmpty(mOrderCtrlResultVo.getOrderNumber())) ) && resultOrderName.equals(mOrderCtrlResultVo.getOrderName())){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "CreateOrUpdateOrderVo{" +
                "resultOrderName='" + resultOrderName + '\'' +
                ", resultOrderNumber='" + resultOrderNumber + '\'' +
                ", orderStatus=" + orderStatus +
                ", clienteleName='" + clienteleName + '\'' +
                ", clientelePhone='" + clientelePhone + '\'' +
                ", carType=" + carType +
                ", founder='" + founder + '\'' +
                ", orderCtrlResult=" + orderCtrlResult +
                ", reason='" + reason + '\'' +
                '}';
    }
}
