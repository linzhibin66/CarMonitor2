package com.shinetech.mvp.network.UDP.bean;

import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.item.CarBaseInfoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017/2/9.
 * 查询车辆基本信息
 */
public class CarBaseInfoBean extends BaseVo{

    /**
     * 请求的车牌号
     */
    private String platenumber;

    /**
     * 返回的车牌号
     */
    private String resultPlateNumber;

    /**
     * 返回的车辆信息条数
     */
    private byte infoSize;

    /**
     * 返回的车辆信息
     */
    private List<CarBaseInfoItem> resultCarInfoItem;

    public CarBaseInfoBean(String platenumber) {
        this.platenumber = platenumber;
        requestProtocolHead = Protocol.PROTOCOL_CARINFO_BEAT;
        responseProtocolHead = Protocol.PROTOCOL_CARINFO_BEAT_RESULT;
    }

    @Override
    public Object[] getProperties() {
        return new Object[]{platenumber};
    }

    @Override
    public void setProperties(Object[] properties) {
        resultPlateNumber =  parseString((byte[]) properties[0]);
        infoSize = (byte) properties[1];
    }

    @Override
    public short[] getDataTypes() {
        return new short[]{STRING,BYTE,LIST};
    }

    @Override
    public short[] getElementDataTypes(int index) {
        return new short[]{STRING,STRING};
    }

    @Override
    public void addListElement(Object[] elementProperties, int index) {
        CarBaseInfoItem carInfoItem = new CarBaseInfoItem();
        carInfoItem.setInfoName(parseString((byte[]) elementProperties[0]));
        carInfoItem.setInfoContent(parseString((byte[]) elementProperties[1]));

        if(resultCarInfoItem == null){
            resultCarInfoItem = new ArrayList<>();
        }
        if(!resultCarInfoItem.contains(carInfoItem)){
            resultCarInfoItem.add(carInfoItem);
        }
    }

    @Override
    public String toString() {
        String tostring = "";
        for(CarBaseInfoItem item: resultCarInfoItem){
            tostring+=item.getInfoName()+" : "+item.getInfoContent()+"  ;  ";
        }

        return tostring;
    }

    public String getResultPlateNumber() {
        return resultPlateNumber;
    }

    public void setResultPlateNumber(String resultPlateNumber) {
        this.resultPlateNumber = resultPlateNumber;
    }

    public List<CarBaseInfoItem> getResultCarInfoItem() {
        return resultCarInfoItem;
    }

    public void setResultCarInfoItem(List<CarBaseInfoItem> resultCarInfoItem) {
        this.resultCarInfoItem = resultCarInfoItem;
    }
}
