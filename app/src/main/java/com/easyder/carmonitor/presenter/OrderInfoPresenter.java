package com.easyder.carmonitor.presenter;

import android.content.Context;

import com.easyder.carmonitor.CarMonitorApplication;
import com.easyder.carmonitor.R;
import com.shinetech.mvp.DB.DBManager;
import com.shinetech.mvp.DB.bean.BaseOrderInfoDB;
import com.shinetech.mvp.DB.bean.CreateMaintenanceInfoDB;
import com.shinetech.mvp.DB.bean.InstallTerminalnfo;
import com.shinetech.mvp.DB.bean.MaintenanceOrderInfoBean;
import com.shinetech.mvp.basePresenter.MvpBasePresenter;
import com.shinetech.mvp.network.UDP.InfoTool.DecodeUDPDataTool;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.bean.orderBean.AttachmentItemVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.InstallFinishVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.MaintenanceOrderProgress;
import com.shinetech.mvp.network.UDP.bean.orderBean.SelectOrderByNumberVo;
import com.shinetech.mvp.view.MvpView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by ljn on 2017-12-27.
 */

public class OrderInfoPresenter extends MvpBasePresenter<BaseVo, MvpView<BaseVo>> {

    public void requestOrderInfo(String orderNumber, String orderName){
        SelectOrderByNumberVo selectOrderByNumberVo = new SelectOrderByNumberVo(orderNumber, orderName);
        loadData(selectOrderByNumberVo);

    }

    public void upLoadinstallResult(Context context, String orderNumber, String orderName, List<InstallTerminalnfo> installTerminalnfoList){

        //维修单申请
        List<List<String>> fieldContentList = new ArrayList<>();

        if(installTerminalnfoList != null && installTerminalnfoList.size()>0){

            for(int i = 0; i<installTerminalnfoList.size(); i++) {
                InstallTerminalnfo installTerminalnfo = installTerminalnfoList.get(i);
                List<String> fieldContentItem = new ArrayList<>();
                fieldContentItem.add(orderName);
                fieldContentItem.add(orderNumber);
                fieldContentItem.add(installTerminalnfo.getPlateNumber());
                fieldContentItem.add(installTerminalnfo.getVin());
                fieldContentItem.add(installTerminalnfo.getTerminalType());
                fieldContentItem.add(installTerminalnfo.getTerminalID());
                fieldContentItem.add(installTerminalnfo.getSimCard());
                fieldContentItem.add(installTerminalnfo.getInstallTime());
                fieldContentItem.add(installTerminalnfo.getDeviceStatus());
                fieldContentItem.add(installTerminalnfo.getInstallationPersonnel());

                fieldContentList.add(fieldContentItem);
            }

        }

        String[] stringArray = context.getResources().getStringArray(R.array.install_record_order);
        byte[] orderListDATA = DecodeUDPDataTool.createOrderListDATA(stringArray, fieldContentList);

        InstallFinishVo installFinishVo = new InstallFinishVo(orderName, orderNumber, orderListDATA);
        loadData(installFinishVo);

    }

    @Override
    public void onSuccess(LoadResult successResult) {

        super.onSuccess(successResult);
    }

    @Override
    public void onError(LoadResult errorResult) {
        super.onError(errorResult);
    }

    public void saveorderStatus(SelectOrderByNumberVo orderinfoVo){

    }

    public int getOrderStatus(SelectOrderByNumberVo orderInfo){

        byte[] processList = orderInfo.getProcessList();
        if(processList != null && processList.length>0) {

            List<MaintenanceOrderProgress> orderProgress = DecodeUDPDataTool.getOrderProgress(processList);

            for(int i = 0; i<orderProgress.size(); i++){
                MaintenanceOrderProgress maintenanceOrderProgress = orderProgress.get(i);
                byte disposeResult = maintenanceOrderProgress.getDisposeResult();
                if(disposeResult == 0){
                    return getOrderStatus(orderInfo.getResultOrderName(),maintenanceOrderProgress);
                }
            }
            return MaintenanceOrderInfoBean.FINISH_STATUS;

        }

        return -1;

    }

