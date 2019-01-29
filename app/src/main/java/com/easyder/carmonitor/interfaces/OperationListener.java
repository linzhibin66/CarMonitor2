package com.easyder.carmonitor.interfaces;

import com.easyder.carmonitor.dialog.OperationPwdDialog;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;

/**
 * Created by ljn on 2017-08-04.
 */
public interface OperationListener {

    void toDoOperation(OperationPwdDialog mOperationDialog, ResponseListener mResponseListener);
}
