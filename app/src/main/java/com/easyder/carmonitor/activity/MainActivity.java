package com.easyder.carmonitor.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mobstat.StatService;
import com.easyder.carmonitor.CarMonitorApplication;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.Utils.ConfigurationChangedManager;
import com.easyder.carmonitor.dialog.AllCarListDialog;
import com.easyder.carmonitor.dialog.HintProgressDialog;
import com.easyder.carmonitor.interfaces.AddPicturesListener;
import com.easyder.carmonitor.interfaces.AllCarClickListener;
import com.easyder.carmonitor.presenter.CompanyMainPresenter;
import com.easyder.carmonitor.presenter.MainActivityPresenter;
import com.easyder.carmonitor.presenter.PersonMainPresenter;
import com.easyder.carmonitor.widget.RoundImageView;
import com.easyder.carmonitor.widget.marker.CachingSchemeMarker;
import com.easyder.carmonitor.widget.marker.NewSchemeMarker;
import com.easyder.carmonitor.widget.marker.NewSchemeMarkerCluster;
import com.shinetech.mvp.DB.DBManager;
import com.shinetech.mvp.DB.bean.BaseOrderInfoDB;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.basePresenter.MvpBasePresenter;
import com.shinetech.mvp.interfaces.BaseUpdateBaiduMarker;
import com.shinetech.mvp.network.UDP.DatagramPacketDefine;
import com.shinetech.mvp.network.UDP.UDPHeartBeatConnect;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.CarLatestStatus;
import com.shinetech.mvp.network.UDP.bean.QueryDesignatedAreaCarInfo;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.network.UDP.listener.CarListChangeListener;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarStatusInfoBean;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.utils.ToastUtil;
import com.shinetech.mvp.utils.UIUtils;
import com.shinetech.mvp.wxpay.WxPayManager;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.List;

import butterknife.Bind;

public class MainActivity extends BaseActivity implements BaiduMap.OnMapStatusChangeListener, BaiduMap.OnMarkerClickListener, CarListChangeListener {

    @Bind(R.id.bmapView)
    MapView bmapView;

    @Bind(R.id.main_person_usericon)
    RoundImageView main_person_usericon;

    @Bind(R.id.main_usericon)
    ImageView main_usericon;
//    RoundImageView main_usericon;

    @Bind(R.id.search_et)
    EditText search_et;

    @Bind(R.id.main_menu)
    ImageButton main_menu;

    @Bind(R.id.map_type_imgb)
    ImageButton map_type_imgb;

    @Bind(R.id.road_condition_imgb)
    ImageButton road_condition_imgb;

    @Bind(R.id.location_imgb)
    ImageButton location_imgb;

    @Bind(R.id.refresh_imgb)
    ImageButton refresh_imgb;

    @Bind(R.id.cancel_search)
    TextView cancel_search;

    @Bind(R.id.main_title_layout)
    RelativeLayout main_title_layout;

    @Bind(R.id.map_ctrl_layout)
    LinearLayout map_ctrl_layout;

    @Bind(R.id.main_person_title_layout)
    RelativeLayout main_person_title_layout;

    private BaiduMap mBaiduMap;

    private final int BAIDU_LOCATION_PERMISSION = 0x1001;
    private final int BAIDU_REFRESH_LOCATION_PERMISSION = 0x1002;

    /*private GeoCoder geoCoder;

    private ReverseGeoCodeOption reverseGeoCodeOption;*/

    /**
     * 搜索点击事件
     */
//    private View.OnClickListener search_click;

    /**
     * 搜索输入框内容改变监听
     */
    private TextWatcher textChangedListener;

    private AllCarListDialog mAllCarListDialog;

    private static boolean isFirst = true;

    private HintProgressDialog mProgressDialog;

    private LocationClient mLocClient;

    private LocationListener bdLocationListener;

    private LatLng locationLatLng;

    /**
     * 定位是否刷新地图位置
     */
    private boolean isUpdataMap = false;

    private boolean isKillApp = true;

    private long firstTime;

    private static AddPicturesListener mAddPicturesListener;

    private boolean isDebug = false && LogUtils.isDebug;;

    /**
     * 刷新marker消息
     */
    public static final int REFER_MARKER = 100;
    public static final int SHOW_REFER_BUTTON = 101;

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;

