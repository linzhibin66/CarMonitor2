package com.shinetech.mvp.network.UDP.listener;

import com.shinetech.mvp.network.UDP.bean.item.CarBaseInfoItem;

import java.util.List;

/**
 * Created by ljn on 2017-04-01.
 */
public interface BaseCarInfoListener {

    void OnSuccess(List<CarBaseInfoItem> list);

    void OnError(String message);
}
