package com.shinetech.mvp.network.UDP.InfoTool;

import android.content.Context;

import com.shinetech.mvp.R;

/**
 * Created by ljn on 2017/2/10.
 */
public class CarStatusInfoTool {

    /****************************车辆状态 start *********************************/

    /**
     * ACC状态
     * status 与 ACC_STATUS 位与的结果，0（状态为关），1（状态为开）
     */
    private static final int ACC_STATUS = 0x0001;

    /**
     * 定位
     * status与 LOCATION_STATUS 位与的结果，0（状态为未），1（状态为锁定）
     */
    private static final int LOCATION_STATUS = 0x0002;

    /**
     * 纬度
     * status与 LAT_STATUS 位与的结果，0（状态为北纬），1（状态为南纬）
     */
    private static final int LAT_STATUS = 0x0004;

    /**
     * 经度
     * status与 LNG_STATUS 位与的结果，0（状态为东经），1（状态为西经）
     */
    private static final int LNG_STATUS = 0x0008;

    /**
     * 运营
     * status与 OPERATION_STATUS 位与的结果，0（状态为重载），1（状态为空载）
     */
    private static final int OPERATION_STATUS = 0x0010;

    /**
     * 保密插件
     * status与 SECRECY_STATUS 位与的结果，0（状态为未加密），1（状态为加密）
     */
    private static final int SECRECY_STATUS = 0x0020;

    /**
     * 刹车
     * status与 BRAKE_STATUS 位与的结果，0（状态为正常），1（状态为制动）
     */
    private static final int BRAKE_STATUS = 0x0040;

    /**
     * 左转向灯
     * status与 SIGNALLEFT_STATUS 位与的结果，0（状态为OFF），1（状态为ON）
     */
    private static final int SIGNALLEFT_STATUS = 0x0080;

    /**
     * 右转向灯
     * status 与 SIGNALRIGHT_STATUS 位与的结果，0（状态为OFF），1（状态为ON）
     */
    private static final int SIGNALRIGHT_STATUS = 0x0100;

    /**
     * 远光灯
     * status 与 HIGHBEAM_STATUS 位与的结果，0（状态为OFF），1（状态为ON）
     */
    private static final int HIGHBEAM_STATUS = 0x0200;
    /**
     * 油路
     * status 与 OILINE_STATUS 位与的结果，0（状态为正常），1（状态为断开）
     */
    private static final int OILINE_STATUS = 0x0400;

    /**
     * 电路 与 CIRCUITRY_STATUS 位与的结果，0（状态为正常），1（状态为断开）
     */
    private static final int CIRCUITRY_STATUS = 0x0800;

    /**
     * 车门锁
     * status 与 DOORLOCK_STATUS 位与的结果，0（状态为解锁），1（状态为加锁）
     */
    private static final int DOORLOCK_STATUS = 0x1000;

    /**
     * 设防
     * status 与 FORTIFY_STATUS 位与的结果，0（状态为未设防），1（状态为已设防）
     */
    private static final int FORTIFY_STATUS = 0x2000;

    /**
     * 上线状态
     * status 与 ONLINE_STATUS 位与的结果，0（状态为掉线），1（状态为在线）
     */
    private static final int ONLINE_STATUS = 0x4000;

    /**
     * 驻停
     * status（速度为0 与 STOPLIFT_STATUS 位与的结果，0（状态为行驶），1（状态为驻停）
     */
    private static final int STOPLIFT_STATUS = 0x8000;

    /****************************车辆状态 end *********************************/



    /**
     * 对应状态值
     */
    private int[] statusArray = new int[16];

    private String[] carStatusName;
    private  String[][] statusvalus;

    public CarStatusInfoTool(Context context,int status) {

        initArray(context);
        setStatusValues(status);

    }

    private void initArray(Context context){
        carStatusName = context.getResources().getStringArray(R.array.car_status_array);
        statusvalus = new String[][]{{"关","开"},{"未", "锁定"},{"北纬", "南纬"},{"东经", "西经"}, {"重载", "空载"}, {"未加密", "加密"},
                {"正常", "制动"}, {"OFF", "ON"}, {"正常", "断开"}, {"解锁", "加锁"}, {"未设防", "已设防"}, {"掉线", "在线"}, {"行驶", "驻停"}};

    }

