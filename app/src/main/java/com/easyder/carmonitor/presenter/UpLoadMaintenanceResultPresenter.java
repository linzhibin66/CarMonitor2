package com.easyder.carmonitor.presenter;

import com.easyder.carmonitor.CarMonitorApplication;
import com.easyder.carmonitor.R;
import com.shinetech.mvp.network.UDP.InfoTool.DecodeUDPDataTool;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.bean.orderBean.MaintenanceFinishVo;
import com.shinetech.mvp.network.UDP.presenter.UpLoadImgPresenter;
import com.shinetech.mvp.utils.TimeUtil;
import com.shinetech.mvp.utils.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by ljn on 2017-12-26.
 */

public class UpLoadMaintenanceResultPresenter extends UpLoadImgPresenter {

    public void uploadMaintenanceResult(String orderName, String orderNumber, String faultAnalyze, String maintenanceMeasures, int terminalStatus, String maintenancePersonnel){


        //构建工单表格数据（0x001E）

        //字段名列表
        String[] maintenance_result_field = CarMonitorApplication.getInstance().getResources().getStringArray(R.array.maintenance_result_field);

        //记录 字段内容
        List<List<String>> fieldContentList = new ArrayList<>();

        ArrayList<String> fieldContentItems = new ArrayList<>();
        fieldContentItems.add(faultAnalyze);
        fieldContentItems.add(maintenanceMeasures);
        fieldContentItems.add(""+terminalStatus);
        fieldContentItems.add(maintenancePersonnel);
        fieldContentItems.add(TimeUtil.getTiemString(new Date(System.currentTimeMillis())));
        fieldContentList.add(fieldContentItems);

        //工单表格转 DATA 数据
        byte[] orderListDATA = DecodeUDPDataTool.createOrderListDATA(maintenance_result_field, fieldContentList);

        MaintenanceFinishVo maintenanceFinishVo = new MaintenanceFinishVo(orderName, orderNumber, orderListDATA);
        loadData(maintenanceFinishVo);
    }

    @Override
    public void onSuccess(LoadResult successResult) {
        super.onSuccess(successResult);
    }

    @Override
    public void onError(LoadResult errorResult) {
        super.onError(errorResult);
    }
}
