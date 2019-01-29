package com.shinetech.mvp.network.UDP.bean;

import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.item.TrafficStatusItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017/2/14.
 * 实时路况查询
 */
public class TrafficStatus extends BaseVo{

    /**
     * 最小经度
     */
    private int minLng;

    /**
     * 最小纬度
     */
    private int minLat;

    /**
     * 最大经度
     */
    private int maxLng;

    /**
     * 最大纬度
     */
    private int maxLat;


    /***********************************  result  ********************************************/

    /**
     * 路况数
     */
    private byte trafficStatusSize;

    private List<TrafficStatusItem> trafficStatusItemList;

    public TrafficStatus(int minLng, int minLat, int maxLng, int maxLat) {
        this.minLng = minLng;
        this.minLat = minLat;
        this.maxLng = maxLng;
        this.maxLat = maxLat;
        requestProtocolHead = Protocol.PROTOCOL_TRAFFICSTATUS_BEAT;
        responseProtocolHead = Protocol.PROTOCOL_TRAFFICSTATUS_BEAT_RESULT;
    }

    @Override
    public Object[] getProperties() {
        return new Object[]{minLng, minLat, maxLng, maxLat};
    }

    @Override
    public void setProperties(Object[] properties) {
        trafficStatusSize = (byte) properties[0];
    }

    @Override
    public short[] getDataTypes() {
        return new short[]{BYTE, LIST};
    }

    @Override
    public void addListElement(Object[] elementProperties, int index) {

        TrafficStatusItem trafficStatusItem = new TrafficStatusItem();
        trafficStatusItem.setRoadSectionID((Short) elementProperties[0]);
        trafficStatusItem.setStartLng((Integer) elementProperties[1]);
        trafficStatusItem.setStartLat((Integer) elementProperties[2]);
        trafficStatusItem.setEndLng((Integer) elementProperties[3]);
        trafficStatusItem.setEndLat((Integer) elementProperties[4]);
        trafficStatusItem.setAverageSpeed((Byte) elementProperties[5]);
        trafficStatusItem.setEventMessage(parseString((byte[]) elementProperties[6]));

        if(trafficStatusItemList == null){
            trafficStatusItemList = new ArrayList<>();
        }

        if(!trafficStatusItemList.contains(trafficStatusItem)){
            trafficStatusItemList.add(trafficStatusItem);
        }
    }

    @Override
    public short[] getElementDataTypes(int index) {
        return new short[]{SHORT, INT, INT, INT, INT,BYTE, STRING};
    }
}
