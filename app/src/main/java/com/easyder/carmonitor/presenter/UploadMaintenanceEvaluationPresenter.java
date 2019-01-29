package com.easyder.carmonitor.presenter;

import com.easyder.carmonitor.CarMonitorApplication;
import com.easyder.carmonitor.R;
import com.shinetech.mvp.DB.bean.BaseOrderInfoDB;
import com.shinetech.mvp.network.UDP.InfoTool.DecodeUDPDataTool;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.bean.orderBean.CustomerEvaluationVo;
import com.shinetech.mvp.network.UDP.presenter.UpLoadImgPresenter;
import com.shinetech.mvp.utils.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ljn on 2017-12-25.
 */

public class UploadMaintenanceEvaluationPresenter extends UpLoadImgPresenter {

    public void uploadEvaluation(BaseOrderInfoDB baseOrderInfoDB, String platenumber, int terminalStatus, String carStatus, String valuation, int starRating){

        //构建工单表格数据（0x001E）

        //字段名列表
        String[] maintenance_evaluation_field = CarMonitorApplication.getInstance().getResources().getStringArray(R.array.maintenance_evaluation_field);

        //记录 字段内容
        List<List<String>> fieldContentList = new ArrayList<>();

        ArrayList<String> fieldContentItems = new ArrayList<>();

        String orderName = baseOrderInfoDB.getOrderName();
        String orderNumber = baseOrderInfoDB.getOrderNumber();

        fieldContentItems.add(orderName);
        fieldContentItems.add(orderNumber);
        fieldContentItems.add("1");
        fieldContentItems.add(TimeUtil.getTiemString(new Date(System.currentTimeMillis())));
        fieldContentItems.add(baseOrderInfoDB.getClienteleName());
        fieldContentItems.add(baseOrderInfoDB.getClientelePhone());

        String[] split = platenumber.split(" ");
        if(split.length == 2) {
            fieldContentItems.add(split[1]);
        }else{
            fieldContentItems.add(platenumber);
        }



        fieldContentItems.add(""+terminalStatus);
        fieldContentItems.add(carStatus);
        fieldContentItems.add(valuation);
        fieldContentItems.add(""+(starRating>0 ? (starRating-1): starRating));
        fieldContentList.add(fieldContentItems);

        //工单表格转 DATA 数据
        byte[] orderListDATA = DecodeUDPDataTool.createOrderListDATA(maintenance_evaluation_field, fieldContentList);

        CustomerEvaluationVo customerEvaluationVo = new CustomerEvaluationVo(orderName, orderNumber, orderListDATA);

        loadData(customerEvaluationVo);

    }

    @Override
    public void onSuccess(LoadResult successResult) {
        BaseVo dataVo = successResult.getDataVo();
        if(dataVo instanceof CustomerEvaluationVo){
            CustomerEvaluationVo mCustomerEvaluationVo = (CustomerEvaluationVo) dataVo;

            if(mCustomerEvaluationVo.getResult() == 1){

                super.onSuccess(successResult);
                return;

            }else{
                onError(LoadResult.STATUS_ERROR.setDataVo(mCustomerEvaluationVo).setMessage(mCustomerEvaluationVo.getResultReason()));
                return;
            }

        }

        super.onSuccess(successResult);
    }

    @Override
    public void onError(LoadResult errorResult) {
        super.onError(errorResult);
    }



}