            switch (what){

                case REFER_MARKER:
                    ((MainActivityPresenter)presenter).getCarOfView(mHandler, mBaiduMap, bmapView, false);
                    break;
                case SHOW_REFER_BUTTON:
                    refresh_imgb.setVisibility(View.VISIBLE);
                   //System.out.println("SHOW_REFER_BUTTON ------------------");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidBug5497Workaround.assistActivity(this,(MainActivityPresenter)presenter);
        MainApplication.getInstance().initWXApi(this);

        initBaiduStatistics();

//      初始化地图右上角快捷功能
        initQuickFunction();

//      初始化标题两边的功能按钮
        initTitleView();

//      初始化地图
        initBmapView();

//      初始化搜索功能
        initSearch();

//      初始化用户图标
        initUsericon();

        ((MainActivityPresenter)presenter).setBaiduMap(mBaiduMap);

//       获取车辆显示到地图中
        ((MainActivityPresenter)presenter).getOrRefreshCar();

//        UDPTest.getCarBaseListResponse();

    }

    private void initUsericon(){
        byte loginType = UserInfo.getInstance().getLoginType();
        if(loginType == DatagramPacketDefine.USER_TYPE){
            main_usericon.setImageResource(R.mipmap.icon_single);
        }else if(loginType == DatagramPacketDefine.COMPANY_TYPE){
            main_usericon.setImageResource(R.mipmap.icon_group);
        }else{
            main_usericon.setImageResource(R.mipmap.icon_gis);
        }
    }

    /**
     * 初始化百度统计
     */
    private void initBaiduStatistics(){

        // setSendLogStrategy已经@deprecated，建议使用新的start接口
        // 如果没有页面和自定义事件统计埋点，此代码一定要设置，否则无法完成统计
        // 进程第一次执行此代码，会导致发送上次缓存的统计数据；若无上次缓存数据，则发送空启动日志
        // 由于多进程等可能造成Application多次执行，建议此代码不要埋点在Application中，否则可能造成启动次数偏高
        // 建议此代码埋点在统计路径触发的第一个页面中，若可能存在多个则建议都埋点
        if(!((CarMonitorApplication)CarMonitorApplication.getInstance()).isStartBaiduStatistics()) {
            ((CarMonitorApplication) CarMonitorApplication.getInstance()).setIsStartBaiduStatistics(true);
            StatService.start(this);

            StatService.setDebugOn(false);
        }

    }

//    int count = 0;

