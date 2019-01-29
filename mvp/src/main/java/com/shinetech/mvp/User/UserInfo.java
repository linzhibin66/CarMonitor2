package com.shinetech.mvp.User;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.baidu.mapapi.model.LatLng;
import com.shinetech.mvp.DB.DBManager;
import com.shinetech.mvp.DB.bean.PushMessage;
import com.shinetech.mvp.DB.bean.SelectCarList;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.network.UDP.DatagramPacketDefine;
import com.shinetech.mvp.network.UDP.bean.MessagePushBean;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.network.UDP.listener.CarListChangeListener;
import com.shinetech.mvp.network.UDP.listener.PushMessageListener;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarBaseInfoBean;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseMessagePushBean;
import com.shinetech.mvp.utils.UIUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ljn on 2017/2/21.
 */
public class UserInfo {

    private static UserInfo mUserInfo;

    private String userName;

    public static final int USER = 0;
    public static final int INSTALLATIONMASTER = 1;

    /**
     * 用户权限 (0 客户; 1 安装师傅)
     */
    private byte userPermission = 0;

    private boolean isPerson;

    private byte loginType;

    private String USERNAME = "Login_UserName";

    private String USER_PWD = "user_pwd";

    private String REMEBER_PWD_SETTING = "remember_pwd_setting";

    private String LOGIN_TYPE = "login_type";

    private String CAR_COLOR = "car_color";

    /**
     * 车牌登录用户，车辆信息
     */
    private CarInfoBean persionCarInfo;

    /**
     * 用户车牌列表（适用企业用户）
     */
    private List<String> plateNumberList;

    /**
     * 用户车辆基础信息列表
     */
    private Map<String,ResponseCarBaseInfoBean>  carBaseInfoMap = new LinkedHashMap();

    /**
     * 当前锁定车辆的车牌（适用企业用户）
     */
    private String currentLocationCar;

    /**
     * 最新车辆信息列表
     */
    private Map<String, CarInfoBean> newestCarInfoMap = new LinkedHashMap<>();

    /**
     * 车辆信息变化监听事件集合
     */
    private List<CarListChangeListener> mChangeListener = new ArrayList<>();

    private List<PushMessageListener> receivePushMessage = new ArrayList<>();

    /**
     * 最后一次车辆信息跟新时间
     */
    private long lastTimeUpdateCarList;

    /**
     * 选择要显示的车辆列表
     */
//    private Map<String, CarInfoBean> selectCarInfoMap = new LinkedHashMap<>();

    /**
     * 选择要显示的车牌列表
     */
    private List<String> selectCarPlateNumber = new ArrayList<>();

    private boolean isLocationCar = false;

    //数据库记录的选择车辆缓存
    private List<SelectCarList> selectCarLists = new ArrayList<>();

    /**
     * 定位经纬度
     */
    private LatLng locationLatLng;

    private String pwsEncoding;

    private UserInfo() {
    }