    private int getOrderStatus(String orderName, MaintenanceOrderProgress orderProgress){
            String progressName = orderProgress.getProgressName();
        if(orderName.equals(CarMonitorApplication.getInstance().getString(com.shinetech.mvp.R.string.order_name_maintenance))){
            String[] ordreMaintenanceProgress = CarMonitorApplication.getInstance().getResources().getStringArray(com.shinetech.mvp.R.array.ordre_maintenance_progress);
            for(int i = 0; i<ordreMaintenanceProgress.length; i++ ){
                if(progressName.equals(ordreMaintenanceProgress[i])){
                    switch (i){
                        case 0:
                        case 1:
                        case 2:
                            return MaintenanceOrderInfoBean.COMMIT_STATUS;
                        case 3:
                            return MaintenanceOrderInfoBean.PROCESSED_STATUS;
                        case 4:
                            return MaintenanceOrderInfoBean.EVALUATE_STATUS;
                    }
                }
            }


        }else if(orderName.equals(CarMonitorApplication.getInstance().getString(com.shinetech.mvp.R.string.order_name_install))){
            String[] ordreInstallProgress = CarMonitorApplication.getInstance().getResources().getStringArray(com.shinetech.mvp.R.array.ordre_install_progress);
            for(int i = 0; i<ordreInstallProgress.length; i++ ){
                if(progressName.equals(ordreInstallProgress[i])){
                    switch (i){
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                            return MaintenanceOrderInfoBean.COMMIT_STATUS;
                        case 6:
                            return MaintenanceOrderInfoBean.PROCESSED_STATUS;
                        case 7:
                        case 8:
                            return MaintenanceOrderInfoBean.FINISH_STATUS;
                    }
                }
            }

        }

        return -1;
    }

