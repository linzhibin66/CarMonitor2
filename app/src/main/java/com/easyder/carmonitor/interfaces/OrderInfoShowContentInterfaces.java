package com.easyder.carmonitor.interfaces;

import com.shinetech.mvp.DB.bean.BaseOrderInfoDB;
import com.shinetech.mvp.DB.bean.InstallOrderBaseInfo;
import com.shinetech.mvp.DB.bean.MaintenanceOrderInfoBean;

/**
 * Created by ljn on 2017-11-16.
 */

public interface OrderInfoShowContentInterfaces {

    /**
     * 显示维修单信息
     * @param baseOrderInfoDB
     */
    void showMaintenanceOrderInfo(BaseOrderInfoDB baseOrderInfoDB);

    /**
     * 显示服务评价界面
     * @param baseOrderInfoDB
     * @param plateNumber
     */
    void showMaintenanceOrderValuation(BaseOrderInfoDB baseOrderInfoDB, String plateNumber);

    /**
     * 显示创建维修单界面
     */
    void ShowCreatMaintenanceOrder(String orderNumber);

    /**
     * 删除工单
     */
    void deleteOrder(BaseOrderInfoDB baseOrderInfoDB);

    /**
     * 接单
     * @param baseOrderInfoDB
     */
    void acceptOrder(BaseOrderInfoDB baseOrderInfoDB);

    /**
     * 上传维修结果
     * @param baseOrderInfoDB
     */
    void upLoadMaintenanceResult(BaseOrderInfoDB baseOrderInfoDB);

    /**
     * 上传安装结果
     * @param installOrderitem
     */
    void upLoadinstallResult(InstallOrderBaseInfo installOrderitem);

    /**
     * 显示安装单信息
     * @param installOrderitem
     */
    void showInstallOrderInfo(InstallOrderBaseInfo installOrderitem);

    void showSearchPlateNumberLayout(String serachPlateNumber, CreatOrderSearchListener listener);

    void dismissHintLayout();

}
