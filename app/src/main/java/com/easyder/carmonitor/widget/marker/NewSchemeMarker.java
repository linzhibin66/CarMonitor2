package com.easyder.carmonitor.widget.marker;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;
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
import java.util.concurrent.ExecutionException;

/**
 * Created by ljn on 2017/3/16.
 */
public class NewSchemeMarker implements BaseUpdateBaiduMarker{

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

    /**
     * baidu 地图
     */
    private BaiduMap mBaiduMap;

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

    public NewSchemeMarker() {
//        this.carMarker = carMarker;
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

                    synchronized (NewSchemeMarker.this) {

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

                            UIUtils.runInMainThread(new mTask(mCarInfoBean));

                        }
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

        //如果已经选择了车辆，该车辆不重新marker到地图，只刷新位置和角度
       /* if(currentClickMarker!=null){
            String title = currentClickMarker.getTitle();
            if(plateNumber.equals(title)){
                currentClickMarker.setPosition(latLng);
                currentClickMarker.setRotate(orientation - 90);

                return latLng;
            }
        }*/

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

        MarkerOptions options = new MarkerOptions().position(latLng).icon(carIcon).title(plateNumber);
        options.rotate(360 - orientation);
        options.title(plateNumber);

        options.alpha(0.7f);

        Overlay overlay = mBaiduMap.addOverlay(options);

       /* if(currentClickMarker!=null){
            String title = currentClickMarker.getTitle();
            if(plateNumber.equals(title)){
                float alpha = currentClickMarker.getAlpha();
                if(overlay instanceof Marker){
                    currentClickMarker = (Marker) overlay;
                    currentClickMarker.setAlpha(alpha);
                }
            }
        }else */
        if(!TextUtils.isEmpty(currentClickMarker_PlateNumber)) {
            if (plateNumber.equals(currentClickMarker_PlateNumber)) {
//                float alpha = currentClickMarker.getAlpha();
                if (overlay instanceof Marker) {
                    currentClickMarker = (Marker) overlay;
                    currentClickMarker.setAlpha(1f);
                }
            }
        }
        return;
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

    private class mTask implements Runnable{

        private CarInfoBean mcarInfoBean;

        public mTask(CarInfoBean mcarInfoBean) {
            this.mcarInfoBean = mcarInfoBean;
        }

        @Override
        public void run() {
            double lng = mcarInfoBean.getLng() / 1E6;
            double lat = mcarInfoBean.getLat() / 1E6;

            LatLng latLng = CoordinateUtil.gps84_to_bd09(lat, lng);
            //地图中添加车辆的覆盖物，显示车辆
            addmakerOptions(latLng, mcarInfoBean.getPlateNumber(), mcarInfoBean.getOrientation());
        }
    }

}
