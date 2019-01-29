package com.easyder.carmonitor.interfaces;

import com.shinetech.mvp.DB.bean.InstallOrderBaseInfo;

/**
 * Created by ljn on 2017-11-28.
 */

public interface InstallChangeStatusListener {

    void acceptOrder(InstallOrderBaseInfo installOrderitem);
    void upLoadDialogFocusable(boolean focusable);
    void showInstallCarList(InstallOrderBaseInfo installOrderitem);
}
