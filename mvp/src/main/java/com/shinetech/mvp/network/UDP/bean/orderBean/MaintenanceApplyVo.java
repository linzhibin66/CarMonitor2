package com.shinetech.mvp.network.UDP.bean.orderBean;

import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.BaseVo;

/**
 * Created by ljn on 2018-01-09.
 */

public class MaintenanceApplyVo extends BaseVo {

    /**
     * 工单名称
     */
    private String orderName;

    /**
     * 工单号
     */
    private String  orderNumber;

    /**
     * 维修申请
     */
    private byte[] maintenanceApply;

    /**************************************** result info start ******************************************************/

    /**
     * 结果： 0失败，1成功
     */
    private byte result;

    /**
     * 原因
     */
    private String resultReason;

    /**************************************** result info end ******************************************************/

    public MaintenanceApplyVo(String orderName, String orderNumber, byte[] maintenanceApply) {
        this.orderName = orderName;
        this.orderNumber = orderNumber;
        this.maintenanceApply = maintenanceApply;
        requestProtocolHead = Protocol.RESPONSE_PROTOCOL_MAINTENANCE_APPLY_BEAT;
        responseProtocolHead = Protocol.RESPONSE_PROTOCOL_ORDER_CTRL_BEAT_RESULT;
        isHasOrderCtrlResult = true;
    }

    @Override
    public Object[] getProperties() {
        return new Object[]{orderName, orderNumber, maintenanceApply};
    }

    @Override
    public void setProperties(Object[] properties) {

    }

    @Override
    public short[] getDataTypes() {
        return new short[]{};
    }

    @Override
    public boolean disposeOrderCtrlResult(BaseVo mBaseVo) {
        if(mBaseVo instanceof OrderCtrlResultVo){
            OrderCtrlResultVo mOrderCtrlResultVo = (OrderCtrlResultVo) mBaseVo;
            if(orderName.equals(mOrderCtrlResultVo.getOrderName())/* && orderNumber.equals(mOrderCtrlResultVo.getOrderNumber())*/ &&
                    mOrderCtrlResultVo.getCtrlType() ==  OrderCtrlResultVo.ORDER_PROCESS_CTRL){
                result = mOrderCtrlResultVo.getResult();
                resultReason = mOrderCtrlResultVo.getReason();
                setDisposeOrderCtrlResult(true);
                return true;
            }
        }
        return super.disposeOrderCtrlResult(mBaseVo);
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

    public byte[] getMaintenanceApply() {
        return maintenanceApply;
    }

    public void setMaintenanceApply(byte[] maintenanceApply) {
        this.maintenanceApply = maintenanceApply;
    }

    public byte getResult() {
        return result;
    }

    public void setResult(byte result) {
        this.result = result;
    }

    public String getResultReason() {
        return resultReason;
    }

    public void setResultReason(String resultReason) {
        this.resultReason = resultReason;
    }

    @Override
    public void setOrderCtrlResultValue(BaseVo mBaseVo) {
        if(mBaseVo instanceof OrderCtrlResultVo){
            OrderCtrlResultVo mOrderCtrlResultVo = (OrderCtrlResultVo) mBaseVo;
            setResult(mOrderCtrlResultVo.getResult());
            setResultReason(mOrderCtrlResultVo.getReason());
        }
    }
}