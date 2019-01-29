package com.shinetech.mvp.network.UDP.bean;

import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.item.BaseCarInfoResult;

/**
 * Created by ljn on 2017/2/17.
 * 查询指定经纬度范围内车辆信息
 */
public class QueryDesignatedAreaCarInfo extends BaseCarInfoResult{

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

    public QueryDesignatedAreaCarInfo(int minLng, int minLat, int maxLng, int maxLat) {
        this.minLng = minLng;
        this.minLat = minLat;
        this.maxLng = maxLng;
        this.maxLat = maxLat;
        requestProtocolHead = Protocol.PROTOCOL_QDACARINFO_BEAT;
        responseProtocolHead = Protocol.PROTOCOL_COMPANYALLCARINFO_BEAT_RESULT;
    }

    @Override
    public Object[] getProperties() {
        return new Object[]{minLng, minLat, maxLng, maxLat};
    }
}