    public SelectOrderByNumberVo getTestData(Context context, BaseOrderInfoDB baseOrderInfoDB){

        SelectOrderByNumberVo mSelectOrderByNumberVo = new SelectOrderByNumberVo(baseOrderInfoDB.getOrderNumber(), baseOrderInfoDB.getOrderName());
        mSelectOrderByNumberVo.setResultOrderName(baseOrderInfoDB.getOrderName());
        mSelectOrderByNumberVo.setOrderNumber(baseOrderInfoDB.getOrderNumber());
        mSelectOrderByNumberVo.setOrderStatus(baseOrderInfoDB.getOrderStatus());
        mSelectOrderByNumberVo.setClienteleName(baseOrderInfoDB.getClienteleName());
        mSelectOrderByNumberVo.setClientelePhone(baseOrderInfoDB.getClientelePhone());

        mSelectOrderByNumberVo.setFounder(baseOrderInfoDB.getFounder());

        if(baseOrderInfoDB.getOrderName().equals(context.getString(R.string.order_name_install))){
            return creatInstallOrderTest(context ,mSelectOrderByNumberVo);
        }

        List<CreateMaintenanceInfoDB> createMaintenanceInfoDBs = DBManager.querySelectCreateMaintenanceInfo(baseOrderInfoDB.getOrderNumber());

        //维修单申请
        List<List<String>> fieldContentList = new ArrayList<>();
        List<String> fieldContentItem = new ArrayList<>();
        if(createMaintenanceInfoDBs != null && createMaintenanceInfoDBs.size()>0){
            CreateMaintenanceInfoDB createMaintenanceInfoDB = createMaintenanceInfoDBs.get(0);
            fieldContentItem.add(createMaintenanceInfoDB.getPlateNumber());
            fieldContentItem.add("2");
            fieldContentItem.add(createMaintenanceInfoDB.getRepairTime());
            fieldContentItem.add(createMaintenanceInfoDB.getProblemDescription());
            fieldContentItem.add(createMaintenanceInfoDB.getAppointmentTime());
            fieldContentItem.add(createMaintenanceInfoDB.getAppointmentLocation());

        }

        fieldContentList.add(fieldContentItem);
        String[] stringArray = context.getResources().getStringArray(R.array.create_maintenance_order);
        byte[] orderListDATA = DecodeUDPDataTool.createOrderListDATA(stringArray, fieldContentList);

        String applyItem = context.getString(R.string.order_content_maintenance_apply);
        LinkedHashMap<String, byte[]> stringLinkedHashMap = new LinkedHashMap<>();
        stringLinkedHashMap.put(applyItem, orderListDATA);


        //客户评价
        List<List<String>> evaluateContentList = new ArrayList<>();
        List<String> evaluateContentItem = new ArrayList<>();
        if(createMaintenanceInfoDBs != null && createMaintenanceInfoDBs.size()>0){

                /*
                <item>回访评价途径</item>
                <item>评价时间</item>
                <item>车牌</item>
                <item>设备状态</item>
                <item>车辆状况</item>
                <item>客户意见</item>
                <item>服务评价</item>
                */
            CreateMaintenanceInfoDB createMaintenanceInfoDB = createMaintenanceInfoDBs.get(0);
            evaluateContentItem.add("1");
            evaluateContentItem.add("2018-1-16 09:30:38");
            evaluateContentItem.add(createMaintenanceInfoDB.getPlateNumber());
            evaluateContentItem.add("1");
            evaluateContentItem.add("定位正常");
            evaluateContentItem.add("服务态度好，维修师傅技术高");
            evaluateContentItem.add("4");

        }

        evaluateContentList.add(evaluateContentItem);
        String[] evaluateStringArray = context.getResources().getStringArray(R.array.evaluate_maintenance_order);
        byte[] evaluateListDATA = DecodeUDPDataTool.createOrderListDATA(evaluateStringArray, evaluateContentList);

        String evaluateItem = context.getString(R.string.order_content_maintenance_evaluate);

        stringLinkedHashMap.put(evaluateItem, evaluateListDATA);

        //维修结果
        List<List<String>> maintenanceResultContentList = new ArrayList<>();
        List<String> maintenanceResultContentItem = new ArrayList<>();
        if(createMaintenanceInfoDBs != null && createMaintenanceInfoDBs.size()>0){

                /*
                <item>故障分析</item>
                <item>维修措施</item>
                <item>设备状态</item>
                <item>维修人员</item>
                <item>维修日期</item>
                */
            maintenanceResultContentItem.add("天线松动了");
            maintenanceResultContentItem.add("重新安装天线");
            maintenanceResultContentItem.add("1");
            maintenanceResultContentItem.add("李师傅");
            maintenanceResultContentItem.add("2018-1-16 09:30:38");

        }

        maintenanceResultContentList.add(maintenanceResultContentItem);
        String[] maintenanceResultStringArray = context.getResources().getStringArray(R.array.result_maintenance_order);
        byte[] maintenanceResultListDATA = DecodeUDPDataTool.createOrderListDATA(maintenanceResultStringArray, maintenanceResultContentList);

        String maintenanceResultItem = context.getString(R.string.order_content_maintenance_result);

        stringLinkedHashMap.put(maintenanceResultItem, maintenanceResultListDATA);

        //创建工单内容data
        byte[] orderContentListDATA = DecodeUDPDataTool.createOrderContentListDATA(stringLinkedHashMap);

        mSelectOrderByNumberVo.setContentList(orderContentListDATA);

        List<BaseVo> jpgList = new ArrayList<>();
        for(int i = 0; i<2; i++){
            AttachmentItemVo jpgItem = new AttachmentItemVo("jpg", "priture" + i +".jpg", "ly" + i, "2018-1-1" + i);
            jpgList.add(jpgItem);
        }
        for(int i = 2; i<4; i++){
            AttachmentItemVo jpgItem = new AttachmentItemVo("pdf", "priture" + i+".pdf", "ly" + i, "2018-1-1" + i);
            jpgList.add(jpgItem);
        }
        for(int i = 4; i<5; i++){
            AttachmentItemVo jpgItem = new AttachmentItemVo("doc", "priture" + i +".doc", "ly" + i, "2018-1-1" + i);
            jpgList.add(jpgItem);
        }

        byte[] setAttachmentbytes = DecodeUDPDataTool.listToByteArray(jpgList);

        mSelectOrderByNumberVo.setAttachmentList(setAttachmentbytes);

        List<BaseVo> progressList = new ArrayList<>();

        MaintenanceOrderProgress progressItem = new MaintenanceOrderProgress("新建工单", "yoyo" , "2018-1-1 07:59:53" , (byte) 2, "正常");
        progressList.add(progressItem);

        MaintenanceOrderProgress progressItem1 = new MaintenanceOrderProgress("客服派单", "yoyo" , "2018-1-1 08:30:00" , (byte) 2, "正常");
        progressList.add(progressItem1);

        MaintenanceOrderProgress progressItem2 = new MaintenanceOrderProgress("师傅接单", "yoyo" , "2018-1-1 08:30:00" , (byte) 2, "正常");
        progressList.add(progressItem2);

        MaintenanceOrderProgress progressItem3 = new MaintenanceOrderProgress("师傅维修", "yoyo" , "2018-1-1 08:30:00" , (byte) 0, "正常");
        progressList.add(progressItem3);

        MaintenanceOrderProgress progressItem4 = new MaintenanceOrderProgress("客服回访评价", "yoyo" , "2018-1-1 08:30:00" , (byte) 0, "正常");
        progressList.add(progressItem4);


        byte[] progressbytes = DecodeUDPDataTool.listToByteArray(progressList);
        mSelectOrderByNumberVo.setProcessList(progressbytes);

        return mSelectOrderByNumberVo;

    }

