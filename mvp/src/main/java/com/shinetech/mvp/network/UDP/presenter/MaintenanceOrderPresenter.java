package com.shinetech.mvp.network.UDP.presenter;

import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.basePresenter.MvpBasePresenter;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.CarHistoricalRouteBean;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarHistoricalRouteBean;
import com.shinetech.mvp.view.MvpView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ljn on 2017-04-25.
 */
public class MaintenanceOrderPresenter extends MvpBasePresenter<BaseVo, MvpView<BaseVo>> {

    public void getAllMaintenanceOrder(String platenumber, String startTime, String endTime){

        if(MainApplication.isUserResponseUDP){

            long time=System.currentTimeMillis();
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1=new Date(time);
            String operationInfo = "CarHistorical "+format.format(d1);

            ResponseCarHistoricalRouteBean mResponseCarHistoricalRouteBean = new ResponseCarHistoricalRouteBean(operationInfo, startTime, endTime, platenumber);
            loadData(mResponseCarHistoricalRouteBean);
            return;
        }
    }

}
