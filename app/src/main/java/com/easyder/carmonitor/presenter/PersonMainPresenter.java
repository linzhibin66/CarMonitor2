package com.easyder.carmonitor.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.interfaces.BaseUpdateBaiduMarker;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.CarLatestStatus;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.listener.RequestAllCarListener;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarStatusInfoBean;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ljn on 2017-03-31.
 */
public class PersonMainPresenter extends MainActivityPresenter {

    public PersonMainPresenter(Context context, BaseUpdateBaiduMarker markerScheme) {
        super(context, markerScheme);
    }

    @Override
    public void updateCarOfView(boolean isShowLoading) {
        getPersionCarOfView(isShowLoading);
    }

    @Override
    public void getOrRefreshCar() {

        UserInfo userInfo = UserInfo.getInstance();
//      TODO 获取车辆信息，显示到地图相应位置
        if(MainApplication.isUserResponseUDP){
            responseUDPGetCarInfo(userInfo.getUserName(), this);
        }else{
            getCarInfo(userInfo.getUserName(), this);
        }

    }

    @Override
    public void getAllCar(final RequestAllCarListener mRequestAllCarListener) {

        UserInfo userInfo = UserInfo.getInstance();
        List<CarInfoBean> newestCarInfoList = userInfo.getNewestCarInfoList();
        if (newestCarInfoList.size() > 0){
            mRequestAllCarListener.OnSuccessCarInfo(newestCarInfoList);
            return;
        }else if(userInfo.getPersionCarInfo() !=null){
            newestCarInfoList.add(userInfo.getPersionCarInfo());
            mRequestAllCarListener.OnSuccessCarInfo(newestCarInfoList);
            return;

        }

//      未获取车辆信息、车辆信息不全 或 信息已过时
        String companyName = userInfo.getUserName();

        getCarInfo(companyName, new ResponseListener() {
            @Override
            public void onSuccess(LoadResult successResult) {
                List<CarInfoBean> newestCarInfoList = UserInfo.getInstance().getNewestCarInfoList();
                mRequestAllCarListener.OnSuccessCarInfo(newestCarInfoList);
            }

            @Override
            public void onError(LoadResult errorResult) {
                List<CarInfoBean> newestCarInfoList = new ArrayList<CarInfoBean>();
                mRequestAllCarListener.OnSuccessCarInfo(newestCarInfoList);

            }
        });
    }

    @Override
    public void getCarInfo(String plateNumber, final ResponseListener mResponseListener) {

        if(TextUtils.isEmpty(plateNumber) || !plateNumber.equals(UserInfo.getInstance().getUserName())){
            mResponseListener.onError(LoadResult.NO_DATA);
            return;
        }

        ResponseListener getCarInfoListener = new ResponseListener() {
            @Override
            public void onSuccess(LoadResult successResult) {

//              TODO 保持最新车辆状态信息
                BaseVo dataVo = successResult.getDataVo();
                CarInfoBean carInfoBean = null;
                if(dataVo != null && dataVo instanceof CarLatestStatus){

                    CarLatestStatus mCarLatestStatus = (CarLatestStatus) dataVo;
                    carInfoBean = mCarLatestStatus.toCarInfoBean();

                }else if(dataVo != null && dataVo instanceof ResponseCarStatusInfoBean) {

                    ResponseCarStatusInfoBean mResponseCarStatusInfoBean = (ResponseCarStatusInfoBean) dataVo;
                    carInfoBean = mResponseCarStatusInfoBean.toCarInfoBean(null);
                }

                if(carInfoBean != null) {
                    UserInfo.getInstance().setPersionCarInfo(carInfoBean);
                    UserInfo.getInstance().addNewestVarInfo(carInfoBean);
                }

                mResponseListener.onSuccess(successResult);

            }

            @Override
            public void onError(LoadResult errorResult) {

                mResponseListener.onError(errorResult);
            }
        };

        //判断调用新接口还是旧接口
        if(MainApplication.isUserResponseUDP){
            super.responseUDPGetCarInfo(plateNumber, getCarInfoListener);
        }else{
            super.getCarInfo(plateNumber, getCarInfoListener);
        }
//        getCarInfo(plateNumber, );
    }
}
