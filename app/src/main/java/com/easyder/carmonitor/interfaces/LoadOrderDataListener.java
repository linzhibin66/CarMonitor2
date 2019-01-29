package com.easyder.carmonitor.interfaces;

import com.easyder.carmonitor.Scheme.OrderInfoScheme.BaseOrderInfoItem;

import java.util.List;

/**
 * Created by ljn on 2017-11-10.
 */

public interface LoadOrderDataListener {

    void onSuccess(BaseOrderInfoItem pagerItem);
    void onSuccess(List<BaseOrderInfoItem> pagerItem);

    void onError(String errorInfo);
}
