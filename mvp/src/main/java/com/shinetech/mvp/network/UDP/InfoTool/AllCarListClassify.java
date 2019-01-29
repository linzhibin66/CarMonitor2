package com.shinetech.mvp.network.UDP.InfoTool;

import android.text.TextUtils;

import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.R;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017/2/28.
 */
public class AllCarListClassify {

    private List<CarInfoBean> allcarList = new ArrayList<>();

    private List<CarInfoBean>  alarmCarList = new ArrayList<>();

    private List<CarInfoBean>  runningCarList = new ArrayList<>();

    private List<CarInfoBean>  stopCarList = new ArrayList<>();

    private List<CarInfoBean>  exceptionList = new ArrayList<>();

    private boolean debug = true;

    public AllCarListClassify(List<CarInfoBean> allcarList) {
        setAllcarList(allcarList);
    }

    public void setAllcarList(List<CarInfoBean> allcarList){
        synchronized (MainApplication.getInstance()){
            this.allcarList.clear();
            alarmCarList.clear();
            runningCarList.clear();
            stopCarList.clear();
            exceptionList.clear();
            if(allcarList!=null && allcarList.size()>0){
                this.allcarList.addAll(allcarList);
            }
        }
    }

    public void classifyCar(){
        synchronized (MainApplication.getInstance()){
            for(CarInfoBean mCarInfoBean : allcarList) {

                int alarmType = mCarInfoBean.getAlarmType();

                int statusInt = mCarInfoBean.getStatus();

//              TODO 报警状态的断定条件（需定义）
             /*   if(alarmType != 0){

                    if ((!TextUtils.isEmpty(AlarmInfoTool.getAlarmIndex(alarmType, 4))) || (!TextUtils.isEmpty(AlarmInfoTool.getAlarmIndex(alarmType, 5))) ||
                            (!TextUtils.isEmpty(AlarmInfoTool.getAlarmIndex(alarmType, 6))) || (!TextUtils.isEmpty(AlarmInfoTool.getAlarmIndex(alarmType, 9))) ||
                            (!TextUtils.isEmpty(AlarmInfoTool.getAlarmIndex(alarmType, 10))) || (!TextUtils.isEmpty(AlarmInfoTool.getAlarmIndex(alarmType, 11))) ||
                            (!TextUtils.isEmpty(AlarmInfoTool.getAlarmIndex(alarmType, 12))) || (!TextUtils.isEmpty(AlarmInfoTool.getAlarmIndex(alarmType, 15))) ||
                            (!TextUtils.isEmpty(AlarmInfoTool.getAlarmIndex(alarmType, 16))) || (!TextUtils.isEmpty(AlarmInfoTool.getAlarmIndex(alarmType, 22))) ||
                            (!TextUtils.isEmpty(AlarmInfoTool.getAlarmIndex(alarmType, 24))) || (!TextUtils.isEmpty(AlarmInfoTool.getAlarmIndex(alarmType, 25)))) {
                        exceptionList.add(mCarInfoBean);
                    }
                }*/

                if(AlarmInfoTool.isExceptionp(alarmType)){
                    exceptionList.add(mCarInfoBean);
                }

                if(AlarmInfoTool.isAlarm(mCarInfoBean)){
                    alarmCarList.add(mCarInfoBean);
                }

              /*  byte violationCount = mCarInfoBean.getViolationCount();
                if(violationCount>0){

//                            List<String> violationList = mCarInfoBean.getViolationList();
//                            if(violationList.contains(MainApplication.getInstance().getString(R.string.))
                    alarmCarList.add(mCarInfoBean);
                }*/


                CarStatusInfoTool carStatusTool = new CarStatusInfoTool(MainApplication.getInstance(),statusInt);

                //获取车辆的驻停状态
                String status = carStatusTool.getStatus(0);
//                    if(debug)System.out.println("status : " + status);
                String locationTime = mCarInfoBean.getLocationTime();

                long tiemlong = TimeUtil.getTiemlong(locationTime);

                boolean isStop = false;

                if(tiemlong == 0 || (System.currentTimeMillis()-tiemlong)> (10 * 60 * 1000) ){
                    isStop = true;
                }

                if(MainApplication.getInstance().getString(R.string.running_status).equals(status) && !isStop){
                    runningCarList.add(mCarInfoBean);
                }else{
                    stopCarList.add(mCarInfoBean);
                }
            }
        }

    }

    public List<CarInfoBean> getAllcarList() {
        return allcarList;
    }

    public List<CarInfoBean> getAlarmCarList() {
        return alarmCarList;
    }

    public List<CarInfoBean> getRunningCarList() {
        return runningCarList;
    }

    public List<CarInfoBean> getStopCarList() {
        return stopCarList;
    }

    public List<CarInfoBean> getExceptionList(){
        return exceptionList;
    }

    public void setAlarmCarList(List<CarInfoBean> alarmCarList) {
        this.alarmCarList.clear();
        this.alarmCarList.addAll(alarmCarList);
    }

    public void setRunningCarList(List<CarInfoBean> runningCarList) {
        this.runningCarList.clear();
        this.runningCarList.addAll(runningCarList);
    }

    public void setStopCarList(List<CarInfoBean> stopCarList) {
        this.stopCarList.clear();
        this.stopCarList.addAll(stopCarList);
    }

    public void setExceptionList(List<CarInfoBean> exceptionList){
        this.exceptionList.clear();
        this.exceptionList.addAll(exceptionList);
    }
}