    /**
     * 初始化快捷功能按键
     */
    private void initQuickFunction(){

        // 路况显示
        road_condition_imgb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean trafficEnabled = mBaiduMap.isTrafficEnabled();

                mBaiduMap.setTrafficEnabled(!trafficEnabled);

                if (!trafficEnabled) {
                    road_condition_imgb.setImageResource(R.mipmap.bmskin_main_icon_roadcondition_on);

                } else {
                    road_condition_imgb.setImageResource(R.mipmap.bmskin_main_icon_roadcondition_off);
                }

            }
        });

        //地图切换（基础地图、卫星地图）
        map_type_imgb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mapType = mBaiduMap.getMapType();
                if (mapType == BaiduMap.MAP_TYPE_NORMAL) {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                    map_type_imgb.setImageResource(R.mipmap.btn_map_satellite);
                } else {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    map_type_imgb.setImageResource(R.mipmap.btn_map_n);
                }
            }
        });



        //定位功能
        location_imgb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLocation();

            }
        });

        //刷新地图中的车辆
        refresh_imgb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                refresh_imgb.setVisibility(View.GONE);
                ((MainActivityPresenter) presenter).getCarOfView(mHandler, mBaiduMap, bmapView, true);
                mHandler.sendEmptyMessageDelayed(SHOW_REFER_BUTTON,5000);

            }
        });

    }

    /**
     * 初始化搜索功能
     */
    private void initSearch(){

        // 搜索界面的点击事件，点击弹出搜索布局
       /* search_click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showSearchDialog();
                ((MainActivityPresenter) presenter).showInput(search_et);

            }
        };*/

        // 搜索输入内容监听
        textChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
               /* Intent intent = new Intent(MainActivity.this, MainActivity.class);
                if(isTop(MainActivity.this, intent)) {
                    if (((MainActivityPresenter) presenter).searchDilaogIsEmpty() || !((MainActivityPresenter) presenter).searchDilaogIsShow()) {
                        showSearchDialog();
                    }
                }*/
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                ToastUtil.showShort("onTextChanged "+ s.toString());
                if (((MainActivityPresenter) presenter).searchDilaogIsShow()) {
                    String searchString = s.toString().toUpperCase();
                    ((MainActivityPresenter) presenter).searchDilaogupdateContent(searchString);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

       /* search_et.setFocusable(true);
        search_et.requestFocus();*/
//        search_et.setOnClickListener(search_click);
        search_et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showSearchDialog();
//                ((MainActivityPresenter) presenter).showInput(search_et);
                return false;
            }
        });

        search_et.addTextChangedListener(textChangedListener);

        //退出搜索功能，恢复指定车辆在地图中的显示（未指定则是全部车辆）
        cancel_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cancelSearch();

            }
        });
    }

    /**
     * 显示取消按钮
     */
    public void showCancel(){
        cancel_search.setVisibility(View.VISIBLE);
        main_menu.setVisibility(View.GONE);

       /* search_et.setFocusable(true);
        search_et.requestFocus();*/
    }

    /**
     * 显示搜索弹框
     */
    public void showSearchDialog(){
        ((MainActivityPresenter) presenter).showSearchDialog(main_title_layout, mBaiduMap, bmapView, new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                ((MainActivityPresenter) presenter).recoverMarkerDialog();

//                search_et.removeTextChangedListener(textChangedListener);
                search_et.setText("");

//                search_et.addTextChangedListener(textChangedListener);
                ((MainActivityPresenter) presenter).hideInput(search_et);

                cancel_search.setVisibility(View.GONE);

                if (!UserInfo.getInstance().isPerson()) {
                    main_menu.setVisibility(View.VISIBLE);
                }

            }
        });
        showCancel();
    }

    /**
     * 取消搜索弹框
     */
    public void cancelSearch(){

        //恢复地图中车辆显示
        UserInfo.getInstance().setIsLocationCar(false);
        ((MainActivityPresenter) presenter).getCarOfView(mHandler, mBaiduMap, bmapView, false);

        ((MainActivityPresenter) presenter).searchDilaogDismiss();

        cancel_search.setVisibility(View.GONE);

        if(!UserInfo.getInstance().isPerson()) {
            main_menu.setVisibility(View.VISIBLE);
        }
//        search_et.setOnClickListener(search_click);

    }



    @Override
    protected int getView() {
        return R.layout.activity_main;
    }

    /**
     * 初始化用户图标和车辆列表图标
     */
    private void initTitleView(){
        int statusBarHeight = UIUtils.getStatusBarHeight();

        if(UserInfo.getInstance().isPerson()){

            initPersonTitleLayout(statusBarHeight);

        }else{

            initCompanyTitleLayout(statusBarHeight);

        }
    }

    /**
     * 初始化个人登录 标题
     * @param statusBarHeight
     */
    private void initPersonTitleLayout(int statusBarHeight){
        //用户图标顶部位置设置
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) main_person_title_layout.getLayoutParams();
        if(layoutParams == null){
            layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        }
        layoutParams.setMargins(0, statusBarHeight, 0, 0);

        //地图右测功能按钮顶部高度调整
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) map_ctrl_layout.getLayoutParams();

        if(lp == null){
            lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        }

        lp.setMargins(0, statusBarHeight + 20, 0, 0);

        map_ctrl_layout.setLayoutParams(lp);

        main_person_title_layout.setVisibility(View.VISIBLE);
        main_title_layout.setVisibility(View.GONE);

        main_person_title_layout.setLayoutParams(layoutParams);

        main_person_title_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivityPresenter) presenter).showUserMenuDialog(main_usericon);

            }
        });
    }

    /**
     * 初始化公司登录 标题
     * @param statusBarHeight
     */
    private void initCompanyTitleLayout(int statusBarHeight){
        main_title_layout.setVisibility(View.VISIBLE);
        main_person_title_layout.setVisibility(View.GONE);

        //用户图标顶部位置设置
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) main_title_layout.getLayoutParams();
        if(layoutParams == null){
            layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        }
        layoutParams.setMargins(0, statusBarHeight + 20, 0, 0);

        main_title_layout.setLayoutParams(layoutParams);

        //用户点击事件，弹出设置菜单
        main_usericon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivityPresenter) presenter).showUserMenuDialog(main_usericon);

            }
        });

        //车辆列表按钮初始化
        main_menu.setVisibility(View.VISIBLE);
        main_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CarMonitorApplication.isUserActivityMode) {

                    Intent intent = new Intent(MainActivity.this, AllCarListActivity.class);
                    startActivity(intent);
//                    overridePendingTransition(R.anim.pop_right2left_anim_open, R.anim.pop_right2left_anim_close);

                } else {

                    if (mAllCarListDialog == null) {
                        mAllCarListDialog = new AllCarListDialog(MainActivity.this);
                        mAllCarListDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                if (mAllCarListDialog.isRecorveMarker) {
                                    ((MainActivityPresenter) presenter).recoverMarkerDialog();
                                }
                                ((MainActivityPresenter) presenter).getCarOfView(mHandler, mBaiduMap, bmapView, true);
                                ((MainActivityPresenter) presenter).removeDailogOfManager(mAllCarListDialog);
                                ConfigurationChangedManager.getInstance().unRegistConfig(mAllCarListDialog);
                            }
                        });

                        //设置点击监听，定位点击的车辆
                        mAllCarListDialog.setItemListener(new AllCarClickListener() {
                            @Override
                            public void showOnlyCarMode(CarInfoBean mCarInfoBean) {
//                                showCancel();
                                CarInfoBean newestCarInfo = UserInfo.getInstance().getNewestCarInfo(mCarInfoBean.getPlateNumber());
                                if(newestCarInfo.getLng() == 0 && newestCarInfo.getLat() == 0){
                                    ToastUtil.showShort(getString(R.string.no_location_info));
                                    return ;
                                }

                                ((MainActivityPresenter) presenter).lockOnlyCar(mBaiduMap, bmapView, newestCarInfo == null ? mCarInfoBean : newestCarInfo);
                            }
                        });
                    }else{
                        mAllCarListDialog.updateView();
                    }
                    if (!mAllCarListDialog.isShowing()) {
                        ConfigurationChangedManager.getInstance().registConfig(mAllCarListDialog);
                        mAllCarListDialog.show(main_menu);
                        ((MainActivityPresenter) presenter).addDialogOfManager(mAllCarListDialog);
                        ((MainActivityPresenter) presenter).markerDialogToBackground();

                    }
                }
            }
        });
    }

    /**
     * 初始化百度地图
     */
    private void initBmapView(){
//      设置是否显示缩放控件
        bmapView.showZoomControls(false);
//      设置是否显示比例尺控件
        bmapView.showScaleControl(false);

        mBaiduMap = bmapView.getMap();

        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(new LatLng(23.01765, 113.75226), 17));

        if(!MainApplication.isUseMarkerCluster) {
            mBaiduMap.setMaxAndMinZoomLevel(21, 12);
        }

        //检测地图状态改变（用于刷新界面中可见的车辆）
        mBaiduMap.setOnMapStatusChangeListener(this);

        mBaiduMap.setOnMarkerClickListener(this);

        //设置是否允许指南针
        mBaiduMap.getUiSettings().setCompassEnabled(false);
        //设置是否允许旋转手势
        mBaiduMap.getUiSettings().setRotateGesturesEnabled(false);
