package com.shinetech.mvp.network.UDP.presenter;

import com.shinetech.mvp.basePresenter.MvpBasePresenter;
import com.shinetech.mvp.network.UDP.UDPRequestCtrl;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.CarBaseInfoBean;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.listener.BaseCarInfoListener;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarBaseInfoBean;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.view.MvpView;

/**
 * Created by ljn on 2017-04-01.
 */
public class BaseCarInfoPresenter extends MvpBasePresenter<BaseVo, MvpView<BaseVo>> {

    public void getBaseCarinfo(String platenumber,final BaseCarInfoListener listener){
        UDPRequestCtrl.getInstance().request(new CarBaseInfoBean(platenumber), new ResponseListener() {
            @Override
            public void onSuccess(LoadResult successResult) {

                BaseVo dataVo = successResult.getDataVo();
                if (dataVo != null && (dataVo instanceof CarBaseInfoBean)) {
                    CarBaseInfoBean mCarInfoBean = (CarBaseInfoBean) dataVo;

//                    LogUtils.error("getCarList Success  ： PlateNumber" + mCarInfoBean.getResultPlateNumber() + "\ninfo : " + mCarInfoBean.toString());
                    listener.OnSuccess(mCarInfoBean.getResultCarInfoItem());
                }else{
                    listener.OnError("获取数据失败");
                }

            }

            @Override
            public void onError(LoadResult errorResult) {
//                System.out.println("getCarInfo error......................");
                listener.OnError(errorResult.getMessage());
            }
        });
    }

    public void getResponseCarBaseInfo(String platenumber,final BaseCarInfoListener listener){

    }
}
