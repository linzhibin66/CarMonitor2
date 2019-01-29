package com.shinetech.mvp.network.UDP.bean;

import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.item.CarHistoricalRouteItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017/2/10.
 */
public class CarHistoricalRouteBean extends BaseVo{

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

    /**
     * 返回车牌号
     */
    private String resultPlateNumber;

    /**
     * 总包数
     */
    private byte packageSize;

    /**
     * 包序号
     */
    private byte packagePosition;

    /**
     * 轨迹点数量
     */
    private byte historicalCount;

    /**
     * 轨迹信息列表
     */
    private List<CarHistoricalRouteItem> resultHistoricalInfo;


    public CarHistoricalRouteBean(String startTime, String endTime, String platenumber) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.platenumber = platenumber;
        requestProtocolHead = Protocol.PROTOCOL_CARHISTORICALROUTE_BEAT;
        responseProtocolHead = Protocol.PROTOCOL_CARHISTORICALROUTE_BEAT_RESULT;
    }

    @Override
    public Object[] getProperties() {
        return new Object[]{platenumber,startTime,endTime};
    }

    @Override
    public void setProperties(Object[] properties) {
        this.resultPlateNumber = parseString((byte[]) properties[0]);
        this.packageSize = (byte) properties[1];
        this.packagePosition = (byte) properties[2];
        this.historicalCount = (byte) properties[3];
    }

    @Override
    public short[] getDataTypes() {
        return new short[]{STRING, BYTE, BYTE, BYTE,LIST};
    }

    @Override
    public void addListElement(Object[] elementProperties, int index) {

        CarHistoricalRouteItem carHistoricalRouteItem = new CarHistoricalRouteItem();
        carHistoricalRouteItem.setLocationTime(parseString((byte[]) elementProperties[0]));
        carHistoricalRouteItem.setLng((Integer) elementProperties[1]);
        carHistoricalRouteItem.setLat((Integer) elementProperties[2]);
        carHistoricalRouteItem.setmGNSSSpeed((Byte) elementProperties[3]);
        carHistoricalRouteItem.setOrientation((Short) elementProperties[4]);

        if(resultHistoricalInfo==null){
            resultHistoricalInfo = new ArrayList<>();
        }
        if(!resultHistoricalInfo.contains(carHistoricalRouteItem)){
            resultHistoricalInfo.add(carHistoricalRouteItem);
        }

    }

    @Override
    public short[] getElementDataTypes(int index) {
        return new short[]{STRING, INT, INT, BYTE, SHORT};
    }

    public boolean isend(){
        return packagePosition==packageSize;
    }

    @Override
    public String toString() {

        if(resultHistoricalInfo == null){
            return super.toString();
        }

        for(CarHistoricalRouteItem item : resultHistoricalInfo){
            System.out.println(item.toString());
        }

        System.out.println("historicalCount : "+ historicalCount + "  CarHistoricalRouteItem size : "+ resultHistoricalInfo.size());

        return super.toString();

    }

    public List<CarHistoricalRouteItem> getHistoriCarInfo(){

        if(resultHistoricalInfo == null){
            resultHistoricalInfo = new ArrayList<>();
        }

        return resultHistoricalInfo;
    }
}
