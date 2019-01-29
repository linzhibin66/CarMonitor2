package com.shinetech.mvp.network.UDP.bean;

import com.shinetech.mvp.network.UDP.InfoTool.Protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017/2/6.
 */
public class CarListBean extends BaseVo {

    /**
     * 车辆数量
     */
    private byte carTotal;

    private List<String> carList;

    public CarListBean() {
        //
        requestProtocolHead = Protocol.PROTOCOL_CARLIST_BEAT;
        responseProtocolHead = Protocol.PROTOCOL_CARLIST_BEAT_RESULT;
    }

    @Override
    public Object[] getProperties() {
        return null;
    }

    @Override
    public void setProperties(Object[] properties) {
        carTotal = (byte) properties[0];
    }

    @Override
    public short[] getDataTypes() {
        return new short[]{BYTE, LIST};
    }

    @Override
    public short[] getElementDataTypes(int index) {
        return new short[]{STRING};
    }

    @Override
    public void addListElement(Object[] elementProperties, int index) {
        if(carList == null){
            carList = new ArrayList<>();
        }
        String item = parseString((byte[]) elementProperties[0]);

        if(!carList.contains(item)){
            carList.add(item);
        }

    }

    @Override
    public String toString() {
        String mstring = "carTotal : = "+carTotal+" carList : ";
        if(carList!=null){
            for(String s : carList){
                mstring += s+"; ";
            }
        }
        return mstring;
    }

    public List<String> getCarList() {
        return carList;
    }

    public void setCarList(List<String> carList) {
        this.carList = carList;
    }
}