//        mBaiduMap.setCompassPosition(new Point(80, 200));
        //设置是否允许俯视手势
        mBaiduMap.getUiSettings().setOverlookingGesturesEnabled(false);

        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        map_type_imgb.setImageResource(R.mipmap.btn_map_n);

        mBaiduMap.setTrafficEnabled(false);
        road_condition_imgb.setImageResource(R.mipmap.bmskin_main_icon_roadcondition_off);

        //初始化定位
        initLocationPermission();

    }

    public void initLocation(){

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, BitmapDescriptorFactory.fromResource(R.mipmap.icon_map_mark)));

        mLocClient = new LocationClient(this);

        bdLocationListener = new LocationListener();

        mLocClient.registerLocationListener(bdLocationListener);

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(200);
        option.setAddrType("all");
      /*  String version = mLocClient.getVersion();
        if(debug) LogUtils.info("baidu sdk version : " + version);*/
        option.setIsNeedLocationPoiList(true);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    /**
     * 定位监听
     */
    class LocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            locationLatLng = new LatLng(bdLocation.getLatitude(),
                    bdLocation.getLongitude());

            UserInfo.getInstance().setLocationLatLng(locationLatLng);

            if (bmapView == null)
                return;

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);

            if(isUpdataMap){

                isUpdataMap = false;

                LatLng latLng = new LatLng(bdLocation.getLatitude(),
                        bdLocation.getLongitude());

                MapStatus mMapStatus = mBaiduMap.getMapStatus();

                MapStatus.Builder builder = new MapStatus.Builder();

                builder.target(latLng).zoom(mMapStatus.zoom);
                MapStatusUpdate u =  MapStatusUpdateFactory.newMapStatus(builder.build());

                mBaiduMap.animateMapStatus(u);

            }
        }

      /*  @Override
        public void onConnectHotSpotMessage(String s, int i) {
//            ToastUtil.showShort("s = "+s+"  i = "+ i);
        }*/
    }

    @Override
    protected MvpBasePresenter createPresenter() {
        //设置Marker刷新策略 (CachingSchemeMarker 、NewSchemeMarker 刷新Marker的策略)
//        return new MainPresenter(new NewSchemeMarker(BitmapDescriptorFactory.fromResource(R.mipmap.icon_car)));
        if(UserInfo.getInstance().isPerson()){
            return new PersonMainPresenter(this,new CachingSchemeMarker());
        }else{

            BaseUpdateBaiduMarker mBaseUpdateBaiduMarker;

            if(MainApplication.isUserResponseUDP){
                if(MainApplication.isUseMarkerCluster){
                    mBaseUpdateBaiduMarker = new NewSchemeMarkerCluster(this);
                }else{
                    mBaseUpdateBaiduMarker = new NewSchemeMarker();
                }

            }else{
                mBaseUpdateBaiduMarker = new CachingSchemeMarker();
            }
            return new CompanyMainPresenter(this, mBaseUpdateBaiduMarker);
        }
    }

    /**
     * 首次定位地图和显示第一俩车
     * @param latLng
     * @param lateNumber
     * @param orientation
     */
    private void updateMapOfFirstCar(LatLng latLng, String lateNumber, short orientation){

        ((MainActivityPresenter) presenter).isShowLoadingFrist = false;
       // System.out.println("updateMapOfFirstCar : isShowLoadingFrist = "+((MainActivityPresenter) presenter).isShowLoadingFrist);
        if(latLng.latitude == 0 && latLng.longitude == 0){
            if(locationLatLng != null){
                latLng =  locationLatLng;
            }else{
                latLng = new LatLng(23.01765, 113.75226); //默认中心点
            }
        }

//          地图中添加车辆的覆盖物，显示车辆
//            LatLng latLng = addmakerOptions(lng, lat, mCarLatestStatus.getResultPlateNumber(), mCarLatestStatus.getResultOrientation());

        //跟新地图位置，定位到当前车辆位置（会调用onMapStatusChangeFinish）
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
        ((MainActivityPresenter) presenter).addmakerOptions(latLng, lateNumber, orientation);

        //add Marker （上面方法不一定回调用onMapStatusChangeFinish）
        /*if(UserInfo.getInstance().isPerson()){
            ((MainActivityPresenter) presenter).addmakerOptions(latLng, lateNumber, orientation);
        }*/
    }

    @Override
    public void showContentView(BaseVo dataVo) {

        if(dataVo instanceof ResponseCarStatusInfoBean){

            //TODO 得到车辆最新状态信息，将车辆显示到地图中。

            ResponseCarStatusInfoBean mResponseCarStatusInfoBean = (ResponseCarStatusInfoBean) dataVo;

            //LogUtils.error(mResponseCarStatusInfoBean.toString());

            LatLng latLng = ((MainActivityPresenter) presenter).getLatLng(mResponseCarStatusInfoBean);
            updateMapOfFirstCar(latLng,mResponseCarStatusInfoBean.getResultPlateNumber(),mResponseCarStatusInfoBean.getResultOrientation());


        }else if(dataVo instanceof CarLatestStatus){
//            TODO 得到车辆最新状态信息，将车辆显示到地图中。

            CarLatestStatus mCarLatestStatus = (CarLatestStatus) dataVo;

            LatLng latLng = ((MainActivityPresenter) presenter).getLatLng(mCarLatestStatus);

            updateMapOfFirstCar(latLng,mCarLatestStatus.getResultPlateNumber(),mCarLatestStatus.getResultOrientation());

        }else if(dataVo instanceof QueryDesignatedAreaCarInfo){
//            TODO 将获取到要显示的车辆列表添加到地图中
            QueryDesignatedAreaCarInfo mQueryDesignatedAreaCarInfo = (QueryDesignatedAreaCarInfo) dataVo;

            synchronized (this){

                List<CarInfoBean> carInfoList = mQueryDesignatedAreaCarInfo.getCarInfoList();

                if(carInfoList == null || carInfoList.size() == 0){
                    return;
                }

                ((MainActivityPresenter) presenter).updateMarkers(carInfoList);

            }
        }
    }

    @Override
    public void onLoading() {
        if(mProgressDialog == null) {
            mProgressDialog = new HintProgressDialog(this);
        }
        if(!mProgressDialog.isShowing()) {
            mProgressDialog.show(bmapView);
        }
    }

    @Override
    public void onStopLoading() {
        if(mProgressDialog!= null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MainActivityPresenter) presenter).recoverMarkerDialog();
        bmapView.onResume();
        UserInfo.getInstance().registerCarListChangeListener(this);
        if(!isFirst){
            ((MainActivityPresenter) presenter).getCarOfView(mHandler, mBaiduMap, bmapView, false);
            isFirst = false;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeMessages(REFER_MARKER);
        bmapView.onPause();
        UserInfo.getInstance().unregisteredCarlistChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时销毁定位
        if(mLocClient != null) {
            mLocClient.unRegisterLocationListener(bdLocationListener);
            mLocClient.stop();
            mLocClient = null;
        }
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);

        bmapView.onDestroy();
        if(isKillApp) {
            UDPHeartBeatConnect.getInstance().onExit();
            CarMonitorApplication.getInstance().exitApp();
        }
    }

