package com.easyder.carmonitor.interfaces;

import com.easyder.carmonitor.dialog.BasePopupWindowDialog;

/**
 * Created by ljn on 2018-04-03.
 */

public interface CarCostBackListener {

    void onBack(BasePopupWindowDialog dialog);

    void onShowFeePay(String plateNumber);
}