    public static UserInfo getInstance(){
        if(mUserInfo==null){
            synchronized (MainApplication.getInstance()){
                if(mUserInfo == null){
                    mUserInfo = new UserInfo();
                }
            }
        }
        return mUserInfo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isPerson() {
        return isPerson;
    }

    public void setIsPerson(boolean isPerson) {
        this.isPerson = isPerson;
    }

    /**
     * 获取用户车牌列表（适用企业用户）
     */
    public List<String> getPlateNumberList() {
        return plateNumberList;
    }

    public void setPlateNumberList(List<String> plateNumberList) {
        if(this.plateNumberList == null){
            this.plateNumberList = new ArrayList<>();
        }
        this.plateNumberList.clear();
        this.plateNumberList.addAll(plateNumberList);

    }

    public boolean isLocationCar() {
        return isLocationCar;
    }

    public void setIsLocationCar(boolean isLocationCar) {
        this.isLocationCar = isLocationCar;
    }

    public String getCurrentLocationCar() {
        return currentLocationCar;
    }

    public void setCurrentLocationCar(String currentLocationCar) {
        this.currentLocationCar = currentLocationCar;
    }

    public Map<String,ResponseCarBaseInfoBean> getCarBaseInfoList() {
        return carBaseInfoMap;
    }

    public ResponseCarBaseInfoBean getCarBaseInfo(String plateNumber){
        ResponseCarBaseInfoBean responseCarBaseInfoBean = carBaseInfoMap.get(plateNumber);
        return responseCarBaseInfoBean;

    }

    public void addCarBaseInfoItem(ResponseCarBaseInfoBean carBaseInfoItem) {

        if(carBaseInfoItem != null && !TextUtils.isEmpty(carBaseInfoItem.getResultPlateNumber())) {
            carBaseInfoMap.put(carBaseInfoItem.getResultPlateNumber(), carBaseInfoItem);
        }
    }

    public void updateCarInfo(final CarInfoBean mCarInfoBean, final boolean isCallBack){

        addNewestVarInfo(mCarInfoBean);

        UIUtils.runInMainThread(new Runnable() {
            @Override
            public void run() {

                if (isCallBack) {

                    if (mChangeListener != null && mChangeListener.size() > 0) {
                        synchronized (getInstance()) {
                            for (CarListChangeListener listener : mChangeListener) {
//                                LogUtils.error("-----------------------------listener-----------------------------------");
                                listener.changed(mCarInfoBean);
                            }
                        }
                    }

                }
            }
        });
    }

    /**
     * 跟新车辆列表信息，并发起通知
     * @param carInfoList 最新的车辆列表信息
     * @param isCallBack 是否回掉监听
     * @param isEndPack 是否是最后数据包
     */
    public void updateCarInfoList(final List<CarInfoBean> carInfoList,final boolean isCallBack,final boolean isEndPack){

//        TODO save carInfoList

            List<CarInfoBean> tempList = new ArrayList<>();

            tempList.addAll(carInfoList);

            for (CarInfoBean mCarInfoBean : tempList) {

                addNewestVarInfo(mCarInfoBean);

            }

            //保存最后一次跟新车辆列表的时间戳
            if(isEndPack){
                lastTimeUpdateCarList = System.currentTimeMillis();
            }

            UIUtils.runInMainThread(new Runnable() {
                @Override
                public void run() {

                    if (isCallBack && isEndPack) {
                        if (mChangeListener != null && mChangeListener.size() > 0) {
                            synchronized (getInstance()) {
                                for (CarListChangeListener listener : mChangeListener) {
                                    listener.changedAll();
                                }
                            }
                        }
                    }
                }
            });

    }

    /**
     * 接收到推送消息
     * @param pushMessage
     */
    public void receivePushMessage(final PushMessage pushMessage){

        if(pushMessage == null){
            return;
        }

//      save to DB
        DBManager.insertPushMessage(pushMessage);

        UIUtils.runInMainThread(new Runnable() {
            @Override
            public void run() {

//                callback to UI
                if(receivePushMessage != null && receivePushMessage.size()>0){

                    int noReadCount = DBManager.queryPushMessageNoReadCount();

                    synchronized (receivePushMessage) {

                        for(PushMessageListener listener : receivePushMessage){
                            listener.receivePushMessage(pushMessage, noReadCount);
                        }
                    }
                }
            }
        });

    }

    /**
     * 注册消息推送监听
     * @param listener
     */
    public void registerPushMessageListener(PushMessageListener listener){

        if(listener != null && !receivePushMessage.contains(listener)) {
            synchronized (receivePushMessage) {
                receivePushMessage.add(listener);
            }
        }
    }

    /**
     * 取消注册消息推送监听
     * @param listener
     */
    public void unRegisterPushMessageListener(PushMessageListener listener){
        if(listener != null) {
            synchronized (receivePushMessage) {
                receivePushMessage.remove(listener);
            }
        }
    }

    /**
     * 注册车辆信息改变监听
     * @param listener
     */
    public void registerCarListChangeListener(CarListChangeListener listener){
        synchronized (getInstance()){
            mChangeListener.add(listener);
        }
    }

    /**
     * 取消注册车辆信息改变监听
     * @param listener
     */
    public void unregisteredCarlistChangeListener(CarListChangeListener listener){
        synchronized (getInstance()){
            mChangeListener.remove(listener);
        }
    }

    public long getLastTimeUpdateCarList(){
        return lastTimeUpdateCarList;
    }

    public List<CarInfoBean> getNewestCarInfoList(){

        synchronized (this) {

            List<CarInfoBean> list = new ArrayList<>();

            Set<Map.Entry<String, CarInfoBean>> entries = newestCarInfoMap.entrySet();
            Iterator<Map.Entry<String, CarInfoBean>> iterator = entries.iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, CarInfoBean> next = iterator.next();
                CarInfoBean carInfoBean = next.getValue();
                list.add(carInfoBean);
            }

            return list;
        }
    }

