package com.easyder.carmonitor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.bean.TrackLogItem;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.basePresenter.MvpBasePresenter;
import com.shinetech.mvp.network.UDP.InfoTool.AlarmInfoTool;
import com.shinetech.mvp.network.UDP.InfoTool.CarTypeTool;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarBaseInfoBean;
import com.shinetech.mvp.utils.CoordinateUtil;
import com.shinetech.mvp.utils.UIUtils;

import java.io.Serializable;

import butterknife.Bind;

/**
 * Created by ljn on 2017-06-20.
 */
public class TrackLogInfoActivity extends BaseActivity implements OnGetGeoCoderResultListener {

    @Bind(R.id.title_back)
    ImageButton title_back;

    @Bind(R.id.title_text)
    TextView title_text;

    @Bind(R.id.title_search)
    ImageButton title_menu;

    @Bind(R.id.title_layout)
    RelativeLayout title_layout;

    @Bind(R.id.track_info_bmapView)
    MapView track_bmapView;

    @Bind(R.id.track_info_map_type_imgb)
    ImageButton track_map_type;

    @Bind(R.id.track_log_info_icon_car)
    ImageView car_icon;

    @Bind(R.id.track_log_info_platenumber)
    TextView track_platenumber;

    @Bind(R.id.track_log_info_acc)
    ImageView track_acc;

    @Bind(R.id.log_info_alarm)
    ImageView track_alarm;

    @Bind(R.id.log_info_exception)
    ImageView track_exception;

    @Bind(R.id.track_log_info_speed)
    TextView track_speed;

    @Bind(R.id.track_log_info_addres)
    TextView track_addres;

    @Bind(R.id.track_log_info_time)
    TextView track_time;

    private BaiduMap mBaiduMap;

    private TrackLogItem mTrackLogItem;

    private LatLng latLng;

    private String plateNumber;

    //地址解析类
    private GeoCoder geoCoder;

    private ReverseGeoCodeOption reverseGeoCodeOption;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        plateNumber = intent.getStringExtra(TrackActivity.TRACK_INFO_PLATENUMBER);

        Serializable serializableExtra = intent.getSerializableExtra(TrackActivity.TRACK_INFO);

        if(serializableExtra != null && serializableExtra instanceof TrackLogItem){
            mTrackLogItem = (TrackLogItem) serializableExtra;
            //      根据坐标解析地址
            double lng = mTrackLogItem.getLng() / 1E6;
            double lat = mTrackLogItem.getLat() / 1E6;

            latLng = CoordinateUtil.gps84_to_bd09(lat, lng);
        }else{
            finish();
        }

        //初始化标题
        initTitle();

        //初始化地图
        initBmapView();

        //初始化地址解析
        initGeoCoder();

        //添加车辆到地图
        addMarkerToMap(latLng, plateNumber, mTrackLogItem.getOrientation());

        //初始化底部信息布局
        initBottonLayout();

        //地图切换（基础地图、卫星地图）
        track_map_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mapType = mBaiduMap.getMapType();
                if (mapType == BaiduMap.MAP_TYPE_NORMAL) {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                    track_map_type.setImageResource(R.mipmap.btn_map_satellite);
                } else {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    track_map_type.setImageResource(R.mipmap.btn_map_n);
                }
            }
        });
    }

    /**
     * 初始化标题
     */
    private void initTitle(){

        title_text.setText(getString(R.string.track_info_activity_title));

        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        title_menu.setVisibility(View.GONE);

        UIUtils.setImmersiveStatusBar(title_layout);
    }

    /**
     * 初始化百度地图
     */
    private void initBmapView(){
//      设置是否显示缩放控件
        track_bmapView.showZoomControls(false);
//      设置是否显示比例尺控件
        track_bmapView.showScaleControl(false);

        mBaiduMap = track_bmapView.getMap();
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomBy(5));



        //检测地图状态改变（用于刷新界面中可见的车辆）
//        mBaiduMap.setOnMapStatusChangeListener(this);

//        mBaiduMap.setOnMarkerClickListener(this);

//        mBaiduMap.setCompassPosition(new Point(80, 200));

        //设置是否允许指南针
        mBaiduMap.getUiSettings().setCompassEnabled(false);
        //设置是否允许旋转手势
        mBaiduMap.getUiSettings().setRotateGesturesEnabled(false);
