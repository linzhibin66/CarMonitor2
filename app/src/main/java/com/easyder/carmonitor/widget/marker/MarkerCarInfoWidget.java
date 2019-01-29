package com.easyder.carmonitor.widget.marker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.CompactMarkerListener;
import com.easyder.carmonitor.interfaces.MarkerClickListener;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.InfoTool.AlarmInfoTool;
import com.shinetech.mvp.network.UDP.InfoTool.CarStatusInfoTool;
import com.shinetech.mvp.network.UDP.InfoTool.CarTypeTool;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarBaseInfoBean;
import com.shinetech.mvp.utils.CoordinateUtil;

/**
 * Created by ljn on 2017/4/11.
 */
public class MarkerCarInfoWidget implements View.OnClickListener, OnGetGeoCoderResultListener {

    /**
     * 车辆信息
     */
    private CarInfoBean mCarInfoBean;

    /**
     * 点击事件监听
     */
    private CompactMarkerListener mMarkerClickListener;

    //地址解析类
    private GeoCoder geoCoder;

    private ReverseGeoCodeOption reverseGeoCodeOption;

    /**
     * 地址
     */
    private TextView address_tv;

    /**
     * 车牌
     */
    private TextView plateNumber_tv;

    /**
     * 状态
     */
    private TextView status_tv;

    /**
     * 时间
     */
    private TextView time_tv;

    /**
     * 速度
     */
    private TextView marker_speed;

    /**
     * 方向
     */
//    private ImageView marker_orientation;

    /**
     * 报警状态
     */
    private TextView alarm_status;

    private TextView exception_status;

    private View rootView;

    private Context context;

    /**
     * 触摸外围区域是否退出弹框
     */
    private final boolean isdismissPopupWindow = false;

    public MarkerCarInfoWidget(Context context) {

        this.context = context;

        rootView = View.inflate(context, R.layout.marker_widget_carinfo_layout,null);

        initGeoCoder();

        initListener();

    }

    public MarkerCarInfoWidget(Context context, CarInfoBean mCarInfoBean) {

        this.context = context;

        rootView = View.inflate(context, R.layout.marker_widget_carinfo_layout,null);

        initGeoCoder();

        initListener();

        updateViewInfo(mCarInfoBean);

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

    /**
     * 设置监听事件
     * @param mMarkerClickListener
     */
    public void setClickListener(CompactMarkerListener mMarkerClickListener){
        this.mMarkerClickListener = mMarkerClickListener;

    }

    public boolean isMarkerCarInfoChange(CarInfoBean carInfoBean){
        if(mCarInfoBean !=null && mCarInfoBean.getPlateNumber().equals(carInfoBean.getPlateNumber()) && !mCarInfoBean.equals(carInfoBean)) {
            return true;
        }
        return false;
    }

    public View getLayout(){
        return rootView;
    }

    /**
     * 更新界面信息
     * @param mCarInfoBean
     */
    public void updateViewInfo(CarInfoBean mCarInfoBean){
        this.mCarInfoBean = mCarInfoBean;

        if(mCarInfoBean==null){
            return;
        }

        View layout = getLayout();

        //车牌
        if(plateNumber_tv == null){
            plateNumber_tv = (TextView) layout.findViewById(R.id.marker_platenumber);
        }

        plateNumber_tv.setText(mCarInfoBean.getPlateNumber());

        int carinfoIcon;

        ResponseCarBaseInfoBean carBaseInfo = UserInfo.getInstance().getCarBaseInfo(mCarInfoBean.getPlateNumber());

        if((carBaseInfo != null) && (!TextUtils.isEmpty(carBaseInfo.getCarType()))) {

            carinfoIcon = CarTypeTool.getMarkerInfoCarIcon(carBaseInfo.getCarType());

        }else{
            carinfoIcon = R.mipmap.marker_info_car;
        }

        plateNumber_tv.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(carinfoIcon),null,null,null);

        //状态
        if(status_tv == null){
            status_tv = (TextView) layout.findViewById(R.id.marker_status);
        }

        if(CarStatusInfoTool.getAccStatus(mCarInfoBean.getStatus())){
            status_tv.setBackgroundResource(R.mipmap.icon_run);
        }else{
            status_tv.setBackgroundResource(R.mipmap.icon_stop);
        }

        // 报警状态
        if(alarm_status == null){
            alarm_status = (TextView) layout.findViewById(R.id.alarm_status);
        }

        if(AlarmInfoTool.isAlarm(mCarInfoBean)){
//        if(mCarInfoBean.getAlarmType()>0 && CarStatusInfoTool.getOnlineStatus(mCarInfoBean.getStatus())){
            alarm_status.setVisibility(View.VISIBLE);
        }else{
            alarm_status.setVisibility(View.GONE);
        }

        //异常状态
        if(exception_status == null){
            exception_status = (TextView) layout.findViewById(R.id.exception_status);
        }

        if(AlarmInfoTool.isExceptionp(mCarInfoBean.getAlarmType())) {
            exception_status.setVisibility(View.VISIBLE);
        }else{
            exception_status.setVisibility(View.GONE);
        }


       /* if(marker_orientation == null){
            marker_orientation = (ImageView) layout.findViewById(R.id.marker_orientation);
        }

        int orientationImage = getOrientationImage(mCarInfoBean.getOrientation());
        marker_orientation.setImageResource(orientationImage);*/


        //地址
        if(address_tv == null){
            address_tv = (TextView) layout.findViewById(R.id.marker_address);
        }

        //时间
        if(time_tv == null){
            time_tv = (TextView) layout.findViewById(R.id.marker_time);
        }

//      根据坐标解析地址
        double lng = mCarInfoBean.getLng() / 1E6;
        double lat = mCarInfoBean.getLat() / 1E6;

        if(lng == 0 && lat == 0){
            address_tv.setText(context.getString(R.string.no_location_info));
            time_tv.setText("");
        }else{
            address_tv.setText(context.getString(R.string.marker_address_analysis));
            LatLng latLng = CoordinateUtil.gps84_to_bd09(lat, lng);

            reverseGeoCodeOption.location(latLng);
            geoCoder.reverseGeoCode(reverseGeoCodeOption);

            time_tv.setText(mCarInfoBean.getLocationTime());
        }

        //速度
        if(marker_speed == null) {
            marker_speed = (TextView) layout.findViewById(R.id.marker_speed);
        }
        marker_speed.setText(mCarInfoBean.getgNSSSpeed() + "km/h");

    }

