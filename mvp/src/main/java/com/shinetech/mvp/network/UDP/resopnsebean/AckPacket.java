package com.shinetech.mvp.network.UDP.resopnsebean;

import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.BaseVo;

/**
 * Created by ljn on 2017-07-25.
 */
public class AckPacket extends BaseVo{


    public AckPacket() {
        requestProtocolHead = Protocol.RESPONSE_ACK_BEAT;
        responseProtocolHead = Protocol.RESPONSE_ACK_BEAT;
    }

    @Override
    public Object[] getProperties() {
        return new Object[0];
    }

    @Override
    public void setProperties(Object[] properties) {

    }

    @Override
    public short[] getDataTypes() {
        return new short[0];
    }
}
