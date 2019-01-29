package com.easyder.carmonitor.widget.marker;

import android.app.Activity;
import android.text.TextUtils;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;
import com.shinetech.mvp.DB.bean.SelectCarList;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.interfaces.BaseUpdateBaiduMarker;
import com.shinetech.mvp.network.UDP.InfoTool.CarTypeTool;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarBaseInfoBean;
import com.shinetech.mvp.utils.BaiduMapUtils;
import com.shinetech.mvp.utils.CoordinateUtil;
import com.shinetech.mvp.utils.LogUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ljn on 2017/3/16.
 */
public class CachingSchemeMarker implements BaseUpdateBaiduMarker{

    /**
     * 车辆信息
     */
    private List<CarInfoBean> mCarInfoList;

    /**
     * 百度地图
     */
    private BaiduMap mBaiduMap;

    private final boolean isDebug = false && LogUtils.isDebug;

    /**
     * 缓存所有地图显示的marker
     */
    private Map<String,Marker> markerMap = new HashMap<>();

    /**
     * 当前点击的Marker
     */
    private Marker currentClickMarker;

    /**
     * Marker icon
     */
//    private BitmapDescriptor carMarker;

    public CachingSchemeMarker() {
        /*this.carMarker = carMarker;*/
    }

    @Override
    public void updateMarkers(Collection<CarInfoBean> carInfoList) {

        if(mCarInfoList == null){
            mCarInfoList = new ArrayList<>();
        }else{
            mCarInfoList.clear();
        }

        mCarInfoList.addAll(carInfoList);


        if(mCarInfoList !=null && mCarInfoList.size()>0){

            for(CarInfoBean mCarInfoBean : mCarInfoList){

                double lng = mCarInfoBean.getLng() / 1E6;
                double lat = mCarInfoBean.getLat() / 1E6;

                if(isDebug)System.out.println(mCarInfoBean.getPlateNumber() + "     Lng = "+mCarInfoBean.getLng()+"  Lat = "+mCarInfoBean.getLat() );
                if(isDebug)System.out.println(mCarInfoBean.toString());

                LatLng latLng = CoordinateUtil.gps84_to_bd09(lat, lng);

//              地图中添加车辆的覆盖物，显示车辆
                addmakerOptions(latLng, mCarInfoBean.getPlateNumber(), mCarInfoBean.getOrientation());
            }

            updataSelectCarMarker();

        }

    }

    private Marker addMarkerToMap(LatLng latLng, String plateNumber, short orientation){

        //获取车辆图标
        ResponseCarBaseInfoBean carBaseInfo = UserInfo.getInstance().getCarBaseInfo(plateNumber);
        BitmapDescriptor carIcon;
        if(carBaseInfo != null) {
            carIcon = CarTypeTool.getCarIcon(carBaseInfo.getCarType());
        }else{
            carIcon = CarTypeTool.getDefCarIcon();
        }

        MarkerOptions options = new MarkerOptions().position(latLng).icon(carIcon).title(plateNumber);
        options.rotate(360 - orientation);
        options.title(plateNumber);

        options.alpha(0.7f);

        Overlay overlay = mBaiduMap.addOverlay(options);
        if(overlay instanceof Marker){
            Marker marker = (Marker) overlay;
            markerMap.put(plateNumber, (Marker) overlay);
            return marker;
        }
        return null;
    }

