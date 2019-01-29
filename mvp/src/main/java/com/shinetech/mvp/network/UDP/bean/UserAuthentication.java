package com.shinetech.mvp.network.UDP.bean;

import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.network.UDP.InfoTool.Protocol;

import java.io.UnsupportedEncodingException;

/**
 * Created by ljn on 2017/2/8.
 */
public class UserAuthentication extends BaseVo{

    /**
     * 用户类型
     */
    private byte loginType;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 登录凭证
     */
    private byte[] token;

    /**
     * 获取凭证结果
     */
    private byte tokenResult;

    /**
     * 服务器返回用户类型
     */
    private byte resultLoginType;

    /**
     * 服务器返回用户名
     */
    private byte[] resultUserName;

    public UserAuthentication(byte loginType, String userName) {
        this.loginType = loginType;
        this.userName = userName;
        if(MainApplication.isUserResponseUDP){
            requestProtocolHead = Protocol.RESPONSE_LOGIN_TOKEN_BEAT;
            responseProtocolHead = Protocol.RESPONSE_LOGIN_TOKEN_RESULT;
        }else{
            requestProtocolHead = Protocol.LOGIN_TOKEN_BEAT;
            responseProtocolHead = Protocol.LOGIN_TOKEN_RESULT;

        }
    }

    @Override
    public Object[] getProperties() {
        return new Object[]{loginType, userName};
    }

    @Override
    public void setProperties(Object[] properties) {
        this.resultLoginType = (byte) properties[0];
        this.resultUserName = (byte[]) properties[1];
        this.tokenResult = (byte) properties[2];
        this.token = (byte[]) properties[3];

    }

    @Override
    public short[] getDataTypes() {
        return new short[]{BYTE,STRING,BYTE,STRING};
    }

    public byte getLoginType() {
        return loginType;
    }

    public void setLoginType(byte loginType) {
        this.loginType = loginType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public byte getTokenResult() {
        return tokenResult;
    }

    public void setTokenResult(byte tokenResult) {
        this.tokenResult = tokenResult;
    }

    public byte getResultLoginType() {
        return resultLoginType;
    }

    public void setResultLoginType(byte resultLoginType) {
        this.resultLoginType = resultLoginType;
    }

    public byte[] getResultUserName() {
        return resultUserName;
    }

    public void setResultUserName(String resultUserName) {
        try {
            this.resultUserName = resultUserName.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public byte[] getToken() {
        return token;
    }
}
