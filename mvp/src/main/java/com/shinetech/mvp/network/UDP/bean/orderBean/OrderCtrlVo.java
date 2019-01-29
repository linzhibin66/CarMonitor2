package com.shinetech.mvp.network.UDP.bean.orderBean;

import android.text.TextUtils;

import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.BaseVo;

/**
 * Created by ljn on 2018-01-04.
 */

public class OrderCtrlVo extends BaseVo {


    /**
     * 操作类型
     * 0 新建工单
     * 1 删除工单
     * 2 上传附件
     * 3 下载附件
     * 4 删除附件（只能删本人上传的附件）
     *
     */
    protected byte ctrlType;

    /**
     * 工单名称
     */
    private String orderName;

    /**
     * 工单号
     */
    private String orderNumber;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件数据
     */
    private byte[] fileData;

    /**************************************** result info start ******************************************************/

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
     *
     * @param ctrlType  操作类型;0 新建工单、1 删除工单、2 上传附件、3下载附件、4 删除附件（只能删本人上传的附件）
     * @param orderName  工单名称
     * @param orderNumber  工单号
     * @param fileType  文件类型 （附件操作用）
     * @param fileName  文件名 （附件操作用）
     * @param fileData  文件数据
     */
    public OrderCtrlVo(byte ctrlType, String orderName, String orderNumber, String fileType, String fileName, byte[] fileData) {
        this.ctrlType = ctrlType;
        this.orderName = orderName;
        this.orderNumber = orderNumber;
        this.fileType = fileType;
        this.fileName = fileName;
        this.fileData = fileData;
        requestProtocolHead = Protocol.RESPONSE_PROTOCOL_ORDER_CTRL_BEAT;
        responseProtocolHead = Protocol.RESPONSE_PROTOCOL_ORDER_CTRL_BEAT_RESULT;
        isHasOrderCtrlResult = true;
    }

    @Override
    public Object[] getProperties() {
        return new Object[]{ctrlType, orderName, orderNumber, fileType, fileName, fileData};
    }

    @Override
    public void setProperties(Object[] properties) {

    }

    @Override
    public short[] getDataTypes() {
        return new short[0];
    }

    @Override
    public boolean disposeOrderCtrlResult(BaseVo mBaseVo) {

        if(mBaseVo instanceof OrderCtrlResultVo){
            OrderCtrlResultVo mOrderCtrlResultVo = (OrderCtrlResultVo) mBaseVo;
            if(isDisposeResult(mOrderCtrlResultVo)){
                orderCtrlResult = mOrderCtrlResultVo.getResult();
                reason = mOrderCtrlResultVo.getReason();

                if(mOrderCtrlResultVo.getCtrlType() == 3){
                    fileData = mOrderCtrlResultVo.getFileData();
                }
                setDisposeOrderCtrlResult(true);
                return true;
            }
        }
        return super.disposeOrderCtrlResult(mBaseVo);
    }

    public boolean isDisposeResult(OrderCtrlResultVo mOrderCtrlResultVo){
        if(mOrderCtrlResultVo.getCtrlType() == ctrlType && ((!TextUtils.isEmpty(orderNumber) && orderNumber.equals(mOrderCtrlResultVo.getOrderNumber()))
                || (TextUtils.isEmpty(orderNumber) && TextUtils.isEmpty(mOrderCtrlResultVo.getOrderNumber())) ) && orderName.equals(mOrderCtrlResultVo.getOrderName())){
            return true;
        }
        return false;
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

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getOrderName() {
        return orderName;
    }

    public String getFileType() {
        return fileType;
    }
}