//    地图状态监听
    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {
    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChangeFinish(final MapStatus mapStatus) {

//      TODO 获取地图坐标范围，根据坐标范围显示车辆。
        ((MainActivityPresenter)presenter).getCarOfView(mHandler, mBaiduMap, bmapView, false);

//        System.out.println("zoom : "+ mapStatus.zoom);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                LatLng target = mapStatus.target;
                if(locationLatLng == null || (Math.abs(target.latitude - locationLatLng.latitude)>0.000001D) && (Math.abs(target.longitude -locationLatLng.longitude)>0.000001D)){
                    if(location_imgb.getVisibility() == View.GONE) {
                        location_imgb.setVisibility(View.VISIBLE);
                    }
                }else{
                    location_imgb.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String plateNumber = marker.getTitle();

        //是否搜索中指定显示的车辆
        if(UserInfo.getInstance().isLocationCar() && (!plateNumber.equals(UserInfo.getInstance().getCurrentLocationCar()))){
            return true;
        }

        //判断是否是指定的车辆，不是不显示车辆信息
        if(UserInfo.getInstance().getSelectCarCount()>0 && (!UserInfo.getInstance().isSelectCarInfo(plateNumber))){
            return true;
        }

        if(plateNumber.contains("cluster")){
            ToastUtil.showShort("该地方有"+plateNumber.replace("cluster"," ")+"辆车");
            return true;
        }

        ((MainActivityPresenter) presenter).setCurrentClickMarker(marker);

//        TODO 显示车辆信息
        return  ((MainActivityPresenter)presenter).showMarkerDialog(bmapView, plateNumber);

    }

    @Override
    public void changedAll() {
    }

    @Override
    public void changed(CarInfoBean mCarInfoBean) {

        //TODO update Marker
        ((MainActivityPresenter) presenter).updateMarker(mCarInfoBean);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

//        System.out.println("KeyEvent.KEYCODE_BACK == keyCode  : "+(KeyEvent.KEYCODE_BACK == keyCode) +" SearchDialog show : "+ ((MainActivityPresenter)presenter).isShowMarkerDialog());
//        拦截返回按键，判断底部弹框是否退出
        if(KeyEvent.KEYCODE_BACK == keyCode){

            //关闭侧菜单
            if(((MainActivityPresenter) presenter).dismissUserMenu()){
                return true;
            }

            if(((MainActivityPresenter) presenter).searchDilaogDismiss()) {

                if (!UserInfo.getInstance().isLocationCar()) {

                    cancel_search.setVisibility(View.GONE);

                    if (!UserInfo.getInstance().isPerson()) {
                        main_menu.setVisibility(View.VISIBLE);
                    }
                }
                return true;
            }

            if(mProgressDialog!= null && mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
                return true;
            }

            boolean b = ((MainActivityPresenter) presenter).onDismissDialog();
            if(b){
                return true;
            }

            long secondTime=System.currentTimeMillis();
            if(secondTime-firstTime>2000){
                ToastUtil.showShort("再按一次退出程序");
                firstTime=secondTime;
                return true;
            }else{
                UDPHeartBeatConnect.getInstance().onExit();
            }

        }


        return super.onKeyDown(keyCode, event);
    }

    /**
     *
     * @Description: TODO 判断activity是否在应用的最顶层
     * @param context 上下文
     * @param intent intent携带activity
     * @return boolean true为在最顶层，false为否
     */
    public static boolean isTop(Context context, Intent intent) {
        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> appTask = am.getRunningTasks(1);
        if (appTask.size() > 0 && appTask.get(0).topActivity.equals(intent.getComponent())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 申请权限并初始化定位
     */
    public void initLocationPermission() {

        if (Build.VERSION.SDK_INT>=23){
            // Verify that all required contact permissions have been granted.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Contacts permissions have not been granted.

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, BAIDU_LOCATION_PERMISSION);

            } else {
                // Contact permissions have been granted. Show the contacts fragment.
                isUpdataMap = false;
                initLocation();
            }

        }else{
            isUpdataMap = false;
            initLocation();
        }
    }

    /**
     * 重新定位
     */
    public void refreshLocation() {

        if (Build.VERSION.SDK_INT>=23) {

            // Verify that all required contact permissions have been granted.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Contacts permissions have not been granted.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, BAIDU_REFRESH_LOCATION_PERMISSION);

            } else {
                // Contact permissions have been granted. Show the contacts fragment.
                isUpdataMap = true;
                if(mLocClient != null) {
                    mLocClient.requestLocation();
                }
            }
        }else{
            isUpdataMap = true;
            if(mLocClient != null) {
                mLocClient.requestLocation();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case BAIDU_LOCATION_PERMISSION:
                isUpdataMap = false;
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                    initLocation();
                } else {
                    // 没有获取到权限，做特殊处理
                    ToastUtil.showShort(getString(R.string.location_hint));
                }
                break;
            case BAIDU_REFRESH_LOCATION_PERMISSION:
                isUpdataMap = true;
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                    initLocation();
                } else {
                    // 没有获取到权限，做特殊处理
                    ToastUtil.showShort(getString(R.string.location_hint));
                }
                break;
            default:
                break;
        }
    }

    public void setKillAppType(boolean isKillApp){
        this.isKillApp = isKillApp;
    }

    public void cleanOpenDialog(){
        if(mProgressDialog!= null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }

        ((MainActivityPresenter) presenter).cleanOpenDoalogs();

    }

    public static void setAddPicturesListener(AddPicturesListener listener){
        mAddPicturesListener = listener;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        boolean isdispose = false;

        if(mAddPicturesListener != null){
            isdispose = mAddPicturesListener.disposePictures(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);

        if(isdispose){
            return;
        }

        Tencent.onActivityResultData(requestCode, resultCode, data, new IUiListener() {
            @Override
            public void onComplete(Object o) {
                if(isDebug)System.out.println(" IUiListener onComplete : "+o.toString());
            }

            @Override
            public void onError(UiError uiError) {
                if(isDebug)System.out.println(" IUiListener onError code : " + uiError.errorCode + " , msg : " + uiError.errorMessage + " , detail : " + uiError.errorDetail);
            }

            @Override
            public void onCancel() {
                if(isDebug)System.out.println(" IUiListener onCancel . . . . . ");
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //切换为竖屏
        if (newConfig.orientation == this.getResources().getConfiguration().ORIENTATION_PORTRAIT) {
            ((MainActivityPresenter) presenter).updateSearchDialogHeight();
            ConfigurationChangedManager.getInstance().updateDialogView();
        }
//切换为横屏
        else if (newConfig.orientation == this.getResources().getConfiguration().ORIENTATION_LANDSCAPE) {
            ((MainActivityPresenter) presenter).updateSearchDialogHeight();
            ConfigurationChangedManager.getInstance().updateDialogView();
        }

//        System.out.println("onConfigurationChanged -- "+newConfig.orientation);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
       // System.out.println("hasFocus = " + hasFocus + "   isShowLoadingFrist = " + ((MainActivityPresenter) presenter).isShowLoadingFrist);
        if (hasFocus && ((MainActivityPresenter) presenter).isShowLoadingFrist) {
            onLoading();
        }
    }
}