    /**
     * 初始化界面点击事件
     */
    private void initListener(){
        View layout = getLayout();
        layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(mMarkerClickListener!=null){
            mMarkerClickListener.onLayoutClick(mCarInfoBean,getAdress());
        }

    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

       /* View layout = getLayout();
        TextView address_tv = (TextView) layout.findViewById(R.id.marker_address);
        System.out.println("onGetGeoCodeResult : "+geoCodeResult.getAddress());
        address_tv.setText(geoCodeResult.getAddress());*/

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
//        System.out.println("onGetReverseGeoCodeResult : "+reverseGeoCodeResult.getAddress());
        String address = context.getString(R.string.marker_address_error);
        if (!TextUtils.isEmpty(reverseGeoCodeResult.getAddress())){
            address = reverseGeoCodeResult.getAddress();
        }

        if(address_tv != null){

            address_tv.setText(address);
        }
    }

    public CarInfoBean getCarInfoBean(){
        return mCarInfoBean;
    }

    public String getAdress(){
        if(address_tv != null){
           return address_tv.getText().toString();
        }
        return context.getString(R.string.marker_address_error);
    }


   /* public int getOrientationImage(short orientation){

        orientation = (short) (orientation%360);

        if((orientation>(360-22.5)) && orientation <= 22.5){
            return R.mipmap.icon_north;
        }else if(orientation>22.5 && (orientation <= (45+22.5))){
            return R.mipmap.icon_northeast;
        }else if(orientation>(45+22.5) && (orientation <= (90+22.5))){
            return R.mipmap.icon_east;
        }else if(orientation>(90+22.5) && (orientation <= (135+22.5))){
            return R.mipmap.icon_southeast;
        }else if(orientation>(135+22.5) && (orientation <= (180+22.5))){
            return R.mipmap.icon_south;
        }else if(orientation>(180+22.5) && (orientation <= (225+22.5))){
            return R.mipmap.icon_southwest;
        }else if(orientation>(225+22.5) && (orientation <= (270+22.5))){
            return R.mipmap.icon_west;
        }else if(orientation>(270+22.5) && (orientation <= (315+22.5))){
            return R.mipmap.icon_northwest;
        }

        return R.mipmap.icon_north;
    }*/


   //View 中车牌号
   public String getplateNumberOfView(){
       if(plateNumber_tv != null) {
           return plateNumber_tv.getText().toString().trim();
       }
       return "";
   }

   //view 中定位时间
   public String getLocationTimeOfView(){
        if(time_tv != null){
            return time_tv.getText().toString().trim();
        }

        return "";
   }


}
