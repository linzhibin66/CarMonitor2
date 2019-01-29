package com.shinetech.mvp.network.UDP.resopnsebean;

import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.item.CarBaseInfoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017/2/9.
 * 查询车辆基本信息
 */
public class ResponseCarBaseInfoBean extends BaseVo{

    // - - - - - - - - - - - - - - - request - - - - - - - - - - - - - - -
    /**
     * 请求的车牌号
     */
    private String platenumber;

    // - - - - - - - - - - - - - - - response - - - - - - - - - - - - - - -

    /**
     * 操作码
     */
    private int operationCode;

    /**
     * 返回的车牌号
     */
    private String resultPlateNumber;

    /**
     * 车辆类型
     */
    private String carType;

    /**
     * 所属公司
     */
    private String subordinateCompanies;

    /**
     * 所属分组
     */
    private String subordinateGroup;

    /**
     * 返回的车辆信息项数
     */
    private byte infoSize;

    /**
     * 返回的车辆信息项列表
     */
    private List<CarBaseInfoItem> resultCarInfoItem;

    public ResponseCarBaseInfoBean(String platenumber) {
        this.platenumber = platenumber;
        requestProtocolHead = Protocol.RESPONSE_PROTOCOL_CARINFO_BEAT;
        responseProtocolHead = Protocol.RESPONSE_PROTOCOL_CARINFO_BEAT_RESULT;
    }

    public ResponseCarBaseInfoBean(){

    }

    @Override
    public Object[] getProperties() {
        return new Object[]{platenumber};
    }

    @Override
    public void setProperties(Object[] properties) {
        operationCode = (int)properties[0];
        resultPlateNumber =  parseString((byte[]) properties[1]);
        carType = parseString((byte[]) properties[2]);
        subordinateCompanies = parseString((byte[]) properties[3]);
        subordinateGroup = parseString((byte[]) properties[4]);
        infoSize = (byte) properties[5];
    }

    @Override
    public short[] getDataTypes() {
        return new short[]{INT,STRING,STRING,STRING,STRING,BYTE,LIST};
    }

    @Override
    public short[] getElementDataTypes(int index) {
        return new short[]{STRING,STRING};
    }

    @Override
    public void addListElement(Object[] elementProperties, int index) {
        CarBaseInfoItem carInfoItem = new CarBaseInfoItem();
        carInfoItem.setInfoName(parseString((byte[]) elementProperties[0]));
        carInfoItem.setInfoContent(parseString((byte[]) elementProperties[1]));

        if(resultCarInfoItem == null){
            resultCarInfoItem = new ArrayList<>();
        }
        if(!resultCarInfoItem.contains(carInfoItem)){
            resultCarInfoItem.add(carInfoItem);
        }
    }

    public String getResultPlateNumber() {
        return resultPlateNumber;
    }

    public void setResultPlateNumber(String resultPlateNumber) {
        this.resultPlateNumber = resultPlateNumber;
    }

    public List<CarBaseInfoItem> getResultCarInfoItem() {
        return resultCarInfoItem;
    }

    public void setResultCarInfoItem(List<CarBaseInfoItem> resultCarInfoItem) {
        this.resultCarInfoItem = resultCarInfoItem;
    }

    public int getOperationCode() {
        return operationCode;
    }

    public void setOperationCode(int operationCode) {
        this.operationCode = operationCode;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getSubordinateCompanies() {
        return subordinateCompanies;
    }

    public void setSubordinateCompanies(String subordinateCompanies) {
        this.subordinateCompanies = subordinateCompanies;
    }

    public String getSubordinateGroup() {
        return subordinateGroup;
    }

    public void setSubordinateGroup(String subordinateGroup) {
        this.subordinateGroup = subordinateGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResponseCarBaseInfoBean that = (ResponseCarBaseInfoBean) o;

        if (getResultPlateNumber() != null ? !getResultPlateNumber().equals(that.getResultPlateNumber()) : that.getResultPlateNumber() != null)
            return false;
        if (getCarType() != null ? !getCarType().equals(that.getCarType()) : that.getCarType() != null)
            return false;
        if (getSubordinateCompanies() != null ? !getSubordinateCompanies().equals(that.getSubordinateCompanies()) : that.getSubordinateCompanies() != null)
            return false;
        return !(getSubordinateGroup() != null ? !getSubordinateGroup().equals(that.getSubordinateGroup()) : that.getSubordinateGroup() != null);

    }

    @Override
    public int hashCode() {
        int result = getResultPlateNumber() != null ? getResultPlateNumber().hashCode() : 0;
        result = 31 * result + (getCarType() != null ? getCarType().hashCode() : 0);
        result = 31 * result + (getSubordinateCompanies() != null ? getSubordinateCompanies().hashCode() : 0);
        result = 31 * result + (getSubordinateGroup() != null ? getSubordinateGroup().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String itemString = "";
        if(resultCarInfoItem != null)
        for(CarBaseInfoItem item : resultCarInfoItem){
            itemString = item.toString()+";";
        }
        return "ResponseCarBaseInfoBean{" +
                "resultPlateNumber='" + resultPlateNumber + '\'' +
                ", operationCode=" + operationCode +
                ", carType='" + carType + '\'' +
                ", subordinateCompanies='" + subordinateCompanies + '\'' +
                ", subordinateGroup='" + subordinateGroup + '\'' +
                ", resultCarInfoItem=" + itemString +
                '}';
    }
}