    private SelectOrderByNumberVo creatInstallOrderTest(Context context, SelectOrderByNumberVo mSelectOrderByNumberVo){

        //安装申请表
        List<List<String>> fieldContentList = new ArrayList<>();
        List<String> fieldContentItem = new ArrayList<>();

        //item
        /*
            <item>客户名称</item>
            <item>客户电话</item>
            <item>车辆类型</item>
            <item>安装地址</item>
            <item>安装形式</item>
            <item>安装台数</item>
            <item>要求完成时间</item>
        * */
        fieldContentItem.add("广东华盈光达科技有限公司");
        fieldContentItem.add("0769-22300444");
        fieldContentItem.add("0");
        fieldContentItem.add("东莞市文化广场");
        fieldContentItem.add("0");
        fieldContentItem.add("50");
        fieldContentItem.add("2018-2-10 23:00:00");



        fieldContentList.add(fieldContentItem);
        String[] stringArray = context.getResources().getStringArray(R.array.install_baseinfo_order);
        byte[] orderListDATA = DecodeUDPDataTool.createOrderListDATA(stringArray, fieldContentList);

        String applyItem = context.getString(R.string.order_content_install_apply);
        LinkedHashMap<String, byte[]> stringLinkedHashMap = new LinkedHashMap<>();
        stringLinkedHashMap.put(applyItem, orderListDATA);


        //安装记录表
        List<List<String>> instatllRecordContentList = new ArrayList<>();

        /*
        <item>车牌号</item>
        <item>车架号</item>
        <item>终端型号</item>
        <item>终端ID</item>
        <item>SIM卡号</item>
        <item>安装日期</item>
        <item>设备运行情况</item>
        <item>安装人员</item>
        */

        for(int i = 0; i<5; i++) {
            List<String> instatllRecordContentItem = new ArrayList<>();
            instatllRecordContentItem.add("蓝 粤S8888"+i);
            instatllRecordContentItem.add("SDF245841311KRCK254"+i);
            instatllRecordContentItem.add("28888"+i);
            instatllRecordContentItem.add("5666"+i);
            instatllRecordContentItem.add("1388888888"+i);
//            instatllRecordContentItem.add("2018-1-08 15:00:00");
            instatllRecordContentItem.add("");
            instatllRecordContentItem.add("");
            instatllRecordContentItem.add("");
            instatllRecordContentList.add(instatllRecordContentItem);
        }

        String[] instatllRecorstringArray = context.getResources().getStringArray(R.array.install_record_order);

        byte[] instatllRecororderListDATA = DecodeUDPDataTool.createOrderListDATA(instatllRecorstringArray, instatllRecordContentList);
        String instatllRecorItem = context.getString(R.string.order_content_install_record);
        stringLinkedHashMap.put(instatllRecorItem, instatllRecororderListDATA);

        //创建工单内容data
        byte[] orderContentListDATA = DecodeUDPDataTool.createOrderContentListDATA(stringLinkedHashMap);

        mSelectOrderByNumberVo.setContentList(orderContentListDATA);

        List<BaseVo> progressList = new ArrayList<>();
        /*
            <item>新建工单</item>
            <item>总经理审批</item>
            <item>物料出库</item>
            <item>资料打印</item>
            <item>装维安排</item>
            <item>师傅接单</item>
            <item>设备安装</item>
            <item>资料更新</item>
            <item>客服回访评价</item>
        */
        MaintenanceOrderProgress progressItem = new MaintenanceOrderProgress("新建工单", "yoyo" , "2018-1-1 07:59:53" , (byte) 2, "正常");
        progressList.add(progressItem);

        MaintenanceOrderProgress progressItem1 = new MaintenanceOrderProgress("总经理审批", "yoyo" , "2018-1-1 08:30:00" , (byte) 2, "正常");
        progressList.add(progressItem1);

        MaintenanceOrderProgress progressItem2 = new MaintenanceOrderProgress("物料出库", "yoyo" , "2018-1-1 08:30:00" , (byte) 2, "正常");
        progressList.add(progressItem2);

        MaintenanceOrderProgress progressItem3 = new MaintenanceOrderProgress("资料打印", "yoyo" , "2018-1-1 08:30:00" , (byte) 2, "正常");
        progressList.add(progressItem3);

        MaintenanceOrderProgress progressItem4 = new MaintenanceOrderProgress("装维安排", "yoyo" , "2018-1-1 08:30:00" , (byte) 2, "正常");
        progressList.add(progressItem4);

        MaintenanceOrderProgress progressItem5 = new MaintenanceOrderProgress("师傅接单", "yoyo" , "2018-1-1 08:30:00" , (byte) 2, "正常");
        progressList.add(progressItem5);

        MaintenanceOrderProgress progressItem6 = new MaintenanceOrderProgress("设备安装", "yoyo" , "2018-1-1 08:30:00" , (byte) 0, "正常");
        progressList.add(progressItem6);

        MaintenanceOrderProgress progressItem7 = new MaintenanceOrderProgress("资料更新", "yoyo" , "2018-1-1 08:30:00" , (byte) 0, "正常");
        progressList.add(progressItem7);

        MaintenanceOrderProgress progressItem8 = new MaintenanceOrderProgress("客服回访评价", "yoyo" , "2018-1-1 08:30:00" , (byte) 0, "正常");
        progressList.add(progressItem8);

        byte[] progressbytes = DecodeUDPDataTool.listToByteArray(progressList);
        mSelectOrderByNumberVo.setProcessList(progressbytes);

        return mSelectOrderByNumberVo;

    }
}
