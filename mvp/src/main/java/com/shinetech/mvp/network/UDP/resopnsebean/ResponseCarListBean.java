package com.shinetech.mvp.network.UDP.resopnsebean;

import com.shinetech.mvp.network.UDP.DatagramPacketDefine;
import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.item.CarBaseInfoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017/2/6.
 */
public class ResponseCarListBean extends BaseVo {

    // - - - - - - - - - - - - - - - request - - - - - - - - - - - - - - -

    /**
     * 操作码
     */
    private int operationCode;

    /**
     * 操作信息
     */
    private String operationInfo;

    /**
     * 结果协议字
     */
    private short resultProtocol;

    // - - - - - - - - - - - - - - - response - - - - - - - - - - - - - - -

    //0x0008
    /**
     * 列表数量
     */
    private int carlistSize = -1;

    /**
     * 计数器（计数接收数据包数量）
     */
    private int timerCount = 0;

    // data
    List<ResponseCarBaseInfoBean> responseCarBaseInfoList;

    public ResponseCarListBean(String operationInfo) {
        requestProtocolHead = Protocol.RESPONSE_PROTOCOL_CARLIST_BEAT;
        responseProtocolHead = Protocol.RESPONSE_PROTOCOL_CARLIST_BEAT_RESULT;
        resultProtocol = Protocol.RESPONSE_PROTOCOL_CARLIST_BEAT_RESULT;
        operationCode = DatagramPacketDefine.OPERATION_CODE_CARLIST;
        this.operationInfo = operationInfo;
    }

    @Override
    public Object[] getProperties() {return new Object[]{operationCode,operationInfo,resultProtocol};
    }

    @Override
    public void setProperties(Object[] properties) {
    }

    @Override
    public short[] getDataTypes() {
        return new short[]{};
    }

    @Override
    public short[] getElementDataTypes(int index) {
        return new short[]{};
    }

    @Override
    public void addListElement(Object[] elementProperties, int index) {
    }

    public int getCarlistSize() {
        return carlistSize;
    }

    public void setCarlistSize(int carlistSize) {
        this.carlistSize = carlistSize;
    }

    public List<ResponseCarBaseInfoBean> getResponseCarBaseInfoList() {
        return responseCarBaseInfoList;
    }

    public void addResponseCarBaseInfoBean(ResponseCarBaseInfoBean mResponseCarBaseInfoBean){
        if(this.responseCarBaseInfoList == null){
            this.responseCarBaseInfoList = new ArrayList<>();
        }

        if(!responseCarBaseInfoList.contains(mResponseCarBaseInfoBean)) {
            responseCarBaseInfoList.add(mResponseCarBaseInfoBean);
        }
    }

    public boolean isEnd(){

        if((carlistSize > 0) && (responseCarBaseInfoList != null) && (carlistSize == responseCarBaseInfoList.size())){
            return true;
        }

        return false;

    }

    public String getOperationInfo() {
        return operationInfo;
    }

    @Override
    public String toString() {
        if(responseCarBaseInfoList == null){
            return super.toString();
        }else{

            for(ResponseCarBaseInfoBean bean : responseCarBaseInfoList){
                System.out.println(" <    ResponseCarListBean    >   : "+bean.toString());
            }
            return super.toString();

        }
    }
}
