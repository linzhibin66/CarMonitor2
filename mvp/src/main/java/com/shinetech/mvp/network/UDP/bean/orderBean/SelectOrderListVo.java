package com.shinetech.mvp.network.UDP.bean.orderBean;

import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.BaseVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2018-01-02.
 */

public class SelectOrderListVo extends BaseVo {

    /**
     * 查询方式
     * 1 按车牌查询(模糊查询)
     * 2 按经手人员查询(含创建人)
     * 3 按客户名称查询
     * 4 按客户电话查询
     * 5 按待处理人查询
     *
     */
    private byte wayToQuery;

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
    private  byte orderType;

    /**************************************** result info start ******************************************************/

    /**
     * 工单列表
     */
    private List<BaseOrderInfoVo> orderInfoList;

    /**************************************** result info end ******************************************************/

    /**
     *
     * @param wayToQuery 大于0 ；
     * 1 按车牌查询(模糊查询)
     * 2 按经手人员查询(含创建人)
     * 3 按客户名称查询
     * 4 按客户电话查询
     * 5 按待处理人查询
     * @param queryCondition
     * @param orderType
     */
    public SelectOrderListVo(byte wayToQuery, String queryCondition, byte orderType) {
        requestProtocolHead = Protocol.RESPONSE_PROTOCOL_SELECT_ORDER_BEAT;
        responseProtocolHead = Protocol.RESPONSE_PROTOCOL_SELECT_ORDER_BEAT_RESULT;
        this.wayToQuery = wayToQuery;
        this.queryCondition = queryCondition;
        this.orderType = orderType;
        orderName = "";

    }

    @Override
    public Object[] getProperties() {
        return new Object[]{wayToQuery, queryCondition, orderName, orderType};
    }

    @Override
    public void setProperties(Object[] properties) {
    }

    @Override
    public short[] getDataTypes() {
        return new short[]{LIST};
    }

   /* @Override
    public short[] getElementDataTypes(int index) {
        return new short[]{INT, INT, STRING, STRING, BYTE, STRING, STRING, STRING};
    }

    @Override
    public void addListElement(Object[] elementProperties, int index) {
        BaseOrderInfoVo baseOrderInfoVo = new BaseOrderInfoVo();
        baseOrderInfoVo.setProperties(elementProperties);

        if(orderInfoList == null){
            orderInfoList =  new ArrayList<>();
        }

        if(!orderInfoList.contains(baseOrderInfoVo)){
            orderInfoList.add(baseOrderInfoVo);
        }
    }*/

    public byte getWayToQuery() {
        return wayToQuery;
    }

    public void setWayToQuery(byte wayToQuery) {
        this.wayToQuery = wayToQuery;
    }

    public String getQueryCondition() {
        return queryCondition;
    }

    public void setQueryCondition(String queryCondition) {
        this.queryCondition = queryCondition;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public byte getOrderType() {
        return orderType;
    }

    public void setOrderType(byte orderType) {
        this.orderType = orderType;
    }

    public List<BaseOrderInfoVo> getOrderInfoList() {
        return orderInfoList;
    }

    public void addOrderInfoList(BaseOrderInfoVo orderInfo){
        if(orderInfoList == null){
            orderInfoList = new ArrayList<>();
        }

        if(!orderInfoList.contains(orderInfo)){
            orderInfoList.add(orderInfo);
        }
    }
}
