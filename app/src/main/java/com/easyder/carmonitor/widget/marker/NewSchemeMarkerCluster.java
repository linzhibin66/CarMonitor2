package com.easyder.carmonitor.widget.marker;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.baidu.mapapi.clusterutil.clustering.ClusterManager;
import com.baidu.mapapi.clusterutil.clustering.OnCreatMarkerListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;
import com.easyder.carmonitor.bean.MarkerClusterItem;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.interfaces.BaseUpdateBaiduMarker;
import com.shinetech.mvp.network.UDP.InfoTool.CarTypeTool;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarBaseInfoBean;
import com.shinetech.mvp.utils.BaiduMapUtils;
import com.shinetech.mvp.utils.CoordinateUtil;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.utils.UIUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by ljn on 2017/3/16.
 */
public class NewSchemeMarkerCluster implements BaseUpdateBaiduMarker{

    private final long TIME_TO_REFER = 1000*5;

    private final int UPDATE_MARKER = 0;

    /**
     * 当前点击的Marker
     */
    private Marker currentClickMarker;

    private String currentClickMarker_PlateNumber;

    //地图显示车辆列表
    private List<CarInfoBean> mCarInfoList;

    /**
     * Marker icon
     */
//    private BitmapDescriptor carMarker;

    private ClusterManager<MarkerClusterItem> mClusterManager;

    /**
     * baidu 地图
     */
    private BaiduMap mBaiduMap;

    private Context context;

    /**
     * 记录显示的Marker的车辆信息
     */
    private Map<String,CarInfoBean> markerMap = new HashMap<>();

    private final boolean isDebug = false && LogUtils.isDebug;

    private Thread mThread;

    /**
     * 刷新机制
     */
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;

