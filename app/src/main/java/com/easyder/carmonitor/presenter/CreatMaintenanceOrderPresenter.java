package com.easyder.carmonitor.presenter;

import android.text.TextUtils;

import com.easyder.carmonitor.CarMonitorApplication;
import com.easyder.carmonitor.R;
import com.shinetech.mvp.DB.DBManager;
import com.shinetech.mvp.DB.bean.BaseOrderInfoDB;
import com.shinetech.mvp.DB.bean.CreateMaintenanceInfoDB;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.InfoTool.DecodeUDPDataTool;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.bean.orderBean.CreateOrUpdateOrderVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.CreateOrderDataVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.MaintenanceApplyVo;
import com.shinetech.mvp.network.UDP.presenter.UpLoadImgPresenter;
import com.shinetech.mvp.utils.TimeUtil;
import com.shinetech.mvp.utils.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by ljn on 2017-12-21.
 */

public class CreatMaintenanceOrderPresenter extends UpLoadImgPresenter {



    public void CreatMaintenanceOrder(String platenumber, String proposer, String contactNumber, String appointmentTime, String appointmentLocation, String problemDescription){
        //工单名
        String orderName = CarMonitorApplication.getInstance().getString(R.string.order_name_maintenance);

        //构建工单表格数据（0x001E）

        //字段名列表
        String[] createMaintenance = CarMonitorApplication.getInstance().getResources().getStringArray(R.array.create_maintenance_order);

        //记录 字段内容
        List<List<String>> fieldContentList = new ArrayList<>();

        ArrayList<String> fieldContentItems = new ArrayList<>();
        fieldContentItems.add(platenumber);
        fieldContentItems.add("2");
        fieldContentItems.add(TimeUtil.getTiemString(new Date(System.currentTimeMillis())));
        fieldContentItems.add(problemDescription);
        fieldContentItems.add(appointmentTime);
        fieldContentItems.add(appointmentLocation);
        fieldContentList.add(fieldContentItems);

        //工单表格转 DATA 数据
        byte[] orderListDATA = DecodeUDPDataTool.createOrderListDATA(createMaintenance, fieldContentList);

        //创建内容列表 DATA
        //内容包名称
        String maintenance_apply = CarMonitorApplication.getInstance().getString(R.string.order_content_maintenance_apply);

        LinkedHashMap<String, byte[]> stringLinkedHashMap = new LinkedHashMap<>();
        stringLinkedHashMap.put(maintenance_apply,orderListDATA);

        //内容列表 数据转 DATA
        byte[] orderContentListDATA = DecodeUDPDataTool.createOrderContentListDATA(stringLinkedHashMap);

        //创建 工单 DATA
        CreateOrderDataVo createOrderDataVo = new CreateOrderDataVo(orderName, "", proposer, contactNumber, proposer, orderContentListDATA);

        byte[] orderDate = DecodeUDPDataTool.getOrderDate(createOrderDataVo);

        CreateOrUpdateOrderVo createOrUpdateOrderVo = new CreateOrUpdateOrderVo(orderName, "", "", "", orderDate);

        loadData(createOrUpdateOrderVo);

    }

    public void CommitCreateMaintenanceOrderProcess(String orderNumber, String platenumber, String appointmentTime, String appointmentLocation, String problemDescription){

        //工单名
        String orderName = CarMonitorApplication.getInstance().getString(R.string.order_name_maintenance);

        //构建工单表格数据（0x001E）

        //字段名列表
        String[] createMaintenance = CarMonitorApplication.getInstance().getResources().getStringArray(R.array.create_maintenance_order);

        //记录 字段内容
        List<List<String>> fieldContentList = new ArrayList<>();

        ArrayList<String> fieldContentItems = new ArrayList<>();
        fieldContentItems.add(platenumber);
        fieldContentItems.add("2");
        fieldContentItems.add(TimeUtil.getTiemString(new Date(System.currentTimeMillis())));
        fieldContentItems.add(problemDescription);
        fieldContentItems.add(appointmentTime);
        fieldContentItems.add(appointmentLocation);
        fieldContentList.add(fieldContentItems);

        //工单表格转 DATA 数据
        byte[] orderListDATA = DecodeUDPDataTool.createOrderListDATA(createMaintenance, fieldContentList);

        MaintenanceApplyVo maintenanceApplyVo = new MaintenanceApplyVo(orderName, orderNumber, orderListDATA);
        loadData(maintenanceApplyVo);
    }

