package com.easyder.carmonitor.interfaces;

import com.shinetech.mvp.network.UDP.resopnsebean.ViolationLogItem;

import java.util.List;

/**
 * Created by ljn on 2017-08-02.
 */
public interface AlarmLogListListener {

    void onSuccess(List<ViolationLogItem> mViolationLogList);

    void onError(String message);
}
