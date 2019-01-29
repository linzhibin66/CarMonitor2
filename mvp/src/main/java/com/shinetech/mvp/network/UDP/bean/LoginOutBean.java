package com.shinetech.mvp.network.UDP.bean;

import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.network.UDP.InfoTool.Protocol;

/**
 * Created by ljn on 2017/2/8.
 */
public class LoginOutBean extends BaseVo {

    public LoginOutBean() {
        if(MainApplication.isUserResponseUDP){
            requestProtocolHead = Protocol.RESPONSE_LOGIN_OUT_BEAT;
            responseProtocolHead = Protocol.RESPONSE_LOGIN_OUT_BEAT;
        }else{
            requestProtocolHead = Protocol.LOGIN_OUT;
            responseProtocolHead = Protocol.LOGIN_OUT;
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
