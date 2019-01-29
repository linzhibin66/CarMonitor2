package com.easyder.carmonitor.interfaces;

import com.shinetech.mvp.DB.bean.InstallTerminalnfo;
import com.shinetech.mvp.network.UDP.bean.orderBean.AttachmentItemVo;

import java.util.List;

/**
 * Created by Lenovo on 2017-12-07.
 */

public interface InstallOrderCarInfoChangeListener {

    void ShowToTerminalnfo(int status, InstallTerminalnfo mInstallTerminalnfo, List<AttachmentItemVo> attachmentItemList);
}
