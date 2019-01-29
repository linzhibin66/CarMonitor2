package com.shinetech.mvp.network.UDP.resopnsebean;

import com.shinetech.mvp.network.UDP.bean.BaseVo;

/**
 * Created by ljn on 2017-07-24.
 */
public class ListCountBean extends BaseVo{

    /**
     * 操作码
     */
    private int operationCode;

    /**
     * 操作信息
     */
    private String operationInfo;

    /**
     * 结果协议字
     */
    private short resultProtocol;

    private  int count;

    @Override
    public Object[] getProperties() {
        return new Object[0];
    }

    @Override
    public void setProperties(Object[] properties) {
        operationCode = (int) properties[0];
        operationInfo = parseString((byte[]) properties[1]);
        resultProtocol = (short) properties[2];
        count = (int) properties[3];

    }

    @Override
    public short[] getDataTypes() {
        return new short[]{INT, STRING, SHORT, INT};
    }

    public int getOperationCode() {
        return operationCode;
    }

    public void setOperationCode(int operationCode) {
        this.operationCode = operationCode;
    }

    public String getOperationInfo() {
        return operationInfo;
    }

    public void setOperationInfo(String operationInfo) {
        this.operationInfo = operationInfo;
    }

    public short getResultProtocol() {
        return resultProtocol;
    }

    public void setResultProtocol(short resultProtocol) {
        this.resultProtocol = resultProtocol;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