            switch (what){
                case UPDATE_MARKER:
                    Collection<CarInfoBean> values = markerMap.values();
                    updateMarkers(values);
                    break;
            }
        }
    };

    public NewSchemeMarkerCluster(Context context) {
        this.context = context;
    }

    @Override
    public void updateMarkers(final Collection<CarInfoBean> carInfoList) {

        if(mThread!= null) {
            try {
                mThread.interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        synchronized (this) {
            if (mCarInfoList == null) {
                mCarInfoList = new ArrayList<>();
            } else {
                mCarInfoList.clear();
            }

            mCarInfoList.addAll(carInfoList);
        }

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {

                markerMap.clear();

                if(mCarInfoList !=null && mCarInfoList.size()>0) {
                    //清除覆盖物
                    mBaiduMap.clear();
                    if(mClusterManager != null)
                    mClusterManager.clearItems();

                    synchronized (NewSchemeMarkerCluster.this) {

                        Iterator<CarInfoBean> iterator = mCarInfoList.iterator();

                        while (iterator.hasNext()) {

                            CarInfoBean mCarInfoBean = iterator.next();

                            if (!UserInfo.getInstance().isSelectCarInfo(mCarInfoBean.getPlateNumber())) {
                                continue;
                            }

                            if (MainApplication.isUserResponseUDP) {
                                int[] mapViewScope = BaiduMapUtils.getMapViewScope(mBaiduMap);
                                if (!((mapViewScope[0] < mCarInfoBean.getLng()) && (mCarInfoBean.getLng() < mapViewScope[2]) && (mCarInfoBean.getLat() > mapViewScope[1]) && (mCarInfoBean.getLat() < mapViewScope[3]))) {
                                    continue;
                                }
                            }

                            double lng = mCarInfoBean.getLng() / 1E6;
                            double lat = mCarInfoBean.getLat() / 1E6;

                            LatLng latLng = CoordinateUtil.gps84_to_bd09(lat, lng);
                            //地图中添加车辆的覆盖物，显示车辆
                            addToClusterManager(latLng, mCarInfoBean.getPlateNumber(), mCarInfoBean.getOrientation());

//                            UIUtils.runInMainThread(new mTask(mCarInfoBean));

                        }

                        UIUtils.runInMainThread(new Runnable() {
                            @Override
                            public void run() {
                                mapStatusChange(mBaiduMap.getMapStatus());
                            }
                        });
                    }
                }

            }
        });

        mThread.start();

    }

    @Override
    public void updateMarker(CarInfoBean mCarInfoBean, int[] mapViewScope) {

        if(!UserInfo.getInstance().isSelectCarInfo(mCarInfoBean.getPlateNumber())){
                return;
        }

        if(mapViewScope == null){
            mapViewScope = BaiduMapUtils.getMapViewScope(mBaiduMap);

            if(mapViewScope == null){
                return;
            }

        }

        if(isDebug)System.out.println(mCarInfoBean.getPlateNumber() + "     Lng = "+mCarInfoBean.getLng()+"  Lat = "+mCarInfoBean.getLat() );
        if(isDebug)System.out.println(mCarInfoBean.toString());

        markerMap.put(mCarInfoBean.getPlateNumber(),mCarInfoBean);

        int lng = mCarInfoBean.getLng();

        int lat = mCarInfoBean.getLat();

        CarInfoBean carInfoBean = markerMap.get(mCarInfoBean.getPlateNumber());

        // 去跟新Marker 信息
        if((mapViewScope[0] < lng) && (lng < mapViewScope[2]) && (lat > mapViewScope[1]) && (lat < mapViewScope[3])){

            mHandler.removeMessages(UPDATE_MARKER);

            markerMap.put(mCarInfoBean.getPlateNumber(), mCarInfoBean);
            mHandler.sendEmptyMessageDelayed(UPDATE_MARKER, TIME_TO_REFER);

        }else{

            if(carInfoBean != null){

                mHandler.removeMessages(UPDATE_MARKER);

                markerMap.remove(mCarInfoBean.getPlateNumber()) ;

                mHandler.sendEmptyMessageDelayed(UPDATE_MARKER, TIME_TO_REFER);
            }
        }
    }

    /**
     * 地图中添加车辆的覆盖物，显示车辆
     * @param latLng
     * @param plateNumber
     * @param orientation
     */
    @Override
    public void addmakerOptions(LatLng latLng, String plateNumber, short orientation){


        addToClusterManager(latLng, plateNumber, orientation);

        UIUtils.runInMainThread(new Runnable() {
            @Override
            public void run() {
                mapStatusChange(mBaiduMap.getMapStatus());
            }
        });

        return;
    }

    private void addToClusterManager(LatLng latLng, String plateNumber, short orientation){

        if(UserInfo.getInstance().isLocationCar()){

            String currentLocationCar = UserInfo.getInstance().getCurrentLocationCar();

            if(TextUtils.isEmpty(currentLocationCar) || (!currentLocationCar.equals(plateNumber))) {
                return ;
            }

        }

        //获取车辆图标
        ResponseCarBaseInfoBean carBaseInfo = UserInfo.getInstance().getCarBaseInfo(plateNumber);
        BitmapDescriptor carIcon;
        if(carBaseInfo != null) {
            carIcon = CarTypeTool.getCarIcon(carBaseInfo.getCarType());
        }else{
            carIcon = CarTypeTool.getDefCarIcon();
        }

        MarkerClusterItem markerClusterItem = new MarkerClusterItem(latLng, plateNumber);

        if(!TextUtils.isEmpty(currentClickMarker_PlateNumber)) {
            if (plateNumber.equals(currentClickMarker_PlateNumber)) {
                markerClusterItem.setAlpha(1f);
            }
        }

        markerClusterItem.setMarkerIcon(carIcon);
        markerClusterItem.setRotate(360 - orientation);

        if(mClusterManager == null) {
            // 定义点聚合管理类ClusterManager
            mClusterManager = new ClusterManager<>(context, mBaiduMap);

            mClusterManager.setOnCreatMarkerListener(new OnCreatMarkerListener() {
                @Override
                public void onMarkerCreat(Marker marker) {
//                    System.out.println("onMarkerCreat marker = "+marker.getTitle());
                    String title = marker.getTitle();
                    if(!TextUtils.isEmpty(currentClickMarker_PlateNumber)) {
                        if (title.equals(currentClickMarker_PlateNumber)) {
                            currentClickMarker = marker;
                        }
                    }
                }
            });
        }

        mClusterManager.addItem(markerClusterItem);

    }

    public void mapStatusChange(MapStatus mapStatus){
        if(mClusterManager != null){
            mClusterManager.mapStatusChange(mapStatus);
        }
    }

    @Override
    public void cleanUNLockCar() {
//        TODO 不用处理，每次刷新都全部移除了
    }

    @Override
    public void setCurrentClickMarker(Marker mMarker){
        resetCurrentClickMarker();
        currentClickMarker_PlateNumber = mMarker.getTitle();
        currentClickMarker = mMarker;
        currentClickMarker.setAlpha(1f);
    }

    @Override
    public void setCurrentClickMarker(String plateNumber) {
        currentClickMarker_PlateNumber = plateNumber;
    }

    @Override
    public void resetCurrentClickMarker(){
        if(currentClickMarker!=null){
            currentClickMarker.setAlpha(0.7f);
        }
        currentClickMarker_PlateNumber = "";
    }

    @Override
    public void setBaiduMap(BaiduMap mBaiduMap) {
        this.mBaiduMap = mBaiduMap;
    }

}
