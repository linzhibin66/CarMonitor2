package com.easyder.carmonitor.interfaces;

import com.shinetech.mvp.network.UDP.resopnsebean.ViolationLogItem;

import java.util.List;

/**
 * Created by ljn on 2017-09-06.
 */

public interface ViolationLogTypeChengedListener {

    void onStart();
    void onFinish(List<ViolationLogItem> showViolationLogList);
}