    /**
     * 地图中添加车辆的覆盖物，显示车辆
     * @param latLng
     * @param plateNumber
     * @param orientation
     */
    @Override
    public void addmakerOptions(LatLng latLng, String plateNumber, short orientation){

        Marker marker = markerMap.get(plateNumber);
        if(marker == null){

            marker = addMarkerToMap(latLng, plateNumber,orientation);

            if(marker == null){
                LogUtils.error(" addmakerOptions no creat marker !");
                return;
            }

        }else{

            marker.setPosition(latLng);

            marker.setRotate(360 - orientation);

            if(currentClickMarker!=null && plateNumber.equals(currentClickMarker.getTitle())){

                float alpha = currentClickMarker.getAlpha();
                marker.setAlpha(alpha);

            }else{
                marker.setAlpha(0.7f);
            }
        }

        if(UserInfo.getInstance().isLocationCar()){

            String currentLocationCar = UserInfo.getInstance().getCurrentLocationCar();

            if((TextUtils.isEmpty(currentLocationCar)) || !currentLocationCar.equals(plateNumber)) {

                marker.setVisible(false);
            }/*else{
                marker.setVisible(true);
                //确保要显示的Marker不会被不显示的Marker挡住，使其不同意点击到。
                marker.setToTop();
//                setCurrentClickMarker(marker);
            }*/

            return;
        }

        if(UserInfo.getInstance().getSelectCarCount()>0 && !UserInfo.getInstance().isSelectCarInfo(plateNumber)){
            // 判断是否是选择了要显示的车辆
//            marker.setAlpha(0.1f);
            marker.setVisible(false);

        }else{
            marker.setVisible(true);
//            确保要显示的Marker不会被不显示的Marker挡住，使其不同意点击到。
            marker.setToTop();
        }

       /* int[] mapViewScope = BaiduMapUtils.getMapViewScope(mBaiduMap);

        if(latLng.longitude>mapViewScope[0] && latLng.latitude>mapViewScope[1] && latLng.longitude<mapViewScope[2] && latLng.latitude<mapViewScope[3]){
            marker.setVisible(true);
        }else{
            marker.setVisible(false);
        }*/

        return;
    }

    private void updataSelectCarMarker(){

        if(UserInfo.getInstance().isLocationCar()) {

            String currentLocationCar = UserInfo.getInstance().getCurrentLocationCar();

            if ((!TextUtils.isEmpty(currentLocationCar))) {
                Marker marker = markerMap.get(currentLocationCar);
                if(marker != null) {
                    marker.setVisible(true);
                    marker.setToTop();
                }
            }
            return;
        }

        if(UserInfo.getInstance().getSelectCarCount()>0){

            List<String> selectCarList = UserInfo.getInstance().getSelectCarList();

            for(String plateNumber: selectCarList){
                Marker marker = markerMap.get(plateNumber);
                if(marker!=null) {
                    marker.setVisible(true);
                    marker.setToTop();
                }
            }
        }
    }

    @Override
    public void cleanUNLockCar() {
        if(markerMap != null && markerMap.size()>0){
            Collection<Marker> values = markerMap.values();

            for(Marker marker : values){
                if(!marker.equals(currentClickMarker)){
                    marker.setVisible(false);
                }
            }
        }
    }

    @Override
    public void updateMarker(CarInfoBean mCarInfoBean, int[] mapViewScope) {

        double lng = mCarInfoBean.getLng() / 1E6;
        double lat = mCarInfoBean.getLat() / 1E6;

        LatLng latLng = CoordinateUtil.gps84_to_bd09(lat, lng);

//      地图中添加车辆的覆盖物，显示车辆
        addmakerOptions(latLng, mCarInfoBean.getPlateNumber(), mCarInfoBean.getOrientation());
    }

    @Override
    public void setCurrentClickMarker(Marker marker) {
        resetCurrentClickMarker();
        currentClickMarker = marker;
        currentClickMarker.setAlpha(1f);

    }

    @Override
    public void setCurrentClickMarker(String plateNumber) {
        if(markerMap !=null && markerMap.size()>0){
            Marker marker = markerMap.get(plateNumber);
            if(marker !=null){
                setCurrentClickMarker(marker);
            }else{
                CarInfoBean newestCarInfo = UserInfo.getInstance().getNewestCarInfo(plateNumber);

                if(newestCarInfo == null){
                    LogUtils.error(" setCurrentClickMarker no CarInfoBean to creat marker !");
                    return;
                }

                double lng = newestCarInfo.getLng() / 1E6;
                double lat = newestCarInfo.getLat() / 1E6;

                LatLng latLng = CoordinateUtil.gps84_to_bd09(lat, lng);

                marker = addMarkerToMap(latLng, newestCarInfo.getPlateNumber(), newestCarInfo.getOrientation());

                if(marker == null){
                    return;
                }

                setCurrentClickMarker(marker);
            }
        }
    }

    @Override
    public void resetCurrentClickMarker() {
        if(currentClickMarker!=null){
            currentClickMarker.setAlpha(0.7f);
        }
    }

    @Override
    public void setBaiduMap(BaiduMap mBaiduMap) {
        this.mBaiduMap = mBaiduMap;
    }
}
