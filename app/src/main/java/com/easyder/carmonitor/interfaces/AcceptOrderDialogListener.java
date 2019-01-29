package com.easyder.carmonitor.interfaces;

import com.shinetech.mvp.network.UDP.bean.BaseVo;

/**
 * Created by ljn on 2017-11-24.
 */

public interface AcceptOrderDialogListener {

    void OnCancel();

    BaseVo OnLoad();

    void OnSuccess();
}
