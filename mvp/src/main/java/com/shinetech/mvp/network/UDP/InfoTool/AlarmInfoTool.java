package com.shinetech.mvp.network.UDP.InfoTool;

import android.text.TextUtils;

import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.R;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017/2/10.
 */
public class AlarmInfoTool {

    /**
     * 报警标志
     */
    public static final String[] ALARM_LIST = new String[]{"紧急报警","超速报警","疲劳驾驶","预警","GNSS模块故障","GNSS天线开路", "GNSS天线短路",
            "主电源欠压","主电源掉电","显示屏故障","TTS模块故障","摄像头故障","通信模块故障","车辆停驶报警","车辆聚集报警","外接设备异常","备用电池异常",
            "密码错误","当天累计驾驶超时","超时停车","进出区域","进出路线","路段行驶时间异常","路线偏离报警","车速传感器故障","油量异常","车辆被盗",
            "车辆非法点火","车辆非法移动","车辆侧翻报警"};

    public static List<String> getAlarmList(int statuc){
        List<String> alarmList = new ArrayList<>();
        int position = 1;
        int index = 0;
        do{
            int temp = statuc & position;
            if(temp >= 1){
                alarmList.add(ALARM_LIST[index]);
            }

            index++;

            position = position<<1;

        }while(position<=0x20000000);

        return alarmList;

    }

    public static String getAlarmIndex(int statuc, int index){
        int position = 1<<index;
        int temp = statuc & position;
        if(temp >= 1){
           return ALARM_LIST[index];
        }else{
            return "";
        }
    }

    public static boolean isExceptionp(int alarmType) {

//              TODO 报警状态的断定条件（需定义）
        if (alarmType != 0) {
            // 54632048 的二进制表示形式 ： 11010000011001111001110000 ；代表 第4、5、6、9、10、11、12、15、16、22、24、25位
            int alarmt = (int) 54632048D;
           /* if ((!TextUtils.isEmpty(AlarmInfoTool.getAlarmIndex(alarmType, 4))) || (!TextUtils.isEmpty(AlarmInfoTool.getAlarmIndex(alarmType, 5))) ||
                    (!TextUtils.isEmpty(AlarmInfoTool.getAlarmIndex(alarmType, 6))) || (!TextUtils.isEmpty(AlarmInfoTool.getAlarmIndex(alarmType, 9))) ||
                    (!TextUtils.isEmpty(AlarmInfoTool.getAlarmIndex(alarmType, 10))) || (!TextUtils.isEmpty(AlarmInfoTool.getAlarmIndex(alarmType, 11))) ||
                    (!TextUtils.isEmpty(AlarmInfoTool.getAlarmIndex(alarmType, 12))) || (!TextUtils.isEmpty(AlarmInfoTool.getAlarmIndex(alarmType, 15))) ||
                    (!TextUtils.isEmpty(AlarmInfoTool.getAlarmIndex(alarmType, 16))) || (!TextUtils.isEmpty(AlarmInfoTool.getAlarmIndex(alarmType, 22))) ||
                    (!TextUtils.isEmpty(AlarmInfoTool.getAlarmIndex(alarmType, 24))) || (!TextUtils.isEmpty(AlarmInfoTool.getAlarmIndex(alarmType, 25)))) {*/
            if((alarmType & alarmt)>0){

                return true;
            }
        }


        return false;
    }

    public static boolean isAlarm(CarInfoBean mCarInfoBean){
        byte violationCount = mCarInfoBean.getViolationCount();
        if(violationCount>0){

          List<String> violationList = mCarInfoBean.getViolationList();
         /*   for(String str : violationList){
                System.out.println("violationList : "+ str);
            }*/
          if(violationList.contains(MainApplication.getInstance().getString(R.string.no_alarm_status)) && (violationCount ==1)){
                return false;
            }

            return true;
        }

        return false;
    }
}
