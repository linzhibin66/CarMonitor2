package com.shinetech.mvp.network.UDP.bean.orderBean;

import com.shinetech.mvp.network.UDP.bean.BaseVo;

/**
 * Created by ljn on 2018-01-10.
 * 发送创建维修单的DATA数据类
 */

public class CreateOrderDataVo extends BaseVo {

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
    private byte orderStatus = 0;

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
    private String carType = "";

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
    private byte[] attachmentList = new byte[0];

    /**
     * 流程列表
     */
    private byte[] processList = new byte[0];


    public CreateOrderDataVo(String orderName, String orderNumber, String clienteleName, String clientelePhone, String founder, byte[] contentList) {
        this.orderName = orderName;
        this.orderNumber = orderNumber;
        this.clienteleName = clienteleName;
        this.clientelePhone = clientelePhone;
        this.founder = founder;
        this.contentList = contentList;
    }

    @Override
    public Object[] getProperties() {
        return new Object[]{orderName, orderNumber, orderStatus, clienteleName, clientelePhone, carType, founder, contentList, attachmentList, processList};
    }

    @Override
    public void setProperties(Object[] properties) {

    }

    @Override
    public short[] getDataTypes() {
        return new short[0];
    }
}
