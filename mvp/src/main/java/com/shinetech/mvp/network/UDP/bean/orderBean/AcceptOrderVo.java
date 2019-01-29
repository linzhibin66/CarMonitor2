package com.shinetech.mvp.network.UDP.bean.orderBean;

import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.BaseVo;

/**
 * Created by ljn on 2017-12-20.
 */

public class AcceptOrderVo extends BaseVo{

    /**
     * 工单名称
     */
    private String orderName;

    /**
     * 工单号
     */
    private String  orderNumber;

    /**
     * 是否接单
     */
    private byte isAccept;

    /**
     * 拒接原因
     */
    private String reason;

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

    public AcceptOrderVo(String orderName, String orderNumber, byte isAccept, String reason) {
        requestProtocolHead = Protocol.RESPONSE_PROTOCOL_ACCEPT_ORDER_BEAT;
        responseProtocolHead = Protocol.RESPONSE_PROTOCOL_ORDER_CTRL_BEAT_RESULT;
        this.orderName = orderName;
        this.orderNumber = orderNumber;
        this.isAccept = isAccept;
        this.reason = reason;
        isHasOrderCtrlResult = true;
    }

    @Override
    public Object[] getProperties() {
        return new Object[]{orderName, orderNumber, isAccept, reason};
    }

    @Override
    public void setProperties(Object[] properties) {
    }

    @Override
    public short[] getDataTypes() {
        return new short[]{};
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public byte getIsAccept() {
        return isAccept;
    }

    public void setIsAccept(byte isAccept) {
        this.isAccept = isAccept;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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
    public boolean disposeOrderCtrlResult(BaseVo mBaseVo) {
        if(mBaseVo instanceof OrderCtrlResultVo){
            OrderCtrlResultVo mOrderCtrlResultVo = (OrderCtrlResultVo) mBaseVo;

            if(mOrderCtrlResultVo.getCtrlType() == OrderCtrlResultVo.ORDER_PROCESS_CTRL /*&& orderNumber.equals(mOrderCtrlResultVo.getOrderNumber())*/
                    && orderName.equals(mOrderCtrlResultVo.getOrderName())){
                result = mOrderCtrlResultVo.getResult();
                resultReason = mOrderCtrlResultVo.getReason();
                setDisposeOrderCtrlResult(true);
                return true;
            }
        }
        return super.disposeOrderCtrlResult(mBaseVo);
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