//        mBaiduMap.setCompassPosition(new Point(80, 200));
        //设置是否允许俯视手势
        mBaiduMap.getUiSettings().setOverlookingGesturesEnabled(false);

        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        track_map_type.setImageResource(R.mipmap.btn_map_n);

        mBaiduMap.setTrafficEnabled(false);

        //初始化定位
//        initLocationPermission();

    }

    @Override
    protected void onResume() {
        super.onResume();
        MapStatus.Builder builder = new MapStatus.Builder();

        builder.target(latLng).zoom(17);
        MapStatusUpdate u =  MapStatusUpdateFactory.newMapStatus(builder.build());

        mBaiduMap.animateMapStatus(u);
    }

    private void addMarkerToMap(LatLng latLng, String plateNumber, short orientation){

        BitmapDescriptor carIcon;
        ResponseCarBaseInfoBean carBaseInfo = UserInfo.getInstance().getCarBaseInfo(plateNumber);
        if(carBaseInfo == null){
            carIcon = CarTypeTool.getDefCarIcon();
        }else {
            carIcon = CarTypeTool.getCarIcon(carBaseInfo.getCarType());
        }

        MarkerOptions options = new MarkerOptions().position(latLng).icon(carIcon).title(plateNumber);
        options.rotate(360 - orientation);
        options.title(plateNumber);

        options.alpha(1f);

         mBaiduMap.addOverlay(options);
    }

    private void initBottonLayout(){

        //设置车牌号
        track_platenumber.setText(plateNumber);

        //设置ACC 状态
        int acc_resource;
        if(mTrackLogItem.isStop()){
            acc_resource = R.mipmap.icon_stop;
        }else{
            acc_resource = R.mipmap.icon_run;
        }
        track_acc.setImageResource(acc_resource);

        if(AlarmInfoTool.isExceptionp(mTrackLogItem.getAlarmType())){
            track_exception.setVisibility(View.VISIBLE);
        }else{
            track_exception.setVisibility(View.GONE);

            if(mTrackLogItem.getAlarmType()>0){
                track_alarm.setVisibility(View.VISIBLE);
            }else{
                track_alarm.setVisibility(View.GONE);
            }

        }

        //设置速度
        track_speed.setText(mTrackLogItem.getmGNSSSpeed() + "km/h");

        //地址解析
        if(TextUtils.isEmpty(mTrackLogItem.getAdress())){
            track_addres.setText(R.string.marker_address_analysis);
            reverseGeoCodeOption.location(latLng);
            geoCoder.reverseGeoCode(reverseGeoCodeOption);
        }else{
            track_addres.setText(mTrackLogItem.getAdress());
        }

        //设置定位时间
        track_time.setText(mTrackLogItem.getLocationTime());

        int carinfoIcon;

        ResponseCarBaseInfoBean carBaseInfo = UserInfo.getInstance().getCarBaseInfo(plateNumber);

        if((carBaseInfo != null) && (!TextUtils.isEmpty(carBaseInfo.getCarType()))) {

            carinfoIcon = CarTypeTool.getMarkerInfoCarIcon(carBaseInfo.getCarType());

        }else{
            carinfoIcon = R.mipmap.marker_info_car;
        }

        car_icon.setImageResource(carinfoIcon);


    }

    /**
     * 初始化地址解析类
     */
    private void initGeoCoder(){
        //地址反向编码
        geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(this);
        reverseGeoCodeOption = new ReverseGeoCodeOption();
    }

    @Override
    protected int getView() {
        return R.layout.activity_track_log_info;
    }

    @Override
    protected MvpBasePresenter createPresenter() {
        return null;
    }

    @Override
    public void onLoading() {
    }

    @Override
    public void showContentView(BaseVo dataVo) {
    }

    @Override
    public void onStopLoading() {
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if (!TextUtils.isEmpty(reverseGeoCodeResult.getAddress())){
            String address = reverseGeoCodeResult.getAddress();
            track_addres.setText(address);
        }else{
            track_addres.setText(R.string.marker_address_error);
        }
    }
}
