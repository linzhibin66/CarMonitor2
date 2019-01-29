package com.shinetech.mvp.network.UDP.bean.item;

import android.text.TextUtils;

import com.shinetech.mvp.network.UDP.bean.BaseVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017/2/17.
 */
public abstract class BaseCarInfoResult extends BaseVo{

    private String resultCompanyName;

    /**
     * 总包数
     */
    private byte packageSize;

    /**
     * 包序号
     */
    private byte packageNumber;

    private List<CarInfoBean> carInfoList;



    @Override
    public void setProperties(Object[] properties) {
        resultCompanyName = parseString((byte[]) properties[0]);
        packageSize = (byte) properties[1];
        packageNumber = (byte) properties[2];

    }

    @Override
    public short[] getDataTypes() {
        return new short[]{STRING, BYTE, BYTE,LIST};
    }

    @Override
    public void addListElement(Object[] elementProperties, int index) {
        CarInfoBean carInfoBean = new CarInfoBean();

        String plateNumber = parseString((byte[]) elementProperties[0]);

        if(TextUtils.isEmpty(plateNumber))
            return;

        carInfoBean.setPlateNumber(plateNumber);
        carInfoBean.setLocationTime(parseString((byte[]) elementProperties[1]));
        carInfoBean.setLng((Integer) elementProperties[2]);
        carInfoBean.setLat((Integer) elementProperties[3]);
        carInfoBean.setSpeed((Byte) elementProperties[4]);
        carInfoBean.setgNSSSpeed((Byte) elementProperties[5]);
        carInfoBean.setOrientation((Short) elementProperties[6]);
        carInfoBean.setAltitude((Short) elementProperties[7]);
        carInfoBean.setMileage((Integer) elementProperties[8]);
        carInfoBean.setOilMass((Short) elementProperties[9]);
        carInfoBean.setStatus((Integer) elementProperties[10]);
        carInfoBean.setAlarmType((Integer) elementProperties[11]);

        if(carInfoList == null){
            carInfoList = new ArrayList<>();
        }

        if(!carInfoList.contains(carInfoBean)){
            carInfoList.add(carInfoBean);
        }

    }

    public List<CarInfoBean> getCarInfoList() {
        return carInfoList;
    }

    public void setCarInfoList(List<CarInfoBean> carInfoList) {
        if(this.carInfoList==null){
            this.carInfoList = new ArrayList<>();
        }
        this.carInfoList.clear();
        this.carInfoList.addAll(carInfoList);
    }

    public String getResultCompanyName() {
        return resultCompanyName;
    }

    public void setResultCompanyName(String resultCompanyName) {
        this.resultCompanyName = resultCompanyName;
    }

    public byte getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(byte packageSize) {
        this.packageSize = packageSize;
    }

    public byte getPackageNumber() {
        return packageNumber;
    }

    public void setPackageNumber(byte packageNumber) {
        this.packageNumber = packageNumber;
    }

    @Override
    public short[] getElementDataTypes(int index) {
        return new short[]{STRING, STRING, INT, INT, BYTE, BYTE, SHORT, SHORT, INT, SHORT, INT, INT};
    }

    @Override
    public String toString() {

        for(CarInfoBean mCarInfoBean : carInfoList){
            System.out.println(mCarInfoBean.toString());
        }

        return "BaseCarInfoResult{" +
                "resultCompanyName='" + resultCompanyName + '\'' +
                ", packageSize=" + packageSize +
                ", packageNumber=" + packageNumber +
                '}';
    }

    public boolean isEnd(){
        if(packageSize == packageNumber){
            return true;
        }
        return false;
    }
}
