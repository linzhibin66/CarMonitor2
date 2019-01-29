package com.easyder.carmonitor.interfaces;

/**
 * Created by ljn on 2017-04-13.
 */
public interface SettingsItemListener {

    void onBack();

    /**
     * 退出登陆
     */
    void onClickLogout();

    /**
     * 关于我们
     */
    void onClickAboutUS();

    /**
     * 推荐给朋友
     */
    void onClickShare();

    /**
     * 离线地图
     */
    void onClickOffLineMap();

    /**
     * 路况刷新设置
     */
    void refreshRoad();

    /**
     * 车辆刷新设置
     */
    void refreshCar();

}
