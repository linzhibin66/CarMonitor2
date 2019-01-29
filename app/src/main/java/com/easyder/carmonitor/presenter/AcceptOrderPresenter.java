package com.easyder.carmonitor.presenter;

import com.shinetech.mvp.basePresenter.MvpBasePresenter;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.bean.orderBean.AcceptOrderVo;
import com.shinetech.mvp.view.MvpView;

/**
 * Created by Lenovo on 2017-12-26.
 */

public class AcceptOrderPresenter extends MvpBasePresenter<BaseVo, MvpView<BaseVo>> {

    public void acceptOrder(String orderName, String orderNumber, byte isAccept, String reason){
        AcceptOrderVo acceptOrderVo = new AcceptOrderVo(orderName, orderNumber, isAccept, reason);
        loadData(acceptOrderVo);
    }

    @Override
    public void onSuccess(LoadResult successResult) {

        BaseVo dataVo = successResult.getDataVo();

        if(dataVo instanceof AcceptOrderVo) {

            AcceptOrderVo mAcceptOrderVo = (AcceptOrderVo) dataVo;

            if (mAcceptOrderVo.getResult() == 1) {
                super.onSuccess(successResult);
            } else {
                super.onError(LoadResult.STATUS_ERROR.setMessage(mAcceptOrderVo.getResultReason()));
            }
        }

        super.onSuccess(successResult);
    }

    @Override
    public void onError(LoadResult errorResult) {
        super.onError(errorResult);
    }
}