    public int getNewestCarInfoCount(){
        return newestCarInfoMap.size();
    }

    public void addNewestVarInfo(CarInfoBean mCarInfoBean){
        synchronized (this) {
            CarInfoBean carInfoBean = newestCarInfoMap.get(mCarInfoBean.getPlateNumber());

            if (carInfoBean == null || !mCarInfoBean.equals(carInfoBean)) {
                newestCarInfoMap.put(mCarInfoBean.getPlateNumber(), mCarInfoBean);
            }
        }
    }

    public CarInfoBean getNewestCarInfo(String plateNumber){
        return newestCarInfoMap.get(plateNumber);
    }

    public boolean addSelectToCacheAndDb(CarInfoBean mCarInfoBean){

        String plateNumber = mCarInfoBean.getPlateNumber();

        if(selectCarPlateNumber.contains(plateNumber)){
            return false;
        }

        selectCarPlateNumber.add(plateNumber);

        SelectCarList selectCarList = new SelectCarList();
        selectCarList.setUserName(userName);
        selectCarList.setPlatenumber(plateNumber);
        DBManager.insertSelectCarList(selectCarList);
        selectCarLists.add(selectCarList);

        return true;
    }

    /**
     * 添加要显示在地图中的车辆到数据库和用户缓存
     */
    public boolean addSelectToCacheAndDb( Map<String, CarInfoBean> selectCarInfoMapCache){


        //清除缓存
        selectCarPlateNumber.clear();

        if(selectCarInfoMapCache.size() == 0){

            DBManager.deleteSelectCarList(selectCarLists);
            selectCarLists.clear();
            return false;
        }

        //保存到缓存
        selectCarPlateNumber.addAll(selectCarInfoMapCache.keySet());

        //跟新数据库
        Map<String, CarInfoBean> updateMap = new LinkedHashMap<>();
        updateMap.putAll(selectCarInfoMapCache);

        //获取要移除的数据
        List<SelectCarList> deleteList = new ArrayList<>();
        List<SelectCarList> updateeList = new ArrayList<>();

        Iterator<SelectCarList> iterator = selectCarLists.iterator();
        while(iterator.hasNext()){
            SelectCarList carinfo = iterator.next();
            CarInfoBean carInfoBean = updateMap.get(carinfo.getPlatenumber());
            if(carInfoBean == null){
                deleteList.add(carinfo);
                iterator.remove();
            }else{
                updateMap.remove(carinfo.getPlatenumber());
            }
        }

        //创建要跟新到数据库的车辆
        if(updateMap.size()>0){
            Set<String> strings = updateMap.keySet();
            for(String plateNumber : strings){
                SelectCarList selectCarList = new SelectCarList();
                selectCarList.setUserName(userName);
                selectCarList.setPlatenumber(plateNumber);
                updateeList.add(selectCarList);
                selectCarLists.add(selectCarList);
            }
        }


//      TODO save TO DB
        DBManager.insertSelectCarList(updateeList);
        DBManager.deleteSelectCarList(deleteList);

        return false;
    }

    public List<String> getSelectCarList(){

        initSelectCarInfoMap();

        return selectCarPlateNumber;

    }

    /**
     * 初始化选择了的车辆列表
     */
    private void initSelectCarInfoMap(){

        synchronized(selectCarPlateNumber) {
            if (selectCarPlateNumber.size() == 0) {

//            TODO get list of DB
                selectCarLists.clear();
                List<SelectCarList> mSelectCarLists = DBManager.querySelectCarList(userName, null);
                if(mSelectCarLists != null) {
                    selectCarLists.addAll(mSelectCarLists);
                }

                if (this.selectCarLists == null) {
                    return;
                }

                selectCarPlateNumber.clear();

                for (SelectCarList mSelectCarList : this.selectCarLists) {
                    selectCarPlateNumber.add(mSelectCarList.getPlatenumber());
                }
            }
        }
    }

