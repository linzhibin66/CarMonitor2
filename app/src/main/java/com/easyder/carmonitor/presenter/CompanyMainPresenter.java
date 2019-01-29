package com.easyder.carmonitor.presenter;

import android.content.Context;

import com.shinetech.mvp.DB.DBManager;
import com.shinetech.mvp.DB.DBThreadPool.Task.TaskBean;
import com.shinetech.mvp.DB.DBThreadPool.TaskCtrl.DBCtrlTask;
import com.shinetech.mvp.DB.bean.CarInfoDB;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.interfaces.BaseUpdateBaiduMarker;
import com.shinetech.mvp.interfaces.ResponsePlateNumberListListener;
import com.shinetech.mvp.network.UDP.UDPRequestCtrl;
import com.shinetech.mvp.network.UDP.bean.CompanyAllCarBaseInfo;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.listener.RequestAllCarListener;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;
import com.shinetech.mvp.network.UDP.presenter.MainPresenter;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarStatusInfoBean;
import com.shinetech.mvp.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-03-31.
 */
public class CompanyMainPresenter extends MainActivityPresenter {

    public CompanyMainPresenter(Context context, BaseUpdateBaiduMarker markerScheme) {
        super(context, markerScheme);
    }


    @Override
    public void updateCarOfView(boolean isShowLoading) {
        getMapCars(mapViewScope[0], mapViewScope[1], mapViewScope[2], mapViewScope[3], isShowLoading);
    }

    @Override
    public void getOrRefreshCar() {

        UserInfo userInfo = UserInfo.getInstance();

        List<String> plateNumberList = userInfo.getPlateNumberList();

        //已经获取了车辆列表
        if (plateNumberList != null && plateNumberList.size() > 0) {
//          TODO refreshCar
            final String currentLocationCar = getFistLocationCar(plateNumberList);

            if(MainApplication.isUserResponseUDP){

                locationFistCar(currentLocationCar, this);
//                responseUDPGetCarInfo(currentLocationCar, this);

            }else{
                getCarInfo(currentLocationCar, this);
            }

        } else {
//             TODO 获取车牌列表，存储到用户个人信息中；并定位首辆车到地图中
            getPlateNumberList();
           // System.out.println("getPlateNumberList ---------------------------------------getOrRefreshCar-----------------------------------");
        }
    }

    @Override
    public void getAllCar(final RequestAllCarListener mRequestAllCarListener) {

        UserInfo userInfo = UserInfo.getInstance();
        //使用新接口，返回车牌列表
        if(MainApplication.isUserResponseUDP){
            List<String> plateNumberList = userInfo.getPlateNumberList();

            //已经存储了车牌列表，直接回掉
            if(plateNumberList != null && plateNumberList.size()>0){
                mRequestAllCarListener.OnSuccessPlateNumberList(new ArrayList<>(plateNumberList));
            }else{
            //没有存储车牌列表，需重新获取
                responseUDPgetPlateNumberList(new ResponsePlateNumberListListener() {
                    @Override
                    public void onSuccess(List<String> carList) {
                        mRequestAllCarListener.OnSuccessPlateNumberList(carList);
                    }

                    @Override
                    public void onError(String message) {
                        mRequestAllCarListener.OnSuccessPlateNumberList(new ArrayList<String>());
                    }
                });
            }
            return;
        }

        //使用旧接口，返回车辆定位信息
        List<CarInfoBean> newestCarInfoList = userInfo.getNewestCarInfoList();
        if (newestCarInfoList.size() == userInfo.getPlateNumberList().size()){
            mRequestAllCarListener.OnSuccessCarInfo(newestCarInfoList);
            return;
        }

//      未获取车辆信息、车辆信息不全 或 信息已过时
        String companyName = userInfo.getUserName();

        CompanyAllCarBaseInfo companyAllCarBaseInfo = new CompanyAllCarBaseInfo(companyName);

        UDPRequestCtrl.getInstance().request(companyAllCarBaseInfo, new ResponseListener() {
            @Override
            public void onSuccess(LoadResult successResult) {
                List<CarInfoBean> newestCarInfoList = UserInfo.getInstance().getNewestCarInfoList();
                mRequestAllCarListener.OnSuccessCarInfo(newestCarInfoList);
            }

            @Override
            public void onError(LoadResult errorResult) {
                List<CarInfoBean> newestCarInfoList = UserInfo.getInstance().getNewestCarInfoList();
                mRequestAllCarListener.OnSuccessCarInfo(newestCarInfoList);

            }
        });
    }


}
