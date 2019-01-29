package com.easyder.carmonitor.interfaces;

import com.shinetech.mvp.DB.bean.BaseOrderInfoDB;
import com.shinetech.mvp.DB.bean.InstallTerminalnfo;
import com.shinetech.mvp.network.UDP.InfoTool.DecodeUDPDataTool;
import com.shinetech.mvp.network.UDP.bean.orderBean.AttachmentItemVo;

import java.util.List;

/**
 * Created by ljn on 2017-11-28.
 */

public interface MaintenanceChangeStatusListener {

    void acceptOrder(BaseOrderInfoDB baseOrderInfoDB);
    void upLoadMaintenanceResult(BaseOrderInfoDB baseOrderInfoDB, List<String> attachmentPathList);
    void showMaintenanceOrderValuation(BaseOrderInfoDB baseOrderInfoDB, String plateNumber);
    void showInstallCarList(int orderStatus, String orderName, String orderNumber, DecodeUDPDataTool.OrderContentListItemData orderContentListItemData, List<AttachmentItemVo> attachmentItemList);
}
