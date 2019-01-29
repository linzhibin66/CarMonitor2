package com.shinetech.mvp.network.UDP.resopnsebean;

import com.shinetech.mvp.network.UDP.DatagramPacketDefine;
import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.BaseVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-07-25.
 */
public class ResponseCarHistoricalRouteBean extends BaseVo{


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

    /**
     * 车牌号
     */
    private String platenumber;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;


    /******************************* result ***************************************/

    //0x0008
    /**
     * 列表数量
     */
    private int carlistSize = -1;

    /**
     * 轨迹信息列表
     */
    private List<ResponseCarHistoricalRouteItem> resultHistoricalInfo;

    public ResponseCarHistoricalRouteBean(String operationInfo, String startTime, String endTime, String platenumber) {
        requestProtocolHead = Protocol.RESPONSE_PROTOCOL_CARHISTORICALROUTE_BEAT;
        responseProtocolHead = Protocol.RESPONSE_PROTOCOL_CARHISTORICALROUTE_BEAT_RESULT;
        resultProtocol = Protocol.RESPONSE_PROTOCOL_CARHISTORICALROUTE_BEAT_RESULT;
        operationCode = DatagramPacketDefine.OPERATION_CODE_HISTORICAL_ROUTE_;
        this.startTime = startTime;
        this.endTime = endTime;
        this.platenumber = platenumber;
        this.operationInfo = operationInfo;
    }

    @Override
    public Object[] getProperties() {return new Object[]{operationCode,operationInfo,resultProtocol,platenumber,startTime,endTime};
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

    public int getOperationCode() {
        return operationCode;
    }

    public String getOperationInfo() {
        return operationInfo;
    }

    public int getCarlistSize() {
        return carlistSize;
    }

    public void setCarlistSize(int carlistSize) {
        this.carlistSize = carlistSize;
    }

    public List<ResponseCarHistoricalRouteItem> getResultHistoricalInfo() {
        return resultHistoricalInfo;
    }

    public void addToResultHistoricalInfo(ResponseCarHistoricalRouteItem resultHistoricalInfoItem) {
        if(this.resultHistoricalInfo == null){
            this.resultHistoricalInfo = new ArrayList<>();
        }

        if(!resultHistoricalInfo.contains(resultHistoricalInfoItem)){
            resultHistoricalInfo.add(resultHistoricalInfoItem);
        }

    }

    public boolean isEnd(){
        if((carlistSize > 0) && (resultHistoricalInfo != null) && (carlistSize == resultHistoricalInfo.size())){
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        if(resultHistoricalInfo != null) {
            for (ResponseCarHistoricalRouteItem item : resultHistoricalInfo) {
                System.out.println(item.toString());
            }
        }

        return "ResponseCarHistoricalRouteBean{" +
                "platenumber='" + platenumber + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", operationInfo='" + operationInfo + '\'' +
                ", operationCode=" + operationCode +
                ", resultProtocol=" + resultProtocol +
                ", carlistSize=" + carlistSize +
                '}';
    }
}
