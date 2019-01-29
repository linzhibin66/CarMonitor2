package com.shinetech.mvp.network.UDP.listener;

import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;

import java.util.List;

/**
 * Created by ljn on 2017-03-31.
 */
public interface RequestAllCarListener {

    /**
     * 旧接口，能一次获取所有车辆状态信息
     * @param allCarList
     */
    void OnSuccessCarInfo(List<CarInfoBean> allCarList);

    /**
     * 新接口，只能获取所有车辆基础信息，状态信息需单独查询
     * @param allCarPlateNumberList
     */
    void OnSuccessPlateNumberList(List<String> allCarPlateNumberList);

    void OnError(String message);
}
