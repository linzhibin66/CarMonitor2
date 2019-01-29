package com.shinetech.mvp.network.UDP.bean;

import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.network.UDP.InfoTool.Protocol;

/**
 * Created by ljn on 2017/2/8.
 */
public class LoginBean extends BaseVo {

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
    private String loginToken;

    /**
     * 登录结果
     */
    private byte loginResult;

    /**
     * 返回用户名
     */
    private String resultUserName;

    /**
     * 错误信息，成功时为空串。
     */
    private String error;

    /**
     * 返回用户类型
     */
    private byte resultLoginType;

    private byte userPermission;

    public LoginBean(byte loginType, String userName, String loginToken) {
        this.loginType = loginType;
        this.userName = userName;
        this.loginToken = loginToken;
        if(MainApplication.isUserResponseUDP) {
            requestProtocolHead = Protocol.RESPONSE_LOGIN_BEAT;
            responseProtocolHead = Protocol.RESPONSE_LOGIN_RESULT;
        }else{
            requestProtocolHead = Protocol.LOGIN_BEAT;
            responseProtocolHead = Protocol.LOGIN_RESULT;

        }
    }

    @Override
    public Object[] getProperties() {
        return new Object[]{loginType,userName,loginToken};
    }

    @Override
    public void setProperties(Object[] properties) {
        this.resultLoginType = (byte) properties[0];
        this.resultUserName = parseString((byte[]) properties[1]);
        this.loginResult = (byte) properties[2];
        this.error = parseString((byte[]) properties[3]);
        this.userPermission = (byte) properties[4];
        System.out.println("loginResult : "+loginResult + " error + "+error!=null?error:"null");

    }

    @Override
    public short[] getDataTypes() {
        return new short[]{BYTE, STRING, BYTE, STRING, BYTE};
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

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    public byte getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(byte loginResult) {
        this.loginResult = loginResult;
    }

    public String getResultUserName() {
        return resultUserName;
    }

    public void setResultUserName(String resultUserName) {
        this.resultUserName = resultUserName;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public byte getResultLoginType() {
        return resultLoginType;
    }

    public void setResultLoginType(byte resultLoginType) {
        this.resultLoginType = resultLoginType;
    }

    public byte getUserPermission() {
        return userPermission;
    }

    public void setUserPermission(byte userPermission) {
        this.userPermission = userPermission;
    }
}
