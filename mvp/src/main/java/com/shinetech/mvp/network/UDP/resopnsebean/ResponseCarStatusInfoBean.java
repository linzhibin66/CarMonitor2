package com.shinetech.mvp.network.UDP.resopnsebean;

import com.shinetech.mvp.DB.bean.CarInfoDB;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-07-25.
 */
public class ResponseCarStatusInfoBean extends BaseVo{


    // - - - - - - - - - - - - - - - request - - - - - - - - - - - - - - -
    /**
     * 请求的车牌号
     */
    private String platenumber;

    // - - - - - - - - - - - - - - - response - - - - - - - - - - - - - - -

    /**
     * 返回的车牌号
     */
    private String resultPlateNumber;

    /**
     * 定位时间
     */
    private String locationTime;

    /**
     * 经度
     */
    private int resultlng;

    /**
     * 纬度
     */
    private int resultlat;

    /**
     * 原车速度
     */
    private byte resultSpeed;

    /**
     * GNSS 速度
     */
    private byte resultGNSSSpeed;

    /**
     * 方向
     */
    private short resultOrientation;

    /**
     * 高度
     */
    private short resultAltitude;

    /**
     * 里程
     */
    private int resultMileage;

    /**
     * 油量
     */
    private short resultOilMass;

    /**
     * 状态
     */
    private int resultStatus;

    /**
     * 报警标志
     */
    private int resultAlarmType;

    /**
     * 违规数量
     */
    private byte violationCount;

    /**
     * 违规列表
     */
    private List<String> violationList;


    public ResponseCarStatusInfoBean(String platenumber) {
        this.platenumber = platenumber;
        requestProtocolHead = Protocol.RESPONSE_PROTOCOL_CARSTATUS_BEAT;
        responseProtocolHead = Protocol.RESPONSE_PROTOCOL_CARSTATUS_BEAT_RESULT;
    }

    @Override
    public Object[] getProperties() {
        return new Object[]{platenumber};
    }

    @Override
    public void setProperties(Object[] properties) {
        this.resultPlateNumber = parseString((byte[]) properties[0]);
        this.locationTime = parseString((byte[]) properties[1]);
        this.resultlng = (int) properties[2];
        this.resultlat = (int) properties[3];
        this.resultSpeed = (byte) properties[4];
        this.resultGNSSSpeed = (byte) properties[5];
        this.resultOrientation = (short) properties[6];
        this.resultAltitude = (short) properties[7];
        this.resultMileage = (int) properties[8];
        this.resultOilMass = (short) properties[9];
        this.resultStatus = (int) properties[10];
        this.resultAlarmType = (int) properties[11];
        this.violationCount = (byte) properties[12];

    }

    @Override
    public short[] getDataTypes() {
        return new short[]{STRING, STRING, INT, INT, BYTE, BYTE, SHORT, SHORT, INT, SHORT, INT, INT, BYTE, LIST};
    }

    @Override
    public short[] getElementDataTypes(int index) {
        return new short[]{STRING};
    }

    @Override
    public void addListElement(Object[] elementProperties, int index) {
       String violstion_str = parseString((byte[]) elementProperties[0]);

        if(violationList == null){
            violationList = new ArrayList<>();
        }
        if(!violationList.contains(violstion_str)){
            violationList.add(violstion_str);
        }
    }

    public String getPlatenumber() {
        return platenumber;
    }

    public void setPlatenumber(String platenumber) {
        this.platenumber = platenumber;
    }

    public String getResultPlateNumber() {
        return resultPlateNumber;
    }

    public void setResultPlateNumber(String resultPlateNumber) {
        this.resultPlateNumber = resultPlateNumber;
    }

    public String getLocationTime() {
        return locationTime;
    }

    public void setLocationTime(String locationTime) {
        this.locationTime = locationTime;
    }

    public int getResultlng() {
        return resultlng;
    }

    public void setResultlng(int resultlng) {
        this.resultlng = resultlng;
    }

    public int getResultlat() {
        return resultlat;
    }

    public void setResultlat(int resultlat) {
        this.resultlat = resultlat;
    }

    public byte getResultSpeed() {
        return resultSpeed;
    }

    public void setResultSpeed(byte resultSpeed) {
        this.resultSpeed = resultSpeed;
    }

    public byte getResultGNSSSpeed() {
        return resultGNSSSpeed;
    }

    public void setResultGNSSSpeed(byte resultGNSSSpeed) {
        this.resultGNSSSpeed = resultGNSSSpeed;
    }

    public short getResultOrientation() {
        return resultOrientation;
    }

    public void setResultOrientation(short resultOrientation) {
        this.resultOrientation = resultOrientation;
    }

