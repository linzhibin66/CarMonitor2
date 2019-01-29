package com.shinetech.mvp.network.UDP.bean.orderBean;

import com.shinetech.mvp.network.UDP.bean.BaseVo;

/**
 * Created by ljn on 2018-01-02.
 */

public class OrderCtrlResultVo extends BaseVo {

    public static byte CREATE_MODIFICATION_TYPE = 0;

    public static byte DELETE_ORDER_TYPE = 1;

    public static byte UPLOAD_ACCESSORY = 2;

    public static byte DOWNLOAD_ACCESSORY = 3;

    public static byte DELETE_ACCESSORY = 4;

    public static byte ORDER_SELECT = 5;

    public static byte ORDER_PROCESS_CTRL = 6;

    /**
     * 操作
     * 0 新建/修改工单
     * 1 删除工单
     * 2 上传附件
     * 3下载附件
     * 4 删除附件（只能删本人上传的附件）
     * 5 工单查询
     * 6 工单流程操作
     */
    private byte ctrlType;

    /**
     * 结果
     * 0 失败
     * 1 成功
     * 2 提示信息
     */
    private byte result;

    /**
     * 原因
     */
    private String reason;

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

    @Override
    public Object[] getProperties() {
        return new Object[]{};
    }

    @Override
    public void setProperties(Object[] properties) {
        this.ctrlType = (byte) properties[0];
        this.result = (byte) properties[1];
        this.reason = parseString((byte[]) properties[2]);
        this.orderName = parseString((byte[]) properties[3]);
        this.orderNumber = parseString((byte[]) properties[4]);
        this.fileType = parseString((byte[]) properties[5]);
        this.fileName = parseString((byte[]) properties[5]);
        this.fileData =(byte[]) properties[7];

    }

    @Override
    public short[] getDataTypes() {
        return new short[]{BYTE, BYTE, STRING, STRING, STRING, STRING, STRING, DATA};
    }

    public byte getCtrlType() {
        return ctrlType;
    }

    public void setCtrlType(byte ctrlType) {
        this.ctrlType = ctrlType;
    }

    public byte getResult() {
        return result;
    }

    public void setResult(byte result) {
        this.result = result;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }
}
