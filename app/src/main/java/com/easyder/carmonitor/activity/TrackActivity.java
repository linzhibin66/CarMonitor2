package com.easyder.carmonitor.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.bean.TrackLogItem;
import com.easyder.carmonitor.dialog.HintDialog;
import com.easyder.carmonitor.dialog.TrackLogDialog;
import com.easyder.carmonitor.dialog.markerShowScheme.CompactMarkerScheme;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.interfaces.TrackDataListener;
import com.easyder.carmonitor.interfaces.TrackLogItemClick;
import com.easyder.carmonitor.interfaces.TrackPlayListener;
import com.easyder.carmonitor.presenter.TrackActivityPresenter;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.basePresenter.MvpBasePresenter;
import com.shinetech.mvp.network.UDP.InfoTool.CarTypeTool;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.CarHistoricalRouteBean;
import com.shinetech.mvp.network.UDP.listener.BaseCarHistoricalRouteItem;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarBaseInfoBean;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarHistoricalRouteBean;
import com.shinetech.mvp.utils.CoordinateUtil;
import com.shinetech.mvp.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by ljn on 2017-04-25.
 */
public class TrackActivity extends BaseActivity implements OnGetGeoCoderResultListener, TrackPlayListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    @Bind(R.id.track_bmapView)
    MapView track_bmapView;

    @Bind(R.id.track_platenumber)
    TextView track_platenumber;

    @Bind(R.id.track_speed)
    TextView track_speed;

    @Bind(R.id.track_address)
    TextView track_address;

    @Bind(R.id.track_time)
    TextView track_time;

    //track_total_mileage
    @Bind(R.id.track_total_mileage)
    TextView track_total_mileage;

    @Bind(R.id.track_play)
    ImageButton track_play;

    @Bind(R.id.track_seekbar)
    SeekBar track_seekbar;

  /*  @Bind(R.id.track_refresh)
    ImageButton track_refresh;
*/
    @Bind(R.id.title_back)
    ImageButton title_back;

    @Bind(R.id.title_text)
    TextView title_text;

    @Bind(R.id.title_search)
    ImageButton title_menu;

    @Bind(R.id.track_play_speed_tv)
    TextView track_play_speed_tv;

    @Bind(R.id.track_play_speed_layout)
    RelativeLayout track_play_speed_layout;

    @Bind(R.id.hint_loading_layout)
    RelativeLayout hint_loading_layout;

    @Bind(R.id.title_layout)
    RelativeLayout title_layout;

    private BaiduMap mBaiduMap;

    public static final String TRACK_INFO = "track_car_info";
    public static final String TRACK_INFO_PLATENUMBER = "track_car_plateNumber_info";

    //地址解析类
    private GeoCoder geoCoder;

    private String plateNumber;

    private String startTime;

    private String endTime;

    private  Marker carMarker;

    private ReverseGeoCodeOption reverseGeoCodeOption;

    private boolean isplayingOfAfterSeek = false;

    private TrackLogDialog mTrackLogDialog;

    private HintDialog mHintDialog;

    private boolean isFirstTime = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //初始化地图
        initMapView();

        //初始化标题
        initTitle();

        //初始化百度地址解析类
        initGeoCoder();

        //初始化播放控制界面
        initCtrlView();

        Intent intent = getIntent();

        plateNumber = intent.getStringExtra(CompactMarkerScheme.MARKER_PLATENUMBER);
        startTime = intent.getStringExtra(CompactMarkerScheme.MARKER_START_TIME);
        endTime = intent.getStringExtra(CompactMarkerScheme.MARKER_END_TIME);
        track_platenumber.setText(plateNumber);
        if(track_address != null) {
            track_address.setText(getString(R.string.marker_address_analysis));
        }
        track_time.setText(startTime + ":00");

    }

    @Override
    protected int getView() {
        return R.layout.activity_track;
    }

    @Override
    protected MvpBasePresenter createPresenter() {
        return new TrackActivityPresenter();
    }

    /**
     * 初始化标题
     */
    private void initTitle(){

        title_text.setText(getString(R.string.track_activity_title));

        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        title_menu.setImageResource(R.mipmap.icon_list2);

        title_menu.setOnClickListener(this);

        UIUtils.setImmersiveStatusBar(title_layout);
    }

    /**
     * 初始化百度地图
     */
    private void initMapView(){
        //      设置是否显示缩放控件
        track_bmapView.showZoomControls(false);
//      设置是否显示比例尺控件
        track_bmapView.showScaleControl(false);

        mBaiduMap = track_bmapView.getMap();
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomBy(5));

        //检测地图状态改变（用于刷新界面中可见的车辆）
