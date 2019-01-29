package com.shinetech.mvp.network.UDP.presenter;

import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.basePresenter.MvpBasePresenter;
import com.shinetech.mvp.network.UDP.UDPRequestCtrl;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.CarHistoricalRouteBean;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarHistoricalRouteBean;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseViolationLogBean;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.view.MvpView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ljn on 2017-04-25.
 */
public class AlarmLogPresenter extends MvpBasePresenter<BaseVo, MvpView<BaseVo>> {

   protected void getViolationLogResponse(String platenumber, String startTime, String endTime, ResponseListener mResponseListener){

      long time=System.currentTimeMillis();
      SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      Date d1=new Date(time);
      String operationInfo = "ViolationLog "+format.format(d1);

      ResponseViolationLogBean mResponseViolationLogBean = new ResponseViolationLogBean(operationInfo, startTime, endTime,platenumber);

//      loadData(mResponseViolationLogBean);
      UDPRequestCtrl.getInstance().request(mResponseViolationLogBean, mResponseListener);

      /*new ResponseListener() {

         @Override
         public void onSuccess(LoadResult successResult) {
            BaseVo dataVo = successResult.getDataVo();
            if(dataVo !=null){
               ResponseViolationLogBean mResponseViolationLogBean = (ResponseViolationLogBean) dataVo;
               System.out.println(mResponseViolationLogBean.toString());
            }else{
               LogUtils.error("getCarHistoricalResponse dataVo is null  ");
            }
         }

         @Override
         public void onError(LoadResult errorResult) {
            LogUtils.error("getCarHistoricalResponse onError : "+errorResult.getMessage());
         }
      }*/
   }


}
