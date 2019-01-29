package com.easyder.carmonitor.presenter;

import android.support.v4.view.PagerAdapter;

import com.easyder.carmonitor.CarMonitorApplication;
import com.easyder.carmonitor.widget.orderManager.OrderInfoWidget;
import com.shinetech.mvp.DB.bean.BaseOrderInfoDB;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.bean.orderBean.OrderCtrlVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.SelectOrderListVo;
import com.shinetech.mvp.network.UDP.presenter.MaintenanceOrderPresenter;

/**
 * Created by ljn on 2017-11-10.
 */

public class MaintenanceOrderActivityPresenter extends MaintenanceOrderPresenter {


    private PagerAdapter maintenanceOrderAdapter;


    public void selectMaintenanceOrder(int maintenanceType){
        byte wayToQuery;
        String queryCondition;
        if(maintenanceType == OrderInfoWidget.MAINTENANCE_ORDER_USER_TYPE) {
            wayToQuery = 1;
            queryCondition = "";
        }else{
            wayToQuery = 5;
            queryCondition = UserInfo.getInstance().getUserName();
        }

        SelectOrderListVo selectOrderListVo = new SelectOrderListVo(wayToQuery, queryCondition, (byte)0);
        loadData(selectOrderListVo);
    }

    public void selectMaintenanceOrder(String plateNumber){
        byte wayToQuery = 1;
        SelectOrderListVo selectOrderListVo = new SelectOrderListVo(wayToQuery, plateNumber, (byte)0);
        selectOrderListVo.setOrderName(CarMonitorApplication.getInstance().getString(com.shinetech.mvp.R.string.order_name_maintenance));
        loadData(selectOrderListVo);
    }


    @Override
    public void onSuccess(LoadResult successResult) {
        super.onSuccess(successResult);
    }

    private void creatPagerAdapter(){
//        maintenanceOrderAdapter
    }

    public void deleteOrder(BaseOrderInfoDB baseOrderInfoDB){
//        (byte ctrlType, String orderName, String orderNumber, String fileType, String fileName, byte[] fileData) {
        OrderCtrlVo orderCtrlVo = new OrderCtrlVo((byte) 1, baseOrderInfoDB.getOrderName(), baseOrderInfoDB.getOrderNumber(), "", "", new byte[0]);
        loadData(orderCtrlVo);
    }

}