//        mBaiduMap.setOnMapStatusChangeListener(this);

        //设置是否允许指南针
        mBaiduMap.getUiSettings().setCompassEnabled(false);
        //设置是否允许旋转手势
        mBaiduMap.getUiSettings().setRotateGesturesEnabled(false);
//        mBaiduMap.setCompassPosition(new Point(80, 200));
        //设置是否允许俯视手势
        mBaiduMap.getUiSettings().setOverlookingGesturesEnabled(false);

        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        mBaiduMap.setTrafficEnabled(false);
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

    private void updataCarinfo(BaseCarHistoricalRouteItem caromfpItem){

//      根据坐标解析地址
        double lng = caromfpItem.getLng() / 1E6;
        double lat = caromfpItem.getLat() / 1E6;

        LatLng latLng = CoordinateUtil.gps84_to_bd09(lat, lng);

        reverseGeoCodeOption.location(latLng);
        geoCoder.reverseGeoCode(reverseGeoCodeOption);

        track_speed.setText(caromfpItem.getmGNSSSpeed() + "km/h");

        track_time.setText(caromfpItem.getLocationTime());


    }

    private void initCtrlView(){
        track_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!((TrackActivityPresenter) presenter).isPlay()) {
                    ((TrackActivityPresenter) presenter).setMarker(carMarker);
                    ((TrackActivityPresenter) presenter).trackPlay();
                    track_play.setImageResource(R.mipmap.icon_pause);
                } else {
                    ((TrackActivityPresenter) presenter).trackStop();
                    track_play.setImageResource(R.mipmap.btn_play);
                }
            }
        });
      /*  track_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TrackActivityPresenter) presenter).trackReset();
            }
        });*/
        String playSpeed = ((TrackActivityPresenter) presenter).getPlaySpeed(this);
        track_play_speed_tv.setText(playSpeed);

        track_play_speed_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String speed = ((TrackActivityPresenter) presenter).setPlaySpeed(TrackActivity.this);
                track_play_speed_tv.setText(speed);
            }
        });

        track_seekbar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void showContentView(BaseVo dataVo) {

        if(dataVo!=null && (dataVo instanceof CarHistoricalRouteBean || dataVo instanceof ResponseCarHistoricalRouteBean)) {
            mBaiduMap.clear();
            List<LatLng> latLngs = ((TrackActivityPresenter) presenter).getpolyLines();
            List<LatLng> polylines = new ArrayList<>();
            if(latLngs.size()>0) {
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(latLngs.get(0)));
            }
            polylines.addAll(latLngs);
//            polylines.add(latLngs.get(0));
            if(polylines.size()>0){
                if(polylines.size()<2){
                    polylines.add(latLngs.get(0));
                }

            }else{
            //TODO hint no track info and finish Activity
//                ToastUtil.showShort("no track info");
                showHintDailog();

                return;

            }

            PolylineOptions mPolylineOptions = new PolylineOptions().points(polylines).width(5).color(Color.RED);
            mBaiduMap.addOverlay(mPolylineOptions);

            OverlayOptions markerOptions;
            markerOptions = new MarkerOptions().flat(true).anchor(0.5f, 1f)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_map_star)).position(polylines.get(0));
            mBaiduMap.addOverlay(markerOptions);


            OverlayOptions markerOptions2 = new MarkerOptions().flat(true).anchor(0.5f, 1f)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_map_stop)).position(polylines.get(polylines.size()-1));

            mBaiduMap.addOverlay(markerOptions2);

            //总里程
            if(MainApplication.isUserResponseUDP) {
                float totalMileage = ((TrackActivityPresenter) presenter).getTotalMileage();
                float mileage = totalMileage / 10f;
                track_total_mileage.setText(getString(R.string.track_total_mileage_str, mileage + ""));
                track_total_mileage.setVisibility(View.VISIBLE);
            }else{
                track_total_mileage.setVisibility(View.GONE);
            }

            track_seekbar.setMax(polylines.size());
            track_seekbar.setProgress(0);

            if(track_address != null) {
                track_address.setText(getString(R.string.marker_address_analysis));
            }
            BaseCarHistoricalRouteItem historiCarInfo = ((TrackActivityPresenter) presenter).getHistoriCarInfo(0);

            if(historiCarInfo != null) {

//                plateNumber
                BitmapDescriptor carIcon;
                ResponseCarBaseInfoBean carBaseInfo = UserInfo.getInstance().getCarBaseInfo(plateNumber);
                if(carBaseInfo == null){
                    carIcon = CarTypeTool.getDefCarIcon();
                }else {
                    carIcon = CarTypeTool.getCarIcon(carBaseInfo.getCarType());
                }
                OverlayOptions carMarkerOptions = new MarkerOptions().flat(true).anchor(0.5f, 0.5f)
                        .icon(carIcon).position(polylines.get(0)).rotate(360-historiCarInfo.getOrientation());
                carMarker = (Marker)mBaiduMap.addOverlay(carMarkerOptions);
                updataCarinfo(historiCarInfo);
            }

            ((TrackActivityPresenter) presenter).setTrackPlayListener(this);
            ((TrackActivityPresenter) presenter).setMarker(carMarker);
        }

    }

    @Override
    public void onLoading() {
        if(hint_loading_layout.getVisibility() != View.VISIBLE) {
            hint_loading_layout.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onStopLoading() {
        if(hint_loading_layout.getVisibility() == View.VISIBLE) {
            hint_loading_layout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if (!TextUtils.isEmpty(reverseGeoCodeResult.getAddress())){
            String address = reverseGeoCodeResult.getAddress();
            if(track_address != null && !TextUtils.isEmpty(address)){

                track_address.setText(address);
            }
        }
    }

    @Override
    public void onTrackChangeProgress(int max, int index, BaseCarHistoricalRouteItem carinfo) {
        track_seekbar.setMax(max);
        track_seekbar.setProgress(index);

        updataCarinfo(carinfo);
    }

    @Override
    public void onTrackStop() {
        track_play.setImageResource(R.mipmap.btn_play);

    }

    @Override
    public void onTrackReset() {
        track_play.setImageResource(R.mipmap.btn_play);
        track_seekbar.setProgress(0);

    }

    @Override
    public void onPlay() {
        track_play.setImageResource(R.mipmap.icon_pause);
    }

    @Override
    protected void onResume() {
        track_bmapView.onResume();
        super.onResume();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

//            System.out.println("------------------ hasFocus = "+hasFocus);
        if(hasFocus && isFirstTime){
            isFirstTime = false;
            ((TrackActivityPresenter) presenter).getCarHistory(plateNumber, startTime + ":00", endTime + ":00", new TrackDataListener() {
                @Override
                public void noData() {
                    //TODO hint no track info and finish Activity
//                ToastUtil.showShort("no track info");
                    onStopLoading();
                    showHintDailog();

                }
            });
        }
    }

    private void showHintDailog(){

        if(mHintDialog == null) {
            mHintDialog = new HintDialog(TrackActivity.this, new LayoutBackListener() {
                @Override
                public void onBack() {
                    finish();
                }
            });
        }

        if(!TrackActivity.this.isFinishing() && !mHintDialog.isShowing()){
            mHintDialog.show(title_layout);
        }
    }

    @Override
    protected void onPause() {
        track_bmapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        ((TrackActivityPresenter) presenter).onDestroy();
        track_bmapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(presenter != null) {
            ((TrackActivityPresenter) presenter).trackSeekIndex(progress);
            BaseCarHistoricalRouteItem historiCarInfo = ((TrackActivityPresenter) presenter).getHistoriCarInfo(progress);
            if (historiCarInfo != null) {
                updataCarinfo(historiCarInfo);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if(presenter != null && ((TrackActivityPresenter) presenter).isPlay()){
            ((TrackActivityPresenter) presenter).trackStop();
            isplayingOfAfterSeek = true;
        }

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        if(presenter != null && isplayingOfAfterSeek){
            ((TrackActivityPresenter) presenter).trackPlay();
            isplayingOfAfterSeek = false;
        }
    }

    @Override
    public void onClick(View v) {

        if(mTrackLogDialog == null) {
            mTrackLogDialog = new TrackLogDialog(this, (TrackActivityPresenter) presenter);
            mTrackLogDialog.setClickListener(new TrackLogItemClick() {
                @Override
                public void onItemClick(TrackLogItem item) {


                    Intent intent = new Intent(TrackActivity.this,TrackLogInfoActivity.class);
//                    plateNumber
                    intent.putExtra(TRACK_INFO_PLATENUMBER, plateNumber);
                    intent.putExtra(TRACK_INFO, item);
                    startActivity(intent);
                    overridePendingTransition(R.anim.pop_right2left_anim_open, R.anim.pop_right2left_anim_close);

                }
            });
        }else {
            mTrackLogDialog.update();
        }

        if(!mTrackLogDialog.isShowing()) {
            mTrackLogDialog.show(title_menu);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        //切换为竖屏
        if (newConfig.orientation == this.getResources().getConfiguration().ORIENTATION_PORTRAIT) {
            if(mTrackLogDialog != null && mTrackLogDialog.isShowing()) {
                mTrackLogDialog.updateLayout();
            }
        }
        //切换为横屏
        else if (newConfig.orientation == this.getResources().getConfiguration().ORIENTATION_LANDSCAPE) {
            if(mTrackLogDialog != null && mTrackLogDialog.isShowing()) {
                mTrackLogDialog.updateLayout();
            }
        }
    }
}
