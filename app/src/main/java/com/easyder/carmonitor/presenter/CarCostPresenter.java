package com.easyder.carmonitor.presenter;

import com.shinetech.mvp.basePresenter.MvpBasePresenter;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.bean.orderBean.CarCostVo;
import com.shinetech.mvp.view.MvpView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ljn on 2017-12-27.
 */

public class CarCostPresenter extends MvpBasePresenter<BaseVo, MvpView<BaseVo>> {

    public void requestCarCost(String plateNumber){
        CarCostVo carCostVo = new CarCostVo(plateNumber);
        loadData(carCostVo);

    }

    @Override
    public void onSuccess(LoadResult successResult) {
        super.onSuccess(successResult);
    }

    @Override
    public void onError(LoadResult errorResult) {
        BaseVo dataVo = errorResult.getDataVo();
        if(dataVo instanceof CarCostVo){
            CarCostVo mCarCostVo = (CarCostVo) dataVo;
            if(mCarCostVo.getAllArrearage() != -1 && mCarCostVo.getArrearageMonth() != -1){
                onSuccess(LoadResult.STATUS_SUCCESS.setDataVo(mCarCostVo));
                return;
            }
        }

        super.onError(errorResult);
    }
}
