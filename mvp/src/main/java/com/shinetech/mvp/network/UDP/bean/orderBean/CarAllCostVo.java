package com.shinetech.mvp.network.UDP.bean.orderBean;

import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.BaseVo;

/**
 * Created by ljn on 2017-12-27.
 */

public class CarAllCostVo extends BaseVo{

    /**
     * 结果 总费用车牌
     */
    private String resultPlateNumber;

    /**
     * 结果 总费用车架号
     */
    private String vin;

    /**
     * 总欠费
     */
    private int allArrearage;

    /**
     * 欠费月份
     */
    private int arrearageMonth;

    private int carCostVoCount;

    @Override
    public Object[] getProperties() {
        return new Object[0];
    }

    public short getResultProtocol(){
        return Protocol.RESPONSE_PROTOCOL_CARCOST_INFO_BEAT;
    }

    @Override
    public void setProperties(Object[] properties) {
        this.resultPlateNumber = parseString((byte[]) properties[0]);
        this.vin = parseString((byte[]) properties[1]);
        this.allArrearage = (int) properties[2];
        this.arrearageMonth = (int) properties[3];
        this.carCostVoCount = (int) properties[4];

    }

    @Override
    public short[] getDataTypes() {
        return new short[]{STRING, STRING, INT, INT, INT};
    }

    public int getAllArrearage() {
        return allArrearage;
    }

    public void setAllArrearage(int allArrearage) {
        this.allArrearage = allArrearage;
    }

    public int getArrearageMonth() {
        return arrearageMonth;
    }

    public void setArrearageMonth(int arrearageMonth) {
        this.arrearageMonth = arrearageMonth;
    }

    public String getResultPlateNumber() {
        return resultPlateNumber;
    }

    public int getCarCostVoCount() {
        return carCostVoCount;
    }

    public void setResultPlateNumber(String resultPlateNumber) {
        this.resultPlateNumber = resultPlateNumber;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }
}