    private void setStatusValues(int status){

        int persion;
        int index = 0;
        while(index<16){
            persion = 1<<index;
            if((status & persion) == 0){
                statusArray[index] = 0;
            }else{
                statusArray[index] = 1;
            }
            index++;
        }
    }

    public String getStatusName(int index){
        return carStatusName[index];
    }

    public String getStatus(int index){

        int statusValus = statusArray[index];
        if((index>7) && (index <= 9)){
            return statusvalus[7][statusValus];
        }else if(index >=10){
            if(index<=11){
                return statusvalus[8][statusValus];
            }else{
                return statusvalus[index-3][statusValus];
            }
        }
        return statusvalus[index][statusValus];
    }


    /**
     * Acc 状态
     * @return true is open else close
     */
    public static boolean getAccStatus(int status){
        if((status & ACC_STATUS) > 0){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 定位状态
     * @param status
     * @return true is 锁定 else 未
     */
    public static boolean getLocationStatus(int status){
        if((status & LOCATION_STATUS) > 0){
            return true;
        }else {
            return false;
        }
    }

    /**
     *  纬度状态
     * @param status
     * @return true is 南纬 else 北纬
     */
    public static boolean getLatStatus(int status){
        if((status & LAT_STATUS) > 0){
            return true;
        }else {
            return false;
        }
    }

    /**
     *  经度状态
     * @param status
     * @return true is 西经 else 东经
     */
    public static boolean getLngStatus(int status){
        if((status & LNG_STATUS) > 0){
            return true;
        }else {
            return false;
        }
    }

    /**
     *  运营状态
     * @param status
     * @return true is 空载 else 重载
     */
    public static boolean getOperationStatus(int status){
        if((status & OPERATION_STATUS) > 0){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 保密插件状态
     * @param status
     * @return true is 加密 else 未加密
     */
    public static boolean getSecrecyStatus(int status){
        if((status & SECRECY_STATUS) > 0){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 刹车状态
     * @param status
     * @return true is 制动 else 正常
     */
    public static boolean getBrakeStatus(int status){
        if((status & BRAKE_STATUS) > 0){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 左转向灯状态
     * @param status
     * @return true is ON else OFF
     */
    public static boolean getSignalLeftStatus(int status){
        if((status & SIGNALLEFT_STATUS) > 0){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 右转向灯状态
     * @param status
     * @return true is ON else OFF
     */
    public static boolean getSignalRightStatus(int status){
        if((status & SIGNALRIGHT_STATUS) > 0){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 远光灯状态
     * @param status
     * @return true is ON else OFF
     */
    public static boolean getHighBeamStatus(int status){
        if((status & HIGHBEAM_STATUS) > 0){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 油路状态
     * @param status
     * @return true is 断开 else 正常
     */
    public static boolean getOilineStatus(int status){
        if((status & OILINE_STATUS) > 0){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 电路状态
     * @param status
     * @return true is 断开 else 正常
     */
    public static boolean getCircuitryStatus(int status){
        if((status & CIRCUITRY_STATUS) > 0){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 车门锁状态
     * @param status
     * @return true is 加锁 else 解锁
     */
    public static boolean getDoorlockStatus(int status){
        if((status & DOORLOCK_STATUS) > 0){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 设防状态
     * @param status
     * @return true is 已设防 else 未设防
     */
    public static boolean getFortifyStatus(int status){
        if((status & FORTIFY_STATUS) > 0){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 上线状态
     * @param status
     * @return true is 在线 else 掉线
     */
    public static boolean getOnlineStatus(int status){
        if((status & ONLINE_STATUS) > 0){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 驻停状态
     * @param status
     * @return true is 驻停 else 行驶
     */
    public static boolean getStopliftStatus(int status){
        if((status & STOPLIFT_STATUS) > 0){
            return true;
        }else {
            return false;
        }
    }



}
