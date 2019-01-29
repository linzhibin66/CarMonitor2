package com.shinetech.mvp.network.UDP.bean.orderBean;

import com.shinetech.mvp.network.UDP.DatagramPacketDefine;
import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.BaseVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2017-12-27.
 */

public class CarCostVo extends BaseVo {

    /**
     * 车牌
     */
    private String plateNumber;

    /**************************************** result  ******************************************************/

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
    private int allArrearage = -1;

    /**
     * 欠费月份
     */
    private int arrearageMonth = -1;

    private int carCostCount = 0;

    //0x0030
    /**
     * 项目明细
     */
    private List<CarCostItemInfoVo> carCostItemInfoList;

    public CarCostVo(String plateNumber) {
        requestProtocolHead = Protocol.RESPONSE_PROTOCOL_SELECT_CARCOST_BEAT;
        responseProtocolHead = Protocol.RESPONSE_PROTOCOL_CARCOST_INFO_BEAT;

        this.plateNumber = plateNumber;
    }

    @Override
    public Object[] getProperties() {
        return new Object[]{plateNumber};
    }

    @Override
    public void setProperties(Object[] properties) {

    }

    @Override
    public short[] getDataTypes() {
        return new short[]{};
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
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

    public int getCarCostCount() {
        return carCostCount;
    }

    public void setCarCostCount(int carCostCount) {
        this.carCostCount = carCostCount;
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

    public List<CarCostItemInfoVo> getCarCostItems(){
        return carCostItemInfoList;
    }

    public void addCarCostItem(CarCostItemInfoVo mCarCostItemInfoVo){

        if(this.carCostItemInfoList == null){
            carCostItemInfoList = new ArrayList<>();
        }

        if(!carCostItemInfoList.contains(mCarCostItemInfoVo)){
            carCostItemInfoList.add(mCarCostItemInfoVo);
        }
    }
}
