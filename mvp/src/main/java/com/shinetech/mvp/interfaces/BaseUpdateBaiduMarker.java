package com.shinetech.mvp.interfaces;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;

import java.util.Collection;

/**
 * Created by ljn on 2017/3/16.
 */
public interface BaseUpdateBaiduMarker {

    /**
     * 更新所有Marker到地图
     * @param carInfoList
     */
    void updateMarkers(Collection<CarInfoBean> carInfoList);

    /**
     * 更新单个Marker图标
     * @param mCarInfoBean 车辆信息
     * @param mapViewScope 当前地图可视区域范围  【0-3】分别为 minLng, minLat, maxLng, maxLat
     */
    void updateMarker(CarInfoBean mCarInfoBean, int[] mapViewScope);

    /**
     * 设置当前点击的Marker
     * @param marker
     */
    void setCurrentClickMarker(Marker marker);

    /**
     * 设置当前点击的Marker
     * @param plateNumber
     */
    void setCurrentClickMarker(String plateNumber);

    /**
     * 复位当前点击的Marker
     */
    void resetCurrentClickMarker();

    /**
     * 设置百度地图
     * @param mBaiduMap
     */
    void setBaiduMap(BaiduMap mBaiduMap);

    /**
     * 添加Marker
     * @param latLng
     * @param plateNumber tag
     * @param orientation 方向
     * @return
     */
    void addmakerOptions(LatLng latLng, String plateNumber, short orientation);

    /**
     * 清除未锁定显示的车辆
     */
    void cleanUNLockCar();
}