    public boolean isSelectCarInfo(String plateNumber){
        if(selectCarPlateNumber.size()>0){
            return selectCarPlateNumber.contains(plateNumber)? true : false;
        }else{
            if(loginType != DatagramPacketDefine.USER_TYPE){

                return ((plateNumberList!= null) && plateNumberList.contains(plateNumber))? true: false;
            }else{
                if(!TextUtils.isEmpty(userName) && userName.equals(plateNumber)){
                    return true;
                }
                return false;
            }
        }
    }

    public byte getLoginType() {
        return loginType;
    }

    public void setLoginType(byte loginType) {
        this.loginType = loginType;
    }

    /**
     * 获取要显示的车辆数量
     * @return
     */
    public int getSelectCarCount(){
        return selectCarPlateNumber.size();
    }

    public void celanSelectCarAll(){
        synchronized (getInstance()){
            selectCarPlateNumber.clear();
        }
    }

    public CarInfoBean getPersionCarInfo() {
        return persionCarInfo;
    }

    public void setPersionCarInfo(CarInfoBean persionCarInfo) {
        this.persionCarInfo = persionCarInfo;
    }

    public void setLocationLatLng(LatLng latLng){
        locationLatLng = new LatLng(latLng.latitude, latLng.longitude);
    }

    public LatLng getLocationLatLng(){
        return locationLatLng;
    }

    public void cleanUserInfo(){
        userName = null;
        isPerson = false;
        persionCarInfo = null;
        if(plateNumberList != null){
            plateNumberList.clear();
        }

        currentLocationCar = null;

        newestCarInfoMap.clear();

        mChangeListener.clear();

        lastTimeUpdateCarList = 0;

        selectCarPlateNumber.clear();

        isLocationCar = false;
    }

    public String getPwsEncoding() {
        return pwsEncoding;
    }

    public void setPwsEncoding(String pwsEncoding) {
        this.pwsEncoding = pwsEncoding;
    }

    public String getHistoryLoginName(){
        SharedPreferences preferences = MainApplication.getPreferences();
        if(preferences != null){
            String userName = preferences.getString(USERNAME, "");
            return userName;
        }

        return "";
    }

    public boolean getRememberPwdSetting(){
        SharedPreferences preferences = MainApplication.getPreferences();
        if(preferences != null){
            boolean remeberPwdSetting = preferences.getBoolean(REMEBER_PWD_SETTING, false);
            return remeberPwdSetting;
        }

        return false;
    }

    public String getHistoryUserPwd(){
        SharedPreferences preferences = MainApplication.getPreferences();
        if(preferences != null){
            String userName = preferences.getString(USER_PWD, "");
            return userName;
        }

        return "";
    }

    public byte getHistoryLoginType(){
        SharedPreferences preferences = MainApplication.getPreferences();
        if(preferences != null){
            int loginType = preferences.getInt(LOGIN_TYPE, 0);
            return (byte)loginType;
        }

        return 0;
    }

    public String getHistoryCarColor(){
        SharedPreferences preferences = MainApplication.getPreferences();
        if(preferences != null){
            String carColor = preferences.getString(CAR_COLOR, "");
            return carColor;
        }

        return "";
    }

    public void cleanHistoryUserPwd(){
        SharedPreferences preferences = MainApplication.getPreferences();
        if(preferences != null){
            preferences.edit().putString(USER_PWD,"").commit();
        }
    }

    public void saveLoginUser(String pwd,String carColor, String username, boolean isRememberPWD){
        SharedPreferences preferences = MainApplication.getPreferences();
        if(preferences != null){
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString(USER_PWD,pwd);
            edit.putInt(LOGIN_TYPE,loginType);
            if((!TextUtils.isEmpty(carColor)) && (!TextUtils.isEmpty(username)) && (loginType == DatagramPacketDefine.USER_TYPE)){
                edit.putString(CAR_COLOR,carColor);
                edit.putString(USERNAME,username);
            }else{
                edit.putString(USERNAME,userName);
            }

            edit.putBoolean(REMEBER_PWD_SETTING, isRememberPWD);

            edit.commit();
        }
    }

    public int getUserPermission() {
        return userPermission;
    }

    public void setUserPermission(byte userPermission) {
        this.userPermission = userPermission;
    }
}
