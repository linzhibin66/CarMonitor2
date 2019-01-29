package com.shinetech.mvp.network.UDP.bean.item;

import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.BaseVo;

/**
 * Created by ljn on 2017/2/14.
 */
public class HeartBeatBean extends BaseVo{

    public HeartBeatBean() {
        if(MainApplication.isUserResponseUDP){
            requestProtocolHead = Protocol.RESPONSE_PROTOCOL_HEART_BEAT;
            responseProtocolHead = Protocol.RESPONSE_PROTOCOL_HEART_BEAT;
        }else {
            requestProtocolHead = Protocol.PROTOCOL_HEART_BEAT;
            responseProtocolHead = Protocol.PROTOCOL_HEART_BEAT;
        }

    }

    @Override
    public Object[] getProperties() {
        return null;
    }

    @Override
    public void setProperties(Object[] properties) {

    }

    @Override
    public short[] getDataTypes() {
        return new short[0];
    }
}