    public short getResultAltitude() {
        return resultAltitude;
    }

    public void setResultAltitude(short resultAltitude) {
        this.resultAltitude = resultAltitude;
    }

    public int getResultMileage() {
        return resultMileage;
    }

    public void setResultMileage(int resultMileage) {
        this.resultMileage = resultMileage;
    }

    public short getResultOilMass() {
        return resultOilMass;
    }

    public void setResultOilMass(short resultOilMass) {
        this.resultOilMass = resultOilMass;
    }

    public int getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(int resultStatus) {
        this.resultStatus = resultStatus;
    }

    public int getResultAlarmType() {
        return resultAlarmType;
    }

    public void setResultAlarmType(int resultAlarmType) {
        this.resultAlarmType = resultAlarmType;
    }

    public byte getViolationCount() {
        return violationCount;
    }

    public void setViolationCount(byte violationCount) {
        this.violationCount = violationCount;
    }

    public List<String> getViolationList() {
        return violationList;
    }

    public void setViolationList(List<String> violationList) {
        this.violationList = violationList;
    }

    @Override
    public String toString() {

        String itemString = "";
        if(violationList != null) {
            for (String str : violationList) {
                itemString = str + ";";
            }
        }

        return "ResponseCarStatusInfoBean{" +
                "resultPlateNumber='" + resultPlateNumber + '\'' +
                ", locationTime='" + locationTime + '\'' +
                ", resultlng=" + resultlng +
                ", resultlat=" + resultlat +
                ", resultSpeed=" + resultSpeed +
                ", resultGNSSSpeed=" + resultGNSSSpeed +
                ", resultOrientation=" + resultOrientation +
                ", resultAltitude=" + resultAltitude +
                ", resultMileage=" + resultMileage +
                ", resultOilMass=" + resultOilMass +
                ", resultStatus=" + resultStatus +
                ", resultAlarmType=" + resultAlarmType +
                ", violationCount=" + violationCount +
                ", violationList=" + itemString +
                '}';
    }

    public CarInfoBean toCarInfoBean(CarInfoBean mCarInfoBean){
        if(mCarInfoBean == null) {
            mCarInfoBean = new CarInfoBean();
        }
        mCarInfoBean.setPlateNumber(resultPlateNumber);
        mCarInfoBean.setLocationTime(locationTime);
        mCarInfoBean.setLng(resultlng);
        mCarInfoBean.setLat(resultlat);
        mCarInfoBean.setSpeed(resultSpeed);
        mCarInfoBean.setgNSSSpeed(resultGNSSSpeed);
        mCarInfoBean.setOrientation(resultOrientation);
        mCarInfoBean.setAltitude(resultAltitude);
        mCarInfoBean.setMileage(resultMileage);
        mCarInfoBean.setOilMass(resultOilMass);
        mCarInfoBean.setStatus(resultStatus);
        mCarInfoBean.setAlarmType(resultAlarmType);
        mCarInfoBean.setViolationCount(violationCount);
        mCarInfoBean.setViolationList(violationList == null ? new ArrayList<String>() : new ArrayList<String>(violationList));
        return mCarInfoBean;

    }

    public CarInfoDB toCarInfoDB(CarInfoDB mCarInfoDB){

        if(mCarInfoDB == null){
            mCarInfoDB = new CarInfoDB();
        }

        mCarInfoDB.setUserName(UserInfo.getInstance().getUserName());

        mCarInfoDB.setPlateNumber(resultPlateNumber);
        mCarInfoDB.setLocationTime(locationTime);
        mCarInfoDB.setLng(resultlng);
        mCarInfoDB.setLat(resultlat);
        mCarInfoDB.setSpeed(resultSpeed);
        mCarInfoDB.setgNSSSpeed(resultGNSSSpeed);
        mCarInfoDB.setOrientation(resultOrientation);
        mCarInfoDB.setAltitude(resultAltitude);
        mCarInfoDB.setMileage(resultMileage);
        mCarInfoDB.setOilMass(resultOilMass);
        mCarInfoDB.setStatus(resultStatus);
        mCarInfoDB.setAlarmType(resultAlarmType);
        mCarInfoDB.setViolationCount(violationCount);

        String mViolationList = "";

        if(violationList !=null && violationList.size()>0){

            for(int i = 0; i<violationList.size(); i++){
                mViolationList += violationList.get(i);

                if(i<(violationList.size()-1)){
                    mViolationList += mCarInfoDB.getViolationSeparator();
                }
            }
        }

        mCarInfoDB.setViolationList(mViolationList);
        return mCarInfoDB;

    }
}