    @Override
    public void onSuccess(LoadResult successResult) {
        BaseVo dataVo = successResult.getDataVo();
        if(dataVo instanceof CreateOrUpdateOrderVo){
            CreateOrUpdateOrderVo mCreateOrUpdateOrderVo = (CreateOrUpdateOrderVo) dataVo;
            byte[] contentList = mCreateOrUpdateOrderVo.getContentList();

            //TODO save to create order info（保持创建工单填写的数据）

            String orderNumber = mCreateOrUpdateOrderVo.getResultOrderNumber();

            CreateMaintenanceInfoDB createMaintenanceInfoDB = new CreateMaintenanceInfoDB();

            createMaintenanceInfoDB.setOrderNumber(orderNumber);
            createMaintenanceInfoDB.setProposer(mCreateOrUpdateOrderVo.getClienteleName());
            createMaintenanceInfoDB.setContactNumber(mCreateOrUpdateOrderVo.getClientelePhone());
            createMaintenanceInfoDB.setRepairTime(TimeUtil.getTiemString(new Date(System.currentTimeMillis())));

            String maintenance_apply = CarMonitorApplication.getInstance().getString(R.string.order_content_maintenance_apply);
            DecodeUDPDataTool.OrderContentListItemData orderContentListItemData = DecodeUDPDataTool.decodeItemByDataName(contentList, maintenance_apply);

            String plate_number_str = CarMonitorApplication.getInstance().getString(R.string.plate_number);
            String platenumber = orderContentListItemData.getValue(plate_number_str, 0);
            createMaintenanceInfoDB.setPlateNumber(platenumber);


            String appointmentTime_str = CarMonitorApplication.getInstance().getString(R.string.create_maintenanceorder_appointment_time);
            String appointmentTime = orderContentListItemData.getValue(appointmentTime_str, 0);
            createMaintenanceInfoDB.setAppointmentTime(appointmentTime);

            String appointmentLocation_str = CarMonitorApplication.getInstance().getString(R.string.create_maintenanceorder_appointment_location);
            String appointmentLocation = orderContentListItemData.getValue(appointmentLocation_str, 0);
            createMaintenanceInfoDB.setAppointmentLocation(appointmentLocation);

            String errordescribe_str = CarMonitorApplication.getInstance().getString(R.string.create_maintenanceorder_errordescribe);
            String errordescribe = orderContentListItemData.getValue(errordescribe_str, 0);
            createMaintenanceInfoDB.setProblemDescription(errordescribe);

            DBManager.insertCreateMaintenanceInfo(createMaintenanceInfoDB);

            //保持工单
            BaseOrderInfoDB baseOrderInfoDB = new BaseOrderInfoDB(UserInfo.getInstance().getUserName(), CarMonitorApplication.getInstance().getString(R.string.order_name_maintenance), orderNumber,
                    (byte)0, mCreateOrUpdateOrderVo.getClienteleName(), mCreateOrUpdateOrderVo.getClientelePhone(), mCreateOrUpdateOrderVo.getFounder());

            List<CreateMaintenanceInfoDB> createMaintenanceInfoDBs = DBManager.querySelectCreateMaintenanceInfo(orderNumber);

            if(createMaintenanceInfoDBs != null && createMaintenanceInfoDBs.size()>0) {
                baseOrderInfoDB.setData(createMaintenanceInfoDBs.get(0));
            }

            DBManager.insertBaseOrderInfo(baseOrderInfoDB);

        }else if(dataVo instanceof MaintenanceApplyVo){
            MaintenanceApplyVo mMaintenanceApplyVo = (MaintenanceApplyVo) dataVo;
            String orderNumber = mMaintenanceApplyVo.getOrderNumber();
            BaseOrderInfoDB baseOrderInfoDB = DBManager.querySelectBaseOrderInfo(UserInfo.getInstance().getUserName(), orderNumber);
            if(baseOrderInfoDB != null && baseOrderInfoDB.getData() != null){
                CreateMaintenanceInfoDB data = baseOrderInfoDB.getData();
                DBManager.deleteCreateMaintenanceInfo(data);
                baseOrderInfoDB.setDataInfoId(-1);
                DBManager.updataBaseOrderInfo(baseOrderInfoDB);
            }
        }
        super.onSuccess(successResult);
    }

    @Override
    public void onError(LoadResult errorResult) {
       /* BaseVo dataVo = errorResult.getDataVo();
        if(dataVo instanceof CreateMaintenanceVo){
            super.onError(errorResult);
        }else if(dataVo instanceof UpLoadIMGVo){
            super.onSuccess(errorResult);
        }*/
        super.onError(errorResult);
    }
}
